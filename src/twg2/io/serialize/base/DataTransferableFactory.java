package twg2.io.serialize.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;
import twg2.io.serialize.base.writer.WritableObject;
import twg2.io.serialize.json.JsonReader;
import twg2.io.serialize.json.JsonWriter;
import twg2.io.serialize.xml.XmlHandler;
import twg2.text.stringUtils.StringEscape;

/** A set of static functions for creating {@link DataTransferInput} and {@link DataTransferOutput} streams
 * @author TeamworkGuy2
 * @since 2014-9-27
 */
public class DataTransferableFactory {
	private static Charset defaultCharset = Charset.forName("UTF-8");


	public static final Charset getDefaultCharset() {
		return defaultCharset;
	}


	public static final void setDefaultCharset(Charset charset) {
		defaultCharset = charset;
	}


	/**
	 * @param type the format of the reader stream to create
	 * @param file the file source to read data from
	 * @param doBuffer true to wrap the input stream in a buffered input stream,
	 * false to use it without modification
	 * @return the new {@link DataTransferInput} stream created from the specified file
	 * @throws IOException
	 */
	public static DataTransferInput createReader(DataTransferFormat type, File file, boolean doBuffer) throws IOException {
		switch(type) {
		case JSON:
			return new JsonReader(file);
		case XML:
			return XmlHandler.createXMLReader(new FileInputStream(file), true, defaultCharset, true, true);
		default:
			throw new AssertionError("unknown format type: " + type);
		}
	}


	public static DataTransferInput createReader(DataTransferFormat type, Reader reader) throws IOException {
		switch(type) {
		case JSON:
			return new JsonReader(reader);
		case XML:
			return XmlHandler.createXMLReader(reader, true, true, true);
		default:
			throw new AssertionError("unknown format type: " + type);
		}
	}


	public static DataTransferInput createReader(DataTransferFormat type, InputStream in)
			throws IOException {
		switch(type) {
		case JSON:
			return new JsonReader(in);
		case XML:
			return XmlHandler.createXMLReader(in, true, defaultCharset, true, true);
		default:
			throw new AssertionError("unknown format type: " + type);
		}
	}


	public static DataTransferInput createReader(DataTransferFormat type, InputStream in, Charset charset)
			throws IOException {
		switch(type) {
		case JSON:
			return new JsonReader(in, charset);
		case XML:
			return XmlHandler.createXMLReader(in, true, charset, true, true);
		default:
			throw new AssertionError("unknown format type: " + type);
		}
	}


	/**
	 * @param type the format of the write stream to create
	 * @param file the file destination to write data to
	 * @param doBuffer true to wrap the output stream in a buffered output stream,
	 * false to use it without modification
	 * @return the new {@link DataTransferOutput} stream created from the specified file
	 * @throws IOException
	 */
	public static DataTransferOutput createWriter(DataTransferFormat type, File file, boolean doBuffer)
			throws IOException {
		switch(type) {
		case JSON:
			return new JsonWriter(file);
		case XML:
			return XmlHandler.createXMLWriter(new FileOutputStream(file), true, defaultCharset);
		default:
			throw new AssertionError("unknown format type: " + type);
		}
	}


	public static DataTransferOutput createWriter(DataTransferFormat type, Writer writer) throws IOException {
		switch(type) {
		case JSON:
			return new JsonWriter(writer);
		case XML:
			return XmlHandler.createXMLWriter(writer, true, defaultCharset);
		default:
			throw new AssertionError("unknown format type: " + type);
		}
	}


	public static DataTransferOutput createWriter(DataTransferFormat type, OutputStream out)
			throws IOException {
		switch(type) {
		case JSON:
			return new JsonWriter(out);
		case XML:
			return XmlHandler.createXMLWriter(out, true, defaultCharset);
		default:
			throw new AssertionError("unknown format type: " + type);
		}
	}


	public static DataTransferOutput createWriter(DataTransferFormat type, OutputStream out, Charset charset)
			throws IOException {
		switch(type) {
		case JSON:
			return new JsonWriter(out, charset);
		case XML:
			return XmlHandler.createXMLWriter(out, true, charset);
		default:
			throw new AssertionError("unknown format type: " + type);
		}
	}


	public static final void writeArray(DataTransferOutput out, String elementName, List<String> ary) throws IOException {
		StringBuilder strB = new StringBuilder(ary.size() > 10 ? ary.size() > 20 ? 128 : 64 : 32);
		strB.append("[");
		for(int i = 0, size = ary.size()-1; i < size; i++) {
			strB.append("\"");
			// replace \ and " with \\ and \"
			StringEscape.escapeChar(ary.get(i), '\\', '\\', '"', strB);
			strB.append("\", ");
		}
		if(ary.size() > 0) {
			strB.append("\"");
			StringEscape.escapeChar(ary.get(ary.size()-1), '\\', '\\', '"', strB);
			strB.append("\"]");
		}
		else {
			strB.append("]");
		}
		out.writeString(elementName, strB.toString());
	}


	public static final List<String> readArray(DataTransferInput in, String elementName) throws IOException {
		List<String> ary = new ArrayList<>();
		readArray(in, elementName, ary);
		return ary;
	}


	public static final void readArray(DataTransferInput in, String elementName, List<String> dst) throws IOException {
		String src = in.readString(elementName);
		StringBuilder strDst = new StringBuilder();
		boolean inAry = false;
		for(int i = 0, size = src.length(); i < size; i++) {
			char chI = src.charAt(i);
			if(chI == '[') {
				inAry = true;
			}
			else if(chI == ']') {
				inAry = false;
			}
			if(inAry) {
				if(chI == '"') {
					// skip the opening quote "
					i++;
					if(i >= size) {
						throw new IllegalArgumentException("found starting '\"' at end of read string: '" + src + "'");
					}
					// read the array element and set i to the index of the closing quote "
					i = StringEscape.unescapeChar(src, i, '\\', '"', strDst);
					if(src.charAt(i) != '"') {
						throw new IllegalArgumentException("could not find closing '\"'");
					}
					// increment i past the closing quote "
					i++;
					dst.add(strDst.toString());
					strDst.setLength(0);
				}
			}
		}
	}


	public static final void writeBlock(DataTransferOutput out, String blockName, String elementName, List<String> elements)
			throws IOException {
		out.writeStartBlock(blockName);
		for(int i = 0, size = elements.size(); i < size; i++) {
			out.writeString(elementName, elements.get(i));
		}
		out.writeEndBlock();
	}


	public static final List<String> readBlock(DataTransferInput in, String blockName, String elementName) throws IOException {
		List<String> ary = new ArrayList<>();
		readBlock(in, blockName, elementName, ary);
		return ary;
	}


	public static final void readBlock(DataTransferInput in, String blockName, String elementName, List<String> dst) throws IOException {
		in.readStartBlock(blockName);
		String str = null;
		DataElement tag = in.peekNext();
		@SuppressWarnings("unused")
		int i = 0;
		while(!tag.isEndBlock() ||
				// TODO: Note: case for empty element being reported as a start and end element 
				(tag.getName().equals(elementName) && tag.getContent().length() == 0)) {
			str = in.readString(elementName);
			dst.add(str);
			tag = in.peekNext();
			i++;
		}
		in.readEndBlock();
		if(!tag.getName().equals(blockName)) {
			throw new IOException("incorrect closing block name '" + tag.getName() + "', expected '" + blockName + "'");
		}
	}


	public static final void writeBlock(DataTransferOutput out, String blockName, List<? extends WritableObject> elements)
			throws IOException {
		out.writeStartBlock(blockName);
		for(int i = 0, size = elements.size(); i < size; i++) {
			elements.get(i).writeData(out);
		}
		out.writeEndBlock();
	}


	public static final <T> void writeBlock(DataTransferOutput out, String blockName,
			DataTransferFactory<T> writer, List<? extends T> elements)
			throws IOException {
		out.writeStartBlock(blockName);
		for(int i = 0, size = elements.size(); i < size; i++) {
			writer.writeData(out, elements.get(i));
		}
		out.writeEndBlock();
	}


	public static final <T> List<T> readBlock(DataTransferInput in, String blockName, DataTransferFactory<T> reader)
			throws IOException {
		List<T> ary = new ArrayList<>();
		readBlock(in, blockName, reader, ary);
		return ary;
	}


	public static final <T> void readBlock(DataTransferInput in, String blockName, DataTransferFactory<T> reader, List<? super T> dst)
			throws IOException {
		in.readStartBlock(blockName);
		DataElement tag = in.peekNext();
		@SuppressWarnings("unused")
		int i = 0;
		while(!tag.isEndBlock() ||
				// TODO: Note: case for empty element being reported as a start and end element 
				(!tag.getName().equals(blockName) && tag.getContent().length() == 0)) {
			dst.add(reader.readData(in));
			tag = in.peekNext();
			i++;
		}
		in.readEndBlock();
		if(!tag.getName().equals(blockName)) {
			throw new IOException("incorrect closing block name '" + tag.getName() + "', expected '" + blockName + "'");
		}
	}

}
