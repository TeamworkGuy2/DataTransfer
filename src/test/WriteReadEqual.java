package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import base.DataTransferInput;
import base.DataTransferOutput;
import base.DataTransferableFactory;
import base.DataTransferableFormat;
import examples.Employee;
import examples.SubWidget;
import examples.Widget;

/**
 * @author TeamworkGuy2
 * @since 2014-12-19
 */
public class WriteReadEqual {

	@Test
	public void checkWriteRead() throws IOException {
		Charset charset = Charset.forName("UTF-8");

		checkWriteReadOfFormat(DataTransferableFormat.XML, charset);
		checkWriteReadOfFormat(DataTransferableFormat.JSON, charset);
	}


	public void checkWriteReadOfFormat(DataTransferableFormat format, Charset charset) throws IOException {
		String formatName = format.name().toLowerCase();
		String fileName = formatName + "_test." + formatName;
		File file = new File(fileName);

		//JsonTest test = new JsonTest(file, charset);
		ReadWriteTest.writeFile(format, file);
		System.out.println("successfully write " + format + " data to " + file);
		ReadWriteTest.readFile(format, file);
		System.out.println("successfully read " + format + " data to " + file);

		File employeeFile = new File("stream_emp.txt");

		Employee emplOut = Employee.createEmployee();
		DataTransferOutput out = DataTransferableFactory.createWriter(format, employeeFile, true);
		emplOut.writeData(out);
		out.close();

		DataTransferInput in = DataTransferableFactory.createReader(format, employeeFile, true);
		Employee emplIn = new Employee();
		emplIn.readData(in);
		in.close();

		Assert.assertTrue("original: " + emplOut + ", saved/read: " + emplIn, emplIn.equals(emplOut));

		File widgetFile = new File("widget." + formatName);

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

		System.out.println("original: " + w + ",\nsaved/read: " + copy);
		Assert.assertTrue("original: " + w + ", saved/read: " + copy, w.equals(copy));
	}

}
