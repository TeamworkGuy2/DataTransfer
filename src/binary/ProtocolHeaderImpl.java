package binary;

import dataTransfer.DataHeader;

/** A protocol header implementation that stores the integer ID of a protocol
 * header. 
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public class ProtocolHeaderImpl implements ProtocolHeader {
	private int tagId;

	/** Create a protocol header with the specified ID.
	 * @param tagId a protocol header ID between [0, Integer#MAX_VALUE]
	 * that represents the identifier of a protocol data block.
	 */
	public ProtocolHeaderImpl(int tagId) {
		this.tagId = (tagId < 0 ? -1 : tagId);
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
		boolean result = false;
		if((obj instanceof DataHeader)) {
			DataHeader header = (DataHeader)obj;
			// Check if the other header name is also null like this header
			result |= (header.getHeaderName() == null);
			// Check if this header has the same ID as the other header
			result |= (tagId == header.getHeaderId());
		}
		return result;
	}


	@Override
	public int hashCode() {
		return 0 ^ tagId;
	}

}
