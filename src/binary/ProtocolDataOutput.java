package binary;

import java.io.IOException;

import dataTransfer.DataTransferOutput;

/** A wrapper that converts a {@link ProtocolOutput} stream to a {@link DataTransferOutput} stream.
 * This can be done relatively safely without data loss by ignoring the {@code name} parameter
 * from the DataTransferOutput stream's write methods when writing to the underlying ProtocolOutput stream.<br/>
 * For this data to be written without loss, the {@code id} parameters passed in by write calls must be unique.
 * @author TeamworkGuy2
 * @since 2013-9-22
 * @see DataTransferOutput
 */
public class ProtocolDataOutput implements DataTransferOutput {
	private ProtocolOutput output;


	public ProtocolDataOutput(ProtocolOutput output) {
		this.output = output;
	}


	@Override
	public void write(int id, String name, byte[] b) throws IOException {
		output.write(id, b);
	}


	@Override
	public void write(int id, String name, byte[] b, int off, int len) throws IOException {
		output.write(id, b, off, len);
	}


	@Override
	public void writeBoolean(int id, String name, boolean v) throws IOException {
		output.writeBoolean(id, v);
	}


	@Override
	public void writeByte(int id, String name, byte v) throws IOException {
		output.writeByte(id, v);
	}


	@Override
	public void writeChar(int id, String name, char v) throws IOException {
		output.writeChar(id, v);
	}


	@Override
	public void writeDouble(int id, String name, double v) throws IOException {
		output.writeDouble(id, v);
	}


	@Override
	public void writeFloat(int id, String name, float v) throws IOException {
		output.writeFloat(id, v);
	}


	@Override
	public void writeInt(int id, String name, int v) throws IOException {
		output.writeInt(id, v);
	}


	@Override
	public void writeLong(int id, String name, long v) throws IOException {
		output.writeLong(id, v);
	}


	@Override
	public void writeShort(int id, String name, short v) throws IOException {
		output.writeShort(id, v);
	}


	@Override
	public void writeUTF(int id, String name, String s) throws IOException {
		output.writeUTF(id, s);
	}


	@Override
	public void writeOpeningBlock(int id, String name) throws IOException {
		output.writeOpeningBlock(id);
	}


	@Override
	public void writeOpeningBlock(int id, String name, String descriptor) throws IOException {
		output.writeOpeningBlock(id, descriptor);
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
