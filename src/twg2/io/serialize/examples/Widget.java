package twg2.io.serialize.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import twg2.io.serialize.base.DataTransferable;
import twg2.io.serialize.base.DataTransferableFactory;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;

public class Widget implements DataTransferable {
	private String name;
	private int id;
	private List<SubWidget> subObjs = new ArrayList<>();
	private String type = String.class.getName();


	public Widget() {
	}


	public Widget(String name, int id, SubWidget[] subWidgets) {
		this.name = name;
		this.id = id;
		Collections.addAll(this.subObjs, subWidgets);
	}


	public Widget(String name, int id, Collection<SubWidget> subWidgets) {
		this.name = name;
		this.id = id;
		this.subObjs.addAll(subWidgets);
	}


	@Override
	public void readData(DataTransferInput inputStream) throws IOException {
		inputStream.readStartBlock("Widget");
		this.name = inputStream.readString("name");
		inputStream.readStartBlock("meta-data");
			type = inputStream.readString("type");
			inputStream.readBoolean("bool");
		inputStream.readEndBlock();
		id = inputStream.readInt("id");
		final int paramCount = inputStream.readInt("count");
		this.subObjs = DataTransferableFactory.readBlock(inputStream, "subObjs", new SubWidget.SubWidgetFactory());
		if(this.subObjs.size() != paramCount) {
			throw new IOException("expected " + paramCount + " sub objects, only parsed " + this.subObjs.size());
		}
		inputStream.readEndBlock();
	}


	@Override
	public void writeData(DataTransferOutput outputStream) throws IOException {
		outputStream.writeStartBlock("Widget");
		outputStream.writeString("name", this.name);
		outputStream.writeStartBlock("meta-data");
			outputStream.writeString("type", type);
			outputStream.writeBoolean("bool", Boolean.TRUE);
		outputStream.writeEndBlock();
		outputStream.writeInt("id", this.id);
		final int paramCount = subObjs.size();
		outputStream.writeInt("count", paramCount);
		DataTransferableFactory.writeBlock(outputStream, "subObjs", this.subObjs);
		outputStream.writeEndBlock();
	}


	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Widget)) {
			return false;
		}
		Widget w = (Widget)obj;
		boolean result = this.id == w.id && this.name.equals(w.name) && this.type.equals(w.type);
		if(this.subObjs.size() != w.subObjs.size()) {
			return false;
		}
		for(int i = 0, len = this.subObjs.size(); i < len; i++) {
			result &= this.subObjs.get(i).equals(w.subObjs.get(i));
			if(result == false) {
				return false;
			}
		}
		return result;
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("Widget { ");
		str.append("name: " + name + ", ");
		str.append("id: " + id + ", ");
		str.append(subObjs.toString());
		str.append('}');
		return str.toString();
	}

}
