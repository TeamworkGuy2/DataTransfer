package twg2.io.serialize.xml;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;

import twg2.io.serialize.base.writer.DataTransferOutput;

/** XML data output stream interface that mirrors the methods of a {@link DataOutput} stream.
 * This interface allows XML opening and closing tags to be written as well as strings and basic data types.
 * @author TeamworkGuy2
 * @since 2013-2-1
 */
public interface XmlOutput extends DataTransferOutput, Closeable {

	/** Write a byte array with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param b the array of bytes to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void write(String name, byte[] b, XmlAttributes attributes) throws IOException;


	/** Write a byte array with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param b the byte array to write in the XML element
	 * @param off the offset into the byte array to write to the XML element
	 * @param len the number of bytes from the byte array to write to the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void write(String name, byte[] b, int off, int len, XmlAttributes attributes) throws IOException;


	/** Write a boolean value with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param v the boolean value to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeBoolean(String name, boolean v, XmlAttributes attributes) throws IOException;


	/** Write a byte with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param v the byte to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeByte(String name, byte v, XmlAttributes attributes) throws IOException;


	/** Write a char with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param v the char (as an integer) to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeChar(String name, char v, XmlAttributes attributes) throws IOException;


	/** Write a double with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param v the double to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeDouble(String name, double v, XmlAttributes attributes) throws IOException;


	/** Write a float with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param v the float to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeFloat(String name, float v, XmlAttributes attributes) throws IOException;


	/** Write an integer with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param v the integer to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeInt(String name, int v, XmlAttributes attributes) throws IOException;


	/** Write a long with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param v the long to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeLong(String name, long v, XmlAttributes attributes) throws IOException;


	/** Write a short with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param v the short to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeShort(String name, short v, XmlAttributes attributes) throws IOException;


	/** Write a String with the specified XML tag name.
	 * @param name the name of the XML tag to write
	 * @param s the String to write in the XML element
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeString(String name, String s, XmlAttributes attributes) throws IOException;


	/** Write an opening XML tag and with the specified name.
	 * @param name the name of opening XML header to write
	 * @param attributes the group of attributes to write with this XML tag
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	public void writeStartBlock(String name, XmlAttributes attributes) throws IOException;


	/** Get the number of open XML tags waiting for their corresponding closing tags to be written.
	 * @return the number of open tags waiting to be written
	 */
	public int getBlocksRemaining();


	/** Get the number of opening and closing XML tags written by this writer.
	 * @return the number of opening and closing XML tags written by this writer
	 */
	public int getBlocksWritten();


	/** Clear this XML output stream, this does not close any internal
	 * resources. It simply sets resource references to null.
	 */
	public void clear();

}
