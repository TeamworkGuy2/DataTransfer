package twg2.io.serialize.base.writer;

import java.io.Closeable;
import java.io.IOException;

/** Output stream interface that allows arbitrary output data to be written to
 * an arbitrary output stream.
 * @author TeamworkGuy2
 * @since 2013-8-27
 */
public interface DataTransferOutput extends PrimitiveWriter, /* TODO at some point PrimitiveArrayWriter,*/ Closeable {

	/** Write a byte array with the specified element name.
	 * @param name the name of the element to write
	 * @param b the array of bytes to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void write(String name, byte[] b) throws IOException;


	/** Write a byte array with the specified element name.
	 * @param name the name of the element to write
	 * @param b the byte array to write as the element's data
	 * @param off the offset into the byte array to write to the element
	 * @param len the number of bytes from the byte array to write to the element
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void write(String name, byte[] b, int off, int len) throws IOException;

}
