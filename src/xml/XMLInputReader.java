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
	//private Charset charset;
	private boolean parseAhead;
	private boolean noElementException;
	private boolean noTagException;
	private XMLTag lastOpeningTag;
	private XMLAttributes attributesStack;
	private XMLTag peekHeader;
	private XMLAttributes peekAttributesStack;


	/** An XML input stream parser
	 * It is recommended to encode strings using the {@link XMLHandler#STRING_TYPE} type
	 * to encode strings that may contain unicode characters.
	 * @param reader - the XML stream reader to read XML data from
	 * @param aggressiveParsing - <code>true</code> causes the parser to search more multiple elements when
	 * an element cannot be found, elements are then cached and used in future read operations.<br/>
	 * <code>false</code> causes the parser to only search one element regardless of whether the element
	 * contains the searched for matching tag name or not.
	 * @param throwsNoElementException - true causes parser to throw an exception if an element name cannot
	 * be found, false causes the parse to silently return null from any <code>readXXX</code> method.
	 * @param throwsNoTagException - true causes parser to throw an exception if an opening or closing tag
	 * cannot be found, false causes the parse to silently ignore the missing tag.
	 */
	public XMLInputReader(XMLStreamReader reader, boolean aggressiveParsing, boolean throwsNoElementException, boolean throwsNoTagException) throws FileNotFoundException {
		if(reader == null) {
			throw new IllegalArgumentException("XMLStreamReader cannot be null input parameters");
		}
		this.parseAhead = aggressiveParsing;
		this.noElementException = throwsNoElementException;
		this.noTagException = throwsNoTagException;
		this.xmlReader = reader;
		this.closingTagsSkipped = new ArrayList<XMLTag>();
		//this.charset = charset;
		this.tagStack = new ArrayList<String>();
		this.attributesStack = new XMLAttributes();
		this.peekAttributesStack = new XMLAttributes();
		this.tagsRead = 0;
	}


	public void readHeader() {
		// The XMLStreamReader passed to this constructor already ready the XML header
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
	 * @param name - the name of the opening XML tag to read
	 * @throws IOException if there is an IO or XML related error while reading from the input stream
	 */
	@Override
	public void readOpeningBlock(String name) throws IOException {
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
			return;
		}

		// Else read the next header as normal
		XMLTag newTag = readOpeningBlockVoid(name, false, xmlReader, attributesStack, closingTagsSkipped, parseAhead, noTagException);
		this.lastOpeningTag = newTag;
		this.tagStack.add(name);
		this.tagsRead++;
	}


	/** Read any opening XML tag and add a corresponding tag to this reader's internal list of open XML tags
	 * @return the name of the next XML opening tag read from the XML stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 * @throws XMLStreamException if there is a XML error while reading from the to input stream
	 */
	@Override
	public XMLTag readOpeningBlock() throws IOException {
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
		XMLTag newTag = readOpeningBlockVoid(null, false, xmlReader, attributesStack, closingTagsSkipped, false, false);
		this.lastOpeningTag = newTag;
		this.tagStack.add(newTag.getHeaderName());
		this.tagsRead++;
		return newTag;
	}


	/** Read a closing XML tag for the last read opening XML tag
	 * @throws IOException if there is an IO or XML related error reading from the input stream
	 */
	@Override
	public void readClosingBlock() throws IOException {
		readClosingBlockVoid(closingTagsSkipped);
	}


	/** Read an opening XML tag
	 * @param name the name of the tag to read
	 * @param peek true to add closing tags to the list of closing tags, false to read past closing tags
	 * @param reader the XML read to read the XML elements from
	 * @param attributes an empty attribute group to fill with attributes attached to the opening tag read
	 * @param closingTags a list of XML closing tags to add closing tags to if {@code peek} is true.
	 * @param doParseAhead true to parse past elements that don't match the specified name,
	 * false to stop at the first opening element encountered.
	 * @param throwIfNoTag throw an exception if the open tag's name does not match {@code name}
	 * @return the XML opening element/tag read
	 * @throws IOException if there is an exception reading from the XML input
	 * stream or if the tag name does not match.
	 */
	private static XMLTag readOpeningBlockVoid(String name, boolean peek, XMLStreamReader reader, XMLAttributes attributes, List<XMLTag> readClosingTags, boolean doParseAhead, boolean throwIfNoTag) throws IOException {
		String elementName = null;
		int attribCount = 0;

		try {
			int readTag = reader.next();
			// If the current element is a start or end element, initialize the elementName
			if(readTag == XMLStreamConstants.START_ELEMENT || readTag == XMLStreamConstants.END_ELEMENT) {
				elementName = reader.getLocalName(); // or .getName().getLocalPart();
				// Add any closing tags to the list of closing tags
				if(peek && readTag == XMLStreamConstants.END_ELEMENT) {
					readClosingTags.add(new XMLTagImpl(elementName, DataHeader.OPENING));
				}
			}
			// If we should only check one element at a time, then look for the next opening or closing element
			if(doParseAhead == false) {
				// Keep reading until the first opening or closing element is encountered
				while(readTag != XMLStreamConstants.START_ELEMENT && readTag != XMLStreamConstants.END_DOCUMENT) {
					readTag = reader.next();
					// Add any closing tags to the list of closing tags
					if(peek && readTag == XMLStreamConstants.END_ELEMENT) {
						readClosingTags.add(new XMLTagImpl(reader.getLocalName(), DataHeader.OPENING));
					}
				}
			}
			// If we should read more than one element in search of the name we are looking for
			else {
				// Reading until an opening or closing element with the name we are looking for is found
				while(!name.equals(elementName) && readTag != XMLStreamConstants.END_DOCUMENT) {
					readTag = reader.next();
					if(readTag == XMLStreamConstants.START_ELEMENT || readTag == XMLStreamConstants.END_ELEMENT) {
						elementName = reader.getLocalName(); // or .getName().getLocalPart();
						// Add any closing tags to the list of closing tags
						if(peek && readTag == XMLStreamConstants.END_ELEMENT) {
							readClosingTags.add(new XMLTagImpl(elementName, DataHeader.OPENING));
						}
					}
				}
			}

			// Get the element's name and attributes
			if(readTag == XMLStreamConstants.START_ELEMENT || readTag == XMLStreamConstants.END_ELEMENT) {
				elementName = reader.getLocalName(); // or .getName().getLocalPart();
				//System.out.println("XML reading tag: \"" + elementName + "\"");
				// Read any attributes attached to the opening element
				if(readTag == XMLStreamConstants.START_ELEMENT) {
					// Read any attributes attached to the opening tag
					attribCount = reader.getAttributeCount();
					if(attribCount > 0) {
						attributes.clear();
						for(int i = 0; i < attribCount; i++) {
							attributes.addAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
						}
					}
				}
			}
			// If the name should match the element name, then thrown an exception if they do not match
			if(throwIfNoTag && !name.equals(elementName)) {
				throw new XMLStreamException("Could not find '" + name + "' XML tag name, found '" + elementName + "' instead");
			}
		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}

		// Create the new element, or leave it null if no valid element name was read
		XMLTag newTag = null;
		if(elementName != null) {
			newTag = new XMLTagImpl(elementName, DataHeader.OPENING);
		}
		return newTag;
	}


	/**
	 * @param peek
	 * @param reader
	 * @param attributes
	 * @param readClosingTags
	 * @return the opening header tag read or null if an opening header could not be found
	 * @throws IOException
	 */
/*	private static XMLTag readOpeningBlockVoid_Old(boolean peek, XMLStreamReader reader, XMLAttributes attributes, List<XMLTag> readClosingTags) throws IOException {
		String chars = null;
		int attribCount = 0;

		try {
			int currentEvent = reader.next();
			// If the current element is a start or end element, initialize the elementName
			if(currentEvent == XMLStreamConstants.START_ELEMENT || currentEvent == XMLStreamConstants.END_ELEMENT) {
				chars = reader.getLocalName(); // or .getName().getLocalPart();
				// Add any closing tags to the list of closing tags
				if(peek && currentEvent == XMLStreamConstants.END_ELEMENT) {
					readClosingTags.add(new XMLTagImpl(chars));
				}
			}
			// Look for the next opening or closing element
			while(currentEvent != XMLStreamConstants.START_ELEMENT && currentEvent != XMLStreamConstants.END_DOCUMENT) {
				currentEvent = reader.next();
				// Add any closing tags to the list of closing tags
				if(peek && currentEvent == XMLStreamConstants.END_ELEMENT) {
					readClosingTags.add(new XMLTagImpl(reader.getLocalName()));
				}
			}

			// Check if the starting element's name matches the name we are looking for
			if(currentEvent == XMLStreamConstants.START_ELEMENT) {
				chars = reader.getLocalName(); // or .getName().getLocalPart();
				// Read any attributes attached to the opening tag
				attribCount = reader.getAttributeCount();
				if(attribCount > 0) {
					attributes.clear();
					for(int i = 0; i < attribCount; i++) {
						attributes.addAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
					}
				}
			}
		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}

		XMLTag newTag = null;
		if(chars != null) {
			newTag = new XMLTagImpl(chars);
		}
		return newTag;
	}*/


	private void readClosingBlockVoid(List<XMLTag> readClosingTags) throws IOException {
		List<String> tags = this.tagStack;
		String name = tags.get(tags.size()-1);
		String chars = null;
		XMLStreamReader reader = this.xmlReader;

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
					this.tagsRead++;
					return;
				}
			}
		}
		// Look for the closing element
		try {
			int readTag = reader.next();
			// If the current element is a start or end element, get the name of the element
			if(readTag == XMLStreamConstants.START_ELEMENT || readTag == XMLStreamConstants.END_ELEMENT) {
				chars = reader.getLocalName(); // or .getName().getLocalPart();
			}
			// If we should only check one element at a time, then just look for an ending element
			if(parseAhead == false) {
				while(readTag != XMLStreamConstants.END_ELEMENT && readTag != XMLStreamConstants.END_DOCUMENT) {
					readTag = reader.next();
				}
			}
			// If we should read more than one element in search of the name we are looking for
			else {
				// Search for the exact element name, reading and throwing away unkown elements if parseAhead == true
				while(!name.equals(chars) && readTag != XMLStreamConstants.END_DOCUMENT) {
					readTag = reader.next();
					if(readTag == XMLStreamConstants.START_ELEMENT || readTag == XMLStreamConstants.END_ELEMENT) {
						chars = reader.getLocalName(); // or .getName().getLocalPart();
					}
				}
			}

			// Check if the ending element's name matches the name we are looking for
			if(readTag == XMLStreamConstants.START_ELEMENT || readTag == XMLStreamConstants.END_ELEMENT) {
				chars = reader.getLocalName(); // or .getName().getLocalPart();
			}

			if(noTagException && !name.equals(chars)) {
				throw new IOException("Could not find '" + name + "' XML tag name, found '" + chars + "' instead");
			}
			tags.remove(tags.size()-1);
			this.tagsRead++;
		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}
		return;
	}


	@Override
	public XMLAttributes getCurrentHeaderBlockAttributes() {
		return this.attributesStack;
	}


	@Override
	public XMLTag getCurrentHeaderBlock() {
		return lastOpeningTag;
	}


	@Override
	public XMLTag peekNextHeaderBlock() throws IOException {
		/* If the peek head has not been read, read the next header and save it as the peek header.
		 * The code is required because {@link #readOpeningBlock()} reuses some
		 * and overwrites some of the current header variables so we save those
		 * values in temp variables and restore them after storing the new
		 * values in the peek header variables.
		 */
		if(peekHeader == null) {
			// Peek at the next header
			XMLTag newTag = readOpeningBlockVoid(null, true, xmlReader, attributesStack, closingTagsSkipped, false, false);
			peekHeader = newTag;
		}
		// Return the new peek header or the current peek header
		return peekHeader;
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
		String chars = null;
		String tagName = null;
		int attribCount = 0;

		try {
			int readTag = stream.next();
			// If the current element is a start element, get the name of the element
			if(readTag == XMLStreamConstants.START_ELEMENT) {
				tagName = stream.getLocalName(); // or .getName().getLocalPart();
				// Read any attributes attached to the opening tag
				attribCount = stream.getAttributeCount();
				if(attribCount > 0) {
					this.attributesStack.clear();
					for(int i = 0; i < attribCount; i++) {
						this.attributesStack.addAttribute(stream.getAttributeLocalName(i), stream.getAttributeValue(i));
					}
				}
			}
			if(readTag == XMLStreamConstants.CHARACTERS && !stream.isWhiteSpace()) {
				chars = stream.getText();
			}
			//int elementsSearched = 0;
			// If we should only check one element at a time, then just look for an ending element
			if(parseAhead == false) {
				while(!(name.equals(tagName) && readTag == XMLStreamConstants.END_ELEMENT) && readTag != XMLStreamConstants.END_ELEMENT && readTag != XMLStreamConstants.END_DOCUMENT) {
					readTag = stream.next();
					if(readTag == XMLStreamConstants.START_ELEMENT) {
						tagName = stream.getLocalName(); // or .getName().getLocalPart();
						// Read any attributes attached to the opening tag
						attribCount = stream.getAttributeCount();
						if(attribCount > 0) {
							this.attributesStack.clear();
							for(int i = 0; i < attribCount; i++) {
								this.attributesStack.addAttribute(stream.getAttributeLocalName(i), stream.getAttributeValue(i));
							}
						}
					}
					if(readTag == XMLStreamConstants.CHARACTERS && !stream.isWhiteSpace()) {
						chars = stream.getText();
					}
					//elementsSearched++;
				}
			}
			// If we should read more than one element in search of the element name we are looking for
			else {
				while(!(name.equals(tagName) && readTag == XMLStreamConstants.END_ELEMENT) && readTag != XMLStreamConstants.END_DOCUMENT) {
					readTag = stream.next();
					if(readTag == XMLStreamConstants.START_ELEMENT) {
						tagName = stream.getLocalName(); // or .getName().getLocalPart();
						// Read any attributes attached to the opening tag
						attribCount = stream.getAttributeCount();
						if(attribCount > 0) {
							this.attributesStack.clear();
							for(int i = 0; i < attribCount; i++) {
								this.attributesStack.addAttribute(stream.getAttributeLocalName(i), stream.getAttributeValue(i));
							}
						}
					}
					if(readTag == XMLStreamConstants.CHARACTERS) {
						chars = stream.getText();
					}
					//elementsSearched++;
				}
			}
			// If the element found does not match the element being searched for, throw an IOException, XMLStreamException
			if(noElementException && !name.equals(tagName)) {
				throw new IOException("Mismatch between XML element name '" + name + "' and found element name '" + tagName + "'");
			}
		} catch(XMLStreamException xmlse) {
			throw new IOException(xmlse);
		}
		// Return the text associated with the matching opening tag
		return chars;
	}


	/** Read a header from the specified XML input stream
	 * @param input the XML input stream to read the header from
	 */
	public static final void readHeader(XMLInputReader input) {
		input.readHeader();
	}

}
