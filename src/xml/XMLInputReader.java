package xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import dataTransfer.DataHeader;

/** {@link XMLInput} implementation for reading XML text data from an {@link XMLStreamReader}.
 * This class allows XML opening and closing tags to be read as well as strings and basic data types.
 * @author TeamworkGuy2
 * @since 2013-2-1
 */
public class XMLInputReader implements XMLInput {
	private List<String> tagStack;
	private int tagsRead;
	private XMLStreamReader xmlReader; // XML reader to read XML data from
	private List<XMLTag> closingTagsSkipped;
	private String[] tempElement;
	private boolean parseAhead;
	private boolean noTagException;
	private XMLTag lastOpeningTag;
	private XMLAttributes attributesStack;
	private XMLTag peekHeader;
	private XMLAttributes peekAttributesStack;


	/** An XML input stream parser
	 * It is recommended to encode strings using the {@link XMLHandler#STRING_TYPE} type
	 * to encode strings that may contain unicode characters.
	 * @param reader the XML stream reader to read XML data from
	 * @param aggressiveParsing <code>true</code> causes the parser to search multiple elements when
	 * an element name cannot be found.<br/>
	 * <code>false</code> causes the parser to only search one element regardless of whether the element
	 * contains the searched for matching tag name or not.
	 * @param throwsNoTagException true causes parser to throw an exception if an opening or closing tag
	 * cannot be found, false causes the parse to silently ignore the missing tag possibly causing some other
	 * error to be thrown.
	 */
	public XMLInputReader(XMLStreamReader reader, boolean aggressiveParsing, boolean throwsNoTagException) throws FileNotFoundException {
		if(reader == null) {
			throw new IllegalArgumentException("XMLStreamReader cannot be a null input parameter");
		}
		this.tempElement = new String[2];
		this.parseAhead = aggressiveParsing;
		this.noTagException = throwsNoTagException;
		this.xmlReader = reader;
		this.closingTagsSkipped = new ArrayList<XMLTag>();
		this.tagStack = new ArrayList<String>();
		this.attributesStack = new XMLAttributes();
		this.peekAttributesStack = new XMLAttributes();
		this.tagsRead = 0;
		readHeader();
	}


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
		String text = readElement(xmlReader, name);
		byte[] result = DatatypeConverter.parseBase64Binary(text);
		System.arraycopy(result, 0, b, 0, b.length);
		return;
	}


	@Override
	public void read(String name, byte[] b, int off, int len) throws IOException {
		String text = readElement(xmlReader, name);
		byte[] result = DatatypeConverter.parseBase64Binary(text);
		System.arraycopy(result, 0, b, off, len);
		return;
	}


	@Override
	public boolean readBoolean(String name) throws IOException {
		String text = readElement(xmlReader, name);
		boolean result = Boolean.parseBoolean(text);
		return result;
	}


	@Override
	public byte readByte(String name) throws IOException {
		String text = readElement(xmlReader, name);
		byte result = (byte)text.charAt(0);
		return result;
	}


	@Override
	public char readChar(String name) throws IOException {
		String text = readElement(xmlReader, name);
		if(text.length() > 1) {
			throw new IllegalArgumentException("Expected 1 character XML data from element '" + name +
					"', but XML data was " + text.length() + " characters long");
		}
		char result = text.charAt(0);
		return result;
	}


	@Override
	public double readDouble(String name) throws IOException {
		String text = readElement(xmlReader, name);
		double result = Double.parseDouble(text);
		return result;
	}


	@Override
	public float readFloat(String name) throws IOException {
		String text = readElement(xmlReader, name);
		float result = Float.parseFloat(text);
		return result;
	}


	@Override
	public int readInt(String name) throws IOException {
		String text = readElement(xmlReader, name);
		int result = Integer.parseInt(text, 10);
		return result;
	}


	@Override
	public long readLong(String name) throws IOException {
		String text = readElement(xmlReader, name);
		long result = Long.parseLong(text, 10);
		return result;
	}


	@Override
	public short readShort(String name) throws IOException {
		String text = readElement(xmlReader, name);
		short result = Short.parseShort(text, 10);
		return result;
	}


	@Override
	public String readUTF(String name) throws IOException {
		String text = readElement(xmlReader, name);
		return text;
	}


	/** Read an opening XML tag and add a corresponding tag to this reader's internal list of open XML tags
	 * @param name the name of the opening XML tag to read
	 * @throws IOException if there is an IO or XML related error while reading from the input stream
	 */
	@Override
	public XMLTag readOpeningBlock(String name) throws IOException {
		// If the peek header has been read (i.e. the head in front of the current head has read)
		// then use the peek header as the next header
		if(this.peekHeader != null) {
			XMLAttributes tempAttributes = this.attributesStack;
			this.attributesStack = this.peekAttributesStack;
			tempAttributes.clear();
			this.peekAttributesStack = tempAttributes;
			this.tagStack.add(peekHeader.getHeaderName());
			this.tagsRead++;
			this.lastOpeningTag = this.peekHeader;
			this.peekHeader = null;
			return this.lastOpeningTag;
		}

		// Else read the next header as normal
		// This call reads the next header with a matching name, this may read past valuable elements
		XMLTag newTag = readOpeningBlockGreedy(xmlReader, name, false, attributesStack, closingTagsSkipped);
		this.lastOpeningTag = newTag;
		if(newTag != null) {
			this.tagStack.add(newTag.getHeaderName());
			this.tagsRead++;
		}

		// If the name should match the element name, then thrown an exception if they do not match
		if(noTagException && !name.equals(newTag.getHeaderName())) {
			throw new IOException("Could not find '" + name + "' XML tag name, found '" + newTag.getHeaderName() + "' instead");
		}

		return newTag;
	}


	/** Read any opening XML tag and add a corresponding tag to this reader's internal list of open XML tags
	 * @return the next opening XML tag read from the XML stream or null if the end of the document has been reached
	 * @throws IOException if there is an IO error while reading from the input stream
	 * @throws XMLStreamException if there is a XML error while reading from the to input stream
	 */
	@Override
	public XMLTag readNextBlock() throws IOException {
		// If the peek header has been read (i.e. the head in front of the current head has read)
		// then use the peek header as the next header
		if(this.peekHeader != null) {
			XMLAttributes tempAttributes = this.attributesStack;
			this.attributesStack = this.peekAttributesStack;
			tempAttributes.clear();
			this.peekAttributesStack = tempAttributes;
			this.tagStack.add(peekHeader.getHeaderName());
			this.tagsRead++;
			this.lastOpeningTag = this.peekHeader;
			this.peekHeader = null;
			return this.lastOpeningTag;
		}

		// Else read the next header as normal
		// This call reads the next immediate header without checking its name
		XMLTag newTag = readOpeningBlockExact(xmlReader, false, attributesStack, closingTagsSkipped);
		this.lastOpeningTag = newTag;
		if(newTag != null) {
			this.tagStack.add(newTag.getHeaderName());
			this.tagsRead++;
		}
		return newTag;
	}


	@Override
	public XMLTag peekNextBlock() throws IOException {
		/* If the peek head has not been read, read the next header and save it as the peek header.
		 * The code is required because {@link #readOpeningBlock()} reuses some
		 * and overwrites some of the current header variables so we save those
		 * values in temp variables and restore them after storing the new
		 * values in the peek header variables.
		 */
		if(peekHeader == null) {
			// Peek at the next header
			XMLTag newTag = readOpeningBlockExact(xmlReader, true, attributesStack, closingTagsSkipped);
			peekHeader = newTag;
		}
		// Return the new peek header or the current peek header
		return peekHeader;
	}


	/** Read a closing XML tag for the last read opening XML tag
	 * @throws IOException if there is an IO or XML related error reading from the input stream
	 */
	@Override
	public void readClosingBlock() throws IOException {
		// This method call reads until a matching closing tag is found, this may read past valuable elements
		int read = readClosingBlockGreedy(xmlReader, tagStack, closingTagsSkipped, noTagException);
		tagsRead += read;
	}


	@Override
	public XMLAttributes getCurrentBlockHeaderAttributes() {
		return this.attributesStack;
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


	/** Closes this converter's XML input stream if possible.
	 */
	@Override
	public void close() throws IOException {
		this.tagStack.clear();
		this.attributesStack.clear();
		this.tagsRead = -1;
		try {
			this.xmlReader.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
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


	/** Read the specified element from the specified XML stream.<br/>
	 * This method reads one element if the constructor's <code>aggressiveParsing</code> parameter was false.
	 * Else this method reads as many elements as possible until the correct element name is found or the end of
	 * the document is reached.
	 * @param stream - the stream to read the element from
	 * @param name - the name of the element to read and return
	 * @return the character data contained in the specified element
	 * @throws XMLStreamIOException, XMLStreamException if there is an XML error while reading the data
	 * @throws IOException, XMLStreamException if there is an error while parsing the XML data
	 */
	private String readElement(XMLStreamReader stream, String name) throws IOException {
		// Read the next element
		readElement(stream, tempElement, name, attributesStack);
		// If reading ahead is enabled, read and discard elements until a matching element is found
		if(parseAhead) {
			while(!name.equals(tempElement[0]) && stream.getEventType() != XMLStreamConstants.END_DOCUMENT) {
				readElement(stream, tempElement, name, attributesStack);
			}
		}
		// If the element found does not match the element being searched for, throw an IOException, XMLStreamException
		if(!name.equals(tempElement[0])) {
			throw new IOException("Mismatch between XML element name '" + name + "' and found element name '" + tempElement[0] + "'");
		}
		return tempElement[1];
	}


	/** Read the specified element from the specified XML stream.<br/>
	 * This method reads one element if the constructor's <code>aggressiveParsing</code> parameter was false.
	 * Else this method reads as many elements as possible until the correct element name is found or the end of
	 * the document is reached.
	 * @param an array of size 2 to put the name and contents of the element read into
	 * @param element the name of the element to read and return
	 * @param debugName the expected name of the element for error messages, null represents any name
	 * @return the character data contained in the specified element
	 * @throws XMLStreamIOException if there is an XML error while reading the data
	 * @throws IOException if there is an error while parsing the XML data
	 */
	private static void readElement(XMLStreamReader stream, String[] element, String debugName, XMLAttributes elementAttribs) throws IOException {
		String chars = null;
		String tagName = null;
		int attribCount = 0;

		// Read a start element, the contents of the element, and an end element
		try {
			int readTag = stream.getEventType();

			// Read any character events (corresponding to whitespace between elements? is this assumption correct)
			while(readTag == XMLStreamConstants.CHARACTERS) {
				readTag = stream.next();
			}

			if(readTag != XMLStreamConstants.START_ELEMENT) {
				throw new IllegalStateException("Mismatching XML tag read, expected START_ELEMENT: " + debugName + ", found " + XMLHandler.toString(readTag) + ((readTag == XMLStreamConstants.END_ELEMENT) ? ": " + stream.getLocalName() : ""));
			}

			tagName = stream.getLocalName(); // or .getName().getLocalPart();
			// Read any attributes attached to the opening tag
			attribCount = stream.getAttributeCount();
			if(attribCount > 0) {
				elementAttribs.clear();
				for(int i = 0; i < attribCount; i++) {
					elementAttribs.addAttribute(stream.getAttributeLocalName(i), stream.getAttributeValue(i));
				}
			}
			// Move into the element and read it's contents
			readTag = stream.next();

			if(readTag == XMLStreamConstants.CHARACTERS && !stream.isWhiteSpace()) {
				chars = stream.getText();
			}

			// Read one element at a time, then just look for an ending element
			while(readTag != XMLStreamConstants.END_ELEMENT && readTag != XMLStreamConstants.END_DOCUMENT) {
				readTag = stream.next();
				if(readTag == XMLStreamConstants.START_ELEMENT) {
					tagName = stream.getLocalName(); // or .getName().getLocalPart();
					throw new IllegalStateException("Found start of tag '" + tagName + "' while reading end of element");
				}
				if(readTag == XMLStreamConstants.CHARACTERS && !stream.isWhiteSpace()) {
					chars = stream.getText();
				}
			}

			if(readTag != XMLStreamConstants.END_ELEMENT) {
				throw new IllegalStateException("Mismatching XML tag read, expected END_ELEMENT, found " + XMLHandler.toString(readTag) + ((readTag == XMLStreamConstants.START_ELEMENT) ? ": " + stream.getLocalName() : ""));
			}

			// Move to the next element once we read the starting element
			stream.next();

		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}
		// Return the text associated with the matching opening tag
		element[0] = tagName;
		element[1] = chars;
	}


	/** Read an opening XML tag
	 * @param reader the XML stream reader to read data from
	 * @param peek true to add closing tags to the list of closing tags, false to read past closing tags
	 * @param reader the XML read to read the XML elements from
	 * @param attributes an empty attribute group to fill with attributes attached to the opening tag read
	 * @param closingTags a list of XML closing tags to add closing tags to if {@code peek} is true.
	 * false to stop at the first opening element encountered.
	 * @return the XML opening element/tag read
	 * @throws IOException if there is an exception reading from the XML input
	 * stream or if the tag name does not match.
	 */
	private static XMLTag readOpeningBlockExact(XMLStreamReader reader, boolean peek, XMLAttributes attributes, List<XMLTag> readClosingTags) throws IOException {
		String elementName = null;
		String descriptor = null;
		int attribCount = 0;

		try {
			int readTag = reader.getEventType();

			// Read any character events (corresponding to whitespace between elements? is this assumption correct)
			while(readTag == XMLStreamConstants.CHARACTERS) {
				readTag = reader.next();
			}

			// Get the element's name and attributes
			if(readTag != XMLStreamConstants.START_ELEMENT) {
				throw new IllegalStateException("Mismatching XML tag read, expected START_ELEMENT, found " + XMLHandler.toString(readTag) + ((readTag == XMLStreamConstants.END_ELEMENT) ? ": " + reader.getLocalName() : ""));
			}

			elementName = reader.getLocalName(); // or .getName().getLocalPart();
			// Read any attributes attached to the opening tag
			attribCount = reader.getAttributeCount();
			if(attribCount > 0) {
				boolean foundDescriptor = false;
				attributes.clear();
				for(int i = 0; i < attribCount; i++) {
					// If the attributes contain the tag's descriptor/name, read it
					if(!foundDescriptor && XMLHandler.DESCRIPTOR_ID.equals(reader.getAttributeLocalName(i))) {
						foundDescriptor = true;
						descriptor = reader.getAttributeValue(i);
					}
					// Else just read a normal attribute
					else {
						attributes.addAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
					}
				}
			}
			// Move to the next element once we read the starting element
			reader.next();

		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}

		// Create the new element, or leave it null if no valid element name was read
		XMLTag newTag = null;
		if(elementName != null) {
			newTag = new XMLTagImpl(elementName, descriptor, DataHeader.OPENING);
		}
		return newTag;
	}


	/** Read an opening XML tag. This method reads past non matching tags until the correct one is found.
	 * @param reader the XML stream reader to read data from
	 * @param name the name of the tag to read
	 * @param peek true to add closing tags to the list of closing tags, false to read past closing tags
	 * @param reader the XML read to read the XML elements from
	 * @param attributes an empty attribute group to fill with attributes attached to the opening tag read
	 * @param closingTags a list of XML closing tags to add closing tags to if {@code peek} is true.
	 * false to stop at the first opening element encountered.
	 * @return the XML opening element/tag read
	 * @throws IOException if there is an exception reading from the XML input
	 * stream or if the tag name does not match.
	 */
	private static XMLTag readOpeningBlockGreedy(XMLStreamReader reader, String name, boolean peek, XMLAttributes attributes, List<XMLTag> readClosingTags) throws IOException {
		String elementName = null;
		String descriptor = null;
		int attribCount = 0;

		try {
			int readTag = reader.getEventType();

			// Read any character events (corresponding to whitespace between elements? is this assumption correct)
			if(readTag == XMLStreamConstants.START_ELEMENT) {
				elementName = reader.getLocalName();
			}

			// Read ahead, discarding elements until the correct element name is found
			while(!(readTag == XMLStreamConstants.START_ELEMENT && name.equals(elementName)) && readTag != XMLStreamConstants.END_DOCUMENT) {
				readTag = reader.next();
				if(readTag == XMLStreamConstants.START_ELEMENT) {
					elementName = reader.getLocalName();
				}
			}

			// Read any attributes attached to the opening tag
			attribCount = reader.getAttributeCount();
			if(attribCount > 0) {
				boolean foundDescriptor = false;
				attributes.clear();
				for(int i = 0; i < attribCount; i++) {
					// If the attributes contain the tag's descriptor/name, read it
					if(!foundDescriptor && XMLHandler.DESCRIPTOR_ID.equals(reader.getAttributeLocalName(i))) {
						foundDescriptor = true;
						descriptor = reader.getAttributeValue(i);
					}
					// Else just read a normal attribute
					else {
						attributes.addAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
					}
				}
			}
			// Move to the next element once we read the starting element
			reader.next();

		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}

		// Create the new element, or leave it null if no valid element name was read
		XMLTag newTag = null;
		if(elementName != null) {
			newTag = new XMLTagImpl(elementName, descriptor, DataHeader.OPENING);
		}
		return newTag;
	}


	/** Read a closing tag including any elements between the current element and the closing tag.
	 * @param reader the XML reader to read closing tags from
	 * @param tags the list of currently open tags to adjust based on whether a closing tag was found
	 * to match the last currently open tag on the stack (last index of list)
	 * @param readClosingTags a list of currently open tags
	 * @param throwNoTag true to throw an exception if the expected closing tag
	 * name taken from the top of the {@link tags} stack did not match the
	 * closing tag name read from the XML stream, false to ignore the
	 * name of the closing tag read.
	 * @throws IOException if there is an error reading the closing tag
	 */
	private static int readClosingBlockGreedy(XMLStreamReader reader, List<String> tags, List<XMLTag> readClosingTags, boolean throwNoTag) throws IOException {
		String name = tags.get(tags.size()-1);
		String chars = null;

		//System.out.println("XML closing tag: " + name);

		// Check if any of the closing tags read by {@link #peekNextHeaderBlock()}
		// match the current closing tag being read
		int closingTagCount = readClosingTags.size();
		if(closingTagCount > 0) {
			for(int i = 0; i < closingTagCount; i++) {
				if(readClosingTags.get(i).getHeaderName().equals(name)) {
					readClosingTags.remove(i);
					tags.remove(tags.size()-1);
					//System.out.println("XML closing peek tag: " + name);
					return 1;
				}
			}
		}
		// Read ahead for the closing tag, keep reading until the correct closing tag is found,
		// possibly discarding valuable elements
		try {
			int readTag = reader.getEventType();

			while(!(readTag == XMLStreamConstants.END_ELEMENT && name.equals(chars)) && readTag != XMLStreamConstants.END_DOCUMENT) {
				readTag = reader.next();
				if(readTag == XMLStreamConstants.END_ELEMENT) {
					chars = reader.getLocalName(); // or .getName().getLocalPart();
				}
			}

			// Check if the next element is an ending element, if so, get the name of the element
			if(readTag != XMLStreamConstants.END_ELEMENT) {
				throw new IllegalStateException("Mismatching XML tag read, expected END_ELEMENT, found " + XMLHandler.toString(readTag));
			}

			chars = reader.getLocalName(); // or .getName().getLocalPart();
			// Move to the next element once we read the ending element
			reader.next();

			if(throwNoTag && !name.equals(chars)) {
				throw new IOException("Could not find '" + name + "' XML tag name, found '" + chars + "' instead");
			}
			tags.remove(tags.size()-1);
		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}
		return 1;
	}


	/** Read a header from the specified XML input stream
	 * @param input the XML input stream to read the header from
	 */
	@Deprecated
	public static final void readHeader(XMLInputReader input) {
		input.readHeader();
	}

}
