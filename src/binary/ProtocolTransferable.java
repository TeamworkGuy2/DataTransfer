package binary;

import java.io.IOException;

/** Interface for objects that can be written to a data stream.
 * @author TeamworkGuy2
 * @since 2013-7-18
 */
public interface ProtocolTransferable {

	/** Read data from the specified {@link ProtocolInput} stream into this object.
	 * @param inputStream the protocol data input stream to read data from
	 * @throws IOException if there is an error reading data from the input stream
	 */
	public void readProtocol(ProtocolInput inputStream) throws IOException;


	/** Write this object's data to the specified {@link ProtocolOutput} stream.
	 * @param outputStream the protocol data output stream to write data to
	 * @throws IOException if there is an error writing data to the output stream
	 */
	public void writeProtocol(ProtocolOutput outputStream) throws IOException;

}
