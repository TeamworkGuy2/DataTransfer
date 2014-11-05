package examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import base.DataElement;
import base.DataTransferInput;
import base.DataTransferOutput;
import base.DataTransferableFactory;
import base.DataTransferableFormat;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class MainIo {


	/** Read an Externalizable Java object from a file
	 * @param externalizableObj - the object to initialize with the Externalizable object found in the file
	 * @param file - file to read the Externalizable object from
	 * @throws SAXException if the XML parser encounters an error
	 * @throws FileNotFoundException if the specified XML file to read can not be found 
	 * @throws IOException if an error occurs while reading the XML file
	 * @throws ClassNotFoundException if the class of the object being deserialized could not be found
	 */
	public static Externalizable readExternal(Externalizable externalizableObj, File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		InputStream inputSource = new BufferedInputStream(new FileInputStream(file));
		ObjectInputStream in = new ObjectInputStream(inputSource);
		// Recover the object
		externalizableObj = (Externalizable)in.readObject();
		//externalizableObj.readExternal(in);
		in.close();
		inputSource.close();
		return externalizableObj;
	}


	/** Write an Externalizable Java object to a file
	 * @param externalizableObj - the Externalizable object to write to the file
	 * @param file - the file to write the object to
	 * @return the Externalizable object written to the file
	 * @throws IOException if there is an error writing the object to the file
	 */
	public static Externalizable writeExternal(Externalizable externalizableObj, File file) throws IOException {
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
		ObjectOutputStream out = new ObjectOutputStream(outputStream);
		// Save the object
		out.writeObject(externalizableObj);
		//externalizableObj.writeExternal(out);
		out.close();
		outputStream.close();
		return externalizableObj;
	}


	public static void wrapUnwrapStrTest() {
		String[] strs = {
				"\"alpha",
				"\\bet\"a",
				"\"\"charlie\"\"",
				"\\",
				"\\\\",
				"\"",
				"\"\"",
		};

		StringBuilder src = new StringBuilder();
		StringBuilder dst = new StringBuilder();
		for(String str : strs) {
			System.out.println("str : " + str);
			DataTransferableFactory.wrapChar(str, '\\', '\\', '"', src);
			System.out.println("wrap: " + src.toString());
			DataTransferableFactory.unwrapChar(src, 0, '\\', '"', dst);
			System.out.println("unwp: " + dst.toString());
			src.setLength(0);
			dst.setLength(0);
		}
	}


	public static void printPlainJsonEvents(File file, Charset charset) throws JsonParseException, FileNotFoundException, IOException {
		JsonParser jsonIn = new JsonFactory().createParser(new FileInputStream(file));
		JsonToken token = jsonIn.nextToken();
		while(token != null) {
			System.out.println(jsonIn.getCurrentName() + ": " + token);
			token = jsonIn.nextToken();
		}
	}


	public static void printEvents(DataTransferInput in) throws IOException {
		DataElement token = in.readNext();
		System.out.println("==Print data transferable events==");
		while(token != null) {
			System.out.println((token.isElement() ? "ELEMENT" : (token.isStartBlock() ? "START" : token.isEndBlock() ? "END" : "UNKNOWN"))
					+ " " + in.getCurrentName() + ": " + token.getContent());
			token = in.readNext();
		}
		System.out.println("==End print data transferable events==");
	}


	public static void main(String[] args) throws IOException {
		String src = "a \\\"block\\\" char '\\\"'";
		StringBuilder strDst = new StringBuilder();
		System.out.println(DataTransferableFactory.unwrapChar(src, 0, '\\', '"', strDst) + ": " + strDst.toString());
		//com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl;
		Charset charset = Charset.forName("UTF-8");
		DataTransferableFormat format = DataTransferableFormat.XML;
		String formatName = format.name().toLowerCase();
		String fileName = formatName + "_test." + formatName;
		File file = new File(fileName);

		//JsonTest test = new JsonTest(file, charset);
		ReadWriteTest.writeFile(format, file);
		System.out.println("successfully wrote " + format + " data to " + file);
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

		System.out.println("Employee write equal read: " + emplIn.equals(emplOut));


		File widgetFile = new File("widget." + formatName);

		// The object to write
		Widget w = new Widget("Alpha", 42, new SubWidget[] {
				new SubWidget(new String[] {"A", "02", "C"}, "first sub widget"),
				new SubWidget(new String[] {"04", "E"}, "special 2 item sub widget"),
				new SubWidget(new String[] {"06", "G", "G6"}, null),
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

		System.out.println("Copy empty:\t " + copy);

		// Create the file input reader for the XML input
		// Create the XML input reader and convert it to a data input reader
		in = DataTransferableFactory.createReader(format, new FileInputStream(widgetFile), charset);
		// Read the object
		copy.readData(in);
		in.close();

		System.out.println("Widget write equal read: " + (w.equals(copy)));
		System.out.println("Original:    " + w);
		System.out.println("Copy parsed: " + w);
	}

}
