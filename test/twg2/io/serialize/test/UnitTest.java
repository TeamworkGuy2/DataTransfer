package twg2.io.serialize.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;
import twg2.io.serialize.json.JsonReader;
import twg2.io.serialize.json.JsonWriter;

/** A Unit test for the transfer protocol package
 * @author TeamworkGuy2
 * @since 2013-7-18
 */
public class UnitTest {
	private static final String protocolHeaderName = "ProtocolFileTransferName";
	private InputStream inputStream;
	private OutputStream outputStream;
	private Widget widget;

	public UnitTest(InputStream inputStream, OutputStream outputStream, Widget widget) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.widget = widget;
	}

	public void testProtocolInputStream() {
		testProtocolStreamReader();
	}


	public void testProtocolOutputStream() {
		testProtocolStreamWriter();
	}


	public void testProtocolStreamReader() {
		DataInputStream in = null;
		DataTransferInput reader = null;
		try {
			in = new DataInputStream(inputStream);
			reader = new JsonReader(in);
			reader.readStartBlock(protocolHeaderName);
			widget.readData(reader);
			reader.readEndBlock();
		} catch (IOException e) {
			System.err.println("Error testing ProtocolInputReader");
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
				in.close();
			} catch (IOException e) {
				System.err.println("Error closing ProtocolInputReader while testing");
				e.printStackTrace();
			}
		}
	}


	public void testProtocolStreamWriter() {
		DataOutputStream out = null;
		DataTransferOutput writer = null;
		try {
			out = new DataOutputStream(outputStream);
			writer = new JsonWriter(out);
			writer.writeStartBlock(protocolHeaderName);
			widget.writeData(writer);
		} catch (IOException e) {
			System.err.println("Error testing ProtocolOutputWriter");
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
				out.close();
			} catch (IOException e) {
				System.err.println("Error closing ProtocolOutputWriter while testing");
				e.printStackTrace();
			}
		}
	}


	public void testDataProtocol() {
		/*DataHeader header = new DataHeaderImpl(null, 23);
		DataHeader header2 = new DataHeaderImpl("item", 23);
		System.out.println(header.equals(header2));
		System.out.println(equals(header, header2));
		header = new DataHeaderImpl(null, -1);
		header2 = new DataHeaderImpl(null, -1);
		System.out.println(header.equals(header2));
		System.out.println(equals(header, header2));
		header = new DataHeaderImpl("item", -1);
		header2 = new DataHeaderImpl("item", 942);
		System.out.println(header.equals(header2));
		System.out.println(equals(header, header2));
		header = new DataHeaderImpl("item", 4);
		header2 = new DataHeaderImpl(null, -1);
		System.out.println(header.equals(header2));
		System.out.println(equals(header, header2));*/
	}


	public static void main(String[] args) throws Exception {
		File file = new File("rsc/test.dat");

		//HeaderUnitTest.testHeaders();

		System.out.println(file.getAbsolutePath() + ", " + file.isFile());
		Widget widget = new Widget();
		//InputStream input = new FileInputStream(file);
		//OutputStream output = new FileOutputStream(file);
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream(64);
		UnitTest test = new UnitTest(input, output, widget);
		System.out.println(widget);
		test.testProtocolOutputStream();
		byte[] data = output.toByteArray();
		test.inputStream = new ByteArrayInputStream(data);
		System.out.println();
		test.testProtocolInputStream();
		System.out.println(widget);
	}

}
