package xml;

import java.io.IOException;

import dataTransfer.DataTransferOutput;

/** A converter for an {@link XmlOutput} stream that converts it to a {@link DataTransferOutput}
 * stream. This is possible by ignoring the {@code id} parameter of the XmlOutput stream's write
 * method calls.
 * @author TeamworkGuy2
 * @since 2013-11-1
 */
public class XmlTransferOutput implements DataTransferOutput {
	private XmlOutput output;


	public XmlTransferOutput(XmlOutput output) {
		this.output = output;
	}


	@Override
	public void write(int id, String name, byte[] b) throws IOException {
		output.write(name, b);
	}


	@Override
	public void write(int id, String name, byte[] b, int off, int len) throws IOException {
		output.write(name, b, off, len);
	}


	@Override
	public void writeBoolean(int id, String name, boolean v) throws IOException {
		output.writeBoolean(name, v);
	}


	@Override
	public void writeByte(int id, String name, byte v) throws IOException {
		output.writeByte(name, v);
	}


	@Override
	public void writeChar(int id, String name, char v) throws IOException {
		output.writeChar(name, v);
	}


	@Override
	public void writeDouble(int id, String name, double v) throws IOException {
		output.writeDouble(name, v);
	}


	@Override
	public void writeFloat(int id, String name, float v) throws IOException {
		output.writeFloat(name, v);
	}


	@Override
	public void writeInt(int id, String name, int v) throws IOException {
		output.writeInt(name, v);
	}


	@Override
	public void writeLong(int id, String name, long v) throws IOException {
		output.writeLong(name, v);
	}


	@Override
	public void writeShort(int id, String name, short v) throws IOException {
		output.writeShort(name, v);
	}


	@Override
	public void writeUTF(int id, String name, String s) throws IOException {
		output.writeUTF(name, s);
	}


	@Override
	public void writeOpeningBlock(int id, String name) throws IOException {
		output.writeOpeningBlock(name);
	}


	@Override
	public void writeOpeningBlock(int id, String name, String descriptor) throws IOException {
		output.writeOpeningBlock(name, descriptor);
	}


	@Override
	public void writeClosingBlock() throws IOException {
		output.writeClosingBlock();
	}


	@Override
	public void close() throws IOException {
		output.close();
	}

}
