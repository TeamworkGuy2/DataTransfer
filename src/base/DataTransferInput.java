package base;

import java.io.Closeable;
import java.io.IOException;

/** Input stream interface that allows arbitrary input data to be read from
 * an arbitrary input stream.
 * @author TeamworkGuy2
 * @since 2013-8-27
 */
public interface DataTransferInput extends Closeable {

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


	/** Parse a boolean from the next element.
	 * @param name the name of the element to read
	 * @return the boolean read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public boolean readBoolean(String name) throws IOException;


	/** Read a byte from the next element.
	 * @param name the name of the element to read
	 * @return the byte read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public byte readByte(String name) throws IOException;


	/** Read a character from the next element.
	 * @param name the name of the element to read
	 * @return the character read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public char readChar(String name) throws IOException;


	/** Parse a double from the next element.
	 * @param name the name of the element to read
	 * @return the double read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public double readDouble(String name) throws IOException;


	/** Parse a float from the next element.
	 * @param name the name of the element to read
	 * @return the float read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public float readFloat(String name) throws IOException;


	/** Parse an integer from the next element.
	 * @param name the name of the element to read
	 * @return the integer read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public int readInt(String name) throws IOException;


	/** Parse a long from the next element.
	 * @param name the name of the element to read
	 * @return the long read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public long readLong(String name) throws IOException;


	/** Parse a short from the next element.
	 * @param name the name of the element to read
	 * @return the short read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public short readShort(String name) throws IOException;


	/** Read a String from the next element of this input stream.
	 * @param name the name of the element to read
	 * @return the String read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public String readString(String name) throws IOException;


	/** Peek at the next block of this input stream without reading it.
	 * This call reads the next data block and returns it, however
	 * the next call to {@link #readNextBlock()} or equivalent parameterized
	 * version will return this peek header.<br/>
	 * The purpose of this method is to parse arbitrary objects from an input
	 * stream by peeking at the next element in the stream and call
	 * the correct sub parser and let that parser read its header
	 * without realizing that the header was already peeked at.
	 * @return the next data header from this input stream.
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public DataElement peekNext() throws IOException;


	/** Read the next element from the stream regardless of its id or name.
	 * @return the next block read from the stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public DataElement readNext() throws IOException;


	/** Read an opening block tag.
	 * This also adds a corresponding tag to this stream's internal list of open block tags
	 * @param name the name of element to read
	 * @return the next opening block tag read from the stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public DataElement readStartBlock(String name) throws IOException;


	/** Read a closing block tag.
	 * This checks the last read opening block against the closing block read and throws
	 * an exception if they do not match.
	 * @throws IOException if the next id read from the stream is not a closing block
	 * tag that matches the last read opening block tag or
	 * if there is an IO error reading from the input stream
	 */
	public void readEndBlock() throws IOException;


	/** Returns the last parsed element.
	 * This method will return a new element once {@link #readNextBlock()} or
	 * an equivalent method is called.
	 * @return the last read element
	 */
	public DataElement getCurrentElement();


	/**
	 * @return the name of the current element's tag
	 */
	public String getCurrentName();

}
