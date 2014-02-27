package xml;

import dataTransfer.DataHeader;

/** An XML header object representing the opening XML tag for a {@link Xmlable} object.
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public interface XmlTag extends DataHeader {

	/**
	 * @return this XML header tag's name
	 */
	@Override
	public String getHeaderName();


	/**
	 * @return this XML header tag's ID between [0, Integer.MAX_VALUE], or -1 if
	 * it does not have an ID.
	 */
	@Override
	public int getHeaderId();


	@Override
	public String getDescriptor();

}
