package binary;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/** A buffer for storing, writing, and manipulating data as a byte array
 * This class was created out of the need for an object that combines the
 * resizeability of a {@link ByteArrayOutputStream} and the indexed
 * positioning of a {@link ByteBuffer} with the utility read/write methods
 * of {@link DataOutput}.<br/>
 * Basically it is a random access data input and output stream based
 * on a byte array rather than a file or socket.
 * @author TeamworkGuy2
 * @since 2013-8-3
 */
public class ByteBufferArray implements DataOutput, DataInput, Closeable {
	/** The buffer to store data in
	 */
	private byte buffer[];
	/** The current absolute max data index (offset is not added to this value)
	 * maxPos-1 is the highest absolute index containing data
	 * Size can be calculated by subtracting offset from maxPos
	 */
	private int maxPos;
	/** The current index to write data to
	 */
	private int pos;
	/** Number of bytes written to this buffer
	 */
	//private int writeCount;


	/** Constructs a byte buffer with an initial size of 64 bytes
	 */
	public ByteBufferArray() {
		this(64);
	}


	/** Constructs a byte buffer with the specified byte size
	 * @param size the initial byte size of the new buffer
	 */
	public ByteBufferArray(int size) {
		if(size < 1) {
			throw new IllegalArgumentException("Buffer size must be greater than zero: " + size);
		}
		this.buffer = new byte[size];
	}


	/** Write the lowest byte of data from the input integer to this buffer array
	 * @param b an integer, write the lowest byte of this integer
	 */
	@Override
	public void write(int b) {
		// Write a byte
		ensureNewDataFits(1);
		buffer[pos] = (byte)b;
		bytesAdded(1);
	}


	@Override
	public void write(byte[] bytes) {
		// Write a byte array
		write(bytes, 0, bytes.length);
	}


	@Override
	public void write(byte[] bytes, int offset, int length) {
		if((offset < 0 || length < 0) || (offset+length > bytes.length || offset+length < 0)) {
			throw new IndexOutOfBoundsException();
		}
		else if(length == 0) {
			return;
		}
		// length is positive since it was checked by the previous if statement for length < 0
		ensureNewDataFits(length);
		System.arraycopy(bytes, offset, buffer, pos, length);
		bytesAdded(length);
	}


	@Override
	public final void writeBoolean(boolean value) {
		// Write a boolean, 1=true, 0=false (big endian format)
		ensureNewDataFits(1);
		buffer[pos] = (byte)(value == true ? 1 : 0);
		bytesAdded(1);
	}


	@Override
	public final void writeByte(int value) {
		// Write a byte
		ensureNewDataFits(1);
		buffer[pos] = (byte)value;
		bytesAdded(1);
	}


	@Override
	public final void writeShort(int value) {
		// Write a short, assumed 2 bytes (big endian format)
		ensureNewDataFits(2);
		buffer[pos] = (byte)((value >>> 8) & 0xFF);
		buffer[pos+1] = (byte)(value & 0xFF);
		bytesAdded(2);
	}


	@Override
	public final void writeChar(int value) {
		// Write a char, assumed 2 bytes (big endian format)
		ensureNewDataFits(2);
		buffer[pos] = (byte)((value >>> 8) & 0xFF);
		buffer[pos+1] = (byte)(value & 0xFF);
		bytesAdded(2);
	}


	@Override
	public final void writeInt(int value) {
		// Write an int, assumed 4 bytes (big endian format)
		ensureNewDataFits(4);
		buffer[pos] = (byte)((value >>> 24) & 0xFF);
		buffer[pos+1] = (byte)((value >>> 16) & 0xFF);
		buffer[pos+2] = (byte)((value >>> 8) & 0xFF);
		buffer[pos+3] = (byte)(value & 0xFF);
		bytesAdded(4);
	}


	@Override
	public final void writeLong(long value) {
		// Write a long, assumed 8 bytes (big endian format)
		ensureNewDataFits(8);
		buffer[pos] = (byte)((value >>> 56) & 0xFF);
		buffer[pos+1] = (byte)((value >>> 48) & 0xFF);
		buffer[pos+2] = (byte)((value >>> 40) & 0xFF);
		buffer[pos+3] = (byte)((value >>> 32) & 0xFF);
		buffer[pos+4] = (byte)((value >>> 24) & 0xFF);
		buffer[pos+5] = (byte)((value >>> 16) & 0xFF);
		buffer[pos+6] = (byte)((value >>> 8) & 0xFF);
		buffer[pos+7] = (byte)(value & 0xFF);
		bytesAdded(8);
	}


	@Override
	public final void writeFloat(float value) {
		writeInt(Float.floatToIntBits(value));
	}


	@Override
	public final void writeDouble(double value) {
		writeLong(Double.doubleToLongBits(value));
	}


	@Override
	public final void writeBytes(String string) {
		int length = string.length();
		ensureNewDataFits(length);
		// Write the lowest byte of each character
		for(int i = 0, index = pos; i < length; i++, index++) {
			buffer[index] = (byte)string.charAt(i);
		}
		bytesAdded(length);
	}


	@Override
	public final void writeChars(String string) {
		int length = string.length();
		int size = (length << 1);
		ensureNewDataFits(size);
		char character = 0;
		// For each character write a char regardless of whether the character is one or two bytes
		for(int i = 0, index = pos; i < length; i++, index+=2) {
			character = string.charAt(i);
			buffer[index] = (byte)((character >>> 8) & 0xFF);
			buffer[index+1] = (byte)(character & 0xFF);
		}
		bytesAdded(size);
	}


	@Override
	public final void writeUTF(String string) throws IOException {
		int stringLength = string.length();
		int utfLength = 0;
		int oneByteCount = 0;
		char character = 0;

		// Calculate the number of bytes in the string by calcuating how many bytes each character will consume
		for(int i = 0; i < stringLength; i++) {
			character = string.charAt(i);
			// Check for the different types of UTF-8 characters
			if((character >= 0x0001) && (character <= 0x007F)) {
				utfLength++;
				// Keep track of how many characters are one byte, so that we can detect
				// strings that are all one byte characters
				oneByteCount++;
			}
			else if(character > 0x07FF) {
				utfLength += 3;
			}
			else {
				utfLength += 2;
			}
		}

		if(utfLength > 0xFFFF) {
			throw new UTFDataFormatException("string length to long to encode: " + utfLength);
		}

		// Ensure the new data will fit in the buffer and write it
		ensureNewDataFits(2+utfLength);
		buffer[pos] = (byte)((utfLength >>> 8) & 0xFF);
		buffer[pos+1] = (byte)(utfLength & 0xFF);
		int offset = pos+2;

		// If the string is made purely of ASCII characters (which many are made from)
		// then just write it byte by byte without complex if conditions
		if(oneByteCount == utfLength) {
			for(int i = 0; i < stringLength; i++) {
				character = string.charAt(i);
				buffer[offset] = (byte)character;
				offset++;
			}
		}
		// The string contains only multi-byte characters, so write it with if conditions
		else {
			for(int i = 0; i < stringLength; i++) {
				character = string.charAt(i);
				if((character >= 0x0001) && (character <= 0x007F)) {
					buffer[offset] = (byte)character;
					offset++;
				}
				else if(character > 0x07FF) {
					buffer[offset] = (byte)(0xE0 | ((character >>> 12) & 0x0F));
					buffer[offset+1] = (byte)(0x80 | ((character >>> 6) & 0x3F));
					buffer[offset+2] = (byte)(0x80 | (character & 0x3F));
					offset+=3;
				}
				else {
					buffer[offset] = (byte)(0xC0 | ((character >>> 6) & 0x1F));
					buffer[offset+1] = (byte)(0x80 | (character & 0x3F));
					offset+=2;
				}
			}
		}
		// Update the buffer position and bytes written count
		bytesAdded(2+utfLength);
	}


	/** Read a single byte and return its unsigned value in the range
	 * <code>0</code> to <code>255</code>
	 * @return the byte value read or -1 if the end of the stream has been reached
	 */
	public int read() {
		return (readByte() & 0xFF);
	}

	/** Copy as many bytes as possible from this buffer starting at the
	 * buffer's current position into the specified destination array
	 * @param dst the destination array to write the bytes to
	 * starting at the first byte up to the length of the array.
	 * @return the number of byte successfully read and transfer into the array
	 */
	public int read(byte[] dst) {
		return read(dst, 0, dst.length);
	}


	/** Copy as many bytes as possible from this buffer starting at the
	 * buffer's current position into the specified destination array
	 * @param dst the destination array to write the bytes to
	 * @param offset the destination array offset at which to begin writing data
	 * @param length the number of bytes to copy from this buffer into the
	 * destination array
	 * @return the number of bytes read
	 */
	public int read(byte[] dst, int offset, int length) {
		// Check that the offset and length and destination array are all within correct ranges
		if((offset < 0 || length < 0) || (offset+length > dst.length || offset+length < 0)) {
			throw new IndexOutOfBoundsException();
		}
		// Make sure that this buffer has enough data to read and fill length number of bytes
		int newPos = pos+length;
		if(newPos > maxPos) {
			newPos = maxPos;
			length = maxPos-pos;
		}
		// Move the position ahead since read operations do move the position
		// This will never exceed maxPos since the previous if statement already checked that newPos <= maxPos
		int validPos = checkAndRead(length); // Adds length to this buffer's position!
		// Copy the bytes into the destination array
		System.arraycopy(buffer, validPos, dst, offset, length);

		return length;
	}


	/** See the {@link DataInput#readFully(byte[]) DataInput.readFully()}
	 * contract documentation. This method roughly follows that documentation
	 * except a {@link BufferUnderflowException} is thrown instead of
	 * an {@link EOFException}
	 */
	@Override
	public void readFully(byte[] b) {
		read(b, 0, b.length);
	}


	/** See the {@link DataInput#readFully(byte[], int, int) DataInput.readFully()}
	 * contract documentation. This method roughly follows that documentation
	 * except a {@link BufferUnderflowException} is thrown instead of
	 * an {@link EOFException}
	 */
	@Override
	public void readFully(byte[] b, int off, int len) {
		read(b, off, len);
	}


	/** See the sk
	 */
	@Override
	public int skipBytes(int n) {
		checkAndRead(n); // Adds n to this buffer's position!
		return n;
	}


	@Override
	public boolean readBoolean() {
		int validPos = checkAndRead(1); // Adds 1 to this buffer's position!
		boolean result = buffer[validPos] != 0;
		return result;
	}


	@Override
	public byte readByte() {
		int validPos = checkAndRead(1); // Adds 1 to this buffer's position!
		byte result = buffer[validPos];
		return result;
	}


	@Override
	public int readUnsignedByte() {
		int validPos = checkAndRead(1); // Adds 1 to this buffer's position!
		int result = (buffer[validPos] & 0xFF);
		return result;
	}


	@Override
	public short readShort() {
		int validPos = checkAndRead(2); // Adds 2 to this buffer's position!
		short result = (short)(((buffer[validPos] & 0xFF) << 8) | (buffer[validPos+1] & 0xFF));
		return result;
	}


	@Override
	public int readUnsignedShort() {
		int validPos = checkAndRead(2); // Adds 2 to this buffer's position!
		int result = (buffer[validPos] << 8) + buffer[validPos+1];
		return result;
	}


	@Override
	public char readChar() {
		int validPos = checkAndRead(2); // Adds 2 to this buffer's position!
		char ch = (char)(((buffer[validPos] & 0xFF) << 8) | (buffer[validPos+1] & 0xFF));
		return ch;
	}


	@Override
	public int readInt() {
		int validPos = checkAndRead(4); // Adds 4 to this buffer's position!
		int result = (int)(((buffer[validPos] & 0xFF) << 24) | ((buffer[validPos+1] & 0xFF) << 16)
				| ((buffer[validPos+2] & 0xFF) << 8) | (buffer[validPos+3] & 0xFF));
		return result;
	}


	@Override
	public long readLong() {
		int validPos = checkAndRead(8); // Adds 8 to this buffer's position!
		long result = (long)(((long)(buffer[validPos] & 0xFF) << 56) |
				((long)(buffer[validPos+1] & 0xFF) << 48) |
				((long)(buffer[validPos+2] & 0xFF) << 40) |
				((long)(buffer[validPos+3] & 0xFF) << 32) |
				((long)(buffer[validPos+4] & 0xFF) << 24) |
				((buffer[validPos+5] & 0xFF) << 16) |
				((buffer[validPos+6] & 0xFF) << 8) |
				(buffer[validPos+7] & 0xFF));
		return result;
	}


	@Override
	public float readFloat() {
		// readInt() takes care of checking for valid buffer position, etc
		float result = Float.intBitsToFloat(readInt());
		return result;
	}


	@Override
	public double readDouble() {
		// readLong() takes care of checking for valid buffer position, etc
		double result = Double.longBitsToDouble(readLong());
		return result;
	}


	@Override
	public String readUTF() throws IOException {
		int utfLength = readUnsignedShort(); // Adds 2 to this buffer's position!
		int validPos = checkAndRead(utfLength); // Adds utfLength to this buffer's position!
		// Wasteful, should find a better way to allocate space without
		// having to constantly bounds checking the array
		char[] chars = new char[utfLength];
		char ch = 0;
		byte b2 = 0;
		byte b3 = 0;
		int offset = validPos;
		int lastIndex = offset+utfLength;
		int charIndex = 0;
		// Read as many one byte characters as possible, either the entire
		// string or until a multi-byte character is encountered
		for( ; offset < lastIndex; offset++, charIndex++) {
			ch = (char)(buffer[offset] & 0xFF);
			if(ch > 127) { break; }
			chars[charIndex] = ch;
		}
		// Read remaining multi-byte or single byte characters
		for( ; offset < lastIndex; charIndex++) {
			ch = (char)(buffer[offset] & 0xFF);
			// If the first bit is 0, it is a 1 byte character (range: 0xxxxxxx)
			if((ch >>> 7) == 0) {
				chars[charIndex] = ch;
				offset++;
			}
			// If the first byte indicates a 2 byte character (range: 110xxxxx 10xxxxxx)
			else if((ch >>> 5) == 0x6) {
				if(offset+2 > utfLength) { throw new UTFDataFormatException("malformed string: partial end character"); }
				b2 = buffer[offset+1];
				if((b2 & 0xC0) != 0x80) {
					throw new UTFDataFormatException("malformed char at buffer index " + (offset+1));
				}
				chars[charIndex] = (char)(((ch & 0x1F) << 6) | (b2 & 0x3F));
				offset += 2;
			}
			// If the first byte indicates a 3 byte character (range: 1110xxxx 10xxxxxx 10xxxxxx)
			else if((ch >>> 4) == 0xE) {
				if(offset+3 > utfLength) { throw new UTFDataFormatException("malformed string: partial end character"); }
				b2 = buffer[offset+1];
				b3 = buffer[offset+2];
				if((b2 & 0xC0) != 0x80 || (b3 & 0xC0) != 0x80) {
					throw new UTFDataFormatException("malformed char at buffer index " + (offset+1) + " to " + (offset+2));
				}
				chars[charIndex] = (char)(((ch & 0x0F) << 12) |
						((b2 & 0x3F) << 6) |
						(b3 & 0x3F));
				offset += 3;
			}
			// Illegal character codes, (example: 10xxxxxx, 1111xxxx)
			else {
				throw new UTFDataFormatException("malformed char at buffer index " + offset);
			}
		}
		return new String(chars, 0, charIndex);
	}


	/** This operation is depreciated and not implemented by this buffer.
	 * Calling this method throws an {@link UnsupportedOperationException}
	 */
	@Deprecated
	@Override
	public String readLine() {
		throw new UnsupportedOperationException("deprecated: readLine()");
	}


	@Override
	public void close() {
		buffer = null;
		pos = -1;
		maxPos = -1;
	}


	/** Clear this buffer completely and set the buffer size to zero.
	 * The number of bytes written is not reset
	 */
	public void clear() {
		pos = 0;
		maxPos = 0;
	}


	/** Write this buffer to the specified output stream
	 * @param out the output stream to write this buffer to starting from the
	 * current position, {@link #size()} bytes are written to the output stream
	 * @throws IOException
	 */
	public void writeTo(OutputStream out) throws IOException {
		out.write(buffer, pos, maxPos-pos);
	}


	/** Write this buffer to the specified data output stream
	 * @param out the data output to write this buffer to starting from the
	 * current position, {@link #size()} bytes are written to the data output
	 * @throws IOException
	 */
	public void writeTo(DataOutput out) throws IOException {
		out.write(buffer, pos, maxPos-pos);
	}


	/** Write this buffer to the specified byte buffer
	 * @param bb the byte buffer to write this buffer to starting from the
	 * current position, {@link #size()} bytes are written to the byte buffer
	 */
	public void writeTo(ByteBuffer bb) {
		bb.put(buffer, pos, maxPos-pos);
	}


	/** Return this buffer's data from the current position to the buffer's
	 * limit. The returned array is {@link #size()} - {@link #position()} bytes long.
	 * @return a byte array {@link #size()} - {@link #position()} bytes long
	 * filled with this buffer's data
	 */
	public byte[] toByteArray() {
		return Arrays.copyOfRange(buffer, pos, maxPos);
	}


	/** This buffer's entire size, not counting the buffer's current position
	 * @return this buffer's entire size
	 */
	public int size() {
		return maxPos;
	}


	/** The remaining size of this buffer based on the buffer's current position
	 * @return this buffer's remaining size, equivalent to <code>{@link #size()} - {@link #position()}</code>
	 */
	public int remaining() {
		return maxPos-pos;
	}


	/** Get the buffer's current position
	 * @return the current byte index position of the buffer in its internal byte array
	 */
	public int position() {
		return pos;
	}


	/** Set the buffer's current position, the buffer's size is not reset to zero.
	 * To reset the buffer's size to zero call {@link #clear()}
	 * throws an exception if the index is outside of the buffer's size
	 * @param index the internal byte array index to set this buffer's current byte index position to.
	 * This value must be greater than or equal to 0 and less than {@link #size()}.
	 */
	public void position(int index) {
		if(index < 0 || index > maxPos) {
			throw new IndexOutOfBoundsException();
		}
		pos = index;
	}


	/** Check that the specified number of bytes exist in this buffer
	 * and can be read.  Increment this buffer's position and return
	 * the old position. This is normally called at the beginning
	 * of a method and the method uses the returned old position
	 * for its duration.
	 * If the number of bytes to read is less than 0 or exceeds the buffer's
	 * size, throw an exception
	 * @param byteCount the number of bytes to ensure currently exist in the buffer
	 * @return the old absolute buffer position/index. Indexes from this returned
	 * value up to (this returned value + byteCount - 1) are ensured to be
	 * valid indexes
	 */
	private final int checkAndRead(int byteCount) {
		if(pos+byteCount > maxPos || byteCount < 0) { throw new IndexOutOfBoundsException(); }
		int oldPos = pos;
		pos += byteCount;
		return oldPos;
	}

	/** Ensure that the specified number of bytes can be added to this buffer
	 * without the buffer overflowing and without an integer overflow
	 * @param byteCount check if this many bytes can safely be added to the buffer
	 */
	private final void ensureNewDataFits(int byteCount) {
		int bufferLength = buffer.length;
		int newSize = pos+byteCount;
		// integer size overflow since adding two positive numbers should be larger than either of the original numbers
		if(pos > newSize) {
			throw new IllegalStateException("buffer integer size overflow");
		}
		if(newSize > bufferLength) {
			buffer = Arrays.copyOf(buffer, (bufferLength << 1)); // Double the size of the buffer
		}
	}


	/** Adjust this buffer's position, max position, and bytes written count
	 * after writing new data to this buffer.
	 * This call should be made after writing data to the buffer and after
	 * calling {@link #ensureNewDataFits(int)}
	 * @param byteCount the number of bytes that were added to this buffer
	 */
	private final void bytesAdded(int byteCount) {
		pos += byteCount;
		if(pos > maxPos) { maxPos = pos; }
		//writeCount += byteCount;
	}

}
