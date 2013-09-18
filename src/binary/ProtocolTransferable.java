package binary;

import java.io.IOException;

/** Interface for objects that implement a protocol that can be send as a data stream.
 * @author TeamworkGuy2
 * @since 2013-7-18
 */
public interface ProtocolTransferable {

	/** Initialize this object with data read from the specified {@link ProtocolInput} stream
	 * @param inputStream - the protocol data input stream
	 * @throws IOException if there is an error reading data from the input stream
	 */
	public void readProtocol(ProtocolInput inputStream) throws IOException;

	/** Write this object to the specified {@link ProtocolOutput} stream
	 * @param outputStream - the protocol data output stream
	 * @throws IOException if there is an error writing data to the output stream
	 */
	public void writeProtocol(ProtocolOutput outputStream) throws IOException;

}
