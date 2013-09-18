package xml;

import java.util.ArrayList;
import java.util.List;

/** A compound XML Attribute that can contain multiple attributes. Its purpose is to be reused for each XML element
 * being written by an {@link XMLOutput} stream or read by an {@link XMLInput} stream.
 * {@link XMLAttributes#clear()}, 
 * @author TeamworkGuy2
 * @since 2013-6-3
 */
public class XMLAttributes {
	List<String> names;
	List<Object> values;
	List<Byte> types;
	List<Integer> arraySize;


	public XMLAttributes() {
		super();
		names = new ArrayList<String>();
		values = new ArrayList<Object>();
		types = new ArrayList<Byte>();
		arraySize = new ArrayList<Integer>();
	}


	// Add an attribute's name and value to this group
	public void addAttribute(String name, boolean value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.BOOLEAN_TYPE));
		arraySize.add(null);
	}

	public void addAttribute(String name, char value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.CHAR_TYPE));
		arraySize.add(null);
	}

	public void addAttribute(String name, byte value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.BYTE_TYPE));
		arraySize.add(null);
	}

	public void addAttribute(String name, short value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.SHORT_TYPE));
		arraySize.add(null);
	}

	public void addAttribute(String name, int value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.INT_TYPE));
		arraySize.add(null);
	}

	public void addAttribute(String name, long value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.LONG_TYPE));
		arraySize.add(null);
	}

	public void addAttribute(String name, float value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.FLOAT_TYPE));
		arraySize.add(null);
	}

	public void addAttribute(String name, double value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.DOUBLE_TYPE));
		arraySize.add(null);
	}

	public void addAttribute(String name, String value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XMLHandler.STRING_TYPE));
		arraySize.add(null);
	}


	/** Add the specified attribute name and list of values to this group of attributes as an {@link XMLHandler#ARRAY_TYPE array type}
	 * attribute. A class cast exception will be generated if the items in the list are not all of the same valid type.
	 * Valid types include: Byte, Short, Integer, Long, Float, Double, Boolean, Character, and String.
	 * @param name - the name of the attribute to add
	 * @param valueList - the list of values to store as the attribute's value
	 */
	public void addAttribute(String name, List<Object> valueList) {
		byte type = checkType(valueList);
		int size = valueList.size();
		Object valuesArray = toArray(valueList, 0, size);
		names.add(name);
		values.add(valuesArray);
		types.add(Byte.valueOf((byte)(type & XMLHandler.ARRAY_TYPE)));
		arraySize.add(size);
	}


	// Get the name or value of one of the attribute stored in this object
	public String getAttributeName(int index) {
		return names.get(index);
	}


	public int getAttributeDataArrayLength(int index) {
		return arraySize.get(index);
	}


	public boolean getAttributeBoolean(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Boolean.parseBoolean((String)obj);
		}
		else {
			return (Boolean)obj;
		}
	}


	public char getAttributeChar(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return ((String)obj).charAt(0);
		}
		else {
			return (Character)obj;
		}
	}


	public byte getAttributeByte(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Byte.parseByte((String)obj, 10);
		}
		else {
			return (Byte)obj;
		}
	}


	public short getAttributeShort(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Short.parseShort((String)obj, 10);
		}
		else {
			return (Short)obj;
		}
	}


	public int getAttributeInt(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Integer.parseInt((String)obj, 10);
		}
		else {
			return (Integer)obj;
		}
	}


	public long getAttributeLong(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Long.parseLong((String)obj, 10);
		}
		else {
			return (Long)obj;
		}
	}


	public float getAttributeFloat(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Float.parseFloat((String)obj);
		}
		else {
			return (Float)obj;
		}
	}


	public double getAttributeDouble(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Double.parseDouble((String)obj);
		}
		else {
			return (Double)obj;
		}
	}


	public String getAttributeString(int index) {
		return (String)values.get(index);
	}


	/** Get the list of attribute names in this attribute group
	 * @return the list of attribute names in this attribute groups
	 */
	public List<String> getAttributeNames() {
		return names;
	}


	/** Returns a list of objects which will be one of the classes:
	 * <code>Byte, Short, Integer, Long, Float, Double, Boolean, Char, String</code>
	 * @return the list of values in this attribute group
	 */
	public List<Object> getAttributeValues() {
		return values;
	}


	/** Returns a list of data types which will be one of the {@link XMLHandler} constants:
	 * <code>{@link XMLHandler#BYTE_TYPE BYTE_TYPE}, {@link XMLHandler#SHORT_TYPE SHORT_TYPE}, {@link XMLHandler#INT_TYPE INT_TYPE},
	 * {@link XMLHandler#LONG_TYPE LONG_TYPE}, {@link XMLHandler#FLOAT_TYPE FLOAT_TYPE}, {@link XMLHandler#DOUBLE_TYPE DOUBLE_TYPE},
	 * {@link XMLHandler#BOOLEAN_TYPE BOOLEAN_TYPE}, {@link XMLHandler#CHAR_TYPE CHAR_TYPE},
	 * {@link XMLHandler#STRING_TYPE STRING_TYPE},
	 * {@link XMLHandler#ARRAY_TYPE ARRAY_TYPE}</code>
	 * @return the list of types in this attribute group
	 */
	public List<Byte> getAttributeTypes() {
		return types;
	}


	/** Returns a list of values corresponding to the array length of the data types stored in this attribute group, or null if the
	 * data element is not an {@link XMLHandler#ARRAY_TYPE ARRAY_TYPE}.
	 * @return a list of values corresponding to the array length of the data types stored in this attribute group, or null if the
	 * data element is not an {@link XMLHandler#ARRAY_TYPE ARRAY_TYPE}.
	 */
	public List<Integer> getAttributeArrayLengths() {
		return arraySize;
	}


	private byte checkType(List<Object> values) {
		Object obj = values.get(0);
		if(obj instanceof String) {
			return XMLHandler.STRING_TYPE;
		}
		else if(obj instanceof Integer) {
			return XMLHandler.INT_TYPE;
		}
		else if(obj instanceof Float) {
			return XMLHandler.FLOAT_TYPE;
		}
		else if(obj instanceof Boolean) {
			return XMLHandler.BOOLEAN_TYPE;
		}
		else if(obj instanceof Byte) {
			return XMLHandler.BYTE_TYPE;
		}
		else if(obj instanceof Short) {
			return XMLHandler.SHORT_TYPE;
		}
		else if(obj instanceof Long) {
			return XMLHandler.LONG_TYPE;
		}
		else if(obj instanceof Double) {
			return XMLHandler.DOUBLE_TYPE;
		}
		else if(obj instanceof Character) {
			return XMLHandler.CHAR_TYPE;
		}
		else {
			return XMLHandler.NO_TYPE;
		}
	}


	/** Converts a list of objects into an array of primitives based on the type of the item in the list at index <code>offset</code>.
	 * For example, if the object at index <code>offset</code> in the list is of type {@link Integer} a <code>new int[length]</code>
	 * array is created and the list of values starting at <code>offset</code> for <code>length</code> number of items are retrieved
	 * from the list and cast to an int and stored in the array.
	 * Valid types are <code>Byte, Short, Integer, Long, Float, Double, Character, Boolean, and String</code>.
	 * @param values - the list of values to convert to an array
	 * @param offset - the offset into the list of values at which to start converting to an array
	 * @param length - the number of items from the list to convert to an array
	 * @return an array <code>length</code> long with the same runtime type as the item at index <code>offset</code> in the list.
	 */
	private Object toArray(List<Object> values, int offset, int length) {
		String[] st = null;
		long[] lo = null;
		int[] in = null;
		short[] sh = null;
		byte[] by = null;
		float[] fl = null;
		double[] dou = null;
		char[] ch = null;
		boolean[] bo = null;
		Object obj = values.get(offset);

		if(obj instanceof String) {
			st = new String[length];
			for(int i = 0; i < length; i++) {
				st[i] = (String)values.get(offset+i);
			}
			return st;
		}
		else if(obj instanceof Integer) {
			in = new int[length];
			for(int i = 0; i < length; i++) {
				in[i] = (Integer)values.get(offset+i);
			}
			return st;
		}
		else if(obj instanceof Float) {
			fl = new float[length];
			for(int i = 0; i < length; i++) {
				fl[i] = (Float)values.get(offset+i);
			}
			return st;
		}
		else if(obj instanceof Boolean) {
			bo = new boolean[length];
			for(int i = 0; i < length; i++) {
				bo[i] = (Boolean)values.get(offset+i);
			}
			return st;
		}
		else if(obj instanceof Byte) {
			by = new byte[length];
			for(int i = 0; i < length; i++) {
				by[i] = (Byte)values.get(offset+i);
			}
			return st;
		}
		else if(obj instanceof Short) {
			sh = new short[length];
			for(int i = 0; i < length; i++) {
				sh[i] = (Short)values.get(offset+i);
			}
			return st;
		}
		else if(obj instanceof Long) {
			lo = new long[length];
			for(int i = 0; i < length; i++) {
				lo[i] = (Long)values.get(offset+i);
			}
			return st;
		}
		else if(obj instanceof Double) {
			dou = new double[length];
			for(int i = 0; i < length; i++) {
				dou[i] = (Double)values.get(offset+i);
			}
			return st;
		}
		else if(obj instanceof Character) {
			ch = new char[length];
			for(int i = 0; i < length; i++) {
				ch[i] = (Character)values.get(offset+i);
			}
			return st;
		}
		else {
			return null;
		}
	}


	public void clear() {
		names.clear();
		values.clear();
		types.clear();
	}


	public int size() {
		return names.size();
	}


	public String toString() {
		StringBuilder str = new StringBuilder();
		List<String> name = names;
		List<Object> value = values;
		int size = name.size();
		for(int i = 0; i < size-1; i++) {
			str.append(name.get(i));
			str.append('=');
			str.append(value.get(i));
			str.append(',');
			str.append(' ');
		}
		str.append(name.get(size-1));
		str.append('=');
		str.append(value.get(size-1));
		return str.toString();
	}

}
