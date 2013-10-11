package xml;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

/** {@link XMLOutput} implementation for writing XML text data to a {@link Writer}.
 * This class allows XML opening and closing tags to be written as well as strings and basic data types.
 * @author TeamworkGuy2
 * @since 2013-2-1
 */
public class XMLOutputWriter implements XMLOutput, Closeable {
	private static char[] indentation = new char[] {'\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t',
		'\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t'};
	//private static final String indentationStep = "\t";
	private static char lineSeparator = (char)0xA; // The XML standard uses LF to mark new lines, do not use System.getProperty("line.separator");
	private static final char OPEN = '<';
	private static final char CLOSE = '>';
	private static final char SLASH = '/';
	private Writer output;
	private Charset charset;
	private ArrayList<String> tagStack;
	private int tagsWritten;
	private int indentationCount;


	/** XMLOutputWriter, basic implementation of {@link XMLOutput}
	 * @param writer - the writer to write data to
	 * @param charset - the charset that the writer is using
	 */
	public XMLOutputWriter(Writer writer, Charset charset) {
		this.output = writer;
		this.tagStack = new ArrayList<String>();
		this.tagsWritten = 0;
		this.charset = charset;
		this.indentationCount = 0;
	}


	public void writeHeader() throws IOException {
		this.output.write(XMLHandler.xmlHeader(charset));
	}


	@Override
	public void write(String name, byte[] b) throws IOException {
		pushTagNoLine(name);
		String base64 = DatatypeConverter.printBase64Binary(b);
		this.output.write(base64);
		popTagNoLine();
	}


	@Override
	public void write(String name, byte[] b, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		String base64 = DatatypeConverter.printBase64Binary(b);
		this.output.write(base64);
		popTagNoLine();
	}


	@Override
	public void write(String name, byte[] b, int off, int len) throws IOException {
		pushTagNoLine(name);
		byte[] bytes = new byte[len];
		System.arraycopy(b, 0, bytes, 0, len);
		String base64 = DatatypeConverter.printBase64Binary(bytes);
		this.output.write(base64);
		popTagNoLine();
	}


	@Override
	public void write(String name, byte[] b, int off, int len, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		byte[] bytes = new byte[len];
		System.arraycopy(b, 0, bytes, 0, len);
		String base64 = DatatypeConverter.printBase64Binary(bytes);
		this.output.write(base64);
		popTagNoLine();
	}


	@Override
	public void writeBoolean(String name, boolean b) throws IOException {
		pushTagNoLine(name);
		this.output.write(b == true ? "true" : "false");
		popTagNoLine();
	}


	@Override
	public void writeBoolean(String name, boolean b, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write(b == true ? "true" : "false");
		popTagNoLine();
	}


	@Override
	public void writeByte(String name, byte v) throws IOException {
		pushTagNoLine(name);
		this.output.write((byte)v);
		popTagNoLine();
	}


	@Override
	public void writeByte(String name, byte v, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write((byte)v);
		popTagNoLine();
	}


	@Override
	public void writeChar(String name, char v) throws IOException {
		pushTagNoLine(name);
		this.output.write((char)v);
		popTagNoLine();
	}


	@Override
	public void writeChar(String name, char v, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write((char)v);
		popTagNoLine();
	}


	@Override
	public void writeDouble(String name, double d) throws IOException {
		pushTagNoLine(name);
		this.output.write(Double.toString(d));
		popTagNoLine();
	}


	@Override
	public void writeDouble(String name, double d, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write(Double.toString(d));
		popTagNoLine();
	}


	@Override
	public void writeFloat(String name, float v) throws IOException {
		pushTagNoLine(name);
		this.output.write(Float.toString(v));
		popTagNoLine();
	}


	@Override
	public void writeFloat(String name, float v, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write(Float.toString(v));
		popTagNoLine();
	}


	@Override
	public void writeInt(String name, int v) throws IOException {
		pushTagNoLine(name);
		this.output.write(Integer.toString(v));
		popTagNoLine();
	}


	@Override
	public void writeInt(String name, int v, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write(Integer.toString(v));
		popTagNoLine();
	}


	@Override
	public void writeLong(String name, long v) throws IOException {
		pushTagNoLine(name);
		this.output.write(Long.toString(v));
		popTagNoLine();
	}


	@Override
	public void writeLong(String name, long v, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write(Long.toString(v));
		popTagNoLine();
	}


	@Override
	public void writeShort(String name, short v) throws IOException {
		pushTagNoLine(name);
		this.output.write(Short.toString((short)v));
		popTagNoLine();
	}


	@Override
	public void writeShort(String name, short v, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write(Short.toString((short)v));
		popTagNoLine();
	}


	@Override
	public void writeUTF(String name, String s) throws IOException {
		pushTagNoLine(name);
		this.output.write(s);
		popTagNoLine();
	}


	@Override
	public void writeUTF(String name, String s, XMLAttributes attributes) throws IOException {
		pushTagNoLine(name, attributes);
		this.output.write(s);
		popTagNoLine();
	}


	@Override
	public void writeOpeningBlock(String name) throws IOException {
		writeOpeningBlock(name, (String)null);
	}


	@Override
	public void writeOpeningBlock(String name, XMLAttributes attributes) throws IOException {
		writeOpeningBlock(name, (String)null, attributes);
	}


	/** Write an opening XML tag and add a corresponding closing XML tag to the tag stack
	 * @param name the name of the XML tag to write
	 * @param descriptor an optional descriptor to write with the XML tag
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	@Override
	public void writeOpeningBlock(String name, String descriptor) throws IOException {
		Writer out = this.output;
		writeIndentation(out, indentationCount);
		this.indentationCount++;
		out.write(OPEN);
		out.write(name);
		if(descriptor != null) {
			out.write(' ');
			out.write(XMLHandler.DESCRIPTOR_ID);
			out.write("=\"");
			out.write(descriptor);
			out.write('\"');
		}
		out.write(CLOSE);
		out.write(lineSeparator);
		this.tagStack.add(name);
		this.tagsWritten++;
	}


	/** Write an opening XML tag and add a corresponding closing XML tag to the tag stack
	 * @param name the name of the XML tag to write
	 * @param descriptor an optional descriptor to write with the XML opening
	 * tag, or null to write a generic XML tag
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	@Override
	public void writeOpeningBlock(String name, String descriptor, XMLAttributes attributes) throws IOException {
		Writer out = this.output;
		writeIndentation(out, indentationCount);
		this.indentationCount++;
		out.write(OPEN);
		out.write(name);
		// Write the descriptor and attributes
		if(descriptor != null) {
			out.write(' ');
			out.write(XMLHandler.DESCRIPTOR_ID);
			out.write("=\"");
			out.write(descriptor);
			out.write('\"');
		}
		if(attributes != null) {
			List<String> names = attributes.getAttributeNames();
			List<Object> values = attributes.getAttributeValues();
			int size = names.size()-1;
			if(size > -1) {
				out.write(' ');
				for(int i = 0; i < size; i++) {
					out.write(names.get(i));
					out.write('=');
					out.write('\"');
					out.write(values.get(i).toString());
					out.write('\"');
					out.write(' ');
				}
				out.write(names.get(size));
				out.write('=');
				out.write('\"');
				out.write(values.get(size).toString());
				out.write('\"');
			}
		}
		out.write(CLOSE);
		out.write(lineSeparator);
		this.tagStack.add(name);
		this.tagsWritten++;
	}


	/** Write a closing XML tag for the last opening XML tag
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	@Override
	public void writeClosingBlock() throws IOException {
		if(this.indentationCount > 0) {
			this.indentationCount--;
		}
		String name = this.tagStack.remove(this.tagStack.size()-1);
		Writer out = this.output;
		this.tagsWritten++;
		writeIndentation(out, indentationCount);
		out.write(OPEN);
		out.write(SLASH);
		out.write(name);
		out.write(CLOSE);
		out.write(lineSeparator);
	}


	/** Get the number of open XML tags waiting for their corresponding closing tags to be written
	 * @return the number of open tags waiting to be written
	 */
	@Override
	public int getBlocksRemaining() {
		return this.tagStack.size();
	}


	/** Get the number of opening and closing XML tags written by this writer
	 * @return the number of opening and closing XML tags written by this writer
	 */
	@Override
	public int getBlocksWritten() {
		return this.tagsWritten;
	}


	/** Closes this XML converter's output stream if possible.
	 */
	@Override
	public void close() throws IOException {
		this.output.close();
		this.tagStack.clear();
		this.charset = null;
		this.tagStack = null;
	}


	/** Does not close this XML convert's output stream. Sets this converter's
	 * reference to the object output stream to null for garbage collection.
	 */
	@Override
	public void clear() {
		this.tagStack.clear();
		this.output = null;
		this.charset = null;
		this.tagStack = null;
	}


	/** Write an opening XML tag (with no ending new line) and add a corresponding closing XML tag to the tag stack
	 * @param name - the name of the XML tag to write
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	private void pushTagNoLine(String name) throws IOException {
		Writer out = this.output;
		writeIndentation(out, indentationCount);
		out.write(OPEN);
		out.write(name);
		out.write(CLOSE);
		this.tagStack.add(name);
	}


	/** Write an opening XML tag (with no ending new line) and add a corresponding closing XML tag to the tag stack
	 * @param name - the name of the XML tag to write
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	private void pushTagNoLine(String name, XMLAttributes attributes) throws IOException {
		Writer out = this.output;
		writeIndentation(out, indentationCount);
		out.write(OPEN);
		out.write(name);
		out.write(' ');
		List<String> names = attributes.getAttributeNames();
		List<Object> values = attributes.getAttributeValues();
		int size = names.size()-1;
		if(size > -1) {
			for(int i = 0; i < size; i++) {
				out.write(names.get(i));
				out.write('=');
				out.write('\"');
				out.write(values.get(i).toString());
				out.write('\"');
				out.write(' ');
			}
			out.write(names.get(size));
			out.write('=');
			out.write('\"');
			out.write(values.get(size).toString());
			out.write('\"');
		}
		out.write(CLOSE);
		this.tagStack.add(name);
	}


	/** Write a closing XML tag (without adjusting indentation) for the last opening XML tag
	 * @throws IOException if there is an IO error writing to the output stream
	 */
	private void popTagNoLine() throws IOException {
		String name = this.tagStack.remove(this.tagStack.size()-1);
		Writer out = this.output;
		out.write(OPEN);
		out.write(SLASH);
		out.write(name);
		out.write(CLOSE);
		out.write(lineSeparator);
	}


	/** Write the indentation based on the writer's current state
	 * (i.e. how many nested blocks were are inside of determines indentation)
	 * @param out the output writer to write the indentation to
	 * @param count the number of indentation elements to write
	 * @throws IOException
	 */
	private static final void writeIndentation(Writer out, int count) throws IOException {
		int length = indentation.length;
		for(int i = 0; i < count; i+=length) {
			out.write(indentation, 0, ((count-i) > length) ? length : (count-i));
		}
	}


	/** Write a header to the specified XML output stream
	 * @param output the XML output stream to write to header to
	 * @throws IOException if there is an error writing the header to the
	 * XML output stream
	 */
	public static final void writeHeader(XMLOutputWriter output) throws IOException {
		output.writeHeader();
	}

}
