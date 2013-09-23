package xml;

import dataTransfer.DataHeader;

/** A XML tag header implementation that stores only the name of an XML tag.
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public class XMLTagImpl implements XMLTag {
	private String tagName;
	private String descriptor;
	private boolean openingHeader;

	/** Create an XML tag block
	 * @param tagName the name of the parent XML tag surrounding the XML
	 * data block that is being described by this XML tag.
	 * @param descriptor an optional descriptor for this XML tag, or null if
	 * it does not have a descriptor.
	 * @param openingHeader true if this header represents an opening tag,
	 * false if it represents a closing header.
	 */
	public XMLTagImpl(String tagName, String descriptor, boolean openingHeader) {
		this.tagName = tagName;
		this.descriptor = descriptor;
		this.openingHeader = openingHeader;
	}


	@Override
	public boolean isOpeningHeader() {
		return openingHeader;
	}


	@Override
	public String getHeaderName() {
		return tagName;
	}


	@Override
	public int getHeaderId() {
		return -1;
	}


	@Override
	public String getDescriptor() {
		return descriptor;
	}


	@Override
	public boolean equals(Object obj) {
		if((obj instanceof DataHeader)) {
			String headerName = ((DataHeader)obj).getHeaderName();
			int headerId = ((DataHeader)obj).getHeaderId();

			// Check if the header names match
			boolean nameMatch = (tagName != null) ? (tagName.equals(headerName)) : (headerName == null);
			// Check if the header IDs match
			boolean idMatch = (-1 == headerId);

			// If the names, IDs, or both match and the remaining non matching field has at
			// least one empty value, then the headers are equal, see {@link DataHeader#equals(Object)}
			return (nameMatch || idMatch) && !(!nameMatch && -1 < 0) && !(!idMatch && tagName == null);
		}
		return false;
	}


	@Override
	public int hashCode() {
		return (tagName != null ? tagName.hashCode() : 0) ^ -1;
	}

}
