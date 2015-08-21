package twg2.io.serialize.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import twg2.io.serialize.base.DataTransferFactory;
import twg2.io.serialize.base.DataTransferable;
import twg2.io.serialize.base.DataTransferableFactory;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;

/**
 * @author TeamworkGuy2
 * @since 2014-9-28
 */
public class SubWidget implements DataTransferable {
	private List<String> arguments;
	private List<String> argumentsIm;
	private String description;


	public SubWidget() {
		setArguments(new String[0]);
	}


	public SubWidget(String[] arguments, String description) {
		super();
		this.description = description;
		setArguments(arguments);
	}


	public SubWidget(Collection<String> arguments, String description) {
		super();
		this.description = description;
		setArguments(arguments);
	}


	public List<String> getArguments() {
		return argumentsIm;
	}


	public void setArguments(String[] arguments) {
		if(this.arguments == null) {
			this.arguments = new ArrayList<>();
			this.argumentsIm = Collections.unmodifiableList(this.arguments);
		}
		this.arguments.clear();
		Collections.addAll(this.arguments, arguments);
	}


	public void setArguments(Collection<String> arguments) {
		if(this.arguments == null) {
			this.arguments = new ArrayList<>();
			this.argumentsIm = Collections.unmodifiableList(this.arguments);
		}
		this.arguments.clear();
		this.arguments.addAll(arguments);
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public void readData(DataTransferInput in) throws IOException {
		in.readStartBlock("SubWidget");
		setArguments(DataTransferableFactory.readBlock(in, "args", "arg").toArray(new String[0]));
		description = in.readString("description");
		in.readEndBlock();
	}


	@Override
	public void writeData(DataTransferOutput out) throws IOException {
		out.writeStartBlock("SubWidget");
		DataTransferableFactory.writeBlock(out, "args", "arg", this.argumentsIm);
		out.writeString("description", this.description != null ? this.description : "");
		out.writeEndBlock();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arguments.hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof SubWidget)) {
			return false;
		}
		SubWidget other = (SubWidget) obj;
		if (!this.arguments.equals(other.arguments))
			return false;
		if (description == null && other.description != null) {
			return false;
		} else if (!description.equals(other.description)) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "SubWidget { arguments: " + this.arguments.toString()
				+ ", description: " + description + " }";
	}


	public static class SubWidgetFactory implements DataTransferFactory<SubWidget> {

		@Override
		public void writeData(DataTransferOutput out, SubWidget obj) throws IOException {
			out.writeStartBlock("SubWidget");
			DataTransferableFactory.writeBlock(out, "args", "arg", obj.arguments);
			out.writeString("description", obj.description != null ? obj.description : "");
			out.writeEndBlock();
		}

		@Override
		public SubWidget readData(DataTransferInput in) throws IOException {
			in.readStartBlock("SubWidget");
			SubWidget obj = new SubWidget();
			obj.setArguments(DataTransferableFactory.readBlock(in, "args", "arg").toArray(new String[0]));
			obj.description = in.readString("description");
			in.readEndBlock();
			return obj;
		}
		
	}

}
