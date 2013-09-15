package dataTransfer;

/** A data header that represents the header for a {@link DataTransferable}
 * object.
 * @author TeamworkGuy2
 * @since 2013-8-30
 */
public interface DataHeader {

	/**
	 * @return this data header's name, or null if it does not have a name.
	 */
	public String getHeaderName();

	/**
	 * @return this data header's ID between [0, Integer.MAX_VALUE], or -1 if
	 * it does not have an ID.
	 */
	public int getHeaderId();

	/** Check if two data headers are equal. Two data headers are equal if both
	 * their names and IDs are equal or either of their names or IDs are empty
	 * and the remaining value (name or ID) are equal.<br/>
	 * For example, two headers:<br/>
	 * <table border="1">
	 * <tr><td>header 1</td><td>header 2</td><td>equals</td></tr>
	 * <tr><td>(23, null)</td><td>(23, "item")</td><td>true</td></tr>
	 * <tr><td>(-1, null)</td><td>(-1, null)</td><td>true</td></tr>
	 * <tr><td>(-1, "item")</td><td>(942, "item")</td><td>true</td></tr>
	 * <tr><td>(4, "item")</td><td>(-1, null)</td><td>false</td></tr>
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
