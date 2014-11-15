package xml;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;

import base.DataTransferInput;

/** XML data input stream interface that mirrors the methods of a {@link DataInput} stream.
 * This interface allows XML opening and closing tags to be read as well as strings and basic data types.
 * @author TeamworkGuy2
 * @since 2013-2-1
 */
public interface XmlInput extends DataTransferInput, Closeable {

	/** Read an array of bytes from the next XML element.
	 * @param b the byte array to load the bytes read from the XML document into.
	 * The length of the byte array determines how many bytes are read.
	 * Bytes are added to the array starting at index 0.
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public void read(byte[] b) throws IOException;


	/** Read an array of bytes from the next XML element.
	 * @param b the byte array to load the bytes read from the XML document into
	 * @param off the offset into the byte array to write the bytes read from the XML document
	 * @param len the number of bytes to read from the XML document and write into the byte array
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public void read(byte[] b, int off, int len) throws IOException;


	/** Parse a boolean value from the next XML element.
	 * @return the boolean read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public boolean readBoolean() throws IOException;


	/** Read a byte from the next XML element.
	 * @return the byte read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public byte readByte() throws IOException;


	/** Read a character from the next XML element.
	 * @return the character read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public char readChar() throws IOException;


	/** Parse a double from the next XML element.
	 * @return the double read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public double readDouble() throws IOException;


	/** Parse a float from the next XML element.
	 * @return the float read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public float readFloat() throws IOException;


	/** Parse an integer from the next XML element.
	 * @return the integer read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public int readInt() throws IOException;


	/** Parse a long from the next XML element.
	 * @return the long read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public long readLong() throws IOException;


	/** Parse a short from the next XML element.
	 * @return the short read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public short readShort() throws IOException;


	/** Read a String from the XML document.
	 * @return the String read from the XML document
	 * @throws IOException if there is an IO or XML error reading from the input stream
	 */
	public String readString() throws IOException;


	/** Get the last read opening XML tag
	 * @return the last read opening XML tag
	 */
	@Override
	public XmlTag getCurrentElement();


	/** Get the group of attributes associated with the last read element or tag.
	 * @return a group of attributes read when the last XML element was read
	 */
	public XmlAttributes getCurrentElementAttributes();


	/**
	 * @return the name of the last element read from this input stream.<br/>
	 * This only applies to elements, not blocks, block header names can be
	 * obtained by calling {@link #getCurrentElement()}
	 */
	public String getCurrentElementName();


	/** Get the number of opening and closing XML tags read by this reader.
	 * @return the number of opening and closing XML tags read by this reader
	 */
	public int getBlocksRead();


	/** Clear this XML input stream, this does not close any internal
	 * resources. It simply sets resource references to null.
	 */
	public void clear();

}
