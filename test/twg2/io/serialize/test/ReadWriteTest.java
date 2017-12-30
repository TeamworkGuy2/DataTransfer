package twg2.io.serialize.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import twg2.io.serialize.base.DataElement;
import twg2.io.serialize.base.DataTransferFormat;
import twg2.io.serialize.base.DataTransferableFactory;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;
import twg2.io.serialize.examples.Employee;
import twg2.io.serialize.examples.SubWidget;
import twg2.io.serialize.examples.Widget;

/**
 * @author TeamworkGuy2
 * @since 2014-9-9
 */
public class ReadWriteTest {
	private static final Charset charset = Charset.forName("UTF-8");


	@Test
	public void checkJsonCustom() throws IOException {
		checkWriteReadCustom(DataTransferFormat.JSON, charset);
	}


	@Test
	public void checkJsonEmployee() throws IOException {
		checkWriteReadEmployee(DataTransferFormat.JSON, charset);
	}


	@Test
	public void checkXmlCustom() throws IOException {
		checkWriteReadCustom(DataTransferFormat.XML, charset);
	}


	@Test
	public void checkXmlEmployee() throws IOException {
		checkWriteReadEmployee(DataTransferFormat.XML, charset);
	}


	public void checkWriteReadCustom(DataTransferFormat format, Charset charset) throws IOException {
		String formatName = format.name().toLowerCase();
		File file = new File("rsc/" + formatName + "_test." + formatName);

		DataTransferOutput out = DataTransferableFactory.createWriter(format, file, true);
		writeDataStream(out);

		DataTransferInput in = DataTransferableFactory.createReader(format, file, true);
		readDataStream(in);
	}


	public void checkWriteReadEmployee(DataTransferFormat format, Charset charset) throws IOException {
		File employeeFile = new File("rsc/stream_employee." + format.name().toLowerCase());
		File widgetFile = new File("rsc/widget." + format.name().toLowerCase());

		Employee emplOut = Employee.createEmployee();
		DataTransferOutput out = DataTransferableFactory.createWriter(format, employeeFile, true);
		emplOut.writeData(out);
		out.close();

		DataTransferInput in = DataTransferableFactory.createReader(format, employeeFile, true);
		Employee emplIn = new Employee();
		emplIn.readData(in);
		in.close();

		Assert.assertTrue("original: " + emplOut + ", saved/read: " + emplIn, emplIn.equals(emplOut));

		// The object to write
		Widget w = new Widget("Alpha", 42, new SubWidget[] {
				new SubWidget(new String[] {"A", "02", "C"}, "first sub widget"),
				new SubWidget(new String[] {"04", "E"}, "special 2 item sub widget"),
				new SubWidget(new String[] {"06", "G", "G6"}, ""),
				new SubWidget(new String[] {}, ""),
		});
		// Create a file output stream for the XML output
		// Create an XML output writer and convert it to data output writer
		out = DataTransferableFactory.createWriter(format, new FileOutputStream(widgetFile), charset);
		// Write the object
		w.writeData(out);
		out.close();

		// The object to read from the file
		Widget copy = new Widget();

		// Create the file input reader for the XML input
		// Create the XML input reader and convert it to a data input reader
		in = DataTransferableFactory.createReader(format, new FileInputStream(widgetFile), charset);
		// Read the object
		copy.readData(in);
		in.close();

		Assert.assertEquals("original: " + w + ", saved/read: " + copy, w, copy);
	}


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
		Assert.assertEquals("body", in.readNext().getName());
		Assert.assertEquals("arch", in.readStartBlock("arch").getName());
		DataElement tag = in.readNext();
		Assert.assertEquals("contents arch 1.1", tag.getContent());
		// read empty block
		//in.readStartBlock("vars");
		//in.readEndBlock();
		//Assert.assertEquals(in.getCurrentName(), "vars");
		in.readString("vars");

		Assert.assertEquals("contents arch 1.2", in.readNext().getContent());
		Assert.assertEquals("prim", in.readNext().getName());
		in.peekNext();
		Assert.assertEquals("" + 256, in.readNext().getContent());
		Assert.assertEquals("" + 4779.98f, in.readNext().getContent());
		in.readNext();
		in.readNext();
		Assert.assertEquals("steps", in.readNext().getName());
		tag = in.peekNext();
		Assert.assertEquals("V<map> upm, ul", in.readString("setting"));
		Assert.assertEquals("inter", in.readStartBlock("inter").getName());
		Assert.assertEquals("inner", in.readNext().getName());
		in.readNext();
		// skip interEntry
		in.readEndBlock();
		// write loop count element
		tag = in.readNext();
		// read loop 1
		List<String> loop1List = DataTransferableFactory.readBlock(in, "loop", "elem");
		int i = 0;
		for(String str : loop1List) {
			Assert.assertEquals("element-" + i, str);
			i++;
		}
		// read loop 2
		List<String> loop2List = DataTransferableFactory.readArray(in, "loop2");
		i = 0;
		for(String str : loop2List) {
			Assert.assertEquals("element-2-" + i, str);
			i++;
		}
		in.readEndBlock();
		in.close();
	}

}
