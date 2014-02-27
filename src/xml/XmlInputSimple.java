package xml;

import java.io.Closeable;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/** A simple XML parser that processes opening/closing tags and elements.
 * To read an element (an opening tag with contents and a matching closing tag),
 * need an opening tag first then finish read the element based on the name of the element:<br/>
 * <code>
 * String name = readTag();<br/>
 * finishElement(name);
 * </code>
 * @author TeamworkGuy2
 * @since 2014-1-28
 */
public class XmlInputSimple implements Closeable {
	private XMLStreamReader xmlReader;
	private StringBuilder tempStrB = new StringBuilder();
	// This is used to indicate that the current tag and name have not been used and should be
	private boolean useCurrent;
	private String lastContents;
	private String currentTagName;
	private int currentTagType;


	public XmlInputSimple(XMLStreamReader reader) {
		this.xmlReader = reader;
	}


	public String readTag() throws XMLStreamException {
		if(useCurrent) {
			useCurrent = false;
			return currentTagName;
		}
		else {
			readTag(null, false);
		}
		return currentTagName;
	}


	public String readTag(String name) throws XMLStreamException {
		if(useCurrent) {
			useCurrent = false;
			return currentTagName;
		}
		else {
			readTag(null, false);
		}

		if(!currentTagName.equals(name)) {
			throw new IllegalStateException("read tag '" + currentTagName + "' does not equal: '" + name + "'");
		}

		return currentTagName;
	}


	/** Read an opening tag, its contents, and a closing tag
	 * @return true if a element's opening tag, contents, and closing tag were successfully read, false otherwise
	 * @throws XMLStreamException if there is an error reading the XML stream
	 */
	public boolean finishElement(String name) throws XMLStreamException {
		if(useCurrent && currentTagType != XMLStreamConstants.CHARACTERS) {
			return false;
		}

		// Read the element's characters
		lastContents = readCharacters();
		if(currentTagType != XMLStreamConstants.CHARACTERS) {
			useCurrent = true;
			return false;
		}

		// Read the element's closing tag
		String closingTag = readTag(null, false);
		if(currentTagType != XMLStreamConstants.END_ELEMENT) {
			// use the current tag/characters and discard the previous read characters because
			// they are not between an opening and closing tag
			useCurrent = true;
			return false;
		}

		// if characters are read followed by a closing element with the incorrect name, then it was
		// probably just two closing tags in a row, save it for the next read call
		if(!closingTag.equals(name)) {
			useCurrent = true;
			return false;
		}

		return true;
	}


	public boolean isLastTagOpening() {
		return currentTagType == XMLStreamConstants.START_ELEMENT;
	}


	public boolean isEmpty() {
		return currentTagType == XMLStreamConstants.END_DOCUMENT;
	}


	public String getLastTagName() {
		return currentTagName;
	}


	public String getLastElementContents() {
		return lastContents;
	}


	@Override
	public void close() {
		try {
			xmlReader.close();
		} catch (XMLStreamException e) {
			// Possible error closing XML stream
			e.printStackTrace();
		}
	}


	/** Read an XML tag
	 * @param name the name of the XML tag
	 * @param checkName true to compare the tag read to {@code name} and throw an exception if they do not match,
	 * false to not check if tag and name match
	 * @return the name of the tag read
	 * @throws XMLStreamException if there is an error reading from the XML stream
	 */
	private String readTag(String name, boolean checkName) throws XMLStreamException {
		int readTag = xmlReader.getEventType();

		while(readTag != XMLStreamConstants.START_ELEMENT
				&& readTag != XMLStreamConstants.END_ELEMENT
				&& readTag != XMLStreamConstants.END_DOCUMENT) {
			readTag = xmlReader.next();
		}

		String tagName = null;
		if(readTag == XMLStreamConstants.START_ELEMENT || readTag == XMLStreamConstants.END_ELEMENT) {
			tagName = xmlReader.getLocalName();
		}

		// Move to the next element, so the next operation is already on the next event
		if(readTag != XMLStreamConstants.END_DOCUMENT) {
			xmlReader.next();
		}

		this.currentTagName = tagName;
		this.currentTagType = readTag;
		return tagName;
	}


	/** Read an {@link XMLStreamConstants#CHARACTERS} string from an XML stream.
	 * @return the character data
	 * @throws XMLStreamException if there is an error reading from the XML stream
	 */
	private String readCharacters() throws XMLStreamException {
		int readTag = xmlReader.getEventType();

		while(readTag != XMLStreamConstants.CHARACTERS
				&& readTag != XMLStreamConstants.START_ELEMENT
				&& readTag != XMLStreamConstants.END_ELEMENT
				&& readTag != XMLStreamConstants.END_DOCUMENT) {
			readTag = xmlReader.next();
		}

		this.currentTagType = readTag;
		if(readTag == XMLStreamConstants.START_ELEMENT || readTag == XMLStreamConstants.END_ELEMENT) {
			this.currentTagName = xmlReader.getLocalName();
			useCurrent = true;
		}
		else if(readTag == XMLStreamConstants.CHARACTERS) {
			tempStrB.setLength(0);
			tempStrB.append(xmlReader.getText());

			// Read any additional characters up to the next element
			while(readTag != XMLStreamConstants.START_ELEMENT
					&& readTag != XMLStreamConstants.END_ELEMENT
					&& readTag != XMLStreamConstants.END_DOCUMENT) {
				readTag = xmlReader.next();
				if(readTag == XMLStreamConstants.CHARACTERS) {
					tempStrB.append(xmlReader.getText());
				}
			}

			this.lastContents = tempStrB.toString();
		}
		else if(readTag == XMLStreamConstants.END_DOCUMENT) {
			this.currentTagName = null;
		}

		// Move to the next element, so the next operation is already on the next event
		// this should normally not execute because of the above while loop that looks for any additional characters
		if(readTag != XMLStreamConstants.END_DOCUMENT
				&& readTag != XMLStreamConstants.START_ELEMENT
				&& readTag != XMLStreamConstants.END_ELEMENT) {
			xmlReader.next();
		}

		return lastContents;
	}

}
