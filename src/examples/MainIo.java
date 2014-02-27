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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import xml.XmlHandler;
import xml.XmlInputSimple;

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


	public static void printPlainXmlEvents(File file, Charset charset) throws IOException, XMLStreamException {
		XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
		FileInputStream stream = new FileInputStream(file);
		InputStream input = new BufferedInputStream(stream);
		XMLStreamReader reader = xmlFactory.createXMLStreamReader(input, charset.name());

		int event = reader.getEventType();
		while(event != XMLStreamConstants.END_DOCUMENT) {
			event = reader.next();
			System.out.print(XmlHandler.toString(event));
			if(event == XMLStreamConstants.CHARACTERS) {
				System.out.print(" [" + reader.getText() + "]");
			}
			else if(event == XMLStreamConstants.START_ELEMENT || event == XMLStreamConstants.END_ELEMENT) {
				System.out.print(" " + reader.getLocalName());
			}
			System.out.println();
		}
	}


	public static void printSimpleXmlReader(File file, Charset charset) throws XMLStreamException, FileNotFoundException {
		XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
		FileInputStream stream = new FileInputStream(file);
		InputStream input = new BufferedInputStream(stream);
		XMLStreamReader reader = xmlFactory.createXMLStreamReader(input, charset.name());
		XmlInputSimple simpleXml = new XmlInputSimple(reader);

		boolean found = false;
		String tagName = null;
		while(!simpleXml.isEmpty()) {
			String openingName = simpleXml.readTag();
			found = simpleXml.finishElement(openingName);
			tagName = simpleXml.getLastTagName();
			System.out.println((found ? "element " : (simpleXml.isLastTagOpening() ? "start " : "end ")) + tagName + ": " + (found ? simpleXml.getLastElementContents() : ""));
		}
	}


	public static void main(String[] args) throws IOException, XMLStreamException {
		//com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl;
		Charset charset = Charset.forName("UTF-8");

		File file = new File("xml_test.xml");
		printPlainXmlEvents(file, charset);

		System.out.println("====");
		printSimpleXmlReader(file, charset);

		/*
		// The object to write
		Widget w = new Widget("Alpha", 42, new String[] {"A", "02", "C", "04", "E", "06", "G"});

		File dataFile = new File("widget.dat");
		// Create a file output stream for the XML output
		OutputStream outStream = new FileOutputStream(dataFile);
		// Create an XML output writer and convert it to data output writer
		XmlOutput outXml = XmlHandler.createXMLOutput(outStream, true, charset, true);
		DataTransferOutput out = new XmlTransferOutput(outXml);

		// Write the object
		w.writeData(out);

		// Close the data output writer and it's underlying XML stream which
		// closes the underlying file stream
		out.close();

		// The object to read from the file
		Widget copy = new Widget();

		System.out.print("Copy empty:\t ");
		System.out.println(copy);

		// Create the file input reader for the XML input
		InputStream inStream = new FileInputStream(dataFile);
		// Create the XML input reader and convert it to a data input reader
		DataTransferInput in = new XmlTransferInput(XmlHandler.createXMLInput(inStream, true, charset, true, true, true));

		// Read the object
		copy.readData(in);

		in.close();

		System.out.print("Original:\t ");
		System.out.println(w);

		System.out.print("Copy parsed:\t ");
		System.out.println(copy);
		*/
	}

}
