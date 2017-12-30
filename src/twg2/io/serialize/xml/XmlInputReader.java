package twg2.io.serialize.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import twg2.io.serialize.base.DataElement;
import twg2.io.serialize.base.DataElementImpl;
import twg2.io.serialize.base.ParsedElementType;
import twg2.io.serialize.base.reader.DataTransferInput;

/** {@link XmlInput} implementation for reading XML text data from an {@link XMLStreamReader}.
 * This class allows XML opening and closing tags to be read as well as strings and basic data types.
 * @author TeamworkGuy2
 * @since 2013-2-1
 */
public class XmlInputReader implements XmlInput, DataTransferInput {
	private static final int START_EL = XMLStreamConstants.START_ELEMENT;
	private static final int END_EL = XMLStreamConstants.END_ELEMENT;
	private static final int END_DOC = XMLStreamConstants.END_DOCUMENT;
	private List<String> tagStack;
	private int tagsRead;
	private XMLStreamReader xmlReader; // XML reader to read XML data from
	private boolean parseAhead;
	@SuppressWarnings("unused")
	private boolean noTagException;
	private DataElement lastOpeningTag;
	private XmlAttributes attributesStack;
	private DataElement peekHeader;
	private StringBuilder contentsBldr = new StringBuilder();
	private String cachedContents = null;


	/** An XML input stream parser
	 * @param reader the XML stream reader to read XML data from
	 * @param aggressiveParsing {@code true} causes the parser to search multiple elements when
	 * an element name cannot be found.<br/>
	 * {@code false} causes the parser to only search one element regardless of whether the element
	 * contains a matching tag name or not.
	 * @param throwsNoTagException true causes parser to throw an exception if an opening or closing tag
	 * cannot be found, false causes the parse to silently ignore the missing tag possibly causing some other
	 * error to be thrown.
	 */
	public XmlInputReader(XMLStreamReader reader, boolean aggressiveParsing, boolean throwsNoTagException) throws FileNotFoundException {
		if(reader == null) {
			throw new IllegalArgumentException("XMLStreamReader cannot be a null input parameter");
		}
		this.parseAhead = aggressiveParsing;
		this.noTagException = throwsNoTagException;
		this.xmlReader = reader;
		this.tagStack = new ArrayList<String>();
		this.attributesStack = new XmlAttributes();
		this.tagsRead = 0;
		readHeader();
	}


	/** Read the header element at the beginning of an XML document.
	 */
	public void readHeader() {
		if(xmlReader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
			try {
				xmlReader.next();
			} catch (XMLStreamException e) {
				System.err.println("Error reading START_DOCUMENT from XML stream");
				e.printStackTrace();
			}
		}
	}


	@Override
	public void read(String name, byte[] b) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		byte[] result = DatatypeConverter.parseBase64Binary(cachedContents);
		System.arraycopy(result, 0, b, 0, b.length);
		return;
	}


	@Override
	public void read(String name, byte[] b, int off, int len) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		byte[] result = DatatypeConverter.parseBase64Binary(cachedContents);
		System.arraycopy(result, 0, b, off, len);
		return;
	}


	@Override
	public boolean readBoolean(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		boolean result = Boolean.parseBoolean(cachedContents);
		return result;
	}


	@Override
	public byte readByte(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		byte result = (byte)cachedContents.charAt(0);
		return result;
	}


	@Override
	public char readChar(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		if(cachedContents.length() > 1) {
			throw new IllegalArgumentException("Expected 1 character XML data from element '" + name +
					"', but XML data was " + cachedContents.length() + " characters long");
		}
		char result = cachedContents.charAt(0);
		return result;
	}


	@Override
	public double readDouble(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		double result = Double.parseDouble(cachedContents);
		return result;
	}


	@Override
	public float readFloat(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		float result = Float.parseFloat(cachedContents);
		return result;
	}


	@Override
	public int readInt(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		int result = Integer.parseInt(cachedContents, 10);
		return result;
	}


	@Override
	public long readLong(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		long result = Long.parseLong(cachedContents, 10);
		return result;
	}


	@Override
	public short readShort(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		short result = Short.parseShort(cachedContents, 10);
		return result;
	}


	@Override
	public String readString(String name) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, parseAhead, name);
		return cachedContents;
	}


	@Override
	public void read(byte[] b) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		byte[] result = DatatypeConverter.parseBase64Binary(cachedContents);
		System.arraycopy(result, 0, b, 0, b.length);
		return;
	}


	@Override
	public void read(byte[] b, int off, int len) throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		byte[] result = DatatypeConverter.parseBase64Binary(cachedContents);
		System.arraycopy(result, 0, b, off, len);
		return;
	}


	@Override
	public boolean readBoolean() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		boolean result = Boolean.parseBoolean(cachedContents);
		return result;
	}


	@Override
	public byte readByte() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		byte result = (byte)cachedContents.charAt(0);
		return result;
	}


	@Override
	public char readChar() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		if(cachedContents.length() > 1) {
			throw new IllegalArgumentException("Expected 1 character XML data from element, but XML data was "
					+ cachedContents.length() + " characters long");
		}
		char result = cachedContents.charAt(0);
		return result;
	}


	@Override
	public double readDouble() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		double result = Double.parseDouble(cachedContents);
		return result;
	}


	@Override
	public float readFloat() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		float result = Float.parseFloat(cachedContents);
		return result;
	}


	@Override
	public int readInt() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		int result = Integer.parseInt(cachedContents, 10);
		return result;
	}


	@Override
	public long readLong() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		long result = Long.parseLong(cachedContents, 10);
		return result;
	}


	@Override
	public short readShort() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		short result = Short.parseShort(cachedContents, 10);
		return result;
	}


	@Override
	public String readString() throws IOException {
		next(ParsedElementType.ELEMENT, true, 0, false, null);
		return cachedContents;
	}


	/** Get the element name of the last read element.
	 * This only applies to element which were read using one of the parameterless read calls, such as:<br/>
	 * {@link #readByte()}, {@link #readFloat()}, {@link #readString()}, etc.
	 * @return the name of the last read element
	 */
	@Override
	public String getCurrentElementName() {
		return lastOpeningTag.getName();
	}


	@Override
	public DataElement readNext() throws IOException {
		return readStartBlock(true, 0, false, null);
	}


	@Override
	public DataElement readStartBlock(String name) throws IOException {
		return readStartBlock(false, START_EL, true, name);
	}


	/** Read an opening XML tag
	 * @param name the name of the opening XML tag to read
	 * @throws IOException if there is an IO or XML related error while reading from the input stream
	 */
	private DataElement readStartBlock(boolean readFirst, int elemType, boolean matchName, String name) throws IOException {
		DataElement xmlTag = null;
		if(peekHeader != null) {
			xmlTag = peekHeader;
			peekHeader = null;
		}
		else {
			try {
				xmlTag = nextElement(null, xmlReader, readFirst, elemType, matchName, name);
				lastOpeningTag = xmlTag;
			} catch(XMLStreamException e) {
				throw new IOException(e);
			}
		}
		if(xmlTag != null) {
			if(xmlTag.isStartBlock()) {
				tagStack.add(xmlTag.getName());
			}
			else if(xmlTag.isEndBlock()) {
				tagStack.remove(tagStack.size()-1);	
			}
		}
		return xmlTag;
	}


	/** Read a closing tag
	 * @throws IOException if there is an IO or data format related error while reading from the input stream
	 */
	@Override
	public void readEndBlock() throws IOException {
		DataElement xmlTag = null;
		if(peekHeader != null) {
			xmlTag = peekHeader;
			peekHeader = null;
		}
		else {
			try {
				String lastTag = tagStack.get(tagStack.size()-1);
				do {
					xmlTag = nextElement(null, xmlReader, false, END_EL, false, null);
				} while(xmlTag != null && !lastTag.equals(xmlTag.getName()));
				lastOpeningTag = xmlTag;
			} catch(XMLStreamException e) {
				throw new IOException(e);
			}
		}
		if(xmlTag == null || !tagStack.get(tagStack.size()-1).equals(xmlTag.getName())) {
			throw new IOException("expected closing tag name '" + tagStack.get(tagStack.size()-1) +
					"' found '" + (xmlTag != null ? xmlTag.getName() : "null") + "' instead");
		}
		tagStack.remove(tagStack.size()-1);
	}


	/** Read the next opening or closing tag and return it without modifying the stream.
	 * The next call to {@link #readStartBlock(String)}, {@link #readEndBlock()}
	 * or an equivalent method will return this tag.
	 * TODO: Note: due to ambiguity of empty elements in XML, an empty tag like {@code <element></element>}
	 * is read as a {@link ParsedElementType#HEADER} and {@link ParsedElementType#FOOTER FOOTER},
	 * not an {@link ParsedElementType#ELEMENT ELEMENT}. To read an empty element, call {@link #readString()}
	 * @return the tag read from the input stream
	 * @throws IOException if there is a format related error while reading from the to input stream
	 */
	@Override
	public DataElement peekNext() throws IOException {
		// read the next header and store it as the peek header or reuse the current peek header
		if(peekHeader == null) {
			peekHeader = next(null, true, 0, false, null);
		}
		// Return the new peek header or the current peek header
		return peekHeader;
	}


	private DataElement next(ParsedElementType typeHint, boolean readFirst, int elemType, boolean matchName, String name)
			throws IOException {
		DataElement xmlTag = null;
		if(peekHeader != null) {
			xmlTag = peekHeader;
			peekHeader = null;
			return xmlTag;
		}
		try {
			xmlTag = nextElement(typeHint, xmlReader, readFirst, elemType, matchName, name);
			lastOpeningTag = xmlTag;
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
		return xmlTag;
	}


	/** Read a starting blocks, elements, or ending blocks.<br>
	 * TODO: Note: due to ambiguity of empty elements in XML, an empty tag like {@code <element></element>}
	 * is read as a {@link ParsedElementType#HEADER} and {@link ParsedElementType#FOOTER FOOTER},
	 * not an {@link ParsedElementType#ELEMENT ELEMENT}. To read an empty element, call {@link #readString()}
	 * @param typeHint optional hint of the data type being read to circumvent the issue with empty elements being read as a header/footer pair
	 * @param reader the reader to read the XML data from
	 * @param readFirst true to read whatever element occurs first in the input stream
	 * @param elmType the type of element to read (only used if {@code readFirst} is false)
	 * @param matchName true to match {@code elementName} exactly, even if that requires skipping elements,
	 * false to read the first matching element
	 * @param elementName the name of the element to read, only applies if {@code matchName} is true
	 * @throws XMLStreamException
	 */
	private DataElement nextElement(ParsedElementType typeHint, XMLStreamReader reader, boolean readFirst, int elmType,
			boolean matchName, String elementName) throws XMLStreamException {
		int curTag = reader.getEventType();
		// read until an opening element is found
		if(readFirst) {
			while(reader.hasNext() && curTag != START_EL && curTag != END_EL && curTag != END_DOC) { curTag = reader.next(); }
		}
		// read until an opening element with the correct tag name is found
		else if(matchName) {
			while(reader.hasNext() && curTag != elmType && curTag != END_DOC &&
					(curTag != START_EL && curTag != END_EL || !elementName.equals(reader.getLocalName()))) {
				curTag = reader.next();
			}
		}
		// read the next matching element type regardless of element name
		else {
			while(reader.hasNext() && curTag != elmType && curTag != END_DOC) { curTag = reader.next(); }
		}

		if(curTag == END_DOC) {
			return null;
		}
		// if we are reading any first element, check the found tag validity and return
		if(readFirst) {
			if(curTag != START_EL && curTag != END_EL) {
				throw new IllegalStateException("could not find '" + XmlHandler.toString(START_EL) + " or " +
						XmlHandler.toString(END_EL) + "' element, found '" + XmlHandler.toString(curTag) + "' instead");
			}
			String tagName = reader.getLocalName();
			ParsedElementType type = null;
			if(curTag == START_EL) {
				readAttributes(xmlReader, attributesStack);

				ParsedElementType nextElementType = readContentsUntil(reader, contentsBldr);
				if(typeHint == ParsedElementType.ELEMENT && nextElementType == ParsedElementType.FOOTER) {
					nextElementType = ParsedElementType.ELEMENT;
				}
				cachedContents = XmlHandler.convertElement(contentsBldr.toString().trim());
				contentsBldr.setLength(0);

				if(nextElementType == ParsedElementType.ELEMENT) {
					readAttributes(xmlReader, attributesStack);
					type = ParsedElementType.ELEMENT;
					// move past element's end since we are processing an entire element
					reader.next();
				}
				else if(nextElementType == ParsedElementType.HEADER || nextElementType == ParsedElementType.FOOTER) {
					type = ParsedElementType.HEADER;
				}
				else {
					throw new AssertionError("unknown " + nextElementType.getClass() + ": " + nextElementType);
				}
			}
			if(curTag == END_EL) {
				type = ParsedElementType.FOOTER;
				// move past current end element now that has been processed
				reader.next();
			}
			return new DataElementImpl(tagName, -1, cachedContents, type);
		}
		// if we are reading a start block or element
		else if(elmType == START_EL) {
			if(curTag != START_EL) {
				throw new IllegalStateException("could not find '" + XmlHandler.toString(START_EL) +
						"' element, found '" + XmlHandler.toString(curTag) + "' instead");
			}
			// read any attributes on the element
			readAttributes(reader, attributesStack);
			String tagName = reader.getLocalName();

			ParsedElementType nextElementType = readContentsUntil(reader, contentsBldr);
			if(typeHint == ParsedElementType.ELEMENT && nextElementType == ParsedElementType.FOOTER) {
				nextElementType = ParsedElementType.ELEMENT;
			}
			cachedContents = XmlHandler.convertElement(contentsBldr.toString().trim());
			contentsBldr.setLength(0);

			if(nextElementType == ParsedElementType.HEADER || nextElementType == ParsedElementType.FOOTER) {
				return new DataElementImpl(tagName, -1, cachedContents, ParsedElementType.HEADER);
			}
			else if(nextElementType == ParsedElementType.ELEMENT) {
				// move past element's end since we are processing an entire element
				reader.next();
			}
			else {
				throw new AssertionError("unknown " + nextElementType.getClass() + ": " + nextElementType);
			}
			return new DataElementImpl(tagName, -1, cachedContents, ParsedElementType.ELEMENT);
		}
		// if we are reading a end block
		else {
			if(curTag != END_EL) {
				throw new IllegalStateException("could not find '" + XmlHandler.toString(elmType) +
						"' element, found '" + XmlHandler.toString(curTag) + "' instead");
			}
			String tagName = reader.getLocalName();
			cachedContents = null;
			reader.next();
			return new DataElementImpl(tagName, -1, cachedContents, ParsedElementType.FOOTER);
		}
	}


	/** Read any attributes attached to the current tag
	 * @param reader
	 * @param attribs
	 */
	private static void readAttributes(XMLStreamReader reader, XmlAttributes attribs) {
		if(reader.getEventType() != START_EL && reader.getEventType() != XMLStreamConstants.ATTRIBUTE) {
			return;
		}
		int attribCount = reader.getAttributeCount();
		if(attribCount == 0) {
			return;
		}

		attribs.clear();
		for(int i = 0; i < attribCount; i++) {
			attribs.addAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
		}
	}


	/** Read the contents of an element<br/>
	 * pre-condition: the {@code reader} is currently on a {@link XMLStreamConstants#START_ELEMENT} event.<br/>
	 * post-condition: the {@code reader} is on a {@code END_ELEMENT}, {@code END_DOCUMENT}, or {@code START_ELEMENT} event.
	 * @param reader the input stream to read XML parser events from
	 * @param dst the destination to store the element's contents in
	 * @return {@link ParsedElementType#HEADER HEADER} if the next element after the parsed contents is an starting block<br>
	 * {@link ParsedElementType#FOOTER FOOTER} if the next element is an ending block and there was no contents in the block<br>
	 * {@link ParsedElementType#ELEMENT ELEMENT} if the next element is an ending block and there was contents in the block
	 * @throws XMLStreamException if there was an error reading from the input stream
	 */
	private static ParsedElementType readContentsUntil(XMLStreamReader reader, StringBuilder dst)
			throws XMLStreamException {
		int curTag = reader.getEventType();
		int dstOffset = dst.length();
		while(reader.hasNext() && curTag != END_DOC && curTag != END_EL) {
			if(curTag == XMLStreamConstants.CHARACTERS && !reader.isWhiteSpace()) {
				dst.append(reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
			}
			curTag = reader.next();
			if(curTag == START_EL) {
				return ParsedElementType.HEADER;
			}
		}
		// The loop ends because the end of the element or document was found,
		// if the element contents is empty or only whitespace, assume the element was a block
		boolean isWhitespace = true;
		for(int i = dstOffset, size = dst.length(); i < size; i++) {
			if(!Character.isWhitespace(dst.charAt(i))) {
				isWhitespace = false;
			}
		}
		// blocks are higher precedence than elements
		if(isWhitespace) {
			return ParsedElementType.FOOTER;
		}
		return ParsedElementType.ELEMENT;
	}


	@Override
	public XmlAttributes getCurrentElementAttributes() {
		return this.attributesStack;
	}


	@Override
	public DataElement getCurrentElement() {
		return lastOpeningTag;
	}


	@Override
	public String getCurrentName() {
		return lastOpeningTag.getName();
	}


	@Override
	public int getBlocksRead() {
		return this.tagsRead;
	}


	/** Closes this converter's XML input stream if possible.
	 */
	@Override
	public void close() throws IOException {
		this.tagStack.clear();
		this.attributesStack.clear();
		this.tagsRead = -1;
		if(this.xmlReader != null) {
			try {
				this.xmlReader.close();
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}
		this.tagStack = null;
		this.attributesStack = null;
	}


	/** Does not close this convert's XML input stream.
	 * Sets object references to null.
	 */
	@Override
	public void clear() {
		this.tagStack.clear();
		this.attributesStack.clear();
		this.tagsRead = -1;
		this.tagStack = null;
		this.attributesStack = null;
		this.xmlReader = null;
	}

}
