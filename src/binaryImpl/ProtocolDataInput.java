package binaryImpl;

import java.io.IOException;

import dataTransfer.DataTransferInput;

public class ProtocolDataInput implements DataTransferInput {
	private ProtocolInput input;

	public ProtocolDataInput(ProtocolInput input) {
		this.input = input;
	}

	@Override
	public void read(int id, String name, byte[] b) throws IOException {
		input.read(id, b);
	}

	@Override
	public void read(int id, String name, byte[] b, int off, int len) throws IOException {
		input.read(id, b, off, len);
	}

	@Override
	public boolean readBoolean(int id, String name) throws IOException {
		return input.readBoolean(id);
	}

	@Override
	public byte readByte(int id, String name) throws IOException {
		return input.readByte(id);
	}

	@Override
	public char readChar(int id, String name) throws IOException {
		return input.readChar(id);
	}

	@Override
	public double readDouble(int id, String name) throws IOException {
		return input.readDouble(id);
	}

	@Override
	public float readFloat(int id, String name) throws IOException {
		return input.readFloat(id);
	}

	@Override
	public int readInt(int id, String name) throws IOException {
		return input.readInt(id);
	}

	@Override
	public long readLong(int id, String name) throws IOException {
		return input.readLong(id);
	}

	@Override
	public short readShort(int id, String name) throws IOException {
		return input.readShort(id);
	}

	@Override
	public String readUTF(int id, String name) throws IOException {
		return input.readUTF(id);
	}

	@Override
	public void readOpeningBlock(int id, String name) throws IOException {
		input.readOpeningBlock(id);
	}

	@Override
	public ProtocolHeader readOpeningBlock() throws IOException {
		return input.readOpeningBlock();
	}

	@Override
	public ProtocolHeader getCurrentHeaderBlock() {
		return input.getCurrentHeaderBlock();
	}

	@Override
	public ProtocolHeader peekNextHeaderBlock() throws IOException {
		return input.peekNextHeaderBlock();
	}

	@Override
	public void readClosingBlock() throws IOException {
		input.readClosingBlock();
	}

	@Override
	public void close() throws IOException {
		input.close();
	}

}
