package dataTransfer;

/** A data header implementation that has a name and integer ID.
 * @author TeamworkGuy2
 * @since 2013-9-1
 */
public class DataHeaderImpl implements DataHeader {
	private String name;
	private int id;

	/** Create a simple data header containing a name and ID.
	 * @param name the name of the data header
	 * @param id the integer ID of the data header
	 */
	public DataHeaderImpl(String name, int id) {
		this.name = name;
		this.id = (id < 0 ? -1 : id);
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
		boolean result = false;
		// If the header names match
		if(a.getHeaderName() != null) {
			result |= a.getHeaderName().equals(b.getHeaderName());
		}
		else {
			result |= (b.getHeaderName() == null);
		}
		// Check if this header has the same ID as the other header
		result |= (a.getHeaderId() == b.getHeaderId());
		return result;
	}


	/** Compare two data header values
	 * @param strA the first data header's name
	 * @param idA the first data header's ID
	 * @param strB the second data header's name
	 * @param idB the second data header's ID
	 * @return true if the two data header's values are equal, false otherwise
	 */
	public static boolean equals(String strA, int idA, String strB, int idB) {
		boolean result = false;
		idA = (idA < 0 ? -1 : idA);
		idB = (idB < 0 ? -1 : idB);
		// If the header names match
		if(strA != null) {
			result |= strA.equals(strB);
		}
		else {
			result |= (strB == null);
		}
		// Check if this header has the same ID as the other header
		result |= (idA == idB);
		return result;
	}

}
