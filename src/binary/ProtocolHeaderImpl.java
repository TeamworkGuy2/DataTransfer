package binary;

import dataTransfer.DataHeader;

/** A protocol header implementation that stores the integer ID of a protocol
 * header. 
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public class ProtocolHeaderImpl implements ProtocolHeader {
	private int tagId;
	private boolean openingHeader;

	/** Create a protocol header with the specified ID.
	 * @param tagId a protocol header ID between [0, Integer#MAX_VALUE]
	 * that represents the identifier of a protocol data block.
	 * @param openingHeader true if this header represents an opening data block,
	 * false if it represents a closing data block.
	 */
	public ProtocolHeaderImpl(int tagId, boolean openingHeader) {
		this.tagId = (tagId < 0 ? -1 : tagId);
		this.openingHeader = openingHeader;
	}


	@Override
	public boolean isOpeningHeader() {
		return openingHeader;
	}


	@Override
	public int getHeaderId() {
		return tagId;
	}


	@Override
	public String getHeaderName() {
		return null;
	}


	@Override
	public boolean equals(Object obj) {
		if((obj instanceof DataHeader)) {
			String headerName = ((DataHeader)obj).getHeaderName();
			int headerId = ((DataHeader)obj).getHeaderId();

			// Check if the header names match
			boolean nameMatch = /*(null != null) ? (null.equals(headerName)) : */(headerName == null);
			// Check if the header IDs match
			boolean idMatch = (tagId == headerId);

			// If the names, IDs, or both match and the remaining non matching field has at
			// least one empty value, then the headers are equal, see {@link DataHeader#equals(Object)}
			return (nameMatch || idMatch) && !(!nameMatch && tagId < 0) && !(!idMatch && null == null);
		}
		return false;
	}


	@Override
	public int hashCode() {
		return 0 ^ tagId;
	}

}
