package twg2.io.serialize.base;

/** A data header implementation that has a name and integer ID.
 * @author TeamworkGuy2
 * @since 2013-9-1
 */
public class DataElementImpl implements DataElement {
	private final String name;
	private final String content;
	private final int id;
	private final ParsedElementType elementType;


	/** Create a data header containing a name and ID.
	 * @param name the name of the data header
	 * @param id the integer ID of the data header
	 * @param content the elment's contents
	 * @param elementType the type of element parsed
	 */
	public DataElementImpl(String name, int id, String content, ParsedElementType elementType) {
		this.name = name;
		this.content = content;
		this.id = (id < 0 ? -1 : id);
		this.elementType = elementType;
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
		return name;
	}


	@Override
	public String getContent() {
		return content;
	}


	@Override
	public int getId() {
		return id;
	}


	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if((obj instanceof DataElement)) {
			DataElement b = (DataElement)obj;
			return equals(this.name, this.id, b.getName(), b.getId());
		}
		return result;
	}


	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + id;
		return result;
	}


	/** Compare two data headers
	 * @param a the first data header
	 * @param b the second data header
	 * @return true if the two data header's are equal, false otherwise
	 */
	public static <T> boolean equals(DataElement a, DataElement b) {
		return equals(a.getName(), a.getId(), b.getName(), b.getId());
	}


	/** Compare two data header values
	 * @param strA the first data header's name
	 * @param idA the first data header's ID
	 * @param strB the second data header's name
	 * @param idB the second data header's ID
	 * @return true if the two data header's values are equal, false otherwise
	 */
	public static boolean equals(String strA, int idA, String strB, int idB) {
		idA = (idA < 0 ? -1 : idA);
		idB = (idB < 0 ? -1 : idB);
		// Check if the header names match
		boolean nameMatch = (strA != null) ? (strA.equals(strB)) : (strB == null);
		// Check if the header IDs match
		boolean idMatch = (idA == idB);

		// If the names, IDs, or both match and the remaining non matching field has at
		// least one empty value, then the headers are equal, see {@link DataElement#equals(Object)}
		return (nameMatch || idMatch) && !(!nameMatch && idA < 0) && !(!idMatch && strA == null);
	}

}
