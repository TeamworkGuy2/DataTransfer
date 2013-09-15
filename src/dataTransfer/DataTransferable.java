package dataTransfer;

import java.io.IOException;

/** Interface for objects that can be read from or written to a data input or
 * output stream.
 * @author TeamworkGuy2
 * @since 2013-8-27
 */
public interface DataTransferable {

	/** Initialize this object with data read from the specified
	 * {@link DataTransferInput} stream.
	 * @param inputStream the data input stream to read data from
	 * @throws IOException if there is an error reading data from the input stream
	 */
	public void readData(DataTransferInput inputStream) throws IOException;

	/** Write this object to the specified {@link DataTransferOutput} stream.
	 * @param outputStream the data output stream to write the data to
	 * @throws IOException if there is an error writing data to the output stream
	 */
	public void writeData(DataTransferOutput outputStream) throws IOException;

}
