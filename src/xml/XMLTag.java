package xml;

import dataTransfer.DataHeader;

/** An XML header object representing the opening XML tag for a {@link XMLable}
 * object.
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public interface XMLTag extends DataHeader {

	/**
	 * @return this XML header tag's name, or null if it does not have a name.
	 */
	public String getHeaderName();

	/**
	 * @return this XML header tag's ID between [0, Integer.MAX_VALUE], or -1 if
	 * it does not have an ID.
	 */
	public int getHeaderId();

}
