package twg2.io.serialize.base.writer;

import java.io.IOException;

/**
 * @author TeamworkGuy2
 * @since 2015-5-21
 */
public interface PrimitiveArrayWriter {

	/** Alias for {@link #writeByteArray(String, byte[])}
	 */
	public void write(String name, byte[] b) throws IOException;


	/** Alias for {@link #writeByteArray(String, byte[], int, int)}
	 */
	public void write(String name, byte[] b, int off, int len) throws IOException;


	/** Write a boolean value with the specified element name.
	 * @param name the name of the element to write
	 * @param v the boolean value to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeBooleanArray(String name, boolean[] v) throws IOException;


	public void writeBooleanArray(String name, boolean[] v, int off, int len) throws IOException;


	/** Write a byte array with the specified element name.
	 * @param name the name of the element to write
	 * @param b the array of bytes to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeByteArray(String name, byte[] b) throws IOException;


	/** Write a byte array with the specified element name.
	 * @param name the name of the element to write
	 * @param b the byte array to write as the element's data
	 * @param off the offset into the byte array to write to the element
	 * @param len the number of bytes from the byte array to write to the element
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeByteArray(String name, byte[] b, int off, int len) throws IOException;


	/** Write a char with the specified element name.
	 * @param name the name of the element to write
	 * @param v the char (as an integer) to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeCharArray(String name, char[] v) throws IOException;


	public void writeCharArray(String name, char[] v, int off, int len) throws IOException;


	/** Write a double with the specified element name.
	 * @param name the name of the element to write
	 * @param v the double to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeDoubleArray(String name, double[] v) throws IOException;


	public void writeDoubleArray(String name, double[] v, int off, int len) throws IOException;


	/** Write a float with the specified element name.
	 * @param name the name of the element to write
	 * @param v the float to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeFloatArray(String name, float[] v) throws IOException;


	public void writeFloatArray(String name, float[] v, int off, int len) throws IOException;


	/** Write an integer with the specified element name.
	 * @param name the name of the element to write
	 * @param v the integer to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeIntArray(String name, int[] v) throws IOException;


	public void writeIntArray(String name, int v, int off, int len) throws IOException;


	/** Write a long with the specified element name.
	 * @param name the name of the element to write
	 * @param v the long to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeLongArray(String name, long[] v) throws IOException;


	public void writeLongArray(String name, long[] v, int off, int len) throws IOException;


	/** Write a short with the specified element name.
	 * @param name the name of the element to write
	 * @param v the short to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeShortArray(String name, short[] v) throws IOException;


	public void writeShortArray(String name, short[] v, int off, int len) throws IOException;


	/** Write a String with the specified element name.
	 * @param name the name of the element to write
	 * @param s the String to write as the element's data
	 * @throws IOException if there is an IO error while writing to the output stream
	 */
	public void writeStringArray(String name, String[] s) throws IOException;


	public void writeStringArray(String name, String[] s, int off, int len) throws IOException;


	/** Write an opening array tag.
	 * This also adds a corresponding tag to this stream's internal list of open block tags
	 * @param name the name of the block
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	public void writeStartArray(String name) throws IOException;


	/** Write a closing array tag.
	 * This automatically writes a closing array block matching the last written opening array block
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	public void writeEndArray() throws IOException;

}
