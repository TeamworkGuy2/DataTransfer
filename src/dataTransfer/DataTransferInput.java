package dataTransfer;

import java.io.Closeable;
import java.io.IOException;

/** Input stream interface that allows arbitrary input data to be read to
 * an arbitrary input stream.
 * @author TeamworkGuy2
 * @since 2013-8-27
 */
public interface DataTransferInput extends Closeable {

	/** Read the specified number of bytes from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @param b the byte array to load the bytes read from this input stream into.
	 * The length of the byte array determines how many bytes are read.
	 * Bytes are put into the array starting at index 0.
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public void read(int id, String name, byte[] b) throws IOException;


	/** Read the specified number of bytes from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @param b the byte array to load the bytes read from this input stream into
	 * @param off the offset into the byte array to write the bytes read from this input stream
	 * @param len the number of bytes to read from this input stream and write into the byte array
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public void read(int id, String name, byte[] b, int off, int len) throws IOException;


	/** Parse a boolean value from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the boolean read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public boolean readBoolean(int id, String name) throws IOException;


	/** Read a byte from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the byte read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public byte readByte(int id, String name) throws IOException;


	/** Read a character from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the character read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public char readChar(int id, String name) throws IOException;


	/** Parse a double from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the double read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public double readDouble(int id, String name) throws IOException;


	/** Parse a float from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the float read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public float readFloat(int id, String name) throws IOException;


	/** Parse an integer from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the integer read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public int readInt(int id, String name) throws IOException;


	/** Parse a long from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the long read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public long readLong(int id, String name) throws IOException;


	/** Parse a short from the next element
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the short read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public short readShort(int id, String name) throws IOException;


	/** Read a String from this input stream
	 * @param id the id of the element to read
	 * @param name the name of the element to read
	 * @return the String read from this input stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public String readUTF(int id, String name) throws IOException;


	/** Peek at the next header block in the data input stream.
	 * This call reads the next data header block and returns it, however
	 * the next call to {@link #readNextBlock()} or equivalent parameterized
	 * version will return this peek header.<br/>
	 * The purpose of this method is to parse arbitrary objects from an input
	 * stream by peeking at the next object's header in the stream and call
	 * the correct object's constructor and let that object read its header
	 * without realizing that the header was already peeked at.
	 * @return the next data header from this input stream.
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public DataHeader peekNextBlock() throws IOException;


	/** Read the next opening or closing block tag, no matter what it's ID or name is
	 * @return the next block tag read from the stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public DataHeader readNextBlock() throws IOException;


	/** Read an opening block tag and add a corresponding tag to this reader's
	 * internal list of open block tags
	 * @param id the block's tag identifier
	 * @param name the name of element to read
	 * @return the next opening block tag read from the stream
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public DataHeader readOpeningBlock(int id, String name) throws IOException;


	/** Read a closing block tag for the last read opening block tag, this
	 * completes a block.
	 * @throws IOException if there is an IO error reading from the input stream
	 */
	public void readClosingBlock() throws IOException;


	/** Get the current read opening data block tag. This is the block header that
	 * is currently being read. This method will return a new header once
	 * {@link #readNextBlock()} or an equivalent method is called.
	 * @return the currently read opening data block tag
	 */
	public DataHeader getCurrentBlockHeader();

}
