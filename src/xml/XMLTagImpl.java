package xml;

import dataTransfer.DataHeader;

/** A XML tag header implementation that stores only the name of an XML tag.
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public class XMLTagImpl implements XMLTag {
	private String openingTag;

	/** Create an XML tag block
	 * @param openingTag the name of the parent XML tag surrounding the XML
	 * data block that is being described by this XML tag.
	 */
	public XMLTagImpl(String openingTag) {
		this.openingTag = openingTag;
	}


	@Override
	public int getHeaderId() {
		return -1;
	}


	@Override
	public String getHeaderName() {
		return openingTag;
	}


	@Override
	public boolean equals(Object obj) {
		boolean result = (obj instanceof DataHeader);
		if(result == true) {
			DataHeader header = (DataHeader)obj;
			// Check if the header names match
			if(openingTag != null) {
				result |= openingTag.equals(header.getHeaderName());
			}
			else {
				result |= (header.getHeaderName() == null);
			}
			// Check if this header has the same ID as the other header
			result |= (-1 == header.getHeaderId());
		}
		return result;
	}


	@Override
	public int hashCode() {
		return (openingTag != null ? openingTag.hashCode() : 0) ^ -1;
	}

}
