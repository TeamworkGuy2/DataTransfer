package binary;

import java.io.IOException;
import java.io.DataInput;
import java.util.List;

import dataTransfer.DataHeader;

/** {@link ProtocolInput} implementation wrapper that converts a {@link DataInput} stream
 * into a {@link ProtocolInput} stream.
 * For example a {@link DataInput} stream could be converted to an {@link ProtocolTransferable} object via this code.
 * <p><blockquote><pre class="brush: java">
 * public void readExternal(ObjectInput in) throws IOException {
 * 	ProtocolInputReader inputStream = new ProtocolInputReader(in);
 * 	try {
 * 		readData(inputStream);
 * 	}
 * 	finally {
 * 		inputStream.clear();
 * 		inputStream = null;
 * 	}
 * }
 * </pre></blockquote>
 * Binary format:
 * <br/>
 * Current format:
 * <pre>
 * block tag format - (bytes)
 * 	1 byte=[AAxxxxxx bits=id byte count format][xxAAxxxx bits=element count byte format],
 * 	1-4 bytes=id,
 * 	1-4 bytes=number of elements
 * element format - (bytes)
 * 	1 byte=[AAxxxxxx bits=id byte count format][xxAAxxxx bits=array count byte format],
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
 * Data types can be found in {@link ProtocolHandler}, such as {@link ProtocolHandler#BYTE_TYPE},
 * and {@link ProtocolHandler#ARRAY_TYPE} for arrays.
 * TODO add support for {@link #peekNextBlock()} to work properly with {@link #readClosingBlock()}
 * @author TeamworkGuy2
 * @since 2013-7-18
 * @see ProtocolInput
 */
public class ProtocolInputReader implements ProtocolInput {
	// three integers per stack data structure
	private static final int STACK_INCREMENT = 3;
	private static int defaultStackSize = 8 * STACK_INCREMENT;
	private DataInput in;
	private int blocksRead;
	/** Each data structure in this stack contains 3 ints:
	 * 1. the block's ID
	 * 2. the number of elements in the block (1 element is an array, primitive, string, or opening or closing block tag)
	 * 3. the number of elements currently read from the block
	 */
	private int[] openBlockInfoStack;
	/** the absolute position into the block stack that is the first index of the current block's info structure */
	private int currentStackIndex = -STACK_INCREMENT;
	private int currentBlockId = -1;
	private int currentElementsRead;
	private int currentBlockElements;
	private ProtocolHeader currentHeader;

	private int peekStackIndex = -STACK_INCREMENT;
	@SuppressWarnings("unused")
	private int peekBlockId = -1;
	@SuppressWarnings("unused")
	private int peekElementsRead;
	@SuppressWarnings("unused")
	private int peekBlockElements;
	private ProtocolHeader peekHeader;


	/** Create a data converter to read binary data
	 * It is recommended to encode strings using the {@link ProtocolHandler#STRING_TYPE} type
	 * to encode strings that may contain unicode characters.
	 * @param in the input stream containing the binary data written by a {@link ProtocolOutputWriter}
	 */
	public ProtocolInputReader(DataInput in) {
		super();
		if(in == null) { throw new IllegalArgumentException("The DataInput stream cannot be null"); }
		this.in = in;
		this.openBlockInfoStack = new int[defaultStackSize];
	}


	/** Read protocol stream's header data from this protocol input stream
	 */
	public void readHeader() {
		int magic = 0;
		@SuppressWarnings("unused")
		int version = 0;
		try {
			magic = in.readInt();
			version = in.readInt();
		} catch (IOException e) {
			System.err.println("Error reading protocol input stream header");
			e.printStackTrace();
		}
		if(magic != ProtocolHandler.MAGIC) {
			throw new IllegalStateException("Protocol stream header format does not match expected format");
		}
	}


	/** Does not close this converter's object input stream, simply sets the reference to the object input stream
	 * to null for garbage collection.
	 */
	@Override
	public void close() throws IOException {
		this.currentStackIndex = -STACK_INCREMENT;
		this.currentBlockId = -1;
		this.currentElementsRead = 0;
		this.currentBlockElements = 0;
		this.currentHeader = null;
		this.peekStackIndex = -STACK_INCREMENT;
		this.peekBlockId = -1;
		this.peekElementsRead = 0;
		this.peekBlockElements = 0;
		this.peekHeader = null;
		this.blocksRead = 0;
		this.openBlockInfoStack = null;
		this.in = null;
	}


	@Override
	public void read(int id, byte[] b) throws IOException {
		int tag = readElement(id, ProtocolHandler.BYTE_TYPE | ProtocolHandler.ARRAY_TYPE);
		in.readFully(b, 0, b.length);
		if(id != tag) { throwTagMissmatchException(id, tag); }
	}


	@Override
	public void read(int id, byte[] b, int off, int len) throws IOException {
		int tag = readElement(id, ProtocolHandler.BYTE_TYPE | ProtocolHandler.ARRAY_TYPE);
		in.readFully(b, off, len);
		if(id != tag) { throwTagMissmatchException(id, tag); }
	}


	@Override
	public boolean readBoolean(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.BOOLEAN_TYPE);
		boolean value = in.readBoolean();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public byte readByte(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.BYTE_TYPE);
		byte value = in.readByte();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public char readChar(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.CHAR_TYPE);
		char value = in.readChar();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public double readDouble(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.DOUBLE_TYPE);
		double value = in.readDouble();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public float readFloat(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.FLOAT_TYPE);
		float value = in.readFloat();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public int readInt(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.INT_TYPE);
		int value = in.readInt();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public long readLong(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.LONG_TYPE);
		long value = in.readLong();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public short readShort(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.SHORT_TYPE);
		short value = in.readShort();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public String readUTF(int id) throws IOException {
		int tag = readElement(id, ProtocolHandler.STRING_TYPE);
		String value = in.readUTF();
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return value;
	}


	@Override
	public ProtocolHeader readNextBlock() throws IOException {
		return readHeader(-1, false);
	}


	@Override
	public ProtocolHeader readOpeningBlock(int id) throws IOException {
		return readHeader(id, true);
	}


	/** Read the next block header
	 * @param id the ID of the next block header to read
	 * @param checkMatching true to check if this ID matches the ID of the
	 * header read
	 * @return the header read from the current input stream
	 * @throws IOException if there is an error reading from the input stream
	 */
	private ProtocolHeader readHeader(int id, boolean checkMatching) throws IOException {
		int tag = -1;
		// If the peek header has not been read, simply read the next header
		if(peekHeader == null) {
			this.currentHeader = readBlockHeader(in, this, openBlockInfoStack, currentStackIndex, currentBlockId, currentElementsRead, currentBlockElements);
			this.currentStackIndex += STACK_INCREMENT;
			this.currentBlockId = openBlockInfoStack[currentStackIndex+0];
			this.currentBlockElements = openBlockInfoStack[currentStackIndex+1];
			this.currentElementsRead = openBlockInfoStack[currentStackIndex+2];
			tag = this.currentHeader.getHeaderId();
		}
		// If the peek header has been read, use it as the next header and set the peek header to null
		else {
			this.currentHeader = peekHeader;
			this.currentStackIndex += STACK_INCREMENT;
			this.currentBlockId = openBlockInfoStack[currentStackIndex+0];
			this.currentBlockElements = openBlockInfoStack[currentStackIndex+1];
			this.currentElementsRead = openBlockInfoStack[currentStackIndex+2];
			peekHeader = null;
			peekStackIndex = -STACK_INCREMENT;
			tag = this.currentHeader.getHeaderId();
		}
		if(id != tag) { throwTagMissmatchException(id, tag); }
		return currentHeader;
	}


	/** Way to read the next header independant of its type,
	 * replaced by {@link #readNextBlock()}
	 * @return the next header read from the input stream
	 * @throws IOException if there is an error reading from the input stream
	 */
	@Override
	public ProtocolHeader peekNextBlock() throws IOException {
		// If the peek header has not been read, read it and set the peek header
		// variables equal to the new header's values
		if(peekHeader == null) {
			this.peekHeader = readBlockHeader(in, this, openBlockInfoStack, currentStackIndex, currentBlockId, currentElementsRead, currentBlockElements);
			this.peekStackIndex = currentStackIndex + STACK_INCREMENT;
			this.peekBlockId = openBlockInfoStack[peekStackIndex+0];
			this.peekBlockElements = openBlockInfoStack[peekStackIndex+1];
			this.peekElementsRead = openBlockInfoStack[peekStackIndex+2];
		}
		return peekHeader;
	}


	/** Read an element's format data
	 * @param id the ID of the element to read
	 * @param dataType the data type of the the element, should be from {@link ProtocolHandler}, such as {@link ProtocolHandler.BYTE_TYPE}
	 * can be ORed with {@link ProtocolHandler.ARRAY_TYPE} to indicate that the data is an array of its type.
	 * @return the ID of the element read
	 * @throws IOException if there is an error reading the input stream
	 */
	private int readElement(int id, int dataType) throws IOException {
		if(this.currentBlockId == -1) {
			throw new IllegalStateException("Cannot read element outside of block, all elements must be inside blocks");
		}
		if(id < 0) {
			throw new IllegalArgumentException("Protocol element ID must be a positive value");
		}
		if((dataType | ProtocolHandler.ANY_TYPE) != ProtocolHandler.ANY_TYPE) {
			throw new IllegalArgumentException("Protocol element data type out of range (" + dataType + ")");
		}
		DataInput reader = in;

		// Read the element's format data
		byte elementTag = reader.readByte();
		byte dataTypeRead = reader.readByte();
		byte idBytes = readIdBytes(elementTag);
		byte lengthBytes = readLengthBytes(elementTag);
		int elementId = readIntFromBytes(reader, idBytes);
		if(dataTypeRead != dataType) {
			throw new IOException("Data type read does not match expected data type (" + dataTypeRead + ", " + dataType + ")");
		}
		// Read array lengths
		@SuppressWarnings("unused")
		int arrayLength = 0;
		if((dataTypeRead & ProtocolHandler.ARRAY_TYPE) == ProtocolHandler.ARRAY_TYPE) {
			arrayLength = readIntFromBytes(reader, lengthBytes);
		}
		this.currentElementsRead++;
		return elementId;
	}


	/** Read a protocol block header
	 * @return the protocol block header read
	 * @throws IOException if there is an error reading the input stream
	 */
	private static ProtocolHeader readBlockHeader(DataInput reader, ProtocolInputReader instance, int[] blockInfoStack, int stackIndex, int blockId, int elementsRead, int elements) throws IOException {
		if(stackIndex % STACK_INCREMENT != 0) {
			throw new InternalError("Stack index out of alignment, please report this issue");
		}

		// Save the current stack block data if one or more blocks are already open
		if(stackIndex >= 0) {
			// openBlockInfoStack[openBlockStackIndex+0] = currentBlockId; // the current block ID has not changed
			// openBlockInfoStack[openBlockStackIndex+1] = #; // the total number of elements in the current block never changes
			blockInfoStack[stackIndex+2] = elementsRead; // save the number of elements read from the current block
		}

		// Add the new block info to the block info stack
		// Move to the new block stack index (this index starts at negative STACK_INCREMENT) so that
		// the first time this method is called the index is set to zero
		stackIndex += STACK_INCREMENT;
		// Check whether the next block info structure will fit on the stack given the stack's current index
		if(stackIndex+STACK_INCREMENT >= blockInfoStack.length) {
			blockInfoStack = instance.expandStack(stackIndex);
		}

		// Read the block format data
		byte blockTag = reader.readByte();
		byte dataTypeRead = reader.readByte();
		byte idBytes = readIdBytes(blockTag);
		byte lengthBytes = readLengthBytes(blockTag);
		blockId = readIntFromBytes(reader, idBytes);
		if(blockId < 0) {
			throw new IllegalStateException("Read out of range block ID, cannot read block (" + blockId + ")");
		}
		elements = readIntFromBytes(reader, lengthBytes);
		elementsRead = 1; // 1 because this opening tag counts as an element

		// Read the block name
		String name = null;
		if(dataTypeRead != ProtocolHandler.NO_TYPE) {
			name = reader.readUTF();
		}

		// Add the new block info to the stack
		blockInfoStack[stackIndex+0] = blockId;
		blockInfoStack[stackIndex+1] = elements;
		blockInfoStack[stackIndex+2] = elementsRead;

		// Debug: System.out.println("Read opening tag: " + toString() + ", read=" + currentElementsRead + ", index=" + openBlockStackIndex);

		// Allow another method (normally this method's calling method) to then read the block's data
		// A binary tag in this format has no closing tag, so we do not need to worry about reading that

		ProtocolHeader newHeader = new ProtocolHeaderImpl(blockId, name, DataHeader.OPENING);
		return newHeader;
	}


	@Override
	public void readClosingBlock() throws IOException {
		if(currentStackIndex < 0) {
			 throw new IllegalStateException("Cannot close a block because no blocks are currently open");
		}
		if(this.currentBlockElements != this.currentElementsRead) {
			throw new IOException("Number of elements read does not match block size (" +
					this.currentElementsRead + " of " + this.currentBlockElements +")");
		}
		this.blocksRead++;

		// Debug: System.out.println("Read closing tag: " + toString() + ", read=" + currentElementsRead + ", index=" + openBlockStackIndex);

		// 'pop' the current block data off the stack by moving the stack pointer down
		// to the parent block and get the parent block's Id and number of currently read elements
		openBlockInfoStack[currentStackIndex+1] = currentElementsRead;
		int newIndex = currentStackIndex-STACK_INCREMENT;
		if(newIndex > -1) {
			currentBlockId = openBlockInfoStack[newIndex+0];
			currentBlockElements = openBlockInfoStack[newIndex+1];
			// Parent blocks contain an element count equal to their own element count + child element counts
			currentElementsRead = openBlockInfoStack[newIndex+2] + currentElementsRead;
		}
		else {
			currentBlockId = -1;
			currentBlockElements = 0;
			currentElementsRead = 0;
		}
		currentStackIndex -= STACK_INCREMENT;
	}


	/** Expand the current block info stack and return it
	 * @param copyToIndex the index up to which to copy from the old stack into
	 * the new stack
	 * @return the new stack
	 */
	private int[] expandStack(int copyToIndex) {
		int[] oldAry = openBlockInfoStack;
		openBlockInfoStack = new int[oldAry.length + (oldAry.length >>> 1) + 4]; // 1.5x + 4 the open block stack size
		System.arraycopy(oldAry, 0, openBlockInfoStack, 0, copyToIndex);
		return openBlockInfoStack;
	}


	@Override
	public ProtocolHeader getCurrentBlockHeader() {
		return currentHeader;
	}


	@Override
	public int getBlockLength() {
		return this.currentBlockElements;
	}

	/** Get the number of blocks (opening + closing tags / 2) read by this stream reader
	 * @return the number of blocks read by this stream reader
	 */
	@Override
	public int getBlocksRead() {
		return this.blocksRead;
	}


	/** Read a specified data type from the specified DataInput stream
	 * @param reader the DataInput stream to read the data type from
	 * @param dataType the data type as defined in ProtocolHandler, such as ProtocolHandler.BYTE_TYPE
	 * @return the data value read from the DataInput stream
	 * @throws IOException if there is an error reading the DataInput stream
	 */
	@SuppressWarnings("unused")
	private final Object readDataType(DataInput reader, int dataType) throws IOException {
		// Read the data type's value
		switch(dataType) {
		case (byte)ProtocolHandler.BYTE_TYPE:
			return reader.readByte();
		case (byte)ProtocolHandler.SHORT_TYPE:
			return reader.readShort();
		case (byte)ProtocolHandler.INT_TYPE:
			return reader.readInt();
		case (byte)ProtocolHandler.LONG_TYPE:
			return reader.readLong();
		case (byte)ProtocolHandler.FLOAT_TYPE:
			return reader.readFloat();
		case (byte)ProtocolHandler.DOUBLE_TYPE:
			return reader.readDouble();
		case (byte)ProtocolHandler.BOOLEAN_TYPE:
			return reader.readBoolean();
		case (byte)ProtocolHandler.CHAR_TYPE:
			return reader.readChar();
		case (byte)ProtocolHandler.STRING_TYPE:
			return reader.readUTF();
		default:
			throw new IllegalStateException("Cannot read binary value with no data type");
		}
	}


	/** Read an array of the specified data type from the specified DataInput stream
	 * @param reader the DataInput stream to read the data type from
	 * @param dataType the data type as defined in ProtocolHandler, such as ProtocolHandler.BYTE_TYPE
	 * @param arrayLength the number of data values to read
	 * @param values the list of objects to add the data values to
	 * @throws IOException if there is an error reading the DataInput stream
	 */
	@SuppressWarnings("unused")
	private final void readDataTypeArray(DataInput reader, int dataType, int arrayLength, List<Object> values) throws IOException {
		// Read the data type's value
		switch(dataType) {
		case (byte)ProtocolHandler.BYTE_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readByte());
			}
		break;
		case (byte)ProtocolHandler.SHORT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readShort());
			}
		break;
		case (byte)ProtocolHandler.INT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readInt());
			}
		break;
		case (byte)ProtocolHandler.LONG_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readLong());
			}
		break;
		case (byte)ProtocolHandler.FLOAT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readFloat());
			}
		break;
		case (byte)ProtocolHandler.DOUBLE_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readDouble());
			}
		break;
		case (byte)ProtocolHandler.BOOLEAN_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readBoolean());
			}
		break;
		case (byte)ProtocolHandler.CHAR_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readChar());
			}
		break;
		case (byte)ProtocolHandler.STRING_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				values.add(reader.readUTF());
			}
		break;
		default:
			throw new IllegalStateException("Cannot read binary value with no data type");
		}
	}


	/** Check whether the specified tag is a block tag or an element tag
	 * @param tag the tag byte packed with input paraemters
	 * @return true if the tag byte is a block tag byte, false if the tag byte is an element tag byte
	 */
	@SuppressWarnings("unused")
	private static final boolean isBlockTag(byte tag) {
		boolean isBlock = (tag & 0x80) == 0x80;
		return isBlock;
	}


	/** Get the number of bytes storing the ID from the specified tag byte
	 * @param tag the byte packed with the input parameters
	 * @return the number of bytes used to store the ID, this is expected to be 2 bits long
	 */
	private static final byte readIdBytes(byte tag) {
		byte idCount = (byte)((tag >>> 2) & 0x03);
		return idCount;
	}


	/** Get the number of bytes storing the length from the specified tag byte
	 * @param tag the byte packed with the input parameters
	 * @return the number of bytes used to store the length, this is expected to be 2 bits long
	 */
	private static final byte readLengthBytes(byte tag) {
		byte lengthCount = (byte)(tag & 0x03);
		return lengthCount;
	}


	/** Read up to 4 bytes from the specified data input stream and convert them into an integer.
	 * The integer is read from left to right with the first byte being the most significant and the last byte being the
	 * least significant. Note that 3 is converted to 4 and 4 bytes are read.
	 * @param reader the data input stream to read the bytes from
	 * @param bytes the number of bytes to read, cannot be greater than 3, 0 = nothing, 1 = read 1 byte,
	 * 2 = read 2 bytes, 3 = read 4 bytes.
	 * @return the integer read made from the specified number of bytes.
	 * @throws IOException if there is an error reading from the data input stream
	 * @throws IllegalArgumentException if the <code>bytes</code> value is greater than 3 or less than 0
	 */
	private static final int readIntFromBytes(DataInput reader, int bytes) throws IOException {
		if(bytes > 3 || bytes < 0) { throw new IllegalArgumentException("Cannot convert: " + bytes +
				" bytes to an integer, must be less than 4 and greater than -1"); }
		int value = 0;
		if(bytes == 1) { value = reader.readByte() & 0xFF; }
		else if(bytes == 2) { value = reader.readShort() & 0xFFFF; }
		else if(bytes == 3) { value = reader.readInt() & 0xFFFFFFFF; }
		return value;
	}


	private final void throwTagMissmatchException(int originalId, int newId) {
		throw new IllegalArgumentException("Element ID " + originalId + " does not match ID read " + newId);
	}


	@Override
	public String toString() {
		return "ProtocolInputReader" + "(id=" + currentBlockId + ", elements=" + currentBlockElements + ")";
	}


	/** See {@link ProtocolInputReader#readHeader()}.
	 * @param input the protocol input stream to read a header from
	 */
	public static final void readHeader(ProtocolInputReader input) {
		input.readHeader();
	}

}
