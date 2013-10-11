package xml.binary;

import java.io.Closeable;
import java.io.IOException;
import java.io.DataOutput;
import java.util.List;

import xml.XMLAttributes;
import xml.XMLHandler;
import xml.XMLOutput;

/** {@link XMLOutput} implementation wrapper that converting a {@link DataOutput} stream into a {@link XMLOutput}
 * stream.
 * It is recommended to encode strings using the {@link XMLHandler#STRING_TYPE} type
 * to encode strings that may contain unicode characters.
 * An {@link xml.XMLable XMLable} object could be written to a {@link DataOutput} stream using this example code.
 * <p><blockquote><pre class="brush: java">
 * public void writeExternal(ObjectOutput out) throws IOException {
 * 	XMLOutputStream outputStream = new XMLOutputStream(out);
 * 	try {
 * 		writeXML(outputStream);
 * 	} catch (XMLStreamException e) {
 * 		throw new IOException(e);
 * 	}
 * 	finally {
 * 		outputStream.clear();
 * 		outputStream = null;
 * 	}
 * }
 * </pre></blockquote>
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
 */
public class XMLOutputStream implements XMLOutput {
	private static final int singleElementArrayLength = 1;
	private DataOutput out;

	public XMLOutputStream(DataOutput out) {
		super();
		this.out = out;
	}


	public void writeHeader() {
		// TODO Do nothing since binary XML does not have a header yet
	}


	/** Closes this converter's output stream if possible.
	 */
	@Override
	public void close() throws IOException {
		if(out instanceof Closeable) {
			((Closeable)out).close();
		}
	}


	/** Does not close this convert's output stream. Sets this converter's
	 * reference to the object output stream to null for garbage collection.
	 */
	public void clear() {
		this.out = null;
	}


	@Override
	public void write(String name, byte[] b) throws IOException {
		writeElement(name, null, false, XMLHandler.BYTE_TYPE | XMLHandler.ARRAY_TYPE, b.length);
		out.write(b, 0, b.length);
	}


	@Override
	public void write(String name, byte[] b, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.BYTE_TYPE | XMLHandler.ARRAY_TYPE, b.length);
		out.write(b, 0, b.length);
	}


	@Override
	public void write(String name, byte[] b, int off, int len) throws IOException {
		writeElement(name, null, false, XMLHandler.BYTE_TYPE | XMLHandler.ARRAY_TYPE, len);
		out.write(b, off, len);
	}


	@Override
	public void write(String name, byte[] b, int off, int len, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.BYTE_TYPE | XMLHandler.ARRAY_TYPE, len);
		out.write(b, off, len);
	}


	@Override
	public void writeBoolean(String name, boolean v) throws IOException {
		writeElement(name, null, false, XMLHandler.BOOLEAN_TYPE, singleElementArrayLength);
		out.writeBoolean(v);
	}


	@Override
	public void writeBoolean(String name, boolean v, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.BOOLEAN_TYPE, singleElementArrayLength);
		out.writeBoolean(v);
	}


	@Override
	public void writeByte(String name, byte v) throws IOException {
		writeElement(name, null, false, XMLHandler.BYTE_TYPE, singleElementArrayLength);
		out.writeByte(v);
	}


	@Override
	public void writeByte(String name, byte v, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.BYTE_TYPE, singleElementArrayLength);
		out.writeByte(v);
	}


	@Override
	public void writeChar(String name, char v) throws IOException {
		writeElement(name, null, false, XMLHandler.CHAR_TYPE, singleElementArrayLength);
		out.writeChar(v);
	}


	@Override
	public void writeChar(String name, char v, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.CHAR_TYPE, singleElementArrayLength);
		out.writeChar(v);
	}


	@Override
	public void writeDouble(String name, double v) throws IOException {
		writeElement(name, null, false, XMLHandler.DOUBLE_TYPE, singleElementArrayLength);
		out.writeDouble(v);
	}


	@Override
	public void writeDouble(String name, double v, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.DOUBLE_TYPE, singleElementArrayLength);
		out.writeDouble(v);
	}


	@Override
	public void writeFloat(String name, float v) throws IOException {
		writeElement(name, null, false, XMLHandler.FLOAT_TYPE, singleElementArrayLength);
		out.writeFloat(v);
	}


	@Override
	public void writeFloat(String name, float v, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.FLOAT_TYPE, singleElementArrayLength);
		out.writeFloat(v);
	}


	@Override
	public void writeInt(String name, int v) throws IOException {
		writeElement(name, null, false, XMLHandler.INT_TYPE, singleElementArrayLength);
		out.writeInt(v);
	}


	@Override
	public void writeInt(String name, int v, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.INT_TYPE, singleElementArrayLength);
		out.writeInt(v);
	}


	@Override
	public void writeLong(String name, long v) throws IOException {
		writeElement(name, null, false, XMLHandler.LONG_TYPE, singleElementArrayLength);
		out.writeLong(v);
	}


	@Override
	public void writeLong(String name, long v, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.LONG_TYPE, singleElementArrayLength);
		out.writeLong(v);
	}


	@Override
	public void writeShort(String name, short v) throws IOException {
		writeElement(name, null, false, XMLHandler.SHORT_TYPE, singleElementArrayLength);
		out.writeShort(v);
	}


	@Override
	public void writeShort(String name, short v, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.SHORT_TYPE, singleElementArrayLength);
		out.writeShort(v);
	}


	@Override
	public void writeUTF(String name, String s) throws IOException {
		writeElement(name, null, false, XMLHandler.STRING_TYPE, singleElementArrayLength);
		out.writeUTF(s);
	}


	@Override
	public void writeUTF(String name, String s, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, false, XMLHandler.STRING_TYPE, singleElementArrayLength);
		out.writeUTF(s);
	}


	@Override
	public void writeOpeningBlock(String name) throws IOException {
		writeElement(name, null, true, XMLHandler.NO_TYPE, 0);
	}


	@Override
	public void writeOpeningBlock(String name, XMLAttributes attributes) throws IOException {
		writeElement(name, null, attributes, true, XMLHandler.NO_TYPE, 0);
	}


	@Override
	public void writeOpeningBlock(String name, String descriptor) throws IOException {
		writeElement(name, descriptor, true, XMLHandler.NO_TYPE, 0);
	}


	@Override
	public void writeOpeningBlock(String name, String descriptor, XMLAttributes attributes) throws IOException {
		writeElement(name, descriptor, attributes, true, XMLHandler.NO_TYPE, 0);
	}


	/** Write an XML tag with the specified nested value
	 * @param name the name of the XML tag to write
	 * @param descriptor an optional descriptor to write with the tag
	 * @param containsNested true indicates that the tag being written will
	 * contain nested tags, false indicates that the tag will contain element data
	 * @param dataType the data type of the the XML element, should be from {@link XMLHandler},
	 * such as {@link XMLHandler.BYTE_TYPE}
	 * can be ORed with {@link XMLHandler.ARRAY_TYPE} to indicate that the data is an array of its type.
	 * @param dataArrayLength only useful if the data type is an array type,
	 * specifics the length of the data array to be stored in this element,
	 * 1 represents a non array data type with one element
	 * @throws IOException if there is an error writing the XML data to the data output
	 */
	private void writeElement(String name, String descriptor, boolean containsNested, int dataType, int dataArrayLength) throws IOException {
		// Create the tag's format info byte with an attribute array length and byte count of 0
		byte tagFormat = createTagType(containsNested, 0);
		// Write the tag's format info byte
		out.writeByte(tagFormat);

		// There are not attributes to write and the tag's format info byte will contain this information
		writeData(out, name, dataType, dataArrayLength);

		//System.out.println("Write tag: " + name + ", attribs: " + 0 + "(0), nested: " + nested + ", data: " + dataType + "(" + arrayLengthByteCount + ")");

		// Allow another method (normally the method's calling this method) to then write the tag's data
		// A binary tag in this format has not closing tag, so we do not need to worry about writing that
	}


	/** Write an XML tag with the specified nested value
	 * FIXME add descriptor
	 * @param name the name of the XML tag to write
	 * @param descriptor an optional descriptor to write with this tag
	 * @param attributes the attributes to add to the tag
	 * @param containsNested true indicates that the tag being written will contain nested tags, false indicates that the tag will contain
	 * element data
	 * @param dataType the data type of the the XML element, should be from {@link XMLHandler}, such as {@link XMLHandler.BYTE_TYPE}
	 * can be ORed with {@link XMLHandler.ARRAY_TYPE} to indicate that the data is an array of its type.
	 * @param arrayLength only useful if the data type is an array type, specifics the length of the data array to be stored in
	 * this element
	 * @throws IOException if there is an error writing the XML data to the data output
	 */
	private void writeElement(String name, String descriptor, XMLAttributes attributes, boolean containsNested, int dataType, int arrayLength) throws IOException {
		int attribCount = attributes.size();

		writeTag(out, containsNested, attribCount);

		writeAttribute(out, attributes);

		writeData(out, name, dataType, arrayLength);

		//System.out.println("Write tag: " + name + ", attribs: " + attribCount + "(" + byteCount + "), nested: " + nested + ", data: " + dataType + "(" + dataByteCount + ")");

		// Allow another method (normally this method's calling method) to then write the tag's data
		// A binary tag in this format has no closing tag, so we do not need to worry about writing that
	}


	private static void writeTag(DataOutput write, boolean containsNested, int attributeCount) throws IOException {
		// Calculate the number of bytes needed to store the number of attributes
		int byteCount = sizeByteCount(attributeCount);
		byte tagByte = createTagType(containsNested, byteCount);
		// Write the tag's format info byte
		write.writeByte(tagByte);

		// Write the tag's attribute count bytes
		if(byteCount == 1) { write.writeByte((byte)attributeCount); }
		else if(byteCount == 2) { write.writeShort((short)attributeCount); }
		else if(byteCount == 3) { write.writeInt((int)attributeCount); }
	}


	/** Write a data block
	 * @param write the data output stream to write the attribute block too
	 * @param name the name of the data block
	 * @param dataType the data type, see {@link XMLHander}, for example {@link XMLHandler#BYTE_TYPE}
	 * @param arrayLength the number of data objects in the data block
	 * @throws IOException if there is an error writing to the output stream
	 */
	private static void writeData(DataOutput write, String name, int dataType, int arrayLength) throws IOException {
		// Calculate the number of bytes needed to store the array length
		int arrayLengthByteCount = sizeByteCount(arrayLength);
		// Create the format byte based on the data type and arrayLengthByteCount
		byte dataByte = createDataType(dataType, arrayLengthByteCount);
		// Write the tag's data format info byte
		write.writeByte(dataByte);
		// Write the tag's data array size bytes
		if(arrayLengthByteCount == 1) { write.writeByte((byte)arrayLength); }
		else if(arrayLengthByteCount == 2) { write.writeShort((short)arrayLength); }
		else if(arrayLengthByteCount == 3) { write.writeInt((int)arrayLength); }

		// Write the tag name
		if(name == null) {
			throw new NullPointerException("Element name may not be null");
		}

		write.writeUTF(name);
	}


	/** Write an attribute data block
	 * @param write the data output stream to write the attribute block too
	 * @param attributes the attributes to write
	 * @throws IOException if there is an error writing to the output stream
	 */
	private static void writeAttribute(DataOutput write, XMLAttributes attributes) throws IOException {
		List<String> attributeNames = attributes.getAttributeNames();
		List<Object> attributeValues = attributes.getAttributeValues();
		List<Byte> attribyteTypes = attributes.getAttributeTypes();
		List<Integer> attributeArrayLengths = attributes.getAttributeArrayLengths();
		int attribCount = attributeNames.size();

		// Write the attributes
		byte attribType = 0;
		int attribByteCount = 0;
		Integer attributeValue = null;
		int attribArraySize = 0;
		for(int i = 0; i < attribCount; i++) {
			attribArraySize = (attributeValue = attributeArrayLengths.get(i)) == null ? 0 : attributeValue;
			attribByteCount = sizeByteCount(attribArraySize);
			attribType = createDataType(attribyteTypes.get(i), attribByteCount);
			// Write the attribute's info byte
			write.writeByte(attribType);
			// Write the attribute's array size bytes
			if(attribByteCount == 1) { write.writeByte((byte)attribArraySize); }
			else if(attribByteCount == 2) { write.writeShort((short)attribArraySize); }
			else if(attribByteCount == 3) { write.writeInt((int)attribArraySize); }
			// Write the attribute's name and value
			write.writeUTF(attributeNames.get(i));
			// Write the attribute's data
			writeDataType(write, attributeValues.get(i));
		}
	}


	@Override
	public void writeClosingBlock() throws IOException {
		// Closing tags are not written when writing binary XML data
	}


	@Override
	public int getBlocksRemaining() {
		return 0;
	}


	@Override
	public int getBlocksWritten() {
		return 0;
	}


	/** Creates a value containing information about an XML tag
	 * @param nested - true if the tag contains further nested tags, false if the tag contains data and an immediate closing tag
	 * @param arrayLengthByteCount - the number of bytes used to store the tag's attribute array length if there are multiple
	 * attributes, this is expected to be 2 bits long
	 * @return the byte packed with the input parameters
	 */
	private static final byte createTagType(boolean nested, int arrayLengthByteCount) {
		byte tagTypeByte = (byte)(nested == true ? XMLHandler.CONVERTER_CONTAINS_NESTED : 0);
		tagTypeByte |= (byte)(arrayLengthByteCount << XMLHandler.CONVERTER_ATTRIBUTE_BYTES_SHIFT);
		return tagTypeByte;
	}


	/** Creates a value containing information about an attribute or tag's data value
	 * @param dataType - the data type of the data, found in {@link XMLHandler}, should be ORed with {@link XMLHandler.ARRAY_TYPE}
	 * if the data type is an array
	 * @param arrayLengthByteCount - the number of bytes used to store the data's array length if the data is an array type,
	 * this is expected to be 2 bits long
	 * @return the byte packed with the input parameters
	 */
	private static final byte createDataType(int dataType, int arrayLengthByteCount) {
		byte dataTypeByte = (byte)(dataType);
		dataTypeByte |= (byte)(arrayLengthByteCount << XMLHandler.CONVERTER_ATTRIBUTE_BYTES_SHIFT);
		return dataTypeByte;
	}


	/** Calculate the number of bytes that the specified value will fit in using 2 bits specifically
	 * for XML data converts that work with a custom binary XML format.<br/>
	 * If the value fits in 1 byte, 0x01 is returned.<br/>
	 * If the value fits in 2 bytes, 0x02 is returned.<br/>
	 * If the value fits in 4 bytes, 0x04 is returned.<br/>
	 * Larger values cannot occur since the input is an integer (4 bytes)
	 * @param valueCount - the value to convert to a byte storage size
	 * @return the number of bytes needed to store the <code>valueCount</code> value in.
	 * 1 means the value will fit in 1 byte, 2 means the value will fit in 2 bytes, etc.
	 */
	private static final int sizeByteCount(int valueCount) {
		// Get the number of bits currently holding the value
		int value = (32 - Integer.numberOfLeadingZeros(valueCount));
		// Save the 3 overflow bits before dividing the bit count by 8
		byte overflow = (byte)(value & 0x07);
		// If the attribute count fits in 1 byte, then = 1, if it fits in 2 bytes, then = 2, if it fits in 3 or 4 bytes, then = 3
		// Divide the bit count by 8 to get the byte count, add 1 if any of the overflow bits are 1, example 15 bits/8 = 1+1 = 2 bytes
		value = (value >>> 3) + (((overflow & 0x04) >>> 2) | ((overflow & 0x02) >>> 1) | (overflow & 0x01));
		value = (value == 4) ? 3 : value;
		return value;
	}


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
		else {
			throw new IllegalArgumentException(value + " is not a recognized attribyte value type");
		}
	}


	/** Write a header to the specified XML output stream
	 * @param output the XML output stream to write to header to
	 */
	public static final void writeHeader(XMLOutputStream output) {
		output.writeHeader();
	}

}
