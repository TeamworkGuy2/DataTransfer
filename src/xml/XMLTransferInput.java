package xml;

import java.io.IOException;

import dataTransfer.DataHeader;
import dataTransfer.DataTransferInput;

public class XMLTransferInput implements DataTransferInput {
	private XMLInput input;


	public XMLTransferInput(XMLInput input) {
		this.input = input;
	}


	@Override
	public void read(int id, String name, byte[] b) throws IOException {
		input.read(name, b);
	}


	@Override
	public void read(int id, String name, byte[] b, int off, int len) throws IOException {
		input.read(name, b, off, len);
	}


	@Override
	public boolean readBoolean(int id, String name) throws IOException {
		return input.readBoolean(name);
	}


	@Override
	public byte readByte(int id, String name) throws IOException {
		return input.readByte(name);
	}


	@Override
	public char readChar(int id, String name) throws IOException {
		return input.readChar(name);
	}


	@Override
	public double readDouble(int id, String name) throws IOException {
		return input.readDouble(name);
	}


	@Override
	public float readFloat(int id, String name) throws IOException {
		return input.readFloat(name);
	}


	@Override
	public int readInt(int id, String name) throws IOException {
		return input.readInt(name);
	}


	@Override
	public long readLong(int id, String name) throws IOException {
		return input.readLong(name);
	}


	@Override
	public short readShort(int id, String name) throws IOException {
		return input.readShort(name);
	}


	@Override
	public String readUTF(int id, String name) throws IOException {
		return input.readUTF(name);
	}


	@Override
	public XMLTag peekNextBlock() throws IOException {
		return input.peekNextBlock();
	}


	@Override
	public XMLTag readNextBlock() throws IOException {
		return input.readNextBlock();
	}


	@Override
	public DataHeader readOpeningBlock(int id, String name) throws IOException {
		return input.readOpeningBlock(name);
	}


	@Override
	public XMLTag getCurrentBlockHeader() {
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
