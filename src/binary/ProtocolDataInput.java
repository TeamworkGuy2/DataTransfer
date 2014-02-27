package binary;

import java.io.IOException;

import dataTransfer.DataTransferInput;

/** A wrapper that converts a {@link ProtocolInput} stream into a {@link DataTransferInput} stream.
 * This can be done without data loss since a ProtocolInput stream provides an auxiliary {@code name} parameter
 * when writing to the stream which the DataTransferInput stream can safely drop.
 * @author TeamworkGuy2
 * @since 2013-9-22
 * @see DataTransferInput
 */
public class ProtocolDataInput implements DataTransferInput {
	private ProtocolInput input;


	/**
	 * @param input the {@link ProtocolInput} stream to read from when this
	 * wrapper's read methods are called.
	 */
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
	public ProtocolHeader peekNextBlock() throws IOException {
		return input.peekNextBlock();
	}


	@Override
	public ProtocolHeader readNextBlock() throws IOException {
		return input.readNextBlock();
	}


	@Override
	public ProtocolHeader readOpeningBlock(int id, String name) throws IOException {
		return input.readOpeningBlock(id);
	}


	@Override
	public ProtocolHeader getCurrentBlockHeader() {
		return input.getCurrentBlockHeader();
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
