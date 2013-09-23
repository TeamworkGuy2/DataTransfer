package xml.binary;

import java.io.Closeable;
import java.io.IOException;
import java.io.DataInput;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import dataTransfer.DataHeader;

import xml.XMLAttributes;
import xml.XMLHandler;
import xml.XMLInput;
import xml.XMLTag;
import xml.XMLTagImpl;

/** {@link XMLInput} implementation wrapper that converts a {@link DataInput} stream into an {@link XMLInput}
 * stream.
 * For example a {@link DataInput} stream could be converted to an {@link xml.XMLable XMLable} object via this code.
 * <p><blockquote><pre class="brush: java">
 * public void readExternal(ObjectInput in) throws IOException {
 * 	XMLInputStream inputStream = new XMLInputStream(in);
 * 	try {
 * 		readXML(inputStream);
 * 	} catch (XMLStreamException e) {
 * 		throw new IOException(e);
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
 * tag format (1 byte, 8th bit=contains nested tags, 7-6th bits=attribute count bytes)
 * attribute count (bytes, 1=byte, 2=short, 3=integer)
 * attributes (objects, 7-6th bits=attribute array bytes, 5-1st bits=attribute data type, array bytes, attribute name string, attribute data)
 * 	attribute count (1-4 bytes, 1=byte, 2=short, 3=integer)
 * 	attribute name (Java string)
 * 	attribute data (bytes)
 * data format (1 byte, 7-6th bits=data array bytes, 5-1st bits=data type, array bytes, tag name string, data)
 * 	data count (bytes, 1=byte, 2=short, 3=integer)
 * 	tag name (Java string)
 * 	data (bytes)
 * </pre>
 * 
 * future format...
 * <pre>
 * Nested block type
 * 	block ID - (2 bytes) ID of block type, see {@link XMLHandler} for some default block types, including, data, name, etc.
 * 	format tag - (1 byte)
 * 		[7-6th] block count storage bits - define number of bytes used to store the block count:
 * 		0 = no blocks, 1 = 1 byte, 2 = 2 bytes, 3 = 4 bytes.
 * 	block count - (variable bytes)
 * 		Big-endian integer equal the number of blocks inside this block, see the format tag byte block count storage bits
 * 		for how many bytes to read.
 * 	N-blocks - (variable bytes) where N = block count
 * 
 * Name block type
 * 	block ID - (2 bytes) ID of block type, see {@link XMLHandler} for some default block types, including, data, name, etc.
 * 	tag name - (Java UTF String)
 * 		The tag's name as a Java UTF-16 char type string
 * 
 * Attribute block type
 * 	block ID - (2 bytes) ID of block type, see {@link XMLHandler} for some default block types, including, data, name, etc.
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
 * 		[5-1st] data type bits - defines the attribute type, see {@link XMLHandler}, one of the bits defines whether the
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
 * 	block ID - (2 bytes) ID of block type, see {@link XMLHandler} for some default block types, including, data, name, etc.
 * 	format tag - (1 byte)
 * 		[8th] name bit - 1 means the data contains a name string directly before the data 
 * 		[7-6th] array storage bits - (if the data type is an array type) define number of bytes storing the data array
 * 		length, 0 = no array only one data element, 1 = 1 byte, 2 = 2 bytes, 3 = 4 bytes.
 * 		[5-1st] data type bits - defines the data type, see {@link XMLHandler}, one of the bits defines whether the
 * 		data is an array or not.
 * 	data array length (if data type is an array type) - (variable bytes)
 * 		Big-endian integer equal the length of the data's array, see the data format byte array storage bits
 * 		for how many bytes to read.
 * 	element data - (variable bytes, 1 or more elements depending on data type)
 * 		Defined by the data's data type found in the data format byte earlier, int = 4 bytes, etc.
 * 
 * Tag block type
 * 	block ID - (2 bytes) ID of block type, see {@link XMLHandler} for some default block types, including, data, name, etc.
 * 	tag byte - (1 byte)
 * 		[8th] nested bit - 1 means the block is a parent block, 0 means it is a leaf block
 * 		[7th] attribute bit - 1 means the tag block will contain an attribute block
 * 		[6th] data bit - 1 means the tag block will contain a data block
 * 	name block - must always be included
 * 	attribute block (if the attribute bit is set) - contains variable number of attributes
 * 	data block (if the data bit is set) - contains tag data
 * </pre>
 * Data types can be found in {@link XMLHandler}, such as {@link XMLHandler#BYTE_TYPE}, and {@link XMLHandler#ARRAY_TYPE} for arrays.
 * @author TeamworkGuy2
 * @since 2013-2-1
 * TODO add support for {@link #peekNextHeaderBlock()} to work properly with {@link #readClosingBlock()}
 */
public class XMLInputStream implements XMLInput {
	private DataInput in;
	private List<String> tagStack;
	private List<Object> tempAttributeList;
	private int tagsRead;

	private XMLTag lastOpeningTag;
	private XMLAttributes attributeStack;
	/** An array of two values:<br/>
	 * The first index is the tag's data type.
	 * The second index in the tag's data array length if it is an array
	 */
	private int[] tagDataTypeAndArrayLength = new int[2];

	private XMLTag peekHeader;
	private XMLAttributes peekAttributeStack;
	/** An array of two values:<br/>
	 * The first index is the peek tag's data type.
	 * The second index in the peek tag's data array length if it is an array
	 */
	private int[] peekTagDataTypeAndArrayLength = new int[2];


	/** Create a data converter to read XML data from binary XML data
	 * It is recommended to encode strings using the {@link XMLHandler#STRING_TYPE} type
	 * to encode strings that may contain unicode characters.
	 * @param in - the input stream containing the binary XML data written by a {@link XMLOutputStream}
	 */
	public XMLInputStream(DataInput in) {
		super();
		if(in == null) { throw new IllegalArgumentException("The DataInput stream cannot be null"); }
		this.in = in;
		this.tagStack = new ArrayList<String>();
		this.tempAttributeList = new ArrayList<Object>();
		this.attributeStack = new XMLAttributes();
		this.peekAttributeStack = new XMLAttributes();
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
	public XMLTag readOpeningBlock(String name) throws IOException {
		XMLTag tag = null;
		// If the next header has not been read (peeked at), read the next header
		if(peekHeader == null) {
			this.lastOpeningTag = readTag(in, attributeStack, tagDataTypeAndArrayLength, tempAttributeList, true);
		}
		// Else use the peek header and set the current header variables to the
		// peek header variables and set the peek header variables to null
		else {
			tag = peekHeader;
			lastOpeningTag = peekHeader;
			peekHeader = null;
			// Switch the attribute stacks so that the current attributes are the peek attributes
			// and reuse the current attributes as the next peek attributes by clearing them
			XMLAttributes tempAttributes = attributeStack;
			attributeStack = peekAttributeStack;
			tempAttributes.clear();
			peekAttributeStack = tempAttributes;
			// Set the current header values to the peek header values and reset the peek header values
			int[] tempDataTypeAndArrayLength = tagDataTypeAndArrayLength;
			tagDataTypeAndArrayLength = peekTagDataTypeAndArrayLength;
			peekTagDataTypeAndArrayLength = tempDataTypeAndArrayLength;
		}
		String tagName = tag.getHeaderName();
		if(!name.equals(tagName)) { throwTagMissmatchException(name, tagName); }
		return lastOpeningTag;
	}


	@Override
	public XMLTag readNextBlock() throws IOException {
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
			XMLAttributes tempAttributes = attributeStack;
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
	public XMLTag peekNextBlock() throws IOException {
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
			XMLTag tempHeader = lastOpeningTag;
			XMLAttributes tempAttributes = attributeStack;
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
		XMLTag tag = readTag(in, attributeStack, tagDataTypeAndArrayLength, tempAttributeList, shouldContainNested);
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
	private static XMLTag readTag(DataInput reader, XMLAttributes attributes, int[] tagData, List<Object> tempList, boolean shouldContainNested) throws IOException {
		String tagName = null;
		XMLTag newTag = null;

		try {
			// Read the tag format info byte
			byte tagInfo = reader.readByte();
			boolean isLeaf = (tagInfo & XMLHandler.CONVERTER_CONTAINS_NESTED) != XMLHandler.CONVERTER_CONTAINS_NESTED;
			byte attributeCountBytes = 0;
			int attributeCount = 0;
			// Get the number of bytes storing the attribute count or non if there are no attributes
			attributeCountBytes = (byte)((tagInfo & XMLHandler.CONVERTER_ATTRIBUTE_BYTES) >>> XMLHandler.CONVERTER_ATTRIBUTE_BYTES_SHIFT);
			// Get 'attribute count size' number of bytes, or zero bytes if there are not attributes, and read them into an int value
			attributeCount = readIntFromBytes(reader, attributeCountBytes);

			// Read the attributes if any
			if(attributeCount > 0) {
				// Read the specified number of attributes
				byte attribInfo = 0;
				byte attribType = 0;
				boolean isArrayType = false;
				byte arrayLengthBytes = 0;
				int arrayLength = 0;
				String attributeName = null;
				attributes.clear();
				// Read each attribute name and value
				for(int i = 0; i < attributeCount; i++) {
					// Read the attribute's info byte
					attribInfo = reader.readByte();
					attribType = (byte)(attribInfo & XMLHandler.DATA_TYPE);
					isArrayType = (attribInfo & XMLHandler.ARRAY_TYPE) != 0;
					if(isArrayType) {
						arrayLengthBytes = (byte)((attribInfo & XMLHandler.CONVERTER_ATTRIBUTE_BYTES) >>> XMLHandler.CONVERTER_ATTRIBUTE_BYTES_SHIFT);
						arrayLength = readIntFromBytes(reader, arrayLengthBytes);
					}
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
			}

			// Read the tag's data format info byte
			byte dataInfo = 0;
			byte dataArrayLengthBytes = 0;
			int dataArrayLength = 0;
			// If the tag is a left tag (does not contain nested tags) then it will contain data
			if(isLeaf == true) {
				dataInfo = reader.readByte();
				dataArrayLengthBytes = (byte)((dataInfo & XMLHandler.CONVERTER_ATTRIBUTE_BYTES) >>> XMLHandler.CONVERTER_ATTRIBUTE_BYTES_SHIFT);
				dataArrayLength = readIntFromBytes(reader, dataArrayLengthBytes);
				tagData[0] = (dataInfo & XMLHandler.ANY_TYPE);
				tagData[1] = dataArrayLength;
			}

			// Read the tag name
			tagName = reader.readUTF();

			//System.out.println("Read tag: " + tag + ", attribs: " + attributeCount + "(" + attributeCountBytes + "), nested: " + ((tagInfo & XMLHandler.CONVERTER_CONTAINS_NESTED) != 0) + ", data: " + dataInfo + "(" + dataArrayLengthBytes + ")");

			// Allow another method (normally this method's calling method) to then read the tag's data
			// A binary tag in this format has no closing tag, so we do not need to worry about reading that

			// If the tag contains element data, but we are reading a tag that should contained nested tags, than throw an exception
			if(isLeaf == true && shouldContainNested == true) {
				throw new XMLStreamException("XML error, [" + tagName + "] tag should contain nested tags, but does not.");
			}
			else if(isLeaf == false && shouldContainNested == false) {
				throw new XMLStreamException("XML error, [" + tagName + "] tag should not contain nested tags, but does.");
			}

			// Save the tag in the current XML tag
			newTag = new XMLTagImpl(tagName, null, DataHeader.OPENING);
		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}
		return newTag;
	}


	@Override
	public void readClosingBlock() throws IOException {
		// Tags are not read in the binary format of a data input stream
	}


	@Override
	public XMLAttributes getCurrentHeaderBlockAttributes() {
		return this.attributeStack;
	}


	@Override
	public XMLTag getCurrentBlockHeader() {
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
	 * @param dataType - the data type as defined in XMLHandler, such as XMLHandler.BYTE_TYPE
	 * @return the data value read from the DataInput stream
	 * @throws IOException if there is an error reading the DataInput stream
	 */
	@SuppressWarnings("unused")
	private static Object readDataType(DataInput reader, int dataType) throws IOException {
		// Read the data type's value
		switch(dataType) {
		case (byte)XMLHandler.BYTE_TYPE:
			return reader.readByte();
		case (byte)XMLHandler.SHORT_TYPE:
			return reader.readShort();
		case (byte)XMLHandler.INT_TYPE:
			return reader.readInt();
		case (byte)XMLHandler.LONG_TYPE:
			return reader.readLong();
		case (byte)XMLHandler.FLOAT_TYPE:
			return reader.readFloat();
		case (byte)XMLHandler.DOUBLE_TYPE:
			return reader.readDouble();
		case (byte)XMLHandler.BOOLEAN_TYPE:
			return reader.readBoolean();
		case (byte)XMLHandler.CHAR_TYPE:
			return reader.readChar();
		case (byte)XMLHandler.STRING_TYPE:
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
	 * @param dataType - the data type as defined in XMLHandler, such as XMLHandler.BYTE_TYPE
	 * @param attributeName - the name of the attribute to add to the attributes
	 * @param attributes - the group of XML attributes to add this attribute value to
	 * @throws IOException if there is an error reading the DataInput stream
	 */
	private static void readAttributeType(DataInput reader, int dataType, String attributeName, XMLAttributes attributes) throws IOException {
		// Read the data type's value
		switch(dataType) {
		case (byte)XMLHandler.BYTE_TYPE:
			attributes.addAttribute(attributeName, reader.readByte());
		break;
		case (byte)XMLHandler.SHORT_TYPE:
			attributes.addAttribute(attributeName, reader.readShort());
		break;
		case (byte)XMLHandler.INT_TYPE:
			attributes.addAttribute(attributeName, reader.readInt());
		break;
		case (byte)XMLHandler.LONG_TYPE:
			attributes.addAttribute(attributeName, reader.readLong());
		break;
		case (byte)XMLHandler.FLOAT_TYPE:
			attributes.addAttribute(attributeName, reader.readFloat());
		break;
		case (byte)XMLHandler.DOUBLE_TYPE:
			attributes.addAttribute(attributeName, reader.readDouble());
		break;
		case (byte)XMLHandler.BOOLEAN_TYPE:
			attributes.addAttribute(attributeName, reader.readBoolean());
		break;
		case (byte)XMLHandler.CHAR_TYPE:
			attributes.addAttribute(attributeName, reader.readChar());
		break;
		case (byte)XMLHandler.STRING_TYPE:
			attributes.addAttribute(attributeName, reader.readUTF());
		break;
		default:
			throw new IllegalStateException("Cannot read XML binary value with no data type");
		}
	}


	/** Read an array of the specified attribute type from the specified DataInput stream
	 * @param reader - the DataInput stream to read the data type from
	 * @param dataType - the data type as defined in XMLHandler, such as XMLHandler.BYTE_TYPE
	 * @param arrayLength - the number of data values to read
	 * @param attributeName - the name of the attribute to add to the attributes
	 * @param attributes - the group of XML attributes to add this attribute array to
	 * @param tempValues - a temporary list of objects that is cleared and used to store a temporary list of values to add to the
	 * group of attributes
	 * @throws IOException if there is an error reading the DataInput stream
	 */
	private static void readAttributeTypeArray(DataInput reader, int dataType, int arrayLength, String attributeName, XMLAttributes attributes, List<Object> tempValues) throws IOException {
		tempValues.clear();
		// Read the data type's value
		switch(dataType) {
		case (byte)XMLHandler.BYTE_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readByte());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XMLHandler.SHORT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readShort());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XMLHandler.INT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readInt());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XMLHandler.LONG_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readLong());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XMLHandler.FLOAT_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readFloat());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XMLHandler.DOUBLE_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readDouble());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XMLHandler.BOOLEAN_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readBoolean());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XMLHandler.CHAR_TYPE:
			for(int a = 0; a < arrayLength; a++) {
				tempValues.add(reader.readChar());
			}
		attributes.addAttribute(attributeName, tempValues);
		break;
		case (byte)XMLHandler.STRING_TYPE:
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
	public static final void readHeader(XMLInputStream input) {
		input.readHeader();
	}

}
