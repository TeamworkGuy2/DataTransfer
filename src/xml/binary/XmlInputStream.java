package xml.binary;

import java.io.Closeable;
import java.io.IOException;
import java.io.DataInput;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import dataTransfer.DataHeader;

import xml.XmlAttributes;
import xml.XmlHandler;
import xml.XmlInput;
import xml.XmlTag;
import xml.XmlTagImpl;

/** {@link XmlInput} implementation wrapper that converts a {@link DataInput} stream into an {@link XmlInput}
 * stream.
 * For example a {@link DataInput} stream could be converted to an {@link xml.Xmlable Xmlable} object via this code.
 *
 * <br/>
 * Current binary format:
 * <pre>
 * tag (required)
 * 	u1 format
 * 		byte 1:
 * 		bit_8 contains_nested - 1 indicates this tag contains nested tags, 0 indicates a leaf tag
 * 		bit_7-6 attribute_array_count_bytes - number of bytes that attribute_count is stored in
 * 		bit_5-1 null
 * 	u0/u1/u2/u4 attribute_count - if tag.attribute_array_count_bytes is 1 then
 * 		read 1 byte, else if it is 2 read 1 short, else if it is 3 read
 * 		1 integer, else if it is 0 do not read any data
 * 
 * attribute[tag.attribute_count] (required) - array of tag.attribute_count
 * 	number of attributes. An attribute is a name-data pair
 * 	u1 format
 * 		byte 1:
 * 		bit_8 null
 * 		bit_7-6 data_array_count_bytes - number of bytes that data_array_size is stored in
 * 		bit_5-1 data_type - the data type of the items in the array
 * 	u0/u1/u2/u4 data_array_size - if attribute.data_array_count_bytes is 1 then read 1 byte,
 * 		else if it is 2 read 1 short, else if it is 3 read 1 integer,
 * 		else if it is 0 do not read any data
 * 	UTF_String attribute_name - the name of the element
 * 	data_type[attribute.data_array_size] attribute_data - attribute.data_array_size number of data
 * 		items of type attribute.data_type
 * 
 * data (required)
 * 	u1 format
 * 		byte 1:
 * 		bit_8 null
 * 		bit_7-6 data_array_count_bytes - number of bytes that data_array_size is stored in
 * 		bit_5-1 data_type - the data type of the items in the array
 * 	u0/u1/u2/u4 data_array_size - if data.data_array_count_bytes is 1 then read 1 byte,
 * 		else if it is 2 read 1 short, else if it is 3 read 1 integer,
 * 		else if it is 0 do not read any data
 * 	UTF_String tag_name - the name of the element
 * 	data_type[data.data_array_size] data - data.data_array_size number of data
 * 		items of type data.data_type
 * </pre>
 * 
 * 
 * future format (not yet implemented)...
 * <pre>
 * Nested block type
 * 	block ID - (2 bytes) ID of block type, see {@link XmlHandler} for some default block types, including, data, name, etc.
 * 	format tag - (1 byte)
 * 		[7-6th] block count storage bits - define number of bytes used to store the block count:
 * 		0 = no blocks, 1 = 1 byte, 2 = 2 bytes, 3 = 4 bytes.
 * 	block count - (variable bytes)
 * 		Big-endian integer equal the number of blocks inside this block, see the format tag byte block count storage bits
 * 		for how many bytes to read.
 * 	N-blocks - (variable bytes) where N = block count
 * 
 * Name block type
 * 	block ID - (2 bytes) ID of block type, see {@link XmlHandler} for some default block types, including, data, name, etc.
 * 	tag name - (Java UTF String)
 * 		The tag's name as a Java UTF-16 char type string
 * 
 * Attribute block type
 * 	block ID - (2 bytes) ID of block type, see {@link XmlHandler} for some default block types, including, data, name, etc.
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
 * 		[5-1st] data type bits - defines the attribute type, see {@link XmlHandler}, one of the bits defines whether the
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
 * 	block ID - (2 bytes) ID of block type, see {@link XmlHandler} for some default block types, including, data, name, etc.
 * 	format tag - (1 byte)
 * 		[8th] name bit - 1 means the data contains a name string directly before the data 
 * 		[7-6th] array storage bits - (if the data type is an array type) define number of bytes storing the data array
 * 		length, 0 = no array only one data element, 1 = 1 byte, 2 = 2 bytes, 3 = 4 bytes.
 * 		[5-1st] data type bits - defines the data type, see {@link XmlHandler}, one of the bits defines whether the
 * 		data is an array or not.
 * 	data array length (if data type is an array type) - (variable bytes)
 * 		Big-endian integer equal the length of the data's array, see the data format byte array storage bits
 * 		for how many bytes to read.
 * 	element data - (variable bytes, 1 or more elements depending on data type)
 * 		Defined by the data's data type found in the data format byte earlier, int = 4 bytes, etc.
 * 
 * Tag block type
 * 	block ID - (2 bytes) ID of block type, see {@link XmlHandler} for some default block types, including, data, name, etc.
 * 	tag byte - (1 byte)
 * 		[8th] nested bit - 1 means the block is a parent block, 0 means it is a leaf block
 * 		[7th] attribute bit - 1 means the tag block will contain an attribute block
 * 		[6th] data bit - 1 means the tag block will contain a data block
 * 	name block - must always be included
 * 	attribute block (if the attribute bit is set) - contains variable number of attributes
 * 	data block (if the data bit is set) - contains tag data
 * </pre>
 * Data types can be found in {@link XmlHandler}, such as {@link XmlHandler#BYTE_TYPE}, and {@link XmlHandler#ARRAY_TYPE} for arrays.
 * @author TeamworkGuy2
 * @since 2013-2-1
 * TODO add support for {@link #peekNextBlock()} to work properly with {@link #readClosingBlock()}
 */
public class XmlInputStream implements XmlInput {
	private DataInput in;
	private List<String> tagStack;
	private List<Object> tempAttributeList;
	private int tagsRead;
	private String lastReadTagElementName = null;

	private XmlTag lastOpeningTag;
	private XmlAttributes attributeStack;
	/** An array of two values:<br/>
	 * The first index is the tag's data type.
	 * The second index in the tag's data array length if it is an array
	 */
	private int[] tagDataTypeAndArrayLength = new int[2];

	private XmlTag peekHeader;
	private XmlAttributes peekAttributeStack;
	/** An array of two values:<br/>
	 * The first index is the peek tag's data type.
	 * The second index in the peek tag's data array length if it is an array
	 */
	private int[] peekTagDataTypeAndArrayLength = new int[2];


	/** Create a data converter to read XML data from a binary XML data format.
	 * It is recommended to encode strings using the {@link XmlHandler#STRING_TYPE} type
	 * to encode strings that may contain unicode characters.
	 * @param in the input stream containing the binary XML data written by a {@link XmlOutputStream}
	 */
	public XmlInputStream(DataInput in) {
		super();
		if(in == null) { throw new IllegalArgumentException("The DataInput stream cannot be null"); }
		this.in = in;
		this.tagStack = new ArrayList<String>();
		this.tempAttributeList = new ArrayList<Object>();
		this.attributeStack = new XmlAttributes();
		this.peekAttributeStack = new XmlAttributes();
		this.tagsRead = 0;
	}


	public void readHeader() {
		// TODO Do nothing since XML binary format does not have a header yet
	}


	/** Closes this converter's input stream if possible.
	 */
	@Override
	public void close() throws IOException {
		this.tagStack.clear();
		this.attributeStack.clear();
		this.peekAttributeStack.clear();
		this.tagsRead = -1;
		this.tagStack = null;
		this.attributeStack = null;
		this.peekAttributeStack = null;
		this.lastOpeningTag = null;
		this.peekHeader = null;
		if(in instanceof Closeable) {
			((Closeable)in).close();
		}
	}


	/** Clear this XML input stream.
	 * Does not close this object's input stream.
	 */
	@Override
	public void clear() {
		this.tagStack.clear();
		this.attributeStack.clear();
		this.peekAttributeStack.clear();
		this.tagsRead = -1;
		this.tagStack = null;
		this.attributeStack = null;
		this.peekAttributeStack = null;
		this.lastOpeningTag = null;
		this.peekHeader = null;
		this.in = null;
	}


	@Override
	public void read(String name, byte[] b) throws IOException {
		String tag = readTagElement(false);
		in.readFully(b, 0, b.length);
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
	}


	@Override
	public void read(String name, byte[] b, int off, int len) throws IOException {
		String tag = readTagElement(false);
		in.readFully(b, off, len);
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
	}


	@Override
	public boolean readBoolean(String name) throws IOException {
		String tag = readTagElement(false);
		boolean value = in.readBoolean();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public byte readByte(String name) throws IOException {
		String tag = readTagElement(false);
		byte value = in.readByte();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public char readChar(String name) throws IOException {
		String tag = readTagElement(false);
		char value = in.readChar();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public double readDouble(String name) throws IOException {
		String tag = readTagElement(false);
		double value = in.readDouble();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public float readFloat(String name) throws IOException {
		String tag = readTagElement(false);
		float value = in.readFloat();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public int readInt(String name) throws IOException {
		String tag = readTagElement(false);
		int value = in.readInt();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public long readLong(String name) throws IOException {
		String tag = readTagElement(false);
		long value = in.readLong();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public short readShort(String name) throws IOException {
		String tag = readTagElement(false);
		short value = in.readShort();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public String readUTF(String name) throws IOException {
		String tag = readTagElement(false);
		String value = in.readUTF();
		if(!name.equals(tag)) { throwTagMissmatchException(name, tag); }
		return value;
	}


	@Override
	public void read(byte[] b) throws IOException {
		readTagElement(false);
		in.readFully(b, 0, b.length);
	}


	@Override
	public void read(byte[] b, int off, int len) throws IOException {
		readTagElement(false);
		in.readFully(b, off, len);
	}


	@Override
	public boolean readBoolean() throws IOException {
		readTagElement(false);
		boolean value = in.readBoolean();
		return value;
	}


	@Override
	public byte readByte() throws IOException {
		readTagElement(false);
		byte value = in.readByte();
		return value;
	}


	@Override
	public char readChar() throws IOException {
		readTagElement(false);
		char value = in.readChar();
		return value;
	}


	@Override
	public double readDouble() throws IOException {
		readTagElement(false);
		double value = in.readDouble();
		return value;
	}


	@Override
	public float readFloat() throws IOException {
		readTagElement(false);
		float value = in.readFloat();
		return value;
	}


	@Override
	public int readInt() throws IOException {
		readTagElement(false);
		int value = in.readInt();
		return value;
	}


	@Override
	public long readLong() throws IOException {
		readTagElement(false);
		long value = in.readLong();
		return value;
	}


	@Override
	public short readShort() throws IOException {
		readTagElement(false);
		short value = in.readShort();
		return value;
	}


	@Override
	public String readUTF() throws IOException {
		readTagElement(false);
		String value = in.readUTF();
		return value;
	}


	@Override
	public String getLastElementName() {
		return lastReadTagElementName;
	}


	@Override
	public XmlTag readOpeningBlock(String name) throws IOException {
		XmlTag tag = readNextBlock();
		// If the element name does not match, throw an exception
		String tagName = tag.getHeaderName();
		if(!name.equals(tagName)) { throwTagMissmatchException(name, tagName); }

		return tag;
	}


	@Override
	public XmlTag readNextBlock() throws IOException {
		// If the next header has not been read (peeked at), read the next header
		if(peekHeader == null) {
			this.lastOpeningTag = readTag(in, attributeStack, tagDataTypeAndArrayLength, tempAttributeList, true);
		}
		// Else use the peek header and set the current header variables to the
		// peek header variables and set the peek header variables to null
		else {
			lastOpeningTag = peekHeader;
			peekHeader = null;
			// Switch the attribute stacks so that the current attributes are the peek attributes
			// and reuse the current attributes as the next peek attributes by clearing them
			XmlAttributes tempAttributes = attributeStack;
			attributeStack = peekAttributeStack;
			tempAttributes.clear();
			peekAttributeStack = tempAttributes;
			// Set the current header values to the peek header values and reset the peek header values
			int[] tempDataTypeAndArrayLength = tagDataTypeAndArrayLength;
			tagDataTypeAndArrayLength = peekTagDataTypeAndArrayLength;
			peekTagDataTypeAndArrayLength = tempDataTypeAndArrayLength;
		}
		return lastOpeningTag;
	}


	@Override
	public XmlTag peekNextBlock() throws IOException {
		/* If the next header has not been read, peek and read it
		 * The rest of the code saves the current head values and restores them
		 * after {@link #readTag(boolean)} is called since that method
		 * overwrites the current header values, so when it returns we save
		 * the current header values to our custom peek header variables
		 * and restore the current header values using the temp variables we
		 * saved them in.
		 * This basically hides the peek header from other methods such as
		 * {@link #getCurrentHeaderBlockAttributes()} or {@link #getCurrentHeaderBlock()}.
		 */
		if(peekHeader == null) {
			XmlTag tempHeader = lastOpeningTag;
			XmlAttributes tempAttributes = attributeStack;
			int[] tempDataTypeAndArrayLength = tagDataTypeAndArrayLength;
			tagDataTypeAndArrayLength = peekTagDataTypeAndArrayLength;
			peekTagDataTypeAndArrayLength = tempDataTypeAndArrayLength;
			lastOpeningTag = peekHeader;
			attributeStack = peekAttributeStack;
			// Read the next header tag
			readTag(in, attributeStack, tagDataTypeAndArrayLength, tempAttributeList, true);
			// Set the peek header to the values read by {@link #readTag(boolean)}
			peekHeader = lastOpeningTag;
			peekAttributeStack = attributeStack;
			peekTagDataTypeAndArrayLength = tagDataTypeAndArrayLength;
			// Reset the values set by {@link #readTag(boolean)} to the saved previous values
			lastOpeningTag = tempHeader;
			attributeStack = tempAttributes;
			tagDataTypeAndArrayLength = tempDataTypeAndArrayLength;
		}
		// Return the peek header
		return peekHeader;
	}


	/** Read an XML tag
	 * @param shouldContainNested true to read a tag containing nested tags,
	 * false to read a leaf/child tag
	 * @return the name of the tag read
	 * @throws IOException if there is an error reading the XML tag
	 */
	private String readTagElement(boolean shouldContainNested) throws IOException {
		XmlTag tag = readTag(in, attributeStack, tagDataTypeAndArrayLength, tempAttributeList, shouldContainNested);
		lastReadTagElementName = tag.getHeaderName();
		return tag.getHeaderName();
	}


	/** Read a XML tag
	 * @param shouldContainNested - true if the tag being read should containing nested tags,
	 * false if the tag being read should be an element.<br/>
	 * The method will throw an {@link XMLStreamException} wrapped in an {@link IOException}
	 * if the nested tag type does not match the actual type of tag read.
	 * @return the new header tag read
	 * @throws IOException if there is an IO or XML related error reading the input stream
	 */
	private static XmlTag readTag(DataInput reader, XmlAttributes attributes, int[] tagData, List<Object> tempList, boolean shouldContainNested) throws IOException {
		String tagName = null;
		XmlTag newTag = null;

		try {
			// Read the tag format
			byte tagFormat = reader.readByte();
			// Is the element a leaf or does it contain nested elements
			boolean isLeaf = (tagFormat & XmlHandler.CONVERTER_CONTAINS_NESTED) != XmlHandler.CONVERTER_CONTAINS_NESTED;
			// Get the number of bytes storing the attribute count or non if there are no attributes
			byte attributeCountBytes = (byte)((tagFormat & XmlHandler.CONVERTER_ATTRIBUTE_BYTES) >>> XmlHandler.CONVERTER_ATTRIBUTE_BYTES_SHIFT);
			// Get 'attribute count size' number of bytes, or zero bytes if there are not attributes, and read them into an int value
			int attributeCount = readIntFromBytes(reader, attributeCountBytes);

			// Read the specified number of attributes
			attributes.clear();
			byte attribFormat = 0;
			byte attribType = 0;
			boolean isArrayType = false;
			byte arrayLengthBytes = 0;
			int arrayLength = 0;
			String attributeName = null;
			// Read each attribute name and value
			for(int i = 0; i < attributeCount; i++) {
				// Read the attribute's info byte
				attribFormat = reader.readByte();
				// The attribute's data array type
				attribType = (byte)(attribFormat & XmlHandler.DATA_TYPE);
				// Is the attribute's data an array of data
				isArrayType = (attribFormat & XmlHandler.ARRAY_TYPE) != 0;
				// Calculate number of bytes storing the length of the attribute data array
				arrayLengthBytes = (byte)((attribFormat & XmlHandler.CONVERTER_ATTRIBUTE_BYTES) >>> XmlHandler.CONVERTER_ATTRIBUTE_BYTES_SHIFT);
				// Read the length of the attribute data array
				arrayLength = readIntFromBytes(reader, arrayLengthBytes);
				// Read the attribute's name
				attributeName = reader.readUTF();
				// Read the attribute's data and add it to the attributeValues list
				if(isArrayType) {
					readAttributeTypeArray(reader, attribType, arrayLength, attributeName, attributes, tempList);
				}
				else {
					readAttributeType(reader, attribType, attributeName, attributes);
				}
			}

			// Read the data
			byte dataFormat = 0;
			//byte dataType = 0;
			byte dataArrayLengthBytes = 0;
			int dataArrayLength = 0;
			// If the tag is a leaf tag (does not contain nested tags) then it will contain data
			dataFormat = reader.readByte();
			// Calculate number of bytes storing the length of the data array
			dataArrayLengthBytes = (byte)((dataFormat & XmlHandler.CONVERTER_ATTRIBUTE_BYTES) >>> XmlHandler.CONVERTER_ATTRIBUTE_BYTES_SHIFT);
			// Read the length of the data array
			dataArrayLength = readIntFromBytes(reader, dataArrayLengthBytes);
			// Store the data type and array length in the tag data array
			tagData[0] = (dataFormat & XmlHandler.ANY_TYPE);
			tagData[1] = dataArrayLength;

			// Read the tag name
			tagName = reader.readUTF();

			//System.out.println("Read tag: " + tag + ", attribs: " + attributeCount + "(" + attributeCountBytes + "), nested: " + ((tagInfo & XmlHandler.CONVERTER_CONTAINS_NESTED) != 0) + ", data: " + dataInfo + "(" + dataArrayLengthBytes + ")");

			// Allow another method (normally this method's calling method) to then read the tag's data
			// A binary tag in this format has no closing tag, so we do not need to worry about reading that

			// If the tag contains element data, but we are reading a tag that should contained nested tags, than throw an exception
			if(isLeaf == true && shouldContainNested == true) {
				throw new XMLStreamException("XML error, [" + tagName + "] tag should contain nested tags, but does not.");
			}
			else if(isLeaf == false && shouldContainNested == false) {
				throw new XMLStreamException("XML error, [" + tagName + "] tag should not contain nested tags, but does.");
			}
		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}
		// Create and return the newly read tag
		newTag = new XmlTagImpl(tagName, null, DataHeader.OPENING);
		return newTag;
	}


	@Override
	public void readClosingBlock() throws IOException {
		// Tags are not read in the binary format of a data input stream
	}


	@Override
	public XmlAttributes getCurrentBlockHeaderAttributes() {
		return this.attributeStack;
	}


	@Override
	public XmlTag getCurrentBlockHeader() {
		return lastOpeningTag;
	}


	@Override
	public int getBlocksRemaining() {
		return this.tagStack.size();
	}


	@Override
	public int getBlocksRead() {
		return this.tagsRead;
	}


	/** Read a specified data type from the specified DataInput stream
	 * @param reader - the DataInput stream to read the data type from
	 * @param dataType - the data type as defined in XmlHandler, such as XmlHandler.BYTE_TYPE
	 * @return the data value read from the DataInput stream
	 * @throws IOException if there is an error reading the DataInput stream
	 */
	@SuppressWarnings("unused")
	private static Object readDataType(DataInput reader, int dataType) throws IOException {
		// Read the data type's value
		switch(dataType) {
		case (byte)XmlHandler.BYTE_TYPE:
			return reader.readByte();
		case (byte)XmlHandler.SHORT_TYPE:
			return reader.readShort();
		case (byte)XmlHandler.INT_TYPE:
			return reader.readInt();
		case (byte)XmlHandler.LONG_TYPE:
			return reader.readLong();
		case (byte)XmlHandler.FLOAT_TYPE:
			return reader.readFloat();
		case (byte)XmlHandler.DOUBLE_TYPE:
			return reader.readDouble();
		case (byte)XmlHandler.BOOLEAN_TYPE:
			return reader.readBoolean();
		case (byte)XmlHandler.CHAR_TYPE:
			return reader.readChar();
		case (byte)XmlHandler.STRING_TYPE:
			return reader.readUTF();
		default:
			throw new IllegalStateException("Cannot read XML binary value with no data type");
		}
	}


	/** Read up to 4 bytes from the specified data input stream and convert them into an integer.
	 * The integer is read from left to right with the first byte being the most significant and the last byte being the
	 * least significant. Note that 3 is converted to 4 and 4 bytes are read.
	 * @param reader - the data input stream to read the bytes from
	 * @param bytes - the number of bytes to read, cannot be greater than 3, 0 = nothing, 1 = read 1 byte,
	 * 2 = read 2 bytes, 3 = read 4 bytes.
	 * @return the integer read made from the specified number of bytes.
	 * @throws IOException if there is an error reading from the data input stream
	 * @throws IllegalArgumentException if the <code>bytes</code> value is greater than 3 or less than 0
	 */
	private static int readIntFromBytes(DataInput reader, int bytes) throws IOException {
		if(bytes > 3 || bytes < 0) { throw new IllegalArgumentException("Cannot convert: " + bytes +
				" bytes to an integer, must be less than 4 and greater than -1"); }
		int value = 0;
		if(bytes == 1) { value = reader.readByte() & 0xFF; }
		else if(bytes == 2) { value = reader.readShort() & 0xFFFF; }
		else if(bytes == 3) { value = reader.readInt() & 0xFFFFFFFF; }
		return value;
	}


	/** Read a specified attribute type from the specified DataInput stream
	 * @param reader - the DataInput stream to read the data type from
	 * @param dataType - the data type as defined in XmlHandler, such as XmlHandler.BYTE_TYPE
	 * @param attributeName - the name of the attribute to add to the attributes
	 * @param attributes - the group of XML attributes to add this attribute value to
	 * @throws IOException if there is an error reading the DataInput stream
	 */
	private static void readAttributeType(DataInput reader, int dataType, String attributeName, XmlAttributes attributes) throws IOException {
		// Read the data type's value
		switch(dataType) {
		case (byte)XmlHandler.BYTE_TYPE:
			attributes.addAttributeByte(attributeName, reader.readByte());
		break;
		case (byte)XmlHandler.SHORT_TYPE:
			attributes.addAttributeShort(attributeName, reader.readShort());
		break;
		case (byte)XmlHandler.INT_TYPE:
			attributes.addAttributeInt(attributeName, reader.readInt());
		break;
		case (byte)XmlHandler.LONG_TYPE:
			attributes.addAttributeLong(attributeName, reader.readLong());
		break;
		case (byte)XmlHandler.FLOAT_TYPE:
			attributes.addAttributeFloat(attributeName, reader.readFloat());
		break;
		case (byte)XmlHandler.DOUBLE_TYPE:
			attributes.addAttributeDouble(attributeName, reader.readDouble());
		break;
		case (byte)XmlHandler.BOOLEAN_TYPE:
			attributes.addAttributeBoolean(attributeName, reader.readBoolean());
		break;
		case (byte)XmlHandler.CHAR_TYPE:
			attributes.addAttributeChar(attributeName, reader.readChar());
		break;
		case (byte)XmlHandler.STRING_TYPE:
			attributes.addAttribute(attributeName, reader.readUTF());
		break;
		default:
			throw new IllegalStateException("Cannot read XML binary value with no data type");
		}
	}


	/** Read an array of the specified attribute type from the specified DataInput stream
	 * @param reader - the DataInput stream to read the data type from
	 * @param dataType - the data type as defined in XmlHandler, such as XmlHandler.BYTE_TYPE
	 * @param arrayLength - the number of data values to read
	 * @param attributeName - the name of the attribute to add to the attributes
	 * @param attributes - the group of XML attributes to add this attribute array to
	 * @param tempValues - a temporary list of objects that is cleared and used to store a temporary list of values to add to the
	 * group of attributes
	 * @throws IOException if there is an error reading the DataInput stream
	 */
	private static void readAttributeTypeArray(DataInput reader, int dataType, int arrayLength, String attributeName, XmlAttributes attributes, List<Object> tempValues) throws IOException {
		tempValues.clear();
		// Read the data type's value
		switch(dataType) {
		case (byte)XmlHandler.BYTE_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readByte());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XmlHandler.SHORT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readShort());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XmlHandler.INT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readInt());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XmlHandler.LONG_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readLong());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XmlHandler.FLOAT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readFloat());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XmlHandler.DOUBLE_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readDouble());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XmlHandler.BOOLEAN_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readBoolean());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XmlHandler.CHAR_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readChar());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XmlHandler.STRING_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readUTF());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		default:
			throw new IllegalStateException("Cannot read XML binary value with no data type");
		}
		tempValues.clear();
	}


	private static void throwTagMissmatchException(String originalTag, String newTag) {
		throw new IllegalArgumentException("Tag name [" + originalTag + "] does not match name read [" + newTag + "]");
	}


	/** Read a XML header from the XML input stream
	 * @param input the XML input stream to read a header from
	 */
	public static final void readHeader(XmlInputStream input) {
		input.readHeader();
	}

}
