package xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import xml.binary.XMLInputStream;
import xml.binary.XMLOutputStream;

/** XML handler that parses an XML file and hands off control to subclasses when certain opening and closing tags are encountered
 * PairList - sometime in 2012, used modified list/map to store object fields and pass them to an reader/writer
 * XMLInput/Output - 2013-2-20, switch to a custom reader/writer interface that allows objects to write whatever.<br/>
 * <br/>
 * Data type bits x xxxx, the high bit defines whether the data type is an array,
 * the lower 4 bits define the data type.<br/>
 * Data types:<br/>
 * NO_TYPE = 0x00<br/>
 * BYTE_TYPE = 0x01<br/>
 * SHORT_TYPE = 0x02<br/>
 * INT_TYPE = 0x03<br/>
 * LONG_TYPE = 0x04<br/>
 * FLOAT_TYPE = 0x05<br/>
 * DOUBLE_TYPE = 0x06<br/>
 * BOOLEAN_TYPE = 0x07<br/>
 * CHAR_TYPE = 0x08<br/>
 * STRING_TYPE = 0x09<br/>
 * <br/>
 * And the important array type:<br/>
 * ARRAY_TYPE = 0x10
 * @author TeamworkGuy2
 * @since 2013-2-20
 */
public class XMLHandler {
	/** Used to identify the mask for all data type values including the <code>ARRAY_TYPE</code> */
	public static final int ANY_TYPE = 0x1F;
	/** Used to identify the mask for data type values excluding arrays such as <code>ARRAY_TYPE</code> */
	public static final int DATA_TYPE = 0x0F;
	/** A type that indicates that the data has not type, this may indicate that there is no data */
	public static final int NO_TYPE = 0;
	/** An identifier for the <code>byte</code> primitive */
	public static final int BYTE_TYPE = 1;
	/** An identifier for the <code>short</code> primitive */
	public static final int SHORT_TYPE = 2;
	/** An identifier for the <code>int</code> primitive */
	public static final int INT_TYPE = 3;
	/** An identifier for the <code>long</code> primitive */
	public static final int LONG_TYPE = 4;
	/** An identifier for the <code>float</code> primitive */
	public static final int FLOAT_TYPE = 5;
	/** An identifier for the <code>double</code> primitive */
	public static final int DOUBLE_TYPE = 6;
	/** An identifier for the <code>boolean</code> primitive */
	public static final int BOOLEAN_TYPE = 7;
	/** An identifier for the <code>char</code> primitive */
	public static final int CHAR_TYPE = 8;
	/** An identifier for a {@link String} object */
	public static final int STRING_TYPE = 9;
	/** An identifier for an array of values */
	public static final int ARRAY_TYPE = 16;
	/** Value used by data input and output converts to specify whether a tag contains nested tags (1) or data (0) */
	public static final int CONVERTER_CONTAINS_NESTED = 128;
	/** Mask to identify the bits used to stored the byte count holding the attribute count */
	public static final int CONVERTER_ATTRIBUTE_BYTES = 0x60;
	/** The number of bits to shift to the right to make the bits used to stored the byte count start at the first bit */
	public static final int CONVERTER_ATTRIBUTE_BYTES_SHIFT = 5;
	/** The attribute name that identifies descriptors */
	static final Charset defaultCharset = Charset.forName("US-ASCII");
	protected static final String DESCRIPTOR_ID = "name";
	private static final char lineSeparator = (char)0xA; // The XML standard uses LF to mark new lines, do not use System.getProperty("line.separator");
	private static final String XML_HEADER_VERSION = "<?xml version=\"";
	private static final String XML_HEADER_ENCODING = "\" encoding=\"";
	private static final String XML_HEADER_CLOSING = "\"?>" + lineSeparator;
	private static final String XML_VERSION = "1.0";
	private static final String[] charsetNames = new String[] {"UTF-8", "US-ASCII", "UTF-16"};
	private static XMLInputFactory factory;


	/** Read an Externalizable Java object from a file
	 * @param externalizableObj - the object to initialize with the Externalizable object found in the file
	 * @param file - file to read the Externalizable object from
	 * @throws SAXException if the XML parser encounters an error
	 * @throws FileNotFoundException if the specified XML file to read can not be found 
	 * @throws IOException if an error occurs while reading the XML file
	 * @throws ClassNotFoundException if the class of the object being deserialized could not be found
	 */
	public static Externalizable readExternal(Externalizable externalizableObj, File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		InputStream inputSource = new BufferedInputStream(new FileInputStream(file));
		ObjectInputStream in = new ObjectInputStream(inputSource);
		// Recover the object
		externalizableObj = (Externalizable)in.readObject();
		//externalizableObj.readExternal(in);
		in.close();
		inputSource.close();
		return externalizableObj;
	}


	/** Write an Externalizable Java object to a file
	 * @param externalizableObj - the Externalizable object to write to the file
	 * @param file - the file to write the object to
	 * @return the Externalizable object written to the file
	 * @throws IOException if there is an error writing the object to the file
	 */
	public static Externalizable writeExternal(Externalizable externalizableObj, File file) throws IOException {
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
		ObjectOutputStream out = new ObjectOutputStream(outputStream);
		// Save the object
		out.writeObject(externalizableObj);
		//externalizableObj.writeExternal(out);
		out.close();
		outputStream.close();
		return externalizableObj;
	}


	/** Get the default XML stream factory
	 * @return the default XML factory for this XML handler
	 */
	private static final synchronized XMLInputFactory getXMLFactory() {
		if(factory == null) {
			factory = XMLInputFactory.newFactory();
		}
		return factory;
	}


	/** Read an array of XML objects from the specified input file.
	 * @param file the file to read the XML objects from.
	 * This stream is not closed when the method returns.
	 * @param textFormat true creates an XML text reader, false creates a binary XML input stream
	 * @param xmlTag the opening and closing tag name to read from the XML stream
	 * @param descriptor an optional descriptor to associate with XML tags
	 * @param xmlObjects the array of XML objects to read from the input stream
	 * @param aggressiveParsing - <code>True</code> causes the parser to read multiple elements when an element
	 * cannot be found. <code>False</code> causes the parser to only read one element regardless of whether the
	 * element contains the matching tag name or not.
	 * @param throwsNoTagException - <code>True</code> causes the parser to throw an exception if an opening or
	 * closing tag cannot be found. <code>False</code> causes the parser to silently ignore the missing tag.
	 * @throws Exception if there is an error reading the XML object from the input stream
	 */
	public static void readXMLObjects(File file, boolean textFormat, String xmlTag, String descriptor, XMLable[] xmlObjects, boolean aggressiveParsing, boolean throwsNoTagException) throws Exception {
		InputStream input = new BufferedInputStream(new FileInputStream(file));
		readXMLObjectsInternal(true, input, textFormat, xmlTag, descriptor, xmlObjects, aggressiveParsing, throwsNoTagException);
	}


	/** Read an array of XML objects from the specified input stream.
	 * @param input the input stream to read the XML objects from.
	 * This stream is not closed when the method returns.
	 * @param textFormat true creates an XML text reader, false creates a binary XML input stream
	 * @param xmlTag the opening and closing tag name to read for the XML stream
	 * @param descriptor an optional descriptor to associate with the XML tag
	 * @param xmlObjects the array of XML objects to read from the input stream
	 * @param aggressiveParsing - <code>True</code> causes the parser to read multiple elements when an element
	 * cannot be found. <code>False</code> causes the parser to only read one element regardless of whether the
	 * element contains the matching tag name or not.
	 * @param throwsNoTagException - <code>True</code> causes the parser to throw an exception if an opening or
	 * closing tag cannot be found. <code>False</code> causes the parser to silently ignore the missing tag.
	 * @throws Exception if there is an error reading the XML objects from the input stream
	 */
	public static void readXMLObjects(InputStream input, boolean textFormat, String xmlTag, String descriptor, XMLable[] xmlObjects, boolean aggressiveParsing, boolean throwsNoTagException) throws Exception {
		readXMLObjectsInternal(false, input, textFormat, xmlTag, descriptor, xmlObjects, aggressiveParsing, throwsNoTagException);
	}


	private static void readXMLObjectsInternal(boolean close, InputStream input, boolean textFormat, String xmlTag, String descriptor, XMLable[] xmlObjects, boolean aggressiveParsing, boolean throwsNoTagException) throws Exception {
		XMLInput in = createXMLInput(input, textFormat, aggressiveParsing, throwsNoTagException);
		if(textFormat == true) {
			XMLInputFactory xmlFactory = getXMLFactory();
			XMLStreamReader reader = xmlFactory.createXMLStreamReader(input, defaultCharset.name());
			in = new XMLInputReader(reader, aggressiveParsing, throwsNoTagException);
		}
		else {
			in = new XMLInputStream(new DataInputStream(input));
		}

		in.readOpeningBlock(xmlTag);

		int widgetCount = xmlObjects.length;
		int initialTagCount = 0;
		for(int i = 0; i < widgetCount; i++) {
			initialTagCount = in.getBlocksRead();
			xmlObjects[i].readXML(in);
			// Check if the object read all tags
			if(in.getBlocksRead() < initialTagCount+2 || ((in.getBlocksRead() & 0x01) == 1)) {
				throw new IllegalStateException("Object: " + xmlObjects[i] + " did not read all element tags when reading from XML stream");
			}
		}
		in.readClosingBlock();

		if(close == true) {
			in.close();
		}
		else {
			in.clear();
		}
	}


	/** Write an array of XML objects to the specified output file.
	 * Does not write an XML header before the first element.
	 * Does not close or clear the output stream once complete.
	 * @param file the file to write the XML objects to
	 * @param textFormat true creates an XML text writer, false creates a binary XML output stream
	 * @param xmlTag the opening and closing tag name to write as the opening and closing tags for the XML file
	 * @param descriptor an optional descriptor to associate with the XML tags written
	 * @param xmlObjects the array of XML objects to write to the file
	 * @throws Exception if there is an error saving the XML object
	 */
	public static void writeXMLObjects(File file, boolean textFormat, String xmlTag, String descriptor, XMLable[] xmlObjects) throws Exception {
		OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
		writeXMLObjectsInternal(true, stream, textFormat, xmlTag, descriptor, xmlObjects);
	}


	/** Write an array of XML objects to the specified XML output stream.
	 * Writes an XML header before the first element.
	 * Does not close or clear the output stream once complete.
	 * @param output the output stream to write the XML objects to
	 * @param textFormat true creates an XML text writer, false creates a binary XML output stream
	 * @param xmlTag the opening and closing tag name to write as the opening and closing tags for the XML file
	 * @param descriptor an optional descriptor to associate with the XML tags to write
	 * @param xmlObjects the array of XML objects to write to the file
	 * @throws Exception if there is an error saving the XML object
	 */
	public static void writeXMLObjects(OutputStream output, boolean textFormat, String xmlTag, String descriptor, XMLable[] xmlObjects) throws Exception {
		writeXMLObjectsInternal(false, output, textFormat, xmlTag, descriptor, xmlObjects);
	}


	private static void writeXMLObjectsInternal(boolean close, OutputStream output, boolean textFormat, String xmlTag, String descriptor, XMLable[] xmlObjects) throws Exception {
		XMLOutput out = createXMLOutput(output, textFormat);
		if(out instanceof XMLOutputWriter) {
			((XMLOutputWriter)out).writeHeader();
		}
		if(out instanceof XMLOutputStream) {
			((XMLOutputStream)out).writeHeader();
		}

		out.writeOpeningBlock(xmlTag, descriptor);
		int widgetCount = xmlObjects.length;
		for(int i = 0; i < widgetCount; i++) {
			xmlObjects[i].writeXML(out);
		}
		out.writeClosingBlock();
		if(close == true) {
			output.close();
		}
	}


	/** Create an XML (text or binary) input stream from the specified input stream
	 * @param input the input stream to create an XML input stream from
	 * @param textFormat true to create an XML text reader, false to create a
	 * binary XML input stream
	 * @return the XML input stream created from the input stream
	 * @param aggressiveParsing - <code>True</code> causes the parser to read multiple elements when an element
	 * cannot be found. <code>False</code> causes the parser to only read one element regardless of whether the
	 * element contains the matching tag name or not.
	 * @param throwsNoElementException - <code>True</code> causes the parser to throw an exception if an element name
	 * cannot be found. <code>False</code> causes the parser to silently return null when unable to parse an element.
	 * @param throwsNoTagException - <code>True</code> causes the parser to throw an exception if an opening or
	 * closing tag cannot be found. <code>False</code> causes the parser to silently ignore the missing tag.
	 * @throws IOException if there is an error creating the XML input stream
	 */
	public static XMLInput createXMLInput(InputStream input, boolean textFormat, boolean aggressiveParsing, boolean throwsNoTagException) throws IOException {
		XMLInput in = null;
		if(textFormat == true) {
			XMLInputFactory xmlFactory = getXMLFactory();
			XMLStreamReader reader;
			try {
				reader = xmlFactory.createXMLStreamReader(input, defaultCharset.name());
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
			in = new XMLInputReader(reader, aggressiveParsing, throwsNoTagException);
		}
		else {
			in = new XMLInputStream(new DataInputStream(input));
		}
		return in;
	}


	/** Create an XML (text or binary) output stream from the specified output stream
	 * @param output the output stream to create an XML output stream from
	 * @param textFormat true to create an XML text writer, false to create a
	 * binary XML output stream
	 * @return the XML output stream created from the output stream
	 * @throws IOException if there is an error creating the XML output stream
	 */
	public static XMLOutput createXMLOutput(OutputStream output, boolean textFormat) {
		XMLOutput out = null;
		if(textFormat == true) {
			Writer writer = new BufferedWriter(new OutputStreamWriter(output, defaultCharset));
			out = new XMLOutputWriter(writer, defaultCharset);
		}
		else {
			out = new XMLOutputStream(new DataOutputStream(output));
		}
		return out;
	}


	protected static Charset getCharset() throws IOException {
		Charset charset = null;
		for(int i = 0; i < charsetNames.length; i++) {
			if(Charset.isSupported(charsetNames[i])) {
				charset = Charset.forName(charsetNames[i]);
				break;
			}
		}
		if(charset == null) {
			throw new IOException("Could not find supported charset");
		}
		return charset;
	}


	/** Generate an XML header tag based on the specified {@link Charset}
	 * @param charset - the {@link Charset} to generate the XML header from
	 * @return an XML document header as a string
	 */
	public static String xmlHeader(Charset charset) {
		return XML_HEADER_VERSION + XML_VERSION + XML_HEADER_ENCODING + charset.displayName() + XML_HEADER_CLOSING;
	}


	/** Convert an {@link XMLStreamConstants} constant to a string
	 * @param xmlConstant - the {@link XMLStreamConstants} integer constant to convert to a string
	 * @return the name of the {@link XMLStreamConstants} as a string
	 */
	public static String toString(int xmlConstant) {
		switch(xmlConstant) {
		case XMLStreamConstants.ATTRIBUTE:
			return "ATTRIBUTE";
		case XMLStreamConstants.CDATA:
			return "CDATA";
		case XMLStreamConstants.CHARACTERS:
			return "CHARACTERS";
		case XMLStreamConstants.COMMENT:
			return "COMMENT";
		case XMLStreamConstants.DTD:
			return "DTD";
		case XMLStreamConstants.END_DOCUMENT:
			return "END_DOCUMENT";
		case XMLStreamConstants.END_ELEMENT:
			return "END_ELEMENT";
		case XMLStreamConstants.ENTITY_DECLARATION:
			return "ENTITY_DECLARATION";
		case XMLStreamConstants.ENTITY_REFERENCE:
			return "ENTITY_REFERENCE";
		case XMLStreamConstants.NAMESPACE:
			return "NAMESPACE";
		case XMLStreamConstants.NOTATION_DECLARATION:
			return "NOTATION_DECLARATION";
		case XMLStreamConstants.PROCESSING_INSTRUCTION:
			return "PROCESSING_INSTRUCTION";
		case XMLStreamConstants.SPACE:
			return "SPACE";
		case XMLStreamConstants.START_DOCUMENT:
			return "START_DOCUMENT";
		case XMLStreamConstants.START_ELEMENT:
			return "START_ELEMENT";
		default:
			return "OTHER";
		}
	}


	/** Validate an XML string containing invalid XML characters into XML values (&amp; &apos; etc.) by replacing
	 * invalid characters with their corresponding character codes
	 * @param content - String to convert non-XML character to XML characters
	 * @return String with invalid XML characters replaced with XML character codes
	 */
	public static String validateElement(String content) {
		StringBuilder validated = new StringBuilder(content);
		int index = 0;
		index = validated.indexOf("&", 0);
		while(index > -1) {
			validated.replace(index, index+1, "&amp;");
			index = validated.indexOf("&", index+1);
		}
		index = validated.indexOf("'", 0);
		while(index > -1) {
			validated.replace(index, index+1, "&apos;");
			index = validated.indexOf("'", index+1);
		}
		index = validated.indexOf("\"", 0);
		while(index > -1) {
			validated.replace(index, index+1, "&quot;");
			index = validated.indexOf("\"", index+1);
		}
		index = validated.indexOf("<", 0);
		while(index > -1) {
			validated.replace(index, index+1, "&lt;");
			index = validated.indexOf("<", index+1);
		}
		index = validated.indexOf(">", 0);
		while(index > -1) {
			validated.replace(index, index+1, "&gt;");
			index = validated.indexOf(">", index+1);
		}
		return validated.toString();
	}


	/** Convert an XML string containing XML character codes (&amp; &apos; etc.) by replacing
	 * them with the corresponding character
	 * @param content - String to convert XML to non-XML characters (&amp; &quot; etc.)
	 * @return String with XML characters replaced with normal characters
	 */
	public static String convertElement(String content) {
		StringBuilder converted = new StringBuilder(content);
		int index = 0;
		index = converted.indexOf("&amp;", 0);
		while(index > -1) {
			converted.replace(index, index+5, "&");
			index = converted.indexOf("&amp;", index+1);
		}
		index = converted.indexOf("&apos;", 0);
		while(index > -1) {
			converted.replace(index, index+6, "'");
			index = converted.indexOf("&apos;", index+1);
		}
		index = converted.indexOf("&quot;", 0);
		while(index > -1) {
			converted.replace(index, index+6, "\"");
			index = converted.indexOf("&quot;", index+1);
		}
		index = converted.indexOf("&lt;", 0);
		while(index > -1) {
			converted.replace(index, index+4, "<");
			index = converted.indexOf("&lt;", index+1);
		}
		index = converted.indexOf("&gt;", 0);
		while(index > -1) {
			converted.replace(index, index+4, ">");
			index = converted.indexOf("&gt;", index+1);
		}
		return converted.toString();
	}

}
