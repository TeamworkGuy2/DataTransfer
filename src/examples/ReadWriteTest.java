package examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import base.DataElement;
import base.DataTransferInput;
import base.DataTransferOutput;
import base.DataTransferableFactory;
import base.DataTransferableFormat;

/**
 * @author TeamworkGuy2
 * @since 2014-9-9
 */
public class ReadWriteTest {

	public static void writeDataStream(DataTransferOutput out) throws IOException {
		out.writeStartBlock("body");
			out.writeStartBlock("arch");
				out.writeString("string", "contents arch 1.1");
				//out.writeStartBlock("vars");
				//out.writeEndBlock();
				out.writeString("vars", "");
				out.writeString("entry", "contents arch 1.2");
				out.writeStartBlock("prim");
					out.writeInt("integer", 256);
					out.writeFloat("float", 4779.98f);
				out.writeEndBlock();
			out.writeEndBlock();
			out.writeBoolean("steps", true);
			out.writeString("setting", "V<map> upm, ul");
			out.writeStartBlock("inter");
				out.writeStartBlock("inner");
				out.writeEndBlock();
				out.writeString("interEntry", "inter 1.1");
			out.writeEndBlock();
			// write loop count element
			short loopCount = (short)(Math.random() * 100);
			out.writeShort("loopCount", loopCount);

			// write loop1
			List<String> loop1List = new ArrayList<>();
			for(int i = 0; i < loopCount; i++) {
				loop1List.add("element-" + i);
			}
			DataTransferableFactory.writeBlock(out, "loop", "elem", loop1List);

			// write loop2
			List<String> loop2List = new ArrayList<>();
			for(int i = 0; i < loopCount; i++) {
				loop2List.add("element-2-" + i);
			}
			DataTransferableFactory.writeArray(out, "loop2", loop2List);
		out.writeEndBlock();
		out.close();
	}


	public static void readDataStream(DataTransferInput in) throws IOException {
		checkStr(in.readNext().getName(), "body");
		checkStr(in.readStartBlock("arch").getName(), "arch");
		DataElement tag = in.readNext();
		checkStr(tag.getContent(), "contents arch 1.1");
		// read empty block
		//in.readStartBlock("vars");
		//in.readEndBlock();
		//checkStr(in.getCurrentName(), "vars");
		in.readString("vars");

		checkStr(in.readNext().getContent(), "contents arch 1.2");
		checkStr(in.readNext().getName(), "prim");
		in.peekNext();
		checkStr(in.readNext().getContent(), "" + 256);
		checkStr(in.readNext().getContent(), "" + 4779.98f);
		in.readNext();
		in.readNext();
		checkStr(in.readNext().getName(), "steps");
		tag = in.peekNext();
		checkStr(in.readString("setting"), "V<map> upm, ul");
		checkStr(in.readStartBlock("inter").getName(), "inter");
		checkStr(in.readNext().getName(), "inner");
		in.readNext();
		// skip interEntry
		in.readEndBlock();
		// write loop count element
		tag = in.readNext();
		// read loop 1
		List<String> loop1List = DataTransferableFactory.readBlock(in, "loop", "elem");
		int i = 0;
		for(String str : loop1List) {
			checkStr(str, "element-" + i);
			i++;
		}
		// read loop 2
		List<String> loop2List = DataTransferableFactory.readArray(in, "loop2");
		i = 0;
		for(String str : loop2List) {
			checkStr(str, "element-2-" + i);
			i++;
		}
		in.readEndBlock();
		in.close();
	}


	public static void writeFile(DataTransferableFormat format, File file) throws IOException {
		DataTransferOutput out = DataTransferableFactory.createWriter(format, file, true);
		writeDataStream(out);
	}


	public static void readFile(DataTransferableFormat format, File file) throws IOException {
		DataTransferInput in = DataTransferableFactory.createReader(format, file, true);
		readDataStream(in);
	}



	private static void checkStr(String in, String check) {
		if(!in.equals(check)) {
			throw new AssertionError("input '" + in + "' does not match expected '" + check + "'");
		}
	}

}
