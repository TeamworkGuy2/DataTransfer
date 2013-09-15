package binaryImpl;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;

/** Protocol data output stream interface that mirrors the methods of a {@link DataOutput} stream.
 * This interface allows opening and closing elements to be written as well as strings and basic data types.
 * @author TeamworkGuy2
 * @since 2013-7-18
 */
public interface ProtocolOutput extends Closeable {

	/** Write a byte array with the specified element name
	 * @param id the id of the element to write
	 * @param b the array of bytes to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void write(int id, byte[] b) throws IOException;

	/** Write a byte array with the specified element name
	 * @param id the id of the element to write
	 * @param b the byte array to write as the element's data
	 * @param off the offset into the byte array to write to the element
	 * @param len the number of bytes from the byte array to write to the element
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void write(int id, byte[] b, int off, int len) throws IOException;

	/** Write a boolean value with the specified element name
	 * @param id the id of the element to write
	 * @param v the boolean value to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeBoolean(int id, boolean v) throws IOException;

	/** Write a byte with the specified element name
	 * @param id the id of the element to write
	 * @param v the byte to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeByte(int id, byte v) throws IOException;

	/** Write a char with the specified element name
	 * @param id the id of the element to write
	 * @param v the char (as an integer) to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeChar(int id, char v) throws IOException;

	/** Write a double with the specified element name
	 * @param id the id of the element to write
	 * @param v the double to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeDouble(int id, double v) throws IOException;

	/** Write a float with the specified element name
	 * @param id the id of the element to write
	 * @param v the float to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeFloat(int id, float v) throws IOException;

	/** Write an integer with the specified element name
	 * @param id the id of the element to write
	 * @param v the integer to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeInt(int id, int v) throws IOException;

	/** Write a long with the specified element name
	 * @param id the id of the element to write
	 * @param v the long to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeLong(int id, long v) throws IOException;

	/** Write a short with the specified element name
	 * @param id the id of the element to write
	 * @param v the short to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeShort(int id, short v) throws IOException;

	/** Write a String with the specified element name
	 * @param id the id of the element to write
	 * @param s the String to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeUTF(int id, String s) throws IOException;

	/** Write an opening block tag and add a corresponding block tag to this
	 * writer's internal list of open block tags
	 * @param id the block's identifier
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	public void writeOpeningBlock(int id) throws IOException;

	/** Write a closing block tag for the last written opening block tag, this completes a block
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	public void writeClosingBlock() throws IOException;

	/** Get the number of blocks written by this writer
	 * @return the number of blocks (opening and closing block tags) written by this writer
	 */
	public int getBlocksWritten();

}
