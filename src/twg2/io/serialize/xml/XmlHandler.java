package twg2.io.serialize.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import twg2.text.stringEscape.StringEscapeXml;

/** XML handler that parses an XML file and hands off control to subclasses when certain opening and closing tags are encountered
 * PairList - sometime in 2012, used modified list/map to store object fields and pass them to an reader/writer
 * XmlInput/Output - 2013-2-20, switch to a custom reader/writer interface that allows objects to write whatever.<br/>
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
public class XmlHandler {
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
	private static final String XML_HEADER_VERSION = "<?twg2.io.serialize.xml version=\"";
	private static final String XML_HEADER_ENCODING = "\" encoding=\"";
	private static final String XML_HEADER_CLOSING = "\"?>" + lineSeparator;
	private static final String XML_VERSION = "1.0";
	private static final String[] charsetNames = new String[] {"UTF-8", "US-ASCII", "UTF-16"};
	private static volatile XMLInputFactory factory;


	/** Get the default XML stream factory
	 * @return the default XML factory for this XML handler
	 */
	static final synchronized XMLInputFactory getXMLFactory() {
		if(factory == null) {
			factory = XMLInputFactory.newInstance();
		}
		return factory;
	}


	/** Read an array of XML objects from the specified input file.
	 * @param file the file to read the XML objects from.
	 * @param doBuffer true to wrap the output stream in a buffered output stream,
	 * false to use it without modification
	 * @param charset the charset to use for textual data, if null the default US-ASCII charset is used.
	 * @param xmlTag the opening and closing tag name to read from the XML stream
	 * @param descriptor an optional descriptor to associate with XML tags
	 * @param xmlObjects the array of XML objects to read from the input stream
	 * @param aggressiveParsing <code>True</code> causes the parser to read multiple elements when an element
	 * cannot be found. <code>False</code> causes the parser to only read one element regardless of whether the
	 * element contains the matching tag name or not.
	 * @param throwsNoTagException <code>True</code> causes the parser to throw an exception if an opening or
	 * closing tag cannot be found. <code>False</code> causes the parser to silently ignore the missing tag.
	 * @throws Exception if there is an error reading the XML object from the input stream
	 */
	public static void readXMLObjects(File file, boolean doBuffer, Charset charset,
			String xmlTag, String descriptor, Xmlable[] xmlObjects, boolean aggressiveParsing, boolean throwsNoTagException) throws Exception {
		InputStream input = new BufferedInputStream(new FileInputStream(file));
		readXMLObjectsInternal(input, doBuffer, true, charset, xmlTag, descriptor,
				xmlObjects, aggressiveParsing, throwsNoTagException);
	}


	/** Read an array of XML objects from the specified input stream.
	 * @param input the input stream to read the XML objects from.
	 * This stream is not closed when the method returns.
	 * @param doBuffer true to wrap the output stream in a buffered output stream,
	 * false to use it without modification
	 * @param charset the charset to use for textual data, if null the default US-ASCII charset is used.
	 * @param xmlTag the opening and closing tag name to read for the XML stream
	 * @param descriptor an optional descriptor to associate with the XML tag
	 * @param xmlObjects the array of XML objects to read from the input stream
	 * @param aggressiveParsing <code>True</code> causes the parser to read multiple elements when an element
	 * cannot be found. <code>False</code> causes the parser to only read one element regardless of whether the
	 * element contains the matching tag name or not.
	 * @param throwsNoTagException <code>True</code> causes the parser to throw an exception if an opening or
	 * closing tag cannot be found. <code>False</code> causes the parser to silently ignore the missing tag.
	 * @throws Exception if there is an error reading the XML objects from the input stream
	 */
	public static void readXMLObjects(InputStream input, boolean doBuffer, Charset charset, String xmlTag,
			String descriptor, Xmlable[] xmlObjects, boolean aggressiveParsing, boolean throwsNoTagException) throws Exception {
		readXMLObjectsInternal(input, doBuffer, false, charset, xmlTag, descriptor,
				xmlObjects, aggressiveParsing, throwsNoTagException);
	}


	private static void readXMLObjectsInternal(InputStream input, boolean doBuffer, boolean close, Charset charset,
			String xmlTag, String descriptor, Xmlable[] xmlObjects, boolean aggressiveParsing, boolean throwsNoTagException) throws Exception {
		XmlInput in = createXMLReader(input, doBuffer, charset, aggressiveParsing, throwsNoTagException);
		XMLStreamReader reader = getXMLFactory().createXMLStreamReader(input, defaultCharset.name());
		in = new XmlInputReader(reader, aggressiveParsing, throwsNoTagException);

		in.readStartBlock(xmlTag);

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
		in.readEndBlock();

		if(close == true) {
			in.close();
		}
		else {
			in.clear();
		}
	}


	/** Write an array of XML objects to the specified output file.
	 * Does not write an XML header before the first element.
	 * @param file the file to write the XML objects to
	 * @param doBuffer true to wrap the output stream in a buffered output stream,
	 * false to use it without modification
	 * @param charset the charset to use for textual data, if null the default US-ASCII charset is used.
	 * @param xmlTag the opening and closing tag name to write as the opening and closing tags for the XML file
	 * @param xmlObjects the array of XML objects to write to the file
	 * @throws Exception if there is an error saving the XML object
	 */
	public static void writeXMLObjects(File file, boolean doBuffer, Charset charset,
			String xmlTag, Xmlable[] xmlObjects) throws Exception {
		OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
		writeXMLObjectsInternal(stream, doBuffer, true, charset, xmlTag, xmlObjects);
	}


	/** Write an array of XML objects to the specified XML output stream.
	 * Writes an XML header before the first element.
	 * Does not close or clear the output stream once complete.
	 * @param output the output stream to write the XML objects to
	 * @param doBuffer true to wrap the output stream in a buffered output stream,
	 * false to use it without modification
	 * @param charset the charset to use for textual data, if null the default US-ASCII charset is used.
	 * @param xmlTag the opening and closing tag name to write as the opening and closing tags for the XML file
	 * @param xmlObjects the array of XML objects to write to the file
	 * @throws Exception if there is an error saving the XML object
	 */
	public static void writeXMLObjects(OutputStream output, boolean doBuffer, Charset charset, boolean textFormat,
			String xmlTag, Xmlable[] xmlObjects) throws Exception {
		writeXMLObjectsInternal(output, doBuffer, false, charset, xmlTag, xmlObjects);
	}


	private static void writeXMLObjectsInternal(OutputStream output, boolean doBuffer,
			boolean close, Charset charset, String xmlTag, Xmlable[] xmlObjects) throws Exception {
		XmlOutput out = createXMLWriter(output, doBuffer, charset);
		if(out instanceof XmlOutputWriter) {
			((XmlOutputWriter)out).writeHeader();
		}

		out.writeStartBlock(xmlTag);
		int widgetCount = xmlObjects.length;
		for(int i = 0; i < widgetCount; i++) {
			xmlObjects[i].writeXML(out);
		}
		out.writeEndBlock();
		if(close == true) {
			output.close();
		}
	}


	/** Create an XML (text or binary) input stream from the specified input stream
	 * @param input the input stream to create an XML input stream from
	 * @param doBuffer true to wrap the input stream in a buffered input stream,
	 * false to use it without modification
	 * @param charset the charset to use for textual data, if null the default US-ASCII charset is used.
	 * @return the XML input stream created from the input stream
	 * @param aggressiveParsing <code>True</code> causes the parser to read multiple elements when an element
	 * cannot be found. <code>False</code> causes the parser to only read one element regardless of whether the
	 * element contains the matching tag name or not.
	 * @param throwsNoTagException <code>True</code> causes the parser to throw an exception if an opening or
	 * closing tag cannot be found. <code>False</code> causes the parser to silently ignore the missing tag.
	 * @throws IOException if there is an error creating the XML input stream
	 */
	public static XmlInput createXMLReader(InputStream input, boolean doBuffer, Charset charset,
			boolean aggressiveParsing, boolean throwsNoTagException) throws IOException {
		if(charset == null) {
			charset = defaultCharset;
		}
		if(doBuffer == true && !(input instanceof BufferedInputStream)) {
			input = new BufferedInputStream(input);
		}
		try {
			XMLStreamReader reader = getXMLFactory().createXMLStreamReader(input, charset.name());
			return new XmlInputReader(reader, aggressiveParsing, throwsNoTagException);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}


	/** Create an XML (text or binary) input stream from the specified reader
	 * @param reader the input reader to create an XML input stream from
	 * @param doBuffer true to wrap the input stream in a buffered input stream,
	 * false to use it without modification
	 * @return the XML input stream created from the input stream
	 * @param aggressiveParsing <code>True</code> causes the parser to read multiple elements when an element
	 * cannot be found. <code>False</code> causes the parser to only read one element regardless of whether the
	 * element contains the matching tag name or not.
	 * @param throwsNoTagException <code>True</code> causes the parser to throw an exception if an opening or
	 * closing tag cannot be found. <code>False</code> causes the parser to silently ignore the missing tag.
	 * @throws IOException if there is an error creating the XML input stream
	 */
	public static XmlInput createXMLReader(Reader reader, boolean doBuffer, boolean aggressiveParsing,
			boolean throwsNoTagException) throws IOException {
		if(doBuffer == true && !(reader instanceof BufferedReader)) {
			reader = new BufferedReader(reader);
		}
		try {
			XMLStreamReader xmlReader = getXMLFactory().createXMLStreamReader(reader);
			return new XmlInputReader(xmlReader, aggressiveParsing, throwsNoTagException);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}


	/** Create an XML (text or binary) output stream from the specified output stream
	 * @param output the output stream to create an XML output stream from
	 * @param doBuffer true to wrap the output stream in a buffered output stream,
	 * false to use it without modification
	 * @param charset the charset to use for textual data, if null the default US-ASCII charset is used.
	 * @return the XML output stream created from the output stream
	 */
	public static XmlOutput createXMLWriter(OutputStream output, boolean doBuffer, Charset charset) {
		if(charset == null) {
			charset = defaultCharset;
		}
		Writer writer = new OutputStreamWriter(output, charset);
		if(doBuffer == true && !(output instanceof BufferedOutputStream)) {
			writer = new BufferedWriter(writer);
		}
		return new XmlOutputWriter(writer, charset);
	}


	/** Create an XML (text or binary) output stream from the specified output writer
	 * @param writer the output writer to create an XML output stream from
	 * @param doBuffer true to wrap the output stream in a buffered output stream,
	 * false to use it without modification
	 * @param charset the charset to use for textual data, if null the default US-ASCII charset is used.
	 * @return the XML output stream created from the output stream
	 */
	public static XmlOutput createXMLWriter(Writer writer, boolean doBuffer, Charset charset) {
		if(charset == null) {
			charset = defaultCharset;
		}
		if(doBuffer == true && !(writer instanceof BufferedWriter)) {
			writer = new BufferedWriter(writer);
		}
		return new XmlOutputWriter(writer, charset);
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
	 * @param content the String to convert non-XML character to XML characters
	 * @return String with invalid XML characters replaced with XML character codes
	 */
	public static String validateElement(String content) {
		return StringEscapeXml.escapeXml(content);
	}


	/** Convert an XML string containing XML character codes (&amp; &apos; etc.) by replacing
	 * them with the corresponding character
	 * @param content the String to convert XML to non-XML characters (&amp; &quot; etc.)
	 * @return String with XML characters replaced with normal characters
	 */
	public static String convertElement(String content) {
		return StringEscapeXml.unescapeXml(content);
	}

}
