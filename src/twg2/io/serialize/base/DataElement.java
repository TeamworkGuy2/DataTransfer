package twg2.io.serialize.base;

import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;

/** A header for a chunk of string data.
 * Used by {@link DataTransferInput} and {@link DataTransferOutput}.
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public interface DataElement {

	/**
	 * @return true if this header is the start of a block, false if not.
	 */
	public boolean isStartBlock();


	/**
	 * @return true if this header is the end of a block, false if not.
	 */
	public boolean isEndBlock();


	/**
	 * @return true if this header is an element, false if not.
	 */
	public boolean isElement();


	/**
	 * @return this data header's name, or null if it does not have a name.
	 */
	public String getName();


	/**
	 * @return this data header's contents, or null if it does not have content.
	 * For example, if it is not an {link ParsedElementType#ELEMENT}
	 */
	public String getContent();


	/**
	 * @return this data header's type ID between [0, Integer.MAX_VALUE], or -1
	 * if it does not have an ID.
	 */
	public int getId();


	/** Check if two data headers are equal. Two data headers are equal if both
	 * their names and IDs are equal or either of their names or IDs are
	 * empty and the remaining value (name or ID) are equal.<br/>
	 * Data header descriptors do not matter when comparing data headers.<br/>
	 * For example, two headers:<br/>
	 * <table border="1">
	 * <tr><td>header 1</td><td>header 2</td><td>equals()</td></tr>
	 * <tr><td>(23, "item")</td><td>(23, "item")</td><td>true</td></tr>
	 * <tr><td>(23, "item")</td><td>(-1, "item")</td><td>true</td></tr>
	 * <tr><td>(-1, "item")</td><td>(-1, "item")</td><td>true</td></tr>
	 * <tr><td>(23, "item")</td><td>(23, null)</td><td>true</td></tr>
	 * <tr><td>(23, "item")</td><td>(-1, null)</td><td>false</td></tr>
	 * <tr><td>(-1, "item")</td><td>(-1, null)</td><td>false</td></tr>
	 * <tr><td>(23, null)</td><td>(23, null)</td><td>true</td></tr>
	 * <tr><td>(23, null)</td><td>(-1, null)</td><td>false</td></tr>
	 * <tr><td>(-1, null)</td><td>(-1, null)</td><td>true</td></tr>
	 * </table>
	 * @param obj the data header to compare this header to
	 * @return true if this data header has the same name and ID
	 * (or empty values) as the input object. 
	 */
	@Override
	public boolean equals(Object obj);


	@Override
	public int hashCode();

}
