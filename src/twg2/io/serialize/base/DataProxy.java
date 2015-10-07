package twg2.io.serialize.base;

import java.util.Arrays;

import twg2.primitiveIoTypes.PrimitiveOrString;

/**
 * @author TeamworkGuy2
 * @since 2015-5-21
 */
public class DataProxy {
	String name;
	PrimitiveOrString type;
	boolean isArray;
	private int arrayOff;
	private int arrayLen;

	private boolean curBool;
	private byte curByte;
	private char curChar;
	private double curDouble;
	private float curFloat;
	private int curInt;
	private long curLong;
	private short curShort;
	private String curString;

	private boolean[] curBoolAry;
	private byte[] curByteAry;
	private char[] curCharAry;
	private double[] curDoubleAry;
	private float[] curFloatAry;
	private int[] curIntAry;
	private long[] curLongAry;
	private short[] curShortAry;
	private String[] curStringAry;


	public DataProxy() {
	}


	private DataProxy(String name) {
		this.name = name;
		this.isArray = false;
	}


	private DataProxy(String name, int aryOff, int aryLen) {
		this.name = name;
		this.isArray = true;
		this.arrayOff = aryOff;
		this.arrayLen = aryLen;
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
	 * @return the length of this data array if this data type is an array, else returns 0
	 */
	public int getArrayLength() {
		checkIsArray();
		return arrayLen;
	}


	public boolean getBoolean() {
		checkType(PrimitiveOrString.BOOLEAN);
		return curBool;
	}


	public boolean[] getBooleanArray() {
		checkArray(PrimitiveOrString.BOOLEAN);
		return curBoolAry;
	}


	public boolean getBooleanFromArray(int index) {
		return curBoolAry[convertIndex(PrimitiveOrString.BOOLEAN, index)];
	}


	public char getChar() {
		checkType(PrimitiveOrString.CHAR);
		return curChar;
	}


	public char[] getCharArray() {
		checkArray(PrimitiveOrString.CHAR);
		return curCharAry;
	}


	public char getCharFromArray(int index) {
		return curCharAry[convertIndex(PrimitiveOrString.CHAR, index)];
	}


	public byte getByte() {
		checkType(PrimitiveOrString.BYTE);
		return curByte;
	}


	public byte[] getByteArray() {
		checkArray(PrimitiveOrString.BYTE);
		return curByteAry;
	}


	public byte getByteFromArray(int index) {
		return curByteAry[convertIndex(PrimitiveOrString.BYTE, index)];
	}


	public short getShort() {
		checkType(PrimitiveOrString.SHORT);
		return curShort;
	}


	public short[] getShortArray() {
		checkArray(PrimitiveOrString.SHORT);
		return curShortAry;
	}


	public short getShortFromArray(int index) {
		return curShortAry[convertIndex(PrimitiveOrString.SHORT, index)];
	}


	public int getInt() {
		checkType(PrimitiveOrString.INT);
		return curInt;
	}


	public int[] getIntArray() {
		checkArray(PrimitiveOrString.INT);
		return curIntAry;
	}


	public int getIntFromArray(int index) {
		return curIntAry[convertIndex(PrimitiveOrString.INT, index)];
	}


	public long getLong() {
		checkType(PrimitiveOrString.LONG);
		return curLong;
	}


	public long[] getLongArray() {
		checkArray(PrimitiveOrString.LONG);
		return curLongAry;
	}


	public long getLongFromArray(int index) {
		return curLongAry[convertIndex(PrimitiveOrString.LONG, index)];
	}


	public float getFloat() {
		checkType(PrimitiveOrString.FLOAT);
		return curFloat;
	}


	public float[] getFloatArray() {
		checkArray(PrimitiveOrString.FLOAT);
		return curFloatAry;
	}


	public float getFloatFromArray(int index) {
		return curFloatAry[convertIndex(PrimitiveOrString.FLOAT, index)];
	}


	public double getDouble() {
		checkType(PrimitiveOrString.DOUBLE);
		return curDouble;
	}


	public double[] getDoubleArray() {
		checkArray(PrimitiveOrString.DOUBLE);
		return curDoubleAry;
	}


	public double getDoubleFromArray(int index) {
		return curDoubleAry[convertIndex(PrimitiveOrString.DOUBLE, index)];
	}


	public String getString() {
		checkType(PrimitiveOrString.STRING);
		return curString;
	}


	public String[] getStringArray() {
		checkArray(PrimitiveOrString.STRING);
		return curStringAry;
	}


	public String getStringFromArray(int index) {
		return curStringAry[convertIndex(PrimitiveOrString.STRING, index)];
	}


	public void setBoolean(String name, boolean val) {
		this.name = name;
		this.curBool = val;
		this.type = PrimitiveOrString.BOOLEAN;
	}


	public void setByte(String name, byte val) {
		this.name = name;
		this.curByte = val;
		this.type = PrimitiveOrString.BYTE;
	}


	public void setChar(String name, char val) {
		this.name = name;
		this.curChar = val;
		this.type = PrimitiveOrString.CHAR;
	}


	public void setDouble(String name, double val) {
		this.name = name;
		this.curDouble = val;
		this.type = PrimitiveOrString.DOUBLE;
	}


	public void setFloat(String name, float val) {
		this.name = name;
		this.curFloat = val;
		this.type = PrimitiveOrString.FLOAT;
	}


	public void setInt(String name, int val) {
		this.name = name;
		this.curInt = val;
		this.type = PrimitiveOrString.INT;
	}


	public void setLong(String name, long val) {
		this.name = name;
		this.curLong = val;
		this.type = PrimitiveOrString.LONG;
	}


	public void setShort(String name, short val) {
		this.name = name;
		this.curShort = val;
		this.type = PrimitiveOrString.SHORT;
	}


	public void setString(String name, String val) {
		this.name = name;
		this.curString = val;
		this.type = PrimitiveOrString.STRING;
	}


	public void setBooleanArray(String name, boolean[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curBoolAry = val;
		this.type = PrimitiveOrString.BOOLEAN;
	}


	public void setByteArray(String name, byte[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curByteAry = val;
		this.type = PrimitiveOrString.BYTE;
	}


	public void setCharArray(String name, char[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curCharAry = val;
		this.type = PrimitiveOrString.CHAR;
	}


	public void setDoubleArray(String name, double[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curDoubleAry = val;
		this.type = PrimitiveOrString.DOUBLE;
	}


	public void setFloatArray(String name, float[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curFloatAry = val;
		this.type = PrimitiveOrString.FLOAT;
	}


	public void setIntArray(String name, int[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curIntAry = val;
		this.type = PrimitiveOrString.INT;
	}


	public void setLongArray(String name, long[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curLongAry = val;
		this.type = PrimitiveOrString.LONG;
	}


	public void setShortArray(String name, short[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curShortAry = val;
		this.type = PrimitiveOrString.SHORT;
	}


	public void setStringArray(String name, String[] val, int off, int len) {
		this.name = name;
		this.arrayOff = off;
		this.arrayLen = len;
		this.curStringAry = val;
		this.type = PrimitiveOrString.STRING;
	}


	@Override
	public String toString() {
		switch(type) {
		case BOOLEAN:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curBoolAry.length ? curBoolAry : Arrays.copyOfRange(curBoolAry, arrayOff, arrayOff + arrayLen)) : curBool);
		case BYTE:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curByteAry.length ? curByteAry : Arrays.copyOfRange(curByteAry, arrayOff, arrayOff + arrayLen)) : curByte);
		case CHAR:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curCharAry.length ? curCharAry : Arrays.copyOfRange(curCharAry, arrayOff, arrayOff + arrayLen)) : curChar);
		case SHORT:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curShortAry.length ? curShortAry : Arrays.copyOfRange(curShortAry, arrayOff, arrayOff + arrayLen)) : curShort);
		case INT:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curIntAry.length ? curIntAry : Arrays.copyOfRange(curIntAry, arrayOff, arrayOff + arrayLen)) : curInt);
		case LONG:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curLongAry.length ? curLongAry : Arrays.copyOfRange(curLongAry, arrayOff, arrayOff + arrayLen)) : curLong);
		case DOUBLE:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curDoubleAry.length ? curDoubleAry : Arrays.copyOfRange(curDoubleAry, arrayOff, arrayOff + arrayLen)) : curDouble);
		case FLOAT:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curFloatAry.length ? curFloatAry : Arrays.copyOfRange(curFloatAry, arrayOff, arrayOff + arrayLen)) : curFloat);
		case STRING:
			return "type " + (isArray ? Arrays.toString(arrayOff == 0 && arrayLen == curStringAry.length ? curStringAry : Arrays.copyOfRange(curStringAry, arrayOff, arrayOff + arrayLen)) : curString);
		default:
			throw new IllegalStateException("unknown type enum " + type);
		}
	}


	private final void checkType(PrimitiveOrString getType) {
		if(getType != type) {
			throw new IllegalStateException("current data type is " + type + ", cannot retrieve array index from array of type " + getType);
		}
	}


	private final void checkIsArray() {
		if(!isArray) {
			throw new IllegalStateException("current data type is " + type + ", cannot retrieve array index");
		}
	}


	private final void checkArray(PrimitiveOrString getType) {
		checkIsArray();
		checkType(getType);
	}


	private final int convertIndex(PrimitiveOrString getType, int index) {
		checkArray(getType);
		if(index > arrayLen) {
			throw new ArrayIndexOutOfBoundsException(index + " off=" + arrayOff + ", len=" + arrayLen);
		}
		return arrayOff + index;
	}


	public static DataProxy newBoolean(String name, boolean val) {
		DataProxy d = new DataProxy(name);
		d.curBool = val;
		d.type = PrimitiveOrString.BOOLEAN;
		return d;
	}


	public static DataProxy newByte(String name, byte val) {
		DataProxy d = new DataProxy(name);
		d.curByte = val;
		d.type = PrimitiveOrString.BYTE;
		return d;
	}


	public static DataProxy newChar(String name, char val) {
		DataProxy d = new DataProxy(name);
		d.curChar = val;
		d.type = PrimitiveOrString.CHAR;
		return d;
	}


	public static DataProxy newDouble(String name, double val) {
		DataProxy d = new DataProxy(name);
		d.curDouble = val;
		d.type = PrimitiveOrString.DOUBLE;
		return d;
	}


	public static DataProxy newFloat(String name, float val) {
		DataProxy d = new DataProxy(name);
		d.curFloat = val;
		d.type = PrimitiveOrString.FLOAT;
		return d;
	}


	public static DataProxy newInt(String name, int val) {
		DataProxy d = new DataProxy(name);
		d.curInt = val;
		d.type = PrimitiveOrString.INT;
		return d;
	}


	public static DataProxy newLong(String name, long val) {
		DataProxy d = new DataProxy(name);
		d.curLong = val;
		d.type = PrimitiveOrString.LONG;
		return d;
	}


	public static DataProxy newShort(String name, short val) {
		DataProxy d = new DataProxy(name);
		d.curShort = val;
		d.type = PrimitiveOrString.SHORT;
		return d;
	}


	public static DataProxy newString(String name, String val) {
		DataProxy d = new DataProxy(name);
		d.curString = val;
		d.type = PrimitiveOrString.STRING;
		return d;
	}


	public static DataProxy newBooleanArray(String name, boolean[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curBoolAry = val;
		d.type = PrimitiveOrString.BOOLEAN;
		return d;
	}


	public static DataProxy newByteArray(String name, byte[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curByteAry = val;
		d.type = PrimitiveOrString.BYTE;
		return d;
	}


	public static DataProxy newCharArray(String name, char[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curCharAry = val;
		d.type = PrimitiveOrString.CHAR;
		return d;
	}


	public static DataProxy newDoubleArray(String name, double[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curDoubleAry = val;
		d.type = PrimitiveOrString.DOUBLE;
		return d;
	}


	public static DataProxy newFloatArray(String name, float[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curFloatAry = val;
		d.type = PrimitiveOrString.FLOAT;
		return d;
	}


	public static DataProxy newIntArray(String name, int[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curIntAry = val;
		d.type = PrimitiveOrString.INT;
		return d;
	}


	public static DataProxy newLongArray(String name, long[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curLongAry = val;
		d.type = PrimitiveOrString.LONG;
		return d;
	}


	public static DataProxy newShortArray(String name, short[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curShortAry = val;
		d.type = PrimitiveOrString.SHORT;
		return d;
	}


	public static DataProxy newStringArray(String name, String[] val, int off, int len) {
		DataProxy d = new DataProxy(name, off, len);
		d.curStringAry = val;
		d.type = PrimitiveOrString.STRING;
		return d;
	}

}
