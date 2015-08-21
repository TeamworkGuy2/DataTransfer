package twg2.io.serialize.base.reader;

import java.io.IOException;

import twg2.io.serialize.base.DataElement;

/**
 * @author TeamworkGuy2
 * @since 2015-5-21
 */
public interface PrimitiveArrayReader {

	/** Read an array of bytes from the next element of the input stream.
	 * The length of the byte array determines how many bytes are read.
	 * Bytes are put into the array starting at index 0.
	 * @param name the name of the element to read
	 * @param b the byte array to store the bytes read from this input stream in.
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public void read(String name, byte[] b) throws IOException;


	public void read(String name, byte[] b, int off) throws IOException;


	/** Alias for {@link #readByteArray(String, byte[], int, int)}
	 */
	public void read(String name, byte[] b, int off, int len) throws IOException;


	/** Read a byte from the next element.
	 * @param name the name of the element to read
	 * @return the byte read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public byte[] readByteArray(String name) throws IOException;


	/** Read the specified number of bytes from the next element of the input stream.
	 * @param name the name of the element to read
	 * @param b the byte array to load the bytes read from this input stream into
	 * @param off the offset into the byte array to write the bytes read from this input stream
	 * @param len the number of bytes to read from this input stream and write into the byte array
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public void readByteArray(String name, byte[] b, int off, int len) throws IOException;


	/** Parse a boolean from the next element.
	 * @param name the name of the element to read
	 * @return the boolean read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public boolean[] readBooleanArray(String name) throws IOException;


	public void readBooleanArray(String name, boolean[] dst, int dstOff) throws IOException;


	/** Read a character from the next element.
	 * @param name the name of the element to read
	 * @return the character read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public char[] readCharArray(String name) throws IOException;


	public void readCharArray(String name, char[] dst, int dstOff) throws IOException;


	/** Parse a double from the next element.
	 * @param name the name of the element to read
	 * @return the double read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public double[] readDoubleArray(String name) throws IOException;


	public void readDoubleArray(String name, double[] dst, int dstOff) throws IOException;


	/** Parse a float from the next element.
	 * @param name the name of the element to read
	 * @return the float read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public float[] readFloatArray(String name) throws IOException;


	public void readFloatArray(String name, float[] dst, int dstOff) throws IOException;


	/** Parse an integer from the next element.
	 * @param name the name of the element to read
	 * @return the integer read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public int[] readIntArray(String name) throws IOException;


	public void readIntArray(String name, int[] dst, int dstOff) throws IOException;


	/** Parse a long from the next element.
	 * @param name the name of the element to read
	 * @return the long read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public long[] readLongArray(String name) throws IOException;


	public void readLongArray(String name, long[] dst, int dstOff) throws IOException;


	/** Parse a short from the next element.
	 * @param name the name of the element to read
	 * @return the short read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public short[] readShortArray(String name) throws IOException;


	public void readShortArray(String name, short[] dst, int dstOff) throws IOException;


	/** Read a String from the next element of this input stream.
	 * @param name the name of the element to read
	 * @return the String read from this input stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public String[] readStringArray(String name) throws IOException;


	public void readStringArray(String name, String[] dst, int dstOff) throws IOException;


	/** Read an opening array tag.
	 * This also adds a corresponding tag to this stream's internal list of open block tags
	 * @param name the name of element to read
	 * @return the next opening block tag read from the stream
	 * @throws IOException if the next id read from the stream does not match {@code id} or
	 * if there is an IO error while reading from the input stream
	 */
	public DataElement readStartArray(String name) throws IOException;


	/** Read a closing array tag.
	 * This checks the last read opening block against the closing array block read and throws
	 * an exception if they do not match.
	 * @throws IOException if the next id read from the stream is not a closing block
	 * tag that matches the last read opening block tag or
	 * if there is an IO error reading from the input stream
	 */
	public void readEndArray() throws IOException;

}
