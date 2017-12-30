package twg2.io.serialize.test;

import java.io.IOException;

import twg2.io.serialize.base.DataTransferable;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;

/** Widget test class for testing the transfer protocol package
 * @author TeamworkGuy2
 * @since 2013-7-18
 */
public class Widget implements DataTransferable {
	private static final String blockId = "Widget";
	private int value;
	private float a = (float)(Math.PI/2);
	private float b = (float)(Math.PI);
	private float c = (float)(Math.PI*2);
	private String name = "test string";
	private SubWidget subWidget = new SubWidget();


	@Override
	public void readData(DataTransferInput inputStream) throws IOException {
		inputStream.readStartBlock(blockId);
		name = inputStream.readString("widgetName");
		subWidget.readData(inputStream);
		value = inputStream.readInt("widgetValue");
		a = inputStream.readFloat("a");
		b = inputStream.readFloat("b");
		c = inputStream.readFloat("c");
		inputStream.readEndBlock();
	}


	@Override
	public void writeData(DataTransferOutput outputStream) throws IOException {
		outputStream.writeStartBlock(blockId);
		outputStream.writeString("widgetName", name);
		subWidget.writeData(outputStream);
		outputStream.writeInt("widgetValue", value);
		outputStream.writeFloat("a", a);
		outputStream.writeFloat("b", b);
		outputStream.writeFloat("c", c);
		outputStream.writeEndBlock();
	}


	@Override
	public String toString() {
		return "Widget: value=" + value + ", a=" + a + ", b=" + b + ", c=" + c + ", name=" + name;
	}

}
