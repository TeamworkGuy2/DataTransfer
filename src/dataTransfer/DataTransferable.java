package dataTransfer;

import java.io.IOException;

/** Interface for objects that can be written to an output stream.
 * @author TeamworkGuy2
 * @since 2013-8-27
 */
public interface DataTransferable {

	/** Write this object to the specified {@link DataTransferOutput} stream.
	 * @param outputStream the data output stream to write the data to
	 * @throws IOException if there is an error writing data to the output stream
	 */
	public void writeData(DataTransferOutput outputStream) throws IOException;

}
