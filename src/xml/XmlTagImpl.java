package xml;

import base.DataElement;
import base.ParsedElementType;

/** A XML tag header implementation that stores only the name of an XML tag.
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public class XmlTagImpl implements XmlTag {
	private String tagName;
	private String content;
	private ParsedElementType elementType;


	/** Create an XML tag block
	 * @param tagName the name of the parent XML tag surrounding the XML
	 * data block that is being described by this XML tag.
	 * @param content the content of this element
	 * @param type the type of this tag
	 */
	public XmlTagImpl(String tagName, String content, ParsedElementType type) {
		this.tagName = tagName;
		this.content = content;
		this.elementType = type;
	}


	/** Create an XML tag block
	 * @param tagName the name of the parent XML tag surrounding the XML
	 * data block that is being described by this XML tag.
	 * @param content the content of this element
	 * @param type the type of this tag
	 * @param type2 an alternative type that this tag might be (due to ambiguity
	 * between empty element and empty block tags in XML)
	 */
	public XmlTagImpl(String tagName, String content, ParsedElementType type, ParsedElementType type2) {
		this.tagName = tagName;
		this.content = content;
		this.elementType = type;
	}


	@Override
	public boolean isStartBlock() {
		return elementType == ParsedElementType.HEADER;
	}


	@Override
	public boolean isEndBlock() {
		return elementType == ParsedElementType.FOOTER;
	}


	@Override
	public boolean isElement() {
		return elementType == ParsedElementType.ELEMENT;
	}


	@Override
	public String getName() {
		return tagName;
	}


	@Override
	public String getContent() {
		return content;
	}


	@Override
	public int getId() {
		return -1;
	}


	@Override
	public boolean equals(Object obj) {
		if((obj instanceof DataElement)) {
			String headerName = ((DataElement)obj).getName();
			int headerId = ((DataElement)obj).getId();

			// Check if the header names match
			boolean nameMatch = (tagName != null) ? (tagName.equals(headerName)) : (headerName == null);
			// Check if the header IDs match
			boolean idMatch = (-1 == headerId);

			// If the names, IDs, or both match and the remaining non matching field has at
			// least one empty value, then the headers are equal, see {@link DataElement#equals(Object)}
			return (nameMatch || idMatch) && !(!nameMatch && -1 < 0) && !(!idMatch && tagName == null);
		}
		return false;
	}


	@Override
	public int hashCode() {
		return (tagName != null ? tagName.hashCode() : 0) ^ -1;
	}

}
