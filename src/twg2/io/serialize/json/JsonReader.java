package twg2.io.serialize.json;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import twg2.io.serialize.base.DataElement;
import twg2.io.serialize.base.DataElementImpl;
import twg2.io.serialize.base.DataProxy;
import twg2.io.serialize.base.ParsedElementType;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.primitiveIoTypes.IoType;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/** A {@code DataTransferInput} wrapper for a JSON formated input stream
 * @author TeamworkGuy2
 * @since 2014-8-2
 */
public class JsonReader implements DataTransferInput {
	private JsonParser jsonIn;
	private int inObject;
	private int inArray;
	private JsonToken currentToken;
	private JsonToken currentValueToken;
	private String currentName;
	private String currentContent;
	private DataElement cachedBlock;
	private DataElement currentBlock;

	private DataProxy curData = new DataProxy();


	/** Create a JSON reader from the specified file contents
	 * @param file the file to read the JSON data from using {@code UTF-8} encoding
	 * @throws FileNotFoundException if the file cannot be found
	 * @throws IOException if there is an error reading from the file
	 */
	public JsonReader(File file) throws FileNotFoundException, IOException {
		this(new BufferedInputStream(new FileInputStream(file)));
	}


	/** Create a JSON reader from the specified input stream
	 * @param stream the input stream to read the JSON data from using {@code UTF-8} encoding
	 * @throws IOException if there is an error reading from the input stream
	 */
	public JsonReader(InputStream stream) throws IOException {
		jsonIn = new JsonFactory().createParser(stream);
		jsonIn.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
		// start the parser, skip the first default opening object
		jsonIn.nextToken();
		jsonIn.nextToken();
	}


	/** Create a JSON reader from the specified input stream
	 * @param stream the input stream to read the JSON data from using {@code UTF-8} encoding
	 * @param charset the text encoding of the input stream
	 * @throws IOException if there is an error reading from the input stream
	 */
	public JsonReader(InputStream stream, Charset charset) throws IOException {
		this(new InputStreamReader(stream, charset));
	}


	/** Create a JSON reader from the specified reader
	 * @param reader the reader to read the JSON data from
	 * @throws IOException if there is an error reading from the reader
	 */
	public JsonReader(Reader reader) throws FileNotFoundException, IOException {
		jsonIn = new JsonFactory().createParser(reader);
		jsonIn.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
		// start the parser, skip the first default opening object
		jsonIn.nextToken();
		jsonIn.nextToken();
	}


	@Override
	public void close() throws IOException {
		readEndBlock();
		jsonIn.close();
	}


	@Override
	public void read(String name, byte[] b) throws IOException {
		read(name, b, 0, b.length);
	}


	@Override
	public void read(String name, byte[] b, int off, int len) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_STRING, IoType.BINARY);
		System.arraycopy(curData.getByteArray(), 0, b, off, len);
	}


	@Override
	public boolean readBoolean(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE, IoType.BOOLEAN);
		return curData.getBoolean();
	}


	// TODO implement in parent interface
	public boolean[] readBooleanArray(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE, IoType.BOOLEAN);
		return curData.getBooleanArray();
	}


	// TODO implement in parent interface
	public void readBooleanArray(String name, boolean[] dst, int dstOff) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_TRUE, JsonToken.VALUE_FALSE, IoType.BOOLEAN);
	}


	@Override
	public byte readByte(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_NUMBER_INT, IoType.BYTE);
		return curData.getByte();
	}


	@Override
	public char readChar(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_STRING, IoType.CHAR);
		if(jsonIn.getTextLength() < 1) {
			throw new IllegalStateException("Could not read one character, string length " + jsonIn.getTextLength());
		}
		return curData.getChar();
	}


	@Override
	public double readDouble(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_NUMBER_FLOAT, IoType.DOUBLE);
		return curData.getDouble();
	}


	@Override
	public float readFloat(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_NUMBER_FLOAT, IoType.FLOAT);
		return curData.getFloat();
	}


	@Override
	public int readInt(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_NUMBER_INT, IoType.INT);
		return curData.getInt();
	}


	@Override
	public long readLong(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_NUMBER_INT, IoType.LONG);
		return curData.getLong();
	}


	@Override
	public short readShort(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_NUMBER_INT, IoType.SHORT);
		return curData.getShort();
	}


	@Override
	public String readString(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.VALUE_STRING, IoType.STRING);
		return curData.getString();
	}


	@Override
	public DataElement peekNext() throws IOException {
		if(cachedBlock != null) {
			return cachedBlock;
		}
		currentBlock = readNext();
		cachedBlock = currentBlock;
		return currentBlock;
	}


	@Override
	public DataElement readNext() throws IOException {
		if(cachedBlock != null) {
			currentBlock = cachedBlock;
			cachedBlock = null;
			return currentBlock;
		}

		// read until a field, or beginning/end of an object is reached
		JsonToken token = jsonIn.getCurrentToken();
		while(token != null && token != JsonToken.START_OBJECT && token != JsonToken.END_OBJECT && token != JsonToken.FIELD_NAME) {
			token = jsonIn.nextToken();
		}

		ParsedElementType parsedType = null;
		if(token == JsonToken.START_OBJECT) { parsedType = ParsedElementType.HEADER; }
		else if(token == JsonToken.END_OBJECT) { parsedType = ParsedElementType.FOOTER; }
		else if(token == JsonToken.FIELD_NAME) { parsedType = ParsedElementType.ELEMENT; }
		else {
			//throw new AssertionError(token + " must be of type " + JsonToken.START_ARRAY + " or " + JsonToken.END_OBJECT + " or " + JsonToken.FIELD_NAME + " at this point");
			return null;
		}
		currentToken = token;
		currentName = jsonIn.getCurrentName();
		// if the token read is a field name, read the field's value
		if(currentToken == JsonToken.FIELD_NAME) {
			jsonIn.nextToken();
			currentValueToken = jsonIn.getCurrentToken();
			parseJsonType(currentName, currentValueToken);
		}
		currentContent = jsonIn.getText();

		currentBlock = new DataElementImpl(currentName, 0,
				parsedType == ParsedElementType.ELEMENT ? currentContent : null, parsedType);
		// if it is an element, the previous nextToken() call moved to the element value, this call moves to the next token
		jsonIn.nextToken();
		return currentBlock;
	}


	@Override
	public DataElement readStartBlock(String name) throws IOException {
		boolean result = readUntil(name, JsonToken.START_OBJECT, null);
		//lastBlock = currentBlock;
		currentBlock = new DataElementImpl(currentName, 0, null, ParsedElementType.HEADER);
		return currentBlock;
	}


	@Override
	public void readEndBlock() throws IOException {
		readUntil(null, JsonToken.END_OBJECT, null);
	}


	@Override
	public DataElement getCurrentElement() {
		return currentBlock;
	}


	@Override
	public String getCurrentName() {
		return currentName;
	}


	/** Skip over items from the input stream until the specified element type and name are encountered
	 * @param name the name of the element to search for, if this value is null, the search stops
	 * at the first element that matches {@code type}
	 * @param type the type of element to search for, null matches the end of the stream
	 * @return true if a matching element and name combination was found
	 * @throws JsonParseException if there is an error reading from the input stream
	 * @throws IOException if there is an error reading from the input stream
	 * @throws IllegalStateException if a matching element and name could not be found
	 */
	private boolean readUntil(String name, JsonToken type, IoType ioType) throws JsonParseException, IOException {
		return readUntil(name, type, type, ioType);
	}


	/** Skip over items from the input stream until the specified element name
	 * and types are encountered
	 * @param name the name of the element to search for, if this value is null, the search stops
	 * at the first element that matches {@code type} or {@code type2}
	 * @param type one of the element types to search for, null matches the end of the stream
	 * @param type2 one of the element types to search for, null matches the end of the stream
	 * @return true if a matching element and name combination was found
	 * @throws JsonParseException if there is an error reading from the input stream
	 * @throws IOException if there is an error reading from the input stream
	 * @throws IllegalStateException if a matching element and name could not be found
	 */
	private boolean readUntil(String name, JsonToken type, JsonToken type2, IoType ioType)
			throws JsonParseException, IOException {
		if(cachedBlock != null) {
			boolean found = false;
			if(isMatchToken(currentToken, currentValueToken, currentName, type, type2, name)) {
				found = true;
			}
			currentBlock = cachedBlock;
			cachedBlock = null;
			if(found) {
				return true;
			}
		}

		// read until a matching element type and name are found
		JsonToken parsedToken = jsonIn.getCurrentToken();
		String parsedName = jsonIn.getCurrentName();
		while(parsedToken != null && !isMatch(parsedToken, parsedName, type, type2, name)) {
			parsedToken = jsonIn.nextToken();
			parsedName = jsonIn.getCurrentName();
		}

		// if the found token does not match, throw an exception
		boolean result = parsedToken != null && isMatch(parsedToken, parsedName, type, type2, name);
		if(!result && type != JsonToken.END_OBJECT && type2 != JsonToken.END_OBJECT) {
			throw new IllegalStateException("could not find property with name: '" + name + "' of type " + type + " or " + type2);
		}

		// if the token read is a field name, read the field's value
		currentToken = parsedToken;
		this.currentName = jsonIn.getCurrentName();
		if(currentToken == JsonToken.FIELD_NAME) {
			jsonIn.nextToken();
			currentValueToken = jsonIn.getCurrentToken();
			parseJsonType(name, currentValueToken);
		}
		currentContent = jsonIn.getText();
		if(ioType != null) {
			parseContent(name, ioType);
		}
		// if it is an element, the previous nextToken() call moved to the element value, this call moves to the next token
		jsonIn.nextToken();
		return result;
	}


	private static final boolean isMatchToken(JsonToken parsedToken, JsonToken parsedValueToken, String parsedName, JsonToken type, JsonToken type2, String expectedName) {
		return (parsedToken != null && (parsedToken == type || parsedToken == type2)) ||
				(parsedValueToken != null && (parsedValueToken == type || parsedValueToken == type2)) &&
				(expectedName == null || (parsedName != null && parsedName.equals(expectedName)));
	}


	private static final boolean isMatch(JsonToken parsedToken, String parsedName, JsonToken type, JsonToken type2, String expectedName) {
		return (parsedToken == type || parsedToken == type2) && (expectedName == null || (parsedName != null && parsedName.equals(expectedName)));
	}


	private String[] readArray() throws JsonParseException, IOException {
		JsonToken token = jsonIn.getCurrentToken();
		ArrayList<String> values = new ArrayList<>();
		while(token != null && token != JsonToken.END_ARRAY) {
			values.add(jsonIn.getValueAsString());
			token = jsonIn.nextToken();
		}
		currentToken = token;
		currentName = jsonIn.getCurrentName();
		currentContent = jsonIn.getText();
		jsonIn.nextToken();
		return values.toArray(new String[values.size()]);
	}


	private String[] readArrayOfType(IoType type) throws JsonParseException, IOException {
		JsonToken token = jsonIn.getCurrentToken();
		ArrayList<String> values = new ArrayList<>();
		while(token != null && token != JsonToken.END_ARRAY) {
			values.add(jsonIn.getValueAsString());
			token = jsonIn.nextToken();
		}
		currentToken = token;
		currentName = jsonIn.getCurrentName();
		currentContent = jsonIn.getText();
		jsonIn.nextToken();
		return values.toArray(new String[values.size()]);
	}


	/** Read the specified data type from {@link #jsonIn} into this object's current fields,
	 * such as {@link #curBytes}, {@link #curFloat}, etc.
	 * @param type the data type to read
	 * @throws IOException if there is an error reading from the {@link JsonParser}
	 */
	private void parseContent(String name, IoType type) throws IOException {
		switch(type) {
		case BINARY:
			byte[] bytes = jsonIn.getCurrentToken().asByteArray();
			curData.setByteArray(name, bytes, 0, bytes.length);
			return;
		case BOOLEAN:
			boolean boolVal = jsonIn.getBooleanValue();
			curData.setBoolean(name, boolVal);
			return;
		case BYTE:
			byte byteVal = jsonIn.getByteValue();
			curData.setByte(name, byteVal);
			return;
		case CHAR:
			char charVal = jsonIn.getTextCharacters()[jsonIn.getTextOffset()];
			curData.setChar(name, charVal);
			return;
		case DOUBLE:
			double doubleVal = jsonIn.getDoubleValue();
			curData.setDouble(name, doubleVal);
			return;
		case FLOAT:
			float floatVal = jsonIn.getFloatValue();
			curData.setFloat(name, floatVal);
			return;
		case INT:
			int intVal = jsonIn.getIntValue();
			curData.setInt(name, intVal);
			return;
		case LONG:
			long longVal = jsonIn.getLongValue();
			curData.setLong(name, longVal);
			return;
		case SHORT:
			short shortVal = jsonIn.getShortValue();
			curData.setShort(name, shortVal);
			return;
		case STRING:
			String stringVal = jsonIn.getText();
			curData.setString(name, stringVal);
			return;
		default:
			throw new AssertionError("unknown type: " + type);
		}
	}


	/** Based on a {@link JsonToken} type, parse a value from {@link #jsonIn} and
	 * store it into this object's
	 * current fields, such as {@link #curBytes}, {@link #curFloat}, etc.
	 * @param token type type of Json parser token to read an associated data type for
	 * @throws IOException if there is an error reading from the {@link JsonParser}
	 */
	private void parseJsonType(String name, JsonToken token) throws IOException {
		switch (token) {
		case START_OBJECT:
		case END_OBJECT:
			break;
		case VALUE_NULL:
			currentValueToken = null;
			break;
		case VALUE_FALSE:
		case VALUE_TRUE:
			boolean curBool = jsonIn.getBooleanValue();
			curData.setBoolean(name, curBool);
			break;
		case VALUE_STRING:
			curData.setString(name, jsonIn.getText());
			break;
		case VALUE_NUMBER_INT:
			curData.setLong(name, jsonIn.getLongValue());
			int intVal = jsonIn.getIntValue();
			curData.setInt(name, intVal);
			curData.setShort(name, (short)intVal);
			curData.setByte(name, (byte)intVal);
			break;
		case VALUE_NUMBER_FLOAT:
			curData.setDouble(name, jsonIn.getDoubleValue());
			curData.setFloat(name, jsonIn.getFloatValue());
			break;
		case START_ARRAY:
			
		case END_ARRAY:
			// TODO not sure if correct
			break;
		default:
			throw new IllegalArgumentException("unknown JSON token type to parse: " + token);
		}
	}

}
