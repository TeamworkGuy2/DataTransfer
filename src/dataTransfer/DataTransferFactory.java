package dataTransfer;

import java.io.IOException;

/** Interface for factory pattern classes that can read and write object
 * instances from input stream and to output streams.
 * @param <T> the type of object that can be read/written by this factory.
 * @author TeamworkGuy2
 * @since 2013-9-18
 */
public interface DataTransferFactory<T> {

	/** Write the specified object to the specified {@link DataTransferOutput} stream.
	 * @param outputStream the data output stream to write the data to
	 * @param obj the object to write
	 * @throws IOException if there is an error writing data to the output stream
	 */
	public void writeData(DataTransferOutput outputStream, T obj) throws IOException;


	/** Read an object from the specified {@link DataTransferInput} stream.
	 * The object returned may be the same or a different object than the input
	 * object. Each implementation should define this behavior.
	 * @param inputStream the data input stream to read data from
	 * @param obj the object to read, implementations should define what is
	 * done if this is null (for example create a new instance).
	 * @return an object, depending on the implementation this may be the same
	 * object passed to the method or a new instance filled with data read from
	 * the input stream.
	 * @throws IOException if there is an error reading data from the input stream
	 */
	public T readData(DataTransferInput inputStream, T obj) throws IOException;

}
