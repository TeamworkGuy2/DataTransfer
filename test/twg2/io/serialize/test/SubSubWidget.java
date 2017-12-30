package twg2.io.serialize.test;

import java.io.IOException;

import twg2.io.serialize.base.DataTransferable;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;

/** A sub sub widget for testing the transfer protocol package
 * @author TeamworkGuy2
 * @since 2013-8-6
 */
public class SubSubWidget implements DataTransferable {
	private static String id = "SubSubWidget";
	private static int originalValue = 0x01020408;
	private int abcd = originalValue;


	@Override
	public void readData(DataTransferInput inputStream) throws IOException {
		inputStream.readStartBlock(id);
		byte[] bytes = new byte[4];
		inputStream.read("info", bytes);
		abcd = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
		if(abcd != originalValue) {
			throw new IllegalStateException("SubSubWidget value does not match read value (" + abcd + ", " + originalValue + ")");
		}
		inputStream.readEndBlock();
	}


	@Override
	public void writeData(DataTransferOutput outputStream) throws IOException {
		outputStream.writeStartBlock(id);
		outputStream.write("info", new byte[] {(byte)((abcd >>> 24) & 0xFF), (byte)((abcd >>> 16) & 0xFF), (byte)((abcd >>> 8) & 0xFF), (byte)(abcd & 0xFF)});
		outputStream.writeEndBlock();
	}

}
