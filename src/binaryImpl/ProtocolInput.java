package binaryImpl;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;

/** Protocol data input stream interface that mirrors the methods of a {@link DataInput} stream.
 * This interface allows opening and closing elements to be read as well as strings and basic data types.
 * @author TeamworkGuy2
 * @since 2013-7-18
 */
public interface ProtocolInput extends Closeable {

	/** Read the specified number of bytes from the next element
	 * @param id the id of the element to read
	 * @param b the byte array to load the bytes read from this input stream into.
	 * The length of the byte array determines how many bytes are read.
	 * Bytes are put into the array starting at index 0.
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public void read(int id, byte[] b) throws IOException;

	/** Read the specified number of bytes from the next element
	 * @param id the id of the element to read
	 * @param b the byte array to load the bytes read from this input stream into
	 * @param off the offset into the byte array to write the bytes read from this input stream
	 * @param len the number of bytes to read from this input stream and write into the byte array
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public void read(int id, byte[] b, int off, int len) throws IOException;

	/** Parse a boolean value from the next element
	 * @param id the id of the element to read
	 * @return the boolean read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public boolean readBoolean(int id) throws IOException;

	/** Read a byte from the next element
	 * @param id the id of the element to read
	 * @return the byte read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public byte readByte(int id) throws IOException;

	/** Read a character from the next element
	 * @param id the id of the element to read
	 * @return the character read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public char readChar(int id) throws IOException;

	/** Parse a double from the next element
	 * @param id the id of the element to read
	 * @return the double read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public double readDouble(int id) throws IOException;

	/** Parse a float from the next element
	 * @param id the id of the element to read
	 * @return the float read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public float readFloat(int id) throws IOException;

	/** Parse an integer from the next element
	 * @param id the id of the element to read
	 * @return the integer read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public int readInt(int id) throws IOException;

	/** Parse a long from the next element
	 * @param id the id of the element to read
	 * @return the long read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public long readLong(int id) throws IOException;

	/** Parse a short from the next element
	 * @param id the id of the element to read
	 * @return the short read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public short readShort(int id) throws IOException;

	/** Read a String from this input stream
	 * @param id the id of the element to read
	 * @return the String read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public String readUTF(int id) throws IOException;

	/** Read an opening block tag and add a corresponding block tag to this
	 * reader's internal list of open block tags
	 * @param id the block's identifier
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public void readOpeningBlock(int id) throws IOException;

	/** Read the next opening block tag, no matter what it's ID is and add a
	 * corresponding block tag to this reader's internal list of open block tags
	 * @return the identifier of the next opening tag read from the stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public ProtocolHeader readOpeningBlock() throws IOException;

	/** Read a closing block tag for the last read opening block tag, this completes a block
	 * @throws IOException if there is an IO error reading from the input stream
	 */
	public void readClosingBlock() throws IOException;

	/** Get the current header tag data block being read
	 * @return the header tag of the data block currently being read
	 */
	public ProtocolHeader getCurrentHeaderBlock();

	/** Peek at the next opening protocol data tag
	 * @return the next opening protocol data tag
	 */
	public ProtocolHeader peekNextHeaderBlock() throws IOException;

	/** Get the size of the current block in bytes
	 * @return the byte size of the current block
	 */
	public int getBlockLength();

	/** Get the number of blocks read by this reader.
	 * This does not including the next opening block header read by {@link #peekNextHeaderBlock()}.
	 * @return the number of blocks (opening + closing tags / 2) read by this reader
	 */
	public int getBlocksRead();

}