package base;

import java.io.IOException;

/** Interface for factory pattern classes that can read objects from input
 * streams and write objects to output streams.
 * @param <T> the type of object that can be read/written by this factory.
 * @author TeamworkGuy2
 * @since 2013-9-18
 */
public interface DataTransferFactory<T> {

	/** Method to query whether this factory supports the reloading of objects
	 * via the {@link #readData(DataTransferInput, Object)} method.
	 * @return true if {@link #readData(DataTransferInput, Object)} is
	 * supported by this factory implementation, false if it is not supported.
	 */
	public default boolean canReloadObjects() {
		return false;
	}


	/** Write the specified object to the specified {@link DataTransferOutput} stream.
	 * @param outputStream the data output stream to write the data to
	 * @param obj the object to write
	 * @throws IOException if there is an error writing data to the output stream
	 */
	public void writeData(DataTransferOutput outputStream, T obj) throws IOException;


	/** Read an object from the specified {@link DataTransferInput} stream.
	 * This method should be implemented by all implementations.
	 * @param inputStream the data input stream to read object data from
	 * @return a new object instance of type {@link DataTransferableFactory T},
	 * initialized with data read from the input stream.
	 * @throws IOException if there is an error reading data from the input stream
	 */
	public T readData(DataTransferInput inputStream) throws IOException;


	/** Read data from the specified {@link DataTransferInput} stream into the
	 * provided object.
	 * Implementations may choose to not implement this method which should be
	 * reflected by the value returned by {@link #canReloadObjects()}.<br/>
	 * For example, a factory that manages immutable objects will not be able
	 * to load new data into an existing immutable object so this method could
	 * throw an exception.
	 * @param inputStream the data input stream to read data from
	 * @param obj the object to read into, implementations should define what is
	 * done if this is null (for example create a new instance).
	 * @throws IOException if there is an error reading data from the input stream
	 */
	public default void readData(DataTransferInput inputStream, T obj) throws IOException {
		throw new IllegalStateException("cannot read data into existing object");
	}

}
