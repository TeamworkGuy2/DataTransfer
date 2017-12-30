package twg2.io.serialize.examples;

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

import twg2.io.serialize.base.DataElement;
import twg2.io.serialize.base.DataTransferFormat;
import twg2.io.serialize.base.DataTransferableFactory;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.text.stringEscape.StringEscape;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class MainIo {


	/** Read an Externalizable Java object from a file
	 * @param externalizableObj - the object to initialize with the Externalizable object found in the file
	 * @param file - file to read the Externalizable object from
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
			StringEscape.escapeChar(str, '\\', '\\', '"', src);
			System.out.println("wrap: " + src.toString());
			StringEscape.unescapeChar(src, 0, '\\', '"', dst);
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
		File file = new File("rsc/widget.json");
		printEvents(DataTransferableFactory.createReader(DataTransferFormat.JSON, file, true));
	}

}
