package examples;

import java.io.IOException;
import java.util.Arrays;

import dataTransfer.DataTransferInput;
import dataTransfer.DataTransferOutput;
import dataTransfer.DataTransferable;

public class Widget implements DataTransferable {
	private String name;
	private int id;
	private String[] params;


	public Widget() {
	}


	public Widget(String name, int id, String[] params) {
		this.name = name;
		this.id = id;
		this.params = params;
	}


	@Override
	public void readData(DataTransferInput inputStream) throws IOException {
		inputStream.readOpeningBlock(0, "Widget");
		this.name = inputStream.readUTF(1, "name");
		id = inputStream.readInt(2, "id");
		final int paramCount = inputStream.readInt(3, "count");
		this.params = new String[paramCount];
		for(int i = 0; i < paramCount; i++) {
			this.params[i] = inputStream.readUTF(4, "param");
		}
		inputStream.readClosingBlock();
	}


	@Override
	public void writeData(DataTransferOutput outputStream) throws IOException {
		outputStream.writeOpeningBlock(0, "Widget");
		outputStream.writeUTF(1, "name", this.name);
		outputStream.writeInt(2, "id", this.id);
		final int paramCount = params.length;
		outputStream.writeInt(3, "count", paramCount);
		for(int i = 0; i < paramCount; i++) {
			outputStream.writeUTF(4, "param", this.params[i]);
		}
		outputStream.writeClosingBlock();
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("Widget(");
		str.append(name);
		str.append(' ');
		str.append(id);
		str.append(' ');
		str.append(Arrays.toString(params));
		str.append(')');
		return str.toString();
	}

}
