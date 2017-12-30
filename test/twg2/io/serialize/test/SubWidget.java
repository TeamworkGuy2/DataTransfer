package twg2.io.serialize.test;

import java.io.IOException;

import twg2.io.serialize.base.DataTransferable;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;

/** SubWidget test class for testing the transfer protocol package
 * @author TeamworkGuy2
 * @since 2013-8-5
 */
public class SubWidget implements DataTransferable {
	private static String classHashCode = "SubWidget";
	private SubSubWidget subItem1 = new SubSubWidget();
	private SubSubWidget subItem2 = new SubSubWidget();
	private int value = 1023;


	@Override
	public void readData(DataTransferInput input) throws IOException {
		input.readStartBlock(classHashCode);
		subItem1.readData(input);
		input.readInt("value");
		subItem2.readData(input);
		input.readEndBlock();
	}


	@Override
	public void writeData(DataTransferOutput output) throws IOException {
		output.writeStartBlock(classHashCode);
		subItem1.writeData(output);
		output.writeInt("value", value);
		subItem2.writeData(output);
		output.writeEndBlock();
	}

}
