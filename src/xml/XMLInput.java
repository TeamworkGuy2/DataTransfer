package xml;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;

/** XML data input stream interface that mirrors the methods of a {@link DataInput} stream.
 * This interface allows XML opening and closing tags to be read as well as strings and basic data types.
 * @author TeamworkGuy2
 * @since 2013-2-1
 */
public interface XMLInput extends Closeable {

	/** Read the specified number of bytes from the next XML element
	 * @param name the name of the XML element to read
	 * @param b the byte array to load the bytes read from the XML document into.
	 * The length of the byte array determines how many bytes are read.
	 * Bytes are added to the array starting at index 0.
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public void read(String name, byte[] b) throws IOException;

	/** Read the specified number of bytes from the next XML element
	 * @param name the name of the XML element to read
	 * @param b the byte array to load the bytes read from the XML document into
	 * @param off the offset into the byte array to write the bytes read from the XML document
	 * @param len the number of bytes to read from the XML document and write into the byte array
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public void read(String name, byte[] b, int off, int len) throws IOException;

	/** Parse a boolean value from the next XML element
	 * @param name the name of the XML element to read
	 * @return the boolean read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public boolean readBoolean(String name) throws IOException;

	/** Read a byte from the next XML element
	 * @param name the name of the XML element to read
	 * @return the byte read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public byte readByte(String name) throws IOException;

	/** Read a character from the next XML element
	 * @param name the name of the XML element to read
	 * @return the character read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public char readChar(String name) throws IOException;

	/** Parse a double from the next XML element
	 * @param name the name of the XML element to read
	 * @return the double read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public double readDouble(String name) throws IOException;

	/** Parse a float from the next XML element
	 * @param name the name of the XML element to read
	 * @return the float read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public float readFloat(String name) throws IOException;

	/** Parse an integer from the next XML element
	 * @param name the name of the XML element to read
	 * @return the integer read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public int readInt(String name) throws IOException;

	/** Parse a long from the next XML element
	 * @param name the name of the XML element to read
	 * @return the long read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public long readLong(String name) throws IOException;

	/** Parse a short from the next XML element
	 * @param name the name of the XML element to read
	 * @return the short read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public short readShort(String name) throws IOException;

	/** Read a String from the XML document
	 * @param name the name of the XML element to read
	 * @return the String read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public String readUTF(String name) throws IOException;

	/** Read an opening XML block tag and add a corresponding tag to this reader's internal list of open XML tags
	 * @param name the name of the opening XML tag to read
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public void readOpeningBlock(String name) throws IOException;

	/** Read the next opening XML block tag, no matter what it's name is and add a corresponding tag to
	 * this reader's internal list of open XML tags
	 * @return the name of the next XML opening tag read from the XML stream
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public XMLTag readOpeningBlock() throws IOException;

	/** Read a closing XML block tag for the last read opening XML tag
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public void readClosingBlock() throws IOException;

	/** Get the group of attributes associated with the last read element or tag 
	 * @return a group of attributes read when the last XML element was read
	 */
	public XMLAttributes getCurrentHeaderBlockAttributes();

	/** Get the number of open XML tags waiting for their corresponding closing tags to be read.
	 * This does not include blocks read by {@link #peekNextHeaderBlock()}.
	 * @return the number of open tags waiting to be read
	 */
	public int getBlocksRemaining();

	/** Get the number of opening and closing XML tags read by this reader.
	 * This does not include the next opening tag read by {@link #peekNextHeaderBlock()}.
	 * @return the number of opening and closing XML tags read by this reader
	 */
	public int getBlocksRead();

	/** Get the last read opening XML tag
	 * @return the last read opening XML tag
	 */
	public XMLTag getCurrentHeaderBlock();

	/** Peek at the next opening XML tag
	 * @return the next opening XML tag
	 */
	public XMLTag peekNextHeaderBlock() throws IOException;

	/** Clear this XML input stream, this does not close any internal
	 * resources. It simply sets resource references to null.
	 */
	public void clear();

}