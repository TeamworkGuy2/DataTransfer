package binary;

import dataTransfer.DataHeader;

/** A transfer protocol header representing the opening header for a
 * {@link ProtocolTransferable} object.
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public interface ProtocolHeader extends DataHeader {

	/**
	 * @return this protocol block's header name, or null if it does not have a name.
	 */
	public String getHeaderName();

	/**
	 * @return this protocol block's header ID between [0, Integer.MAX_VALUE], or -1 if
	 * it does not have an ID.
	 */
	public int getHeaderId();

}
