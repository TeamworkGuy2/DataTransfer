package xml;

import java.io.IOException;

import dataTransfer.DataHeader;
import dataTransfer.DataTransferInput;

/** A converter for an {@link XmlInput} stream that converts it to a {@link DataTransferInput}
 * stream. This is possible by ignoring the {@code id} parameter of the XmlInput stream's read
 * method calls.
 * @author TeamworkGuy2
 * @since 2013-11-1
 */
public class XmlTransferInput implements DataTransferInput {
	private XmlInput input;


	public XmlTransferInput(XmlInput input) {
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
	public XmlTag peekNextBlock() throws IOException {
		return input.peekNextBlock();
	}


	@Override
	public XmlTag readNextBlock() throws IOException {
		return input.readNextBlock();
	}


	@Override
	public DataHeader readOpeningBlock(int id, String name) throws IOException {
		return input.readOpeningBlock(name);
	}


	@Override
	public XmlTag getCurrentBlockHeader() {
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
