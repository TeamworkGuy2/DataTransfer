package twg2.io.serialize.base;

import java.lang.reflect.Array;
import java.util.Arrays;

import twg2.simpleTypes.ioPrimitives.PrimitiveOrString;

/** A single object which can contain a primitive, primitive array, String, or String array.<br>
 * Contains {@code add*ToArray()} (e.g. {@code addFloatToArray(float)}) methods which allow this object to be treated like a primitive {@code ArrayList}.<br>
 * Useful as a wrapper for data elements during serialization/deserialization.
 * @author TeamworkGuy2
 * @since 2015-5-21
 */
public class DataProxy {
	String name;
	PrimitiveOrString type;
	boolean isArray;
	// floats are stored via Float.floatToRawIntBits() and Float.intBitsToFloat()
	// doubles are stored via Double.doubleToRawLongBits() and Double.longBitsToDouble()
	// if curObj is an array, the lower 32 bits is array offset, the upper 32 bits is array length
	private long curPrimitive;
	// boolean[] | byte[] | char[] | double[] | float[] | int[] | long[] | short[] | String[] | String
	private Object curObj;


	public DataProxy() {
	}


	public DataProxy(PrimitiveOrString type, boolean isArray) {
		this.isArray = isArray;
		this.type = type;
	}


	private DataProxy(String name, long rawPrimitive, PrimitiveOrString type) {
		this.name = name;
		this.isArray = false;
		this.curPrimitive = rawPrimitive;
		this.type = type;
	}


	private DataProxy(String name, String string, PrimitiveOrString type) {
		this.name = name;
		this.isArray = false;
		this.curObj = string;
		this.type = type;
	}


	private DataProxy(String name, Object ary, int aryOff, int aryLen, PrimitiveOrString type) {
		this.name = name;
		this.isArray = true;
		this.curObj = ary;
		this.curPrimitive = ((long)aryLen << 32) | aryOff;
		this.type = type;
	}


	/**
	 * @return the data value's name
	 */
	public String getName() {
		return name;
	}


	public boolean isArray() {
		return isArray;
	}


	/**
	 * @return a copy of the current data array with the array offset removed (first data element index = 0).
	 * If the current data type is not an array throw an {@code IllegalStateException}
	 */
	public Object getArrayCopy() {
		if(!isArray) throwNotArray();
		int arrayOff = (int)(curPrimitive & 0xFFFFFFFF);
		int arrayLen = (int)(curPrimitive >>> 32);
		Object ary = Array.newInstance(type.getType(), arrayLen);
		if(curObj != null) {
			System.arraycopy(curObj, arrayOff, ary, 0, arrayLen);
		}
		return ary;
	}


	/**
	 * @return the current data array. If the current data type is not an array throw an {@code IllegalStateException}
	 */
	public Object getArrayRaw() {
		if(!isArray) throwNotArray();
		return curObj;
	}


	/**
	 * @return the length of this data array. If the current data type is not an array throw an {@code IllegalStateException}
	 */
	public int getArrayLength() {
		if(!isArray) throwNotArray();
		return (int)(curPrimitive >>> 32);
	}


	/**
	 * @return the {@link #getArrayRaw()} offset at which data begins if the current data type is an array, else throw an {@code IllegalStateException}
	 */
	public int getArrayOffset() {
		if(!isArray) throwNotArray();
		return (int)(curPrimitive >>> 32);
	}


	// ==== get Primitive or String ====

	public boolean getBoolean() {
		checkType(PrimitiveOrString.BOOLEAN);
		return curPrimitive == 1;
	}


	public byte getByte() {
		checkType(PrimitiveOrString.BYTE);
		return (byte)curPrimitive;
	}


	public char getChar() {
		checkType(PrimitiveOrString.CHAR);
		return (char)curPrimitive;
	}


	public short getShort() {
		checkType(PrimitiveOrString.SHORT);
		return (short)curPrimitive;
	}


	public int getInt() {
		checkType(PrimitiveOrString.INT);
		return (int)curPrimitive;
	}


	public long getLong() {
		checkType(PrimitiveOrString.LONG);
		return curPrimitive;
	}


	public float getFloat() {
		checkType(PrimitiveOrString.FLOAT);
		return Float.intBitsToFloat((int)(curPrimitive & 0xFFFFFFFF));
	}


	public double getDouble() {
		checkType(PrimitiveOrString.DOUBLE);
		return Double.longBitsToDouble(curPrimitive);
	}


	public String getString() {
		checkType(PrimitiveOrString.STRING);
		return (String)curObj;
	}


	// ==== get Array ====

	public boolean[] getBooleanArray() {
		checkArray(PrimitiveOrString.BOOLEAN);
		return (boolean[])curObj;
	}


	public byte[] getByteArray() {
		checkArray(PrimitiveOrString.BYTE);
		return (byte[])curObj;
	}


	public char[] getCharArray() {
		checkArray(PrimitiveOrString.CHAR);
		return (char[])curObj;
	}


	public short[] getShortArray() {
		checkArray(PrimitiveOrString.SHORT);
		return (short[])curObj;
	}


	public int[] getIntArray() {
		checkArray(PrimitiveOrString.INT);
		return (int[])curObj;
	}


	public long[] getLongArray() {
		checkArray(PrimitiveOrString.LONG);
		return (long[])curObj;
	}


	public float[] getFloatArray() {
		checkArray(PrimitiveOrString.FLOAT);
		return (float[])curObj;
	}


	public double[] getDoubleArray() {
		checkArray(PrimitiveOrString.DOUBLE);
		return (double[])curObj;
	}


	public String[] getStringArray() {
		checkArray(PrimitiveOrString.STRING);
		return (String[])curObj;
	}


	// ==== get Element at Array Index ====

	public boolean getBooleanFromArray(int index) {
		return ((boolean[])curObj)[convertIndex(PrimitiveOrString.BOOLEAN, index)];
	}


	public byte getByteFromArray(int index) {
		return ((byte[])curObj)[convertIndex(PrimitiveOrString.BYTE, index)];
	}


	public char getCharFromArray(int index) {
		return ((char[])curObj)[convertIndex(PrimitiveOrString.CHAR, index)];
	}


	public short getShortFromArray(int index) {
		return ((short[])curObj)[convertIndex(PrimitiveOrString.SHORT, index)];
	}


	public int getIntFromArray(int index) {
		return ((int[])curObj)[convertIndex(PrimitiveOrString.INT, index)];
	}


	public long getLongFromArray(int index) {
		return ((long[])curObj)[convertIndex(PrimitiveOrString.LONG, index)];
	}


	public float getFloatFromArray(int index) {
		return ((float[])curObj)[convertIndex(PrimitiveOrString.FLOAT, index)];
	}


	public double getDoubleFromArray(int index) {
		return ((double[])curObj)[convertIndex(PrimitiveOrString.DOUBLE, index)];
	}


	public String getStringFromArray(int index) {
		return ((String[])curObj)[convertIndex(PrimitiveOrString.STRING, index)];
	}


	// ==== set Primitive or String ====

	public void setBoolean(String name, boolean val) {
		this.name = name;
		this.curPrimitive = val ? 1 : 0;
		this.type = PrimitiveOrString.BOOLEAN;
	}


	public void setByte(String name, byte val) {
		this.name = name;
		this.curPrimitive = val;
		this.type = PrimitiveOrString.BYTE;
	}


	public void setChar(String name, char val) {
		this.name = name;
		this.curPrimitive = val;
		this.type = PrimitiveOrString.CHAR;
	}


	public void setDouble(String name, double val) {
		this.name = name;
		this.curPrimitive = Double.doubleToRawLongBits(val);
		this.type = PrimitiveOrString.DOUBLE;
	}


	public void setFloat(String name, float val) {
		this.name = name;
		this.curPrimitive = Float.floatToRawIntBits(val);
		this.type = PrimitiveOrString.FLOAT;
	}


	public void setInt(String name, int val) {
		this.name = name;
		this.curPrimitive = val;
		this.type = PrimitiveOrString.INT;
	}


	public void setLong(String name, long val) {
		this.name = name;
		this.curPrimitive = val;
		this.type = PrimitiveOrString.LONG;
	}


	public void setShort(String name, short val) {
		this.name = name;
		this.curPrimitive = val;
		this.type = PrimitiveOrString.SHORT;
	}


	public void setString(String name, String val) {
		this.name = name;
		this.curObj = val;
		this.type = PrimitiveOrString.STRING;
	}


	// ==== set Array index ====

	public void setBooleanInArray(int index, boolean val) {
		((boolean[])curObj)[convertIndex(PrimitiveOrString.BOOLEAN, index)] = val;
	}


	public void setByteInArray(int index, byte val) {
		((byte[])curObj)[convertIndex(PrimitiveOrString.BYTE, index)] = val;
	}


	public void setCharInArray(int index, char val) {
		((char[])curObj)[convertIndex(PrimitiveOrString.CHAR, index)] = val;
	}


	public void setDoubleInArray(int index, double val) {
		((double[])curObj)[convertIndex(PrimitiveOrString.DOUBLE, index)] = val;
	}


	public void setFloatInArray(int index, float val) {
		((float[])curObj)[convertIndex(PrimitiveOrString.FLOAT, index)] = val;
	}


	public void setIntInArray(int index, int val) {
		((int[])curObj)[convertIndex(PrimitiveOrString.INT, index)] = val;
	}


	public void setLongInArray(int index, long val) {
		((long[])curObj)[convertIndex(PrimitiveOrString.LONG, index)] = val;
	}


	public void setShortInArray(int index, short val) {
		((short[])curObj)[convertIndex(PrimitiveOrString.SHORT, index)] = val;
	}


	public void setStringInArray(int index, String val) {
		((String[])curObj)[convertIndex(PrimitiveOrString.STRING, index)] = val;
	}


	// ==== add to Array ====

	public void addBooleanToArray(boolean val) {
		curObj = addToPrimitiveArray(curObj, val ? 1 : 0);
	}


	public void addByteToArray(byte val) {
		curObj = addToPrimitiveArray(curObj, val);
	}


	public void addCharToArray(char val) {
		curObj = addToPrimitiveArray(curObj, val);
	}


	public void addDoubleToArray(double val) {
		curObj = addToPrimitiveArray(curObj, Double.doubleToRawLongBits(val));
	}


	public void addFloatToArray(float val) {
		curObj = addToPrimitiveArray(curObj, Float.floatToRawIntBits(val));
	}


	public void addIntToArray(int val) {
		curObj = addToPrimitiveArray(curObj, val);
	}


	public void addLongToArray(long val) {
		curObj = addToPrimitiveArray(curObj, val);
	}


	public void addShortToArray(short val) {
		curObj = addToPrimitiveArray(curObj, val);
	}


	public void addStringToArray(String val) {
		curObj = addToObjectArray(curObj, val);
	}


	// ==== set Array ====

	public void setBooleanArray(String name, boolean[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.BOOLEAN);
	}


	public void setByteArray(String name, byte[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.BYTE);
	}


	public void setCharArray(String name, char[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.CHAR);
	}


	public void setDoubleArray(String name, double[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.DOUBLE);
	}


	public void setFloatArray(String name, float[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.FLOAT);
	}


	public void setIntArray(String name, int[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.INT);
	}


	public void setLongArray(String name, long[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.LONG);
	}


	public void setShortArray(String name, short[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.SHORT);
	}


	public void setStringArray(String name, String[] val, int off, int len) {
		setArray(name, val, off, len, PrimitiveOrString.STRING);
	}


	private void setArray(String name, Object ary, int off, int len, PrimitiveOrString type) {
		this.name = name;
		this.curPrimitive = ((long)len << 32) | off;
		this.curObj = ary;
		this.type = type;
	}


	private Object addToPrimitiveArray(Object ary, long primitiveValue) {
		if(!isArray) throwNotArray();
		int size = 0;
		int arrayOff = (int)(this.curPrimitive & 0xFFFFFFFF);
		int arrayLen = (int)(this.curPrimitive >>> 32);
		if(ary == null || arrayOff + arrayLen >= (size = Array.getLength(ary))) {
			ary = expandList(size, ary, type);
		}

		switch(type) {
		case BOOLEAN:
			Array.setBoolean(ary, arrayOff + arrayLen, primitiveValue == 1);
			break;
		case BYTE:
			Array.setByte(ary, arrayOff + arrayLen, (byte)primitiveValue);
			break;
		case CHAR:
			Array.setChar(ary, arrayOff + arrayLen, (char)primitiveValue);
			break;
		case SHORT:
			Array.setShort(ary, arrayOff + arrayLen, (short)primitiveValue);
			break;
		case INT:
			Array.setInt(ary, arrayOff + arrayLen, (int)primitiveValue);
			break;
		case LONG:
			Array.setLong(ary, arrayOff + arrayLen, primitiveValue);
			break;
		case DOUBLE:
			Array.setDouble(ary, arrayOff + arrayLen, Double.longBitsToDouble(primitiveValue));
			break;
		case FLOAT:
			Array.setFloat(ary, arrayOff + arrayLen, Float.intBitsToFloat((int)(primitiveValue & 0xFFFFFFFF)));
			break;
		case STRING:
			throw new IllegalArgumentException("cannot add String to array using addToPrimitiveArray(), use addToObjectArray()");
		default:
			throw new IllegalStateException("unknown type enum " + type);
		}
		arrayLen++;
		this.curPrimitive = ((long)arrayLen << 32) | arrayOff;
		return ary;
	}


	private Object addToObjectArray(Object ary, Object value) {
		if(!isArray) throwNotArray();
		int size = 0;
		int arrayOff = (int)(this.curPrimitive & 0xFFFFFFFF);
		int arrayLen = (int)(this.curPrimitive >>> 32);
		if(ary == null || arrayOff + arrayLen >= (size = Array.getLength(ary))) {
			ary = expandList(size, ary, type);
		}

		Array.set(ary, arrayOff + arrayLen, value);
		arrayLen++;
		this.curPrimitive = ((long)arrayLen << 32) | arrayOff;
		return ary;
	}


	private static final Object expandList(int curSize, Object ary, PrimitiveOrString type) {
		// Expand the size by 1.5x or set it to 8 whichever is larger to prevent small arrays for resizing frequently
		int newSize = curSize < 8 ? 8 : (curSize + (curSize >>> 1));
		// create the new array
		Object newAry = Array.newInstance(type.getType(), newSize);
		// copy old data into new array
		if(ary != null) {
			System.arraycopy(ary, 0, newAry, 0, curSize);
		}
		return newAry;
	}


	@Override
	public String toString() {
		int arrayOff = (int)(curPrimitive & 0xFFFFFFFF);
		int arrayLen = (int)(curPrimitive >>> 32);
		switch(type) {
		case BOOLEAN:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((boolean[])curObj).length ? (boolean[])curObj : Arrays.copyOfRange((boolean[])curObj, arrayOff, arrayOff + arrayLen)) : curPrimitive == 1);
		case BYTE:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((byte[])curObj).length ? (byte[])curObj : Arrays.copyOfRange((byte[])curObj, arrayOff, arrayOff + arrayLen)) : (byte)curPrimitive);
		case CHAR:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((char[])curObj).length ? (char[])curObj : Arrays.copyOfRange((char[])curObj, arrayOff, arrayOff + arrayLen)) : (char)curPrimitive);
		case SHORT:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((short[])curObj).length ? (short[])curObj : Arrays.copyOfRange((short[])curObj, arrayOff, arrayOff + arrayLen)) : (short)curPrimitive);
		case INT:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((int[])curObj).length ? (int[])curObj : Arrays.copyOfRange((int[])curObj, arrayOff, arrayOff + arrayLen)) : (int)curPrimitive);
		case LONG:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((long[])curObj).length ? (long[])curObj : Arrays.copyOfRange((long[])curObj, arrayOff, arrayOff + arrayLen)) : curPrimitive);
		case DOUBLE:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((double[])curObj).length ? (double[])curObj : Arrays.copyOfRange((double[])curObj, arrayOff, arrayOff + arrayLen)) : Double.longBitsToDouble(curPrimitive));
		case FLOAT:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((float[])curObj).length ? (float[])curObj : Arrays.copyOfRange((float[])curObj, arrayOff, arrayOff + arrayLen)) : Float.intBitsToFloat((int)(curPrimitive & 0xFFFFFFFF)));
		case STRING:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == ((String[])curObj).length ? (String[])curObj : Arrays.copyOfRange((String[])curObj, arrayOff, arrayOff + arrayLen)) : curObj);
		default:
			throw new IllegalStateException("unknown type enum " + type);
		}
	}


	private final void checkType(PrimitiveOrString getType) {
		if(getType != type) {
			throw new IllegalStateException("current data type is " + type + (isArray ? "[]" : "") + ", cannot fulfill data request of type " + getType);
		}
	}


	private final void throwNotArray() {
		throw new IllegalStateException("current data type is not an array");
	}


	private final void checkArray(PrimitiveOrString getType) {
		if(!isArray) throwNotArray();
		if(getType != type) {
			throw new IllegalStateException("current data type is " + type + (isArray ? "[]" : "") + ", cannot retrieve array type " + getType);
		}
	}


	private final int convertIndex(PrimitiveOrString getType, int index) {
		checkArray(getType);
		int arrayOff = (int)(curPrimitive & 0xFFFFFFFF);
		int arrayLen = (int)(curPrimitive >>> 32);
		if(index > arrayLen) {
			throw new ArrayIndexOutOfBoundsException(index + " off=" + arrayOff + ", len=" + arrayLen);
		}
		return arrayOff + index;
	}


	public static DataProxy newBoolean(String name, boolean val) {
		return new DataProxy(name, val ? 1 : 0, PrimitiveOrString.BOOLEAN);
	}


	public static DataProxy newByte(String name, byte val) {
		return new DataProxy(name, val, PrimitiveOrString.BYTE);
	}


	public static DataProxy newChar(String name, char val) {
		return new DataProxy(name, val, PrimitiveOrString.CHAR);
	}


	public static DataProxy newDouble(String name, double val) {
		return new DataProxy(name, Double.doubleToRawLongBits(val), PrimitiveOrString.DOUBLE);
	}


	public static DataProxy newFloat(String name, float val) {
		return new DataProxy(name, Float.floatToRawIntBits(val), PrimitiveOrString.FLOAT);
	}


	public static DataProxy newInt(String name, int val) {
		return new DataProxy(name, val, PrimitiveOrString.INT);
	}


	public static DataProxy newLong(String name, long val) {
		return new DataProxy(name, val, PrimitiveOrString.LONG);
	}


	public static DataProxy newShort(String name, short val) {
		return new DataProxy(name, val, PrimitiveOrString.SHORT);
	}


	public static DataProxy newString(String name, String val) {
		return new DataProxy(name, val, PrimitiveOrString.STRING);
	}


	public static DataProxy newBooleanArray(String name, boolean[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.BOOLEAN);
	}


	public static DataProxy newByteArray(String name, byte[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.BYTE);
	}


	public static DataProxy newCharArray(String name, char[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.CHAR);
	}


	public static DataProxy newDoubleArray(String name, double[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.DOUBLE);
	}


	public static DataProxy newFloatArray(String name, float[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.FLOAT);
	}


	public static DataProxy newIntArray(String name, int[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.INT);
	}


	public static DataProxy newLongArray(String name, long[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.LONG);
	}


	public static DataProxy newShortArray(String name, short[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.SHORT);
	}


	public static DataProxy newStringArray(String name, String[] val, int off, int len) {
		return new DataProxy(name, val, off, len, PrimitiveOrString.STRING);
	}

}
