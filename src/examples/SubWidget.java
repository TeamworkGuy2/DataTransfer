package examples;

import java.io.IOException;
import java.util.Arrays;

import base.DataTransferFactory;
import base.DataTransferInput;
import base.DataTransferOutput;
import base.DataTransferable;
import base.DataTransferableFactory;

/**
 * @author TeamworkGuy2
 * @since 2014-9-28
 */
public class SubWidget implements DataTransferable {
	private String[] arguments;
	private String description;


	public SubWidget() {
	}


	public SubWidget(String[] arguments, String description) {
		super();
		this.arguments = arguments;
		this.description = description;
	}


	public String[] getArguments() {
		return arguments;
	}


	public void setArguments(String[] arguments) {
		this.arguments = arguments;
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
		arguments = DataTransferableFactory.readBlock(in, "args", "arg").toArray(new String[0]);
		description = in.readString("description");
		in.readEndBlock();
	}


	@Override
	public void writeData(DataTransferOutput out) throws IOException {
		out.writeStartBlock("SubWidget");
		DataTransferableFactory.writeBlock(out, "args", "arg", Arrays.asList(this.arguments));
		out.writeString("description", this.description != null ? this.description : "");
		out.writeEndBlock();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arguments);
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
		if (!Arrays.equals(arguments, other.arguments))
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
		return "SubWidget { arguments: " + Arrays.toString(arguments)
				+ ", description: " + description + " }";
	}


	public static class SubWidgetFactory implements DataTransferFactory<SubWidget> {

		@Override
		public void writeData(DataTransferOutput out, SubWidget obj) throws IOException {
			out.writeStartBlock("SubWidget");
			DataTransferableFactory.writeBlock(out, "args", "arg", Arrays.asList(obj.arguments));
			out.writeString("description", obj.description != null ? obj.description : "");
			out.writeEndBlock();
		}

		@Override
		public SubWidget readData(DataTransferInput in) throws IOException {
			in.readStartBlock("SubWidget");
			SubWidget obj = new SubWidget();
			obj.arguments = DataTransferableFactory.readBlock(in, "args", "arg").toArray(new String[0]);
			obj.description = in.readString("description");
			in.readEndBlock();
			return obj;
		}
		
	}

}
