package twg2.io.serialize.base.writer;

import java.io.IOException;

/**
 * @author TeamworkGuy2
 * @since 2015-5-21
 */
public interface PrimitiveWriter {

	/** Write a boolean value with the specified element name.
	 * @param name the name of the element to write
	 * @param v the boolean value to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeBoolean(String name, boolean v) throws IOException;


	/** Write a byte with the specified element name.
	 * @param name the name of the element to write
	 * @param v the byte to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeByte(String name, byte v) throws IOException;


	/** Write a char with the specified element name.
	 * @param name the name of the element to write
	 * @param v the char (as an integer) to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeChar(String name, char v) throws IOException;


	/** Write a double with the specified element name.
	 * @param name the name of the element to write
	 * @param v the double to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeDouble(String name, double v) throws IOException;


	/** Write a float with the specified element name.
	 * @param name the name of the element to write
	 * @param v the float to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeFloat(String name, float v) throws IOException;


	/** Write an integer with the specified element name.
	 * @param name the name of the element to write
	 * @param v the integer to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeInt(String name, int v) throws IOException;


	/** Write a long with the specified element name.
	 * @param name the name of the element to write
	 * @param v the long to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeLong(String name, long v) throws IOException;


	/** Write a short with the specified element name.
	 * @param name the name of the element to write
	 * @param v the short to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeShort(String name, short v) throws IOException;


	/** Write a String with the specified element name.
	 * @param name the name of the element to write
	 * @param s the String to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeString(String name, String s) throws IOException;


	/** Write an opening block tag.
	 * This also adds a corresponding block tag to this stream's internal list of open block tags
	 * @param name the name of the block
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	public void writeStartBlock(String name) throws IOException;


	/** Write a closing block tag.
	 * This automatically writes a closing block matching the last written opening block
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	public void writeEndBlock() throws IOException;

}
