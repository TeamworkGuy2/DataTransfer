package twg2.io.serialize.json;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import twg2.io.serialize.base.writer.DataTransferOutput;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

/** A {@code DataTransferOutput} wrapper that writes a JSON formated output stream
 * @author TeamworkGuy2
 * @since 2014-8-2
 */
public class JsonWriter implements DataTransferOutput {
	private JsonGenerator jsonOut;
	/** the nested level of objects the writer is currently in, this is incremented
	 * each time {@link #writeOpeningBlock(String)} is called and is decremented each
	 * time {@link #writeClosingBlock()} is called
	 */
	private int inObject;
	@SuppressWarnings("unused")
	private int inArray;


	/** Open a {@link BufferedOutputStream} to the specified {@code file} using {@code UTF-8} encoding
	 * to create a JSON writer
	 * @param file the file to write the data to
	 * @throws IOException if there is an error opening the file
	 */
	public JsonWriter(File file) throws FileNotFoundException, IOException {
		this(new BufferedOutputStream(new FileOutputStream(file)));
	}


	/** Create an {@link OutputStreamWriter} using the specified {@code stream} and {@code UTF-8} encoding
	 * to create a JSON writer
	 * @param stream the output stream to write the data to
	 * @throws IOException if there is an error opening the file
	 */
	public JsonWriter(OutputStream stream) throws IOException {
		jsonOut = new JsonFactory().createGenerator(stream);
		jsonOut.setPrettyPrinter(new DefaultPrettyPrinter());
		jsonOut.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, true);
		//jsonOut.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false); // JsonParser.Feature
		jsonOut.writeStartObject();
	}


	/** Create an {@link OutputStreamWriter} using the specified {@code stream} and {@code charset}
	 * to create a JSON writer 
	 * @param stream the output stream to write the data to
	 * @param charset the text encoding to use
	 * @throws IOException if there is an error opening the file
	 */
	public JsonWriter(OutputStream stream, Charset charset) throws IOException {
		this(new OutputStreamWriter(stream, charset));
	}


	/** Create an {@link OutputStreamWriter} using the specified {@code writer}
	 * to create a JSON writer 
	 * @param writer the writer to write the data to
	 * @throws IOException if there is an error opening the file
	 */
	public JsonWriter(Writer writer) throws IOException {
		jsonOut = new JsonFactory().createGenerator(writer);
		jsonOut.setPrettyPrinter(new DefaultPrettyPrinter());
		jsonOut.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, true);
		//jsonOut.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false); // JsonParser.Feature
		jsonOut.writeStartObject();
	}



	@Override
	public void close() throws IOException {
		jsonOut.writeEndObject();
		jsonOut.close();
	}


	@Override
	public void write(String name, byte[] b) throws IOException {
		checkStatus();
		jsonOut.writeBinaryField(name, b);
	}


	@Override
	public void write(String name, byte[] b, int off, int len) throws IOException {
		checkStatus();
		jsonOut.writeFieldName(name);
		jsonOut.writeBinary(b, off, len);
	}


	@Override
	public void writeBoolean(String name, boolean v) throws IOException {
		checkStatus();
		jsonOut.writeBooleanField(name, v);
	}


	@Override
	public void writeByte(String name, byte v) throws IOException {
		checkStatus();
		jsonOut.writeNumberField(name, v);
	}


	@Override
	public void writeChar(String name, char v) throws IOException {
		checkStatus();
		jsonOut.writeStringField(name, "" + v);
	}


	@Override
	public void writeDouble(String name, double v) throws IOException {
		checkStatus();
		jsonOut.writeNumberField(name, v);
	}


	@Override
	public void writeFloat(String name, float v) throws IOException {
		checkStatus();
		jsonOut.writeNumberField(name, v);
	}


	@Override
	public void writeInt(String name, int v) throws IOException {
		checkStatus();
		jsonOut.writeNumberField(name, v);
	}


	@Override
	public void writeLong(String name, long v) throws IOException {
		checkStatus();
		jsonOut.writeNumberField(name, v);
	}


	@Override
	public void writeShort(String name, short v) throws IOException {
		checkStatus();
		jsonOut.writeNumberField(name, v);
	}


	@Override
	public void writeString(String name, String s) throws IOException {
		checkStatus();
		jsonOut.writeStringField(name, s);
	}


	@Override
	public void writeStartBlock(String name) throws IOException {
		inObject++;
		jsonOut.writeFieldName(name);
		jsonOut.writeStartObject();
	}


	@Override
	public void writeEndBlock() throws IOException {
		checkStatus();
		inObject--;
		jsonOut.writeEndObject();
	}


	private void checkStatus() {
		if(inObject < 1) {
			throw new IllegalStateException("Cannot write field without starting opening block");
		}
	}

}
