package binaryImpl;

import java.io.IOException;
import java.nio.charset.Charset;

/** Protocol handler for assisting in parsing binary files.<br/>
 * Data type bits x xxxx, the high bit defines whether the data type is an array,
 * the lower 4 bits define the data type.<br/>
 * Data types:<br/>
 * NO_TYPE = 0x00<br/>
 * BYTE_TYPE = 0x01<br/>
 * SHORT_TYPE = 0x02<br/>
 * INT_TYPE = 0x03<br/>
 * LONG_TYPE = 0x04<br/>
 * FLOAT_TYPE = 0x05<br/>
 * DOUBLE_TYPE = 0x06<br/>
 * BOOLEAN_TYPE = 0x07<br/>
 * CHAR_TYPE = 0x08<br/>
 * STRING_TYPE = 0x09<br/>
 * <br/>
 * And the important array type:<br/>
 * ARRAY_TYPE = 0x10
 * @see ProtocolOutput
 * @see ProtocolInput
 * @author TeamworkGuy2
 * @since 2013-7-18
 */
public class ProtocolHandler {
	/** Used to identify the mask for all data type values including the <code>ARRAY_TYPE</code> */
	public static final int ANY_TYPE = 0x1F;
	/** Used to identify the mask for data type values excluding arrays such as <code>ARRAY_TYPE</code> */
	public static final int DATA_TYPE = 0x0F;
	/** A type that indicates that the data has not type, this may indicate that there is no data */
	public static final int NO_TYPE = 0;
	/** An identifier for the <code>byte</code> primitive */
	public static final int BYTE_TYPE = 1;
	/** An identifier for the <code>short</code> primitive */
	public static final int SHORT_TYPE = 2;
	/** An identifier for the <code>int</code> primitive */
	public static final int INT_TYPE = 3;
	/** An identifier for the <code>long</code> primitive */
	public static final int LONG_TYPE = 4;
	/** An identifier for the <code>float</code> primitive */
	public static final int FLOAT_TYPE = 5;
	/** An identifier for the <code>double</code> primitive */
	public static final int DOUBLE_TYPE = 6;
	/** An identifier for the <code>boolean</code> primitive */
	public static final int BOOLEAN_TYPE = 7;
	/** An identifier for the <code>char</code> primitive */
	public static final int CHAR_TYPE = 8;
	/** An identifier for a {@link String} object */
	public static final int STRING_TYPE = 9;
	/** An identifier for an array of values */
	public static final int ARRAY_TYPE = 16;
	/** identifier bytes TPS (0x18 Transfer Protocol Stream) */
	public static final int MAGIC = 0x18545053;
	/** Version number */
	public static final int VERSION = (0x01 << 8) | (0x00);
	private static final String[] charsetNames = new String[] {"UTF-8", "US-ASCII", "UTF-16"};
	static final Charset defaultCharset = Charset.forName("US-ASCII");


	protected static Charset getCharset() throws IOException {
		Charset charset = null;
		for(int i = 0; i < charsetNames.length; i++) {
			if(Charset.isSupported(charsetNames[i])) {
				charset = Charset.forName(charsetNames[i]);
				break;
			}
		}
		if(charset == null) {
			throw new IOException("Could not find supported charset");
		}
		return charset;
	}

	/** Read an array of protocol objects from the specified protocol input
	 * stream. Reads the specified tag ID only if the ID is not -1.
	 * @param id the tag ID to read before reading the array of objects. This
	 * tag is only read if it is not -1.
	 * @param objects the array of objects to read from the protocol input stream
	 * @param input the protocol input stream to read the objects from
	 * @throws IOException if there is an I/O error reading the objects from
	 * the protocol input stream
	 */
	public static void readProtocolObjects(int id, ProtocolTransferable[] objects, ProtocolInput input) throws IOException {
		if(id > -1) {
			input.readOpeningBlock(id);
		}

		int size = objects.length;
		for(int i = 0; i < size; i++) {
			objects[i].readProtocol(input);
		}

		if(id > -1) {
			input.readClosingBlock();
		}
	}


	/** Write an array of protocol objects to the specified protocol output
	 * stream. Writes the specified tag ID only if the ID is not -1.
	 * @param id the tag ID to write before writing the array of objects. This
	 * tag is only written if it is not -1.
	 * @param objects the array of objects to write to the protocol output stream
	 * @param output the protocol output stream to write the objects to
	 * @throws IOException if there is an I/O error writing the objects to the
	 * protocol output stream
	 */
	public static void writeProtocolObjects(int id, ProtocolTransferable[] objects, ProtocolOutput output) throws IOException {
		if(id > -1) {
			output.writeOpeningBlock(id);
		}

		int size = objects.length;
		for(int i = 0; i < size; i++) {
			objects[i].writeProtocol(output);
		}

		if(id > -1) {
			output.writeClosingBlock();
		}
	}

}
