package dataTransfer;

import java.io.IOException;

/** Interface for objects that can be read from an input stream.
 * @author TeamworkGuy2
 * @since 2013-9-18
 */
public interface DataTransferReadable {

	/** Initialize this object with data read from the specified
	 * {@link DataTransferInput} stream.
	 * @param inputStream the data input stream to read data from
	 * @throws IOException if there is an error reading data from the input stream
	 */
	public void readData(DataTransferInput inputStream) throws IOException;

}
