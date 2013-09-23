package binary;

import java.io.IOException;
import java.io.DataOutput;

/** {@link ProtocolOutput} implementation wrapper that converting a {@link DataOutput} stream into a
 * {@link ProtocolOutput} stream.
 * It is recommended to encode strings using the {@link ProtocolHandler#STRING_TYPE} type
 * to encode strings that may contain unicode characters.
 * AN {@link ProtocolTransferable} object could be written to a {@link DataOutput} stream using this example code.
 * <p><blockquote><pre class="brush: java">
 * public void writeExternal(ObjectOutput out) throws IOException {
 * 	ProtocolOutputWriter outputStream = new ProtocolOutputWriter(out);
 * 	try {
 * 		writeData(outputStream);
 * 	}
 * 	finally {
 * 		outputStream.clear();
 * 		outputStream = null;
 * 	}
 * }
 * </pre></blockquote>
 * Binary format:
 * <br/>
 * Current format:
 * <pre>
 * block tag format - (bytes)
 * 	1 byte=[7-8bits=id byte count format][5-6bits=element count byte format],
 * 	1-4 bytes=id,
 * 	1-4 bytes=number of elements
 * element format - (bytes)
 * 	1 byte=[7-8bits=id byte count format][5-6bits=array count byte format],
 * 	1 byte=data type,
 * 	1-4 bytes=id,
 * 	if(data type == array type) {1-4 bytes=array length})
 * 
 * Note: count byte format
 * 	consists of 2 bits representing (00) = 0 bytes, (01) = 1 byte, (10) = 2 bytes, or (11) = 4 bytes,
 * </pre>
 * 
 * future format...
 * <pre>
 * Nested block type
 * 	block ID - (2 bytes) ID of block type, see {@link ProtocolHandler} for some default block types, including, data, name, etc.
 * 	format tag - (1 byte)
 * 		[7-6th] block count storage bits - define number of bytes used to store the block count:
 * 		0 = no blocks, 1 = 1 byte, 2 = 2 bytes, 3 = 4 bytes.
 * 	block count - (variable bytes)
 * 		Big-endian integer equal the number of blocks inside this block, see the format tag byte block count storage bits
 * 		for how many bytes to read.
 * 	N-blocks - (variable bytes) where N = block count
 * 
 * Name block type
 * 	block ID - (2 bytes) ID of block type, see {@link ProtocolHandler} for some default block types, including, data, name, etc.
 * 	tag name - (Java UTF String)
 * 		The tag's name as a Java UTF-16 char type string
 * 
 * Attribute block type
 * 	block ID - (2 bytes) ID of block type, see {@link ProtocolHandler} for some default block types, including, data, name, etc.
 * 	format tag - (1 byte)
 * 		[7-6th] attribute count storage bits - define number of bytes used to store the attribute count:
 * 		0 = no attributes, 1 = 1 byte, 2 = 2 bytes, 3 = 4 bytes.
 * 	attribute count (if attribute storage bits are greater than 1) - (variable bytes)
 * 		Big-endian integer equal the number of attributes in this block, see the format tag byte attribute count storage bits
 * 		for how many bytes to read.
 * 	attributes (if attribute count > 0) - (variable bytes)
 * 		format repeated for each attribute, repeated for 'attribute count' number of attributes defined in attribute count
 * 	attribute format info - (1 byte)
 * 		[8th] name bit - 1 means the attribute contains a name string directly before the data
 * 		[7-6th] array storage bits - define number of bytes storing the attribute's array length, 0 not allowed since the
 * 		attribute's storage bits must be > 0 for this element to exist, 1 = 1 byte, 2 = 2 bytes, 3 = 4 bytes.
 * 		[5-1st] data type bits - defines the attribute type, see {@link ProtocolHandler}, one of the bits defines whether the
 * 		attribute is an array or not.
 * 	attribute array length (if attribute data type is an array) - (variable bytes)
 * 		Big-endian integer equal to the length of the attribute's array, see the attribute format byte array storage bits
 * 		for many many bits to read.
 * 	attribute name (if attribute name bit = 1) - (Java UTF string)
 * 		The attribute's name as a Java UTF-16 char type string
 * 	attribute data - (variable bytes, 1 or more elements depending on data type)
 * 		Defined by the attribute's data type, int = 4 bytes, etc.
 * 
 * Data block type
 * 	block ID - (2 bytes) ID of block type, see {@link ProtocolHandler} for some default block types, including, data, name, etc.
 * 	format tag - (1 byte)
 * 		[8th] name bit - 1 means the data contains a name string directly before the data 
 * 		[7-6th] array storage bits - (if the data type is an array type) define number of bytes storing the data array
 * 		length, 0 = no array only one data element, 1 = 1 byte, 2 = 2 bytes, 3 = 4 bytes.
 * 		[5-1st] data type bits - defines the data type, see {@link ProtocolHandler}, one of the bits defines whether the
 * 		data is an array or not.
 * 	data array length (if data type is an array type) - (variable bytes)
 * 		Big-endian integer equal the length of the data's array, see the data format byte array storage bits
 * 		for how many bytes to read.
 * 	element data - (variable bytes, 1 or more elements depending on data type)
 * 		Defined by the data's data type found in the data format byte earlier, int = 4 bytes, etc.
 * 
 * Tag block type
 * 	block ID - (2 bytes) ID of block type, see {@link ProtocolHandler} for some default block types, including, data, name, etc.
 * 	tag byte - (1 byte)
 * 		[8th] nested bit - 1 means the block is a parent block, 0 means it is a leaf block
 * 		[7th] attribute bit - 1 means the tag block will contain an attribute block
 * 		[6th] data bit - 1 means the tag block will contain a data block
 * 	name block - must always be included
 * 	attribute block (if the attribute bit is set) - contains variable number of attributes
 * 	data block (if the data bit is set) - contains tag data
 * </pre>
 * Data types can be found in {@link ProtocolHandler}, such as {@link ProtocolHandler#BYTE_TYPE}, and {@link ProtocolHandler#ARRAY_TYPE} for arrays.
 * @author TeamworkGuy2
 * @since 2013-7-18
 */
public class ProtocolOutputWriter implements ProtocolOutput {
	// three integers per stack data structure
	private static final int STACK_INCREMENT = 3;
	// 8 stack data structures
	private static int defaultStackSize = 8 * STACK_INCREMENT;
	private static int defaultBufferSize = 512;
	private DataOutput output;
	private ByteBufferArray dataBuf;
	/** Each data structure in this stack contains 3 ints:
	 * 1. the block's ID
	 * 2. the number of elements in the block (1 element is an array, primitive, string, or opening or closing block tag)
	 * 3. the byte buffer position/index in this block's opening tag that is the element count for the block's tag
	 * this element count index will always begin at the first byte of a big endian integer that should be set to
	 * equal the number of elements in this block when the number of elements in the block is known.
	 */
	private int[] openBlockInfoStack;
	/** the absolute position into the block stack that is the first index of the current block's info structure */
	private int openBlockStackIndex = -STACK_INCREMENT;
	private int currentBlockId = -1;
	private int currentElementsWritten;
	private int blocksWritten;


	/** Create a new protocol stream writer, the default implementation of a {@link ProtocolOutput}
	 * @param out - the data output stream to use for writing data
	 */
	public ProtocolOutputWriter(DataOutput out) {
		super();
		this.output = out;
		this.openBlockInfoStack = new int[defaultStackSize];
		this.dataBuf = new ByteBufferArray(defaultBufferSize);
	}


	/** Write the protocol stream's header to this protocol output stream
	 */
	public void writeHeader() {
		try {
			output.writeInt(ProtocolHandler.MAGIC);
			output.writeInt(ProtocolHandler.VERSION);
		} catch (IOException e) {
			System.err.println("Error writing protocol output stream header");
			e.printStackTrace();
		}
	}


	/** Does not close this converter's object output stream, simply sets the reference to the object output stream
	 * to null for garbage collection.
	 */
	@Override
	public void close() throws IOException {
		this.currentBlockId = -1;
		this.currentElementsWritten = 0;
		this.blocksWritten = 0;
		this.openBlockInfoStack = null;
		this.openBlockStackIndex = -STACK_INCREMENT;
		this.dataBuf.close();
		this.dataBuf = null;
		this.output = null;
	}


	@Override
	public void write(int id, byte[] b) throws IOException {
		writeElement(id, ProtocolHandler.BYTE_TYPE | ProtocolHandler.ARRAY_TYPE, b.length);
		dataBuf.write(b, 0, b.length);
	}


	@Override
	public void write(int id, byte[] b, int off, int len) throws IOException {
		writeElement(id, ProtocolHandler.BYTE_TYPE | ProtocolHandler.ARRAY_TYPE, len);
		dataBuf.write(b, off, len);
	}


	@Override
	public void writeBoolean(int id, boolean v) throws IOException {
		writeElement(id, ProtocolHandler.BOOLEAN_TYPE, 0);
		dataBuf.writeBoolean(v);
	}


	@Override
	public void writeByte(int id, byte v) throws IOException {
		writeElement(id, ProtocolHandler.BYTE_TYPE, 0);
		dataBuf.writeByte(v);
	}


	@Override
	public void writeChar(int id, char v) throws IOException {
		writeElement(id, ProtocolHandler.CHAR_TYPE, 0);
		dataBuf.writeChar(v);
	}


	@Override
	public void writeDouble(int id, double v) throws IOException {
		writeElement(id, ProtocolHandler.DOUBLE_TYPE, 0);
		dataBuf.writeDouble(v);
	}


	@Override
	public void writeFloat(int id, float v) throws IOException {
		writeElement(id, ProtocolHandler.FLOAT_TYPE, 0);
		dataBuf.writeFloat(v);
	}


	@Override
	public void writeInt(int id, int v) throws IOException {
		writeElement(id, ProtocolHandler.INT_TYPE, 0);
		dataBuf.writeInt(v);
	}


	@Override
	public void writeLong(int id, long v) throws IOException {
		writeElement(id, ProtocolHandler.LONG_TYPE, 0);
		dataBuf.writeLong(v);
	}


	@Override
	public void writeShort(int id, short v) throws IOException {
		writeElement(id, ProtocolHandler.SHORT_TYPE, 0);
		dataBuf.writeShort(v);
	}


	@Override
	public void writeUTF(int id, String s) throws IOException {
		writeElement(id, ProtocolHandler.STRING_TYPE, 0);
		dataBuf.writeUTF(s);
	}


	@Override
	public void writeOpeningBlock(int id) throws IOException {
		writeBlockTag(id, null);
	}


	@Override
	public void writeOpeningBlock(int id, String name) throws IOException {
		writeBlockTag(id, name);
	}


	/** Write element format data
	 * @param id the element's identifier
	 * @param dataType the data type of the the element, should be from {@link ProtocolHandler}, such as {@link ProtocolHandler.BYTE_TYPE}
	 * can be ORed with {@link ProtocolHandler.ARRAY_TYPE} to indicate that the data is an array of its type.
	 * @param arrayLength only useful if the data type is an array type, specifics the length of the data array to be stored in
	 * this element
	 * @throws IOException if there is an error writing the data to the data output
	 */
	private void writeElement(int id, int dataType, int arrayLength) throws IOException {
		if(this.currentBlockId == -1) {
			throw new IllegalStateException("Cannot write data outside of a block, all elements must be inside blocks, write a block opening tag first");
		}
		if(id < 0) {
			throw new IllegalArgumentException("Protocol element ID must be a positive value");
		}
		boolean isArrayType = ((dataType & ProtocolHandler.ARRAY_TYPE) == ProtocolHandler.ARRAY_TYPE);
		if((isArrayType && arrayLength < 0)) {
			throw new IllegalArgumentException("Protocol element array length must be a positive value");
		}
		if((dataType | ProtocolHandler.ANY_TYPE) != ProtocolHandler.ANY_TYPE) {
			throw new IllegalArgumentException("Protocol element data type out of range (" + dataType + ")");
		}
		ByteBufferArray write = dataBuf;

		// Write the block's format data
		byte idBytes = sizeByteCount(id);
		// Make sure it is an array type before writing any array length bytes
		byte arrayBytes = (isArrayType == true) ? sizeByteCount(arrayLength) : 0;
		byte elementTag = createBlockTag(idBytes, arrayBytes, false);
		write.writeByte(elementTag);
		write.writeByte(dataType);
		writeVariableInt(write, id, idBytes);
		if(isArrayType) {
			writeVariableInt(write, arrayLength, arrayBytes);
		}
		// Count each element written
		currentElementsWritten++;

		// Allow another method (normally this method's calling method) to then write the element's data
		// A binary element in this format has no closing tag, so we do not need to worry about writing that
	}


	/** Write a data block tag
	 * @param id the identifier of the block
	 * @param descriptor an optional descriptor to associate with the block tag to write
	 * @throws IOException if there is an error writing the data to the data output stream
	 */
	private void writeBlockTag(int id, String descriptor) throws IOException {
		if(id < 0) {
			throw new IllegalArgumentException("Protocol block ID must be a positive value");
		}
		if(openBlockStackIndex % STACK_INCREMENT != 0) {
			throw new InternalError("Stack index out of alignment, please report this issue");
		}
		ByteBufferArray write = dataBuf;
		int[] blockStack = openBlockInfoStack;

		// Save the current stack block data if one or more blocks are already open
		if(openBlockStackIndex >= 0) {
			// openBlockInfoStack[openBlockStackIndex+1] = currentBlockId; // the current block ID has not changed
			blockStack[openBlockStackIndex+1] = currentElementsWritten;
			// openBlockInfoStack[openBlockStackIndex+2] = #; // the current block's element count index is already known
		}

		// Add the new block info to the block info stack
		// Move to the new block stack index (this index starts at negative STACK_INCREMENT) so that
		// the first time this method is called the index is set to zero
		openBlockStackIndex += STACK_INCREMENT;
		// Check whether the next block info structure will fit on the stack given the stack's current index
		if(openBlockStackIndex+STACK_INCREMENT >= blockStack.length) {
			// 1.5x + 4 the open block stack size
			openBlockInfoStack = new int[blockStack.length + (blockStack.length >>> 1) + 4];
			System.arraycopy(blockStack, 0, openBlockInfoStack, 0, openBlockStackIndex);
			blockStack = openBlockInfoStack;
		}

		// Write the block's format data
		byte idBytes = sizeByteCount(id);
		byte dataType = (byte)((descriptor != null) ? ProtocolHandler.STRING_TYPE : ProtocolHandler.NO_TYPE); // the block name is a string
		byte elementCountBytes = 3; // assume the length is maximum sizeByteCount(length);
		byte blockTag = createBlockTag(idBytes, elementCountBytes, true);
		write.writeByte(blockTag);
		write.writeByte(dataType);
		writeVariableInt(write, id, idBytes);
		writeVariableInt(write, 0, elementCountBytes); // Write nothing, fill in the actual length later
		currentBlockId = id;
		currentElementsWritten = 0;

		// Write the block name
		if(descriptor != null) {
			write.writeUTF(descriptor);
		}

		// Add the new stack info data to the stack, some of the values will
		// be filled in later when the block closing tag is written
		blockStack[openBlockStackIndex+0] = id;
		blockStack[openBlockStackIndex+1] = 0; // Element count eventually needs to be filled in
		// The last item written to the buffer was the length int which we need to overwrite later,
		// so the current position - 4 is the beginning of this length int
		blockStack[openBlockStackIndex+2] = write.position()-4;
		// Opening block tags count as elements
		currentElementsWritten++;

		// Debug: System.out.println("Write opening tag: " + toString() + ", write=" + currentElementsWritten + ", index=" + openBlockStackIndex);

		// Allow another method (normally the method's calling this method) to then write the block's data
		// A binary tag in this format has not closing tag, so we do not need to worry about writing that
	}


	@Override
	public void writeClosingBlock() throws IOException {
		if(openBlockStackIndex < 0) {
			throw new IllegalStateException("Cannot close a block because no blocks are currently open");
		}
		blocksWritten++;
		ByteBufferArray buffer = dataBuf;
		int[] blockStack = openBlockInfoStack;
		// Set the stack's elements written count for this block tag
		blockStack[openBlockStackIndex+1] = currentElementsWritten;
		// Note: this data buffer position and write operation relies on
		// the exact format of the protocol block tag written by {@link #writeBlockTag(int)}
		buffer.position(blockStack[openBlockStackIndex+2]);
		// Overwrite the previously empty element count integer for this block tag with the
		// now calculated number of elements in the block
		// elements in this block may include: arrays, ints, booleans, etc. each of which count as one element
		// this is why the class requires a repositionable/rewindable buffer
		// so that the number of elements in each block does not need to be known ahead of time
		buffer.writeInt(currentElementsWritten);
		// Reposition to the end of the buffer
		buffer.position(buffer.size());

		// Debug: System.out.println("Write closing tag: " + toString() + ", write=" + currentElementsWritten + ", index=" + openBlockStackIndex);

		// Write the entire data buffer to the underlying output stream if there are no more open blocks
		// this can occur multiple times, whenever a block is closed and there are no other open blocks
		if(openBlockStackIndex == 0) {
			buffer.position(0);
			buffer.writeTo(output);
			buffer.clear();
		}

		// 'pop' the current block data off the stack by moving the stack pointer down
		// to the parent block and get the parent block's Id and number of currently written elements
		int newIndex = openBlockStackIndex-STACK_INCREMENT;
		if(newIndex > -1) {
			currentBlockId = blockStack[newIndex+0];
			// Parent blocks contain an element count equal to their own element count + child element counts
			currentElementsWritten = blockStack[newIndex+1] + currentElementsWritten;
		}
		// If the new parent stack block 
		else {
			currentBlockId = -1;
			currentElementsWritten = 0;
		}
		openBlockStackIndex -= STACK_INCREMENT;
	}


	@Override
	public int getBlocksWritten() {
		return this.blocksWritten;
	}


	/** Creates a value containing information about a block tag
	 * @param idCount the number of bytes used to store the block's ID, this is expected to be 2 bits long
	 * @param lengthCount the number of bytes used to store the block's length, this is expected to be 2 bits long
	 * @param isBlockTag true indicates that the tag being created is a block tag, false indicates that it
	 * an element tag
	 * @return the byte packed with the input parameters
	 */
	private static final byte createBlockTag(byte idCount, byte lengthCount, boolean isBlockTag) {
		byte blockTagByte = (byte)(((isBlockTag==true ? 1 : 0) << 7) | (idCount << 2) | (lengthCount));
		return blockTagByte;
	}


	/** Write the number of bytes specified by the byte count
	 * @param write the output to write the data to
	 * @param value the value to write
	 * @param byteCount 1 means write 1 byte of the value, 2 means write 2 bytes of the value,
	 * 3 means write 4 bytes of the value
	 * @throws IOException if there is an error writing the data to the output
	 */
	private static final void writeVariableInt(DataOutput write, int value, byte byteCount) throws IOException {
		if(byteCount == 1) { write.writeByte((byte)value); }
		else if(byteCount == 2) { write.writeShort((short)value); }
		else if(byteCount == 3) { write.writeInt((int)value); }
		// Else if the byte count is zero or negative or larger than 3, write nothing
	}


	/** Calculate the number of bytes that the specified value will fit in using 2 bits specifically
	 * for data converters that work with a custom binary data format.<br/>
	 * If the value fits in 1 byte, 0x01 is returned.<br/>
	 * If the value fits in 2 bytes, 0x02 is returned.<br/>
	 * If the value fits in 4 bytes, 0x04 is returned.<br/>
	 * Larger values cannot occur since the input is an integer (4 bytes)
	 * @param valueCount the value to convert to a byte storage size
	 * @return the number of bytes needed to store the <code>valueCount</code> value in.
	 * 1 means the value will fit in 1 byte, 2 means the value will fit in 2 bytes, etc.
	 */
	private static byte sizeByteCount(int valueCount) {
		// Get the number of bits currently holding the value
		byte value = (byte)(32 - Integer.numberOfLeadingZeros(valueCount));
		// Save the 3 overflow bits before dividing the bit count by 8
		byte overflow = (byte)(value & 0x07);
		// If the attribute count fits in 1 byte, then = 1, if it fits in 2 bytes, then = 2, if it fits in 3 or 4 bytes, then = 3
		// Divide the bit count by 8 to get the byte count, add 1 if any of the overflow bits are 1, example 15 bits/8 = 1+1 = 2 bytes
		value = (byte)((value >>> 3) + (((overflow & 0x04) >>> 2) | ((overflow & 0x02) >>> 1) | (overflow & 0x01)));
		value = (value == 4) ? 3 : value;
		return value;
	}


	@SuppressWarnings("unused")
	private static void writeDataType(DataOutput writer, Object value) throws IOException {
		if(value instanceof Byte) {
			writer.writeByte((Byte)value);
		}
		else if(value instanceof Short) {
			writer.writeShort((Short)value);
		}
		else if(value instanceof Integer) {
			writer.writeInt((Integer)value);
		}
		else if(value instanceof Long) {
			writer.writeLong((Long)value);
		}
		else if(value instanceof Float) {
			writer.writeFloat((Float)value);
		}
		else if(value instanceof Double) {
			writer.writeDouble((Double)value);
		}
		else if(value instanceof Boolean) {
			writer.writeBoolean((Boolean)value);
		}
		else if(value instanceof Character) {
			writer.writeChar((Character)value);
		}
		else if(value instanceof String) {
			writer.writeUTF((String)value);
		}
		else if(value instanceof Short) {
			writer.writeShort((Short)value);
		}
	}


	@Override
	public String toString() {
		return "ProtocolOutputWriter" + "(id=" + currentBlockId + ", elements=" + currentElementsWritten + ")";
	}


	/** See {@link ProtocolOutputWriter#writeHeader()}
	 * @param output the protocol output stream to write a header to
	 */
	public static final void writeHeader(ProtocolOutputWriter output) {
		output.writeHeader();
	}

}
