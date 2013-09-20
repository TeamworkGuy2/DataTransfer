package dataTransfer;

/** A data header implementation that has a name and integer ID.
 * @author TeamworkGuy2
 * @since 2013-9-1
 */
public class DataHeaderImpl implements DataHeader {
	private final String name;
	private final int id;
	private final boolean openingHeader;

	/** Create a simple data header containing a name and ID.
	 * @param name the name of the data header
	 * @param id the integer ID of the data header
	 * @param openingHeader true if this header represents an opening header, false if it represents a closing header
	 */
	public DataHeaderImpl(String name, int id, boolean openingHeader) {
		this.name = name;
		this.id = (id < 0 ? -1 : id);
		this.openingHeader = openingHeader;
	}


	@Override
	public boolean isOpeningHeader() {
		return openingHeader;
	}


	@Override
	public String getHeaderName() {
		return name;
	}

	@Override
	public int getHeaderId() {
		return id;
	}


	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if((obj instanceof DataHeader)) {
			DataHeader header = (DataHeader)obj;
			// Check if the header names match
			if(name != null) {
				result |= name.equals(header.getHeaderName());
			}
			else {
				result |= (header.getHeaderName() == null);
			}
			// Check if this header has the same ID as the other header
			result |= (id == header.getHeaderId());
		}
		return result;
	}


	@Override
	public int hashCode() {
		return (name != null ? name.hashCode() : 0) ^ id;
	}


	/** Compare two data headers
	 * @param a the first data header
	 * @param b the second data header
	 * @return true if the two data header's are equal, false otherwise
	 */
	public static boolean equals(DataHeader a, DataHeader b) {
		// Check if the header names match
		boolean nameMatch = (a.getHeaderName() != null) ? a.getHeaderName().equals(b.getHeaderName()) : (b.getHeaderName() == null);
		// Check if the header IDs match
		boolean idMatch = (a.getHeaderId() == b.getHeaderId());

		// If the names, IDs, or both match and the remaining non matching field has at
		// least one empty value, then the headers are equal, see {@link DataHeader#equals(Object)}
		return (nameMatch || idMatch) && !(!nameMatch && a.getHeaderId() < 0) && !(!idMatch && a.getHeaderName() == null);
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
		// least one empty value, then the headers are equal, see {@link DataHeader#equals(Object)}
		return (nameMatch || idMatch) && !(!nameMatch && idA < 0) && !(!idMatch && strA == null);
	}

}
