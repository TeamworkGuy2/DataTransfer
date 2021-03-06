package twg2.io.serialize.base.reader;

import java.io.Closeable;
import java.io.IOException;

import twg2.io.serialize.base.DataElement;

/** Input stream interface that allows arbitrary input data to be read from
 * an arbitrary input stream.
 * @author TeamworkGuy2
 * @since 2013-8-27
 */
public interface DataTransferInput extends PrimitiveReader, /* TODO at some point PrimitiveArrayReader, */ Closeable {

	/** Read an array of bytes from the next element of the input stream.
	 * The length of the byte array determines how many bytes are read.
	 * Bytes are put into the array starting at index 0.
	 * @param name the name of the element to read
	 * @param b the byte array to store the bytes read from this input stream in.
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public void read(String name, byte[] b) throws IOException;


	/** Read the specified number of bytes from the next element of the input stream.
	 * @param name the name of the element to read
	 * @param b the byte array to load the bytes read from this input stream into
	 * @param off the offset into the byte array to write the bytes read from this input stream
	 * @param len the number of bytes to read from this input stream and write into the byte array
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public void read(String name, byte[] b, int off, int len) throws IOException;


	/** Peek at the next block of this input stream without reading it.
	 * This call reads the next data block and returns it, however
	 * the next call to {@link #readNext()} or equivalent parameterized
	 * version will return this peek header.<br/>
	 * The purpose of this method is to parse arbitrary objects from an input
	 * stream by peeking at the next element in the stream and call
	 * the correct sub parser and let that parser read its header
	 * without realizing that the header was already peeked at.
	 * @return the next data header from this input stream.
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public DataElement peekNext() throws IOException;

}
