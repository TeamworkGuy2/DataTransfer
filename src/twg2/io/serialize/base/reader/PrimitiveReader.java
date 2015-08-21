package twg2.io.serialize.base.reader;

import java.io.IOException;

import twg2.io.serialize.base.DataElement;

/**
 * @author TeamworkGuy2
 * @since 2015-5-21
 */
public interface PrimitiveReader {

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
	 * This method will return a new element once {@link #readNext()} or
	 * an equivalent method is called.
	 * @return the last read element
	 */
	public DataElement getCurrentElement();


	/**
	 * @return the name of the current element's tag
	 */
	public String getCurrentName();

}
