package xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A compound XML Attribute that can contain multiple attributes. Its purpose is to be reused for each XML element
 * being written by an {@link XmlOutput} stream or read by an {@link XmlInput} stream.
 * {@link XmlAttributes#clear()}, 
 * @author TeamworkGuy2
 * @since 2013-6-3
 */
public class XmlAttributes {
	List<String> names;
	List<String> namesIm;
	List<Object> values;
	List<Object> valuesIm;
	List<Byte> types;
	List<Byte> typesIm;
	List<Integer> arraySize;
	List<Integer> arraySizeIm;


	public XmlAttributes() {
		super();
		names = new ArrayList<String>();
		namesIm = Collections.unmodifiableList(names);
		values = new ArrayList<Object>();
		valuesIm = Collections.unmodifiableList(values);
		types = new ArrayList<Byte>();
		typesIm = Collections.unmodifiableList(types);
		arraySize = new ArrayList<Integer>();
		arraySizeIm = Collections.unmodifiableList(arraySize);
	}


	/** Add a boolean attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's boolean value
	 */
	public void addAttributeBoolean(String name, boolean value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.BOOLEAN_TYPE));
		arraySize.add(null);
	}


	/** Add a character attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's character value
	 */
	public void addAttributeChar(String name, char value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.CHAR_TYPE));
		arraySize.add(null);
	}


	/** Add a byte attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's byte value
	 */
	public void addAttributeByte(String name, byte value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.BYTE_TYPE));
		arraySize.add(null);
	}


	/** Add a short value attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's short value
	 */
	public void addAttributeShort(String name, short value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.SHORT_TYPE));
		arraySize.add(null);
	}


	/** Add an int attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's int value
	 */
	public void addAttributeInt(String name, int value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.INT_TYPE));
		arraySize.add(null);
	}


	/** Add a long value attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's long value
	 */
	public void addAttributeLong(String name, long value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.LONG_TYPE));
		arraySize.add(null);
	}


	/** Add a float attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's float value
	 */
	public void addAttributeFloat(String name, float value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.FLOAT_TYPE));
		arraySize.add(null);
	}


	/** Add a double value attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's double value
	 */
	public void addAttributeDouble(String name, double value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.DOUBLE_TYPE));
		arraySize.add(null);
	}


	/** Add an attribute to this list of XML attributes.
	 * @param name the name of the attribute
	 * @param value the attribute's value (as a string)
	 */
	public void addAttribute(String name, String value) {
		names.add(name);
		values.add(value);
		types.add(Byte.valueOf((byte)XmlHandler.STRING_TYPE));
		arraySize.add(null);
	}


	/** Add the specified attribute name and list of values
	 * to this group of attributes as an {@link XmlHandler#ARRAY_TYPE array type} attribute.
	 * A class cast exception will be generated if the items in the list are not all of the same valid type.
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
		types.add(Byte.valueOf((byte)(type & XmlHandler.ARRAY_TYPE)));
		arraySize.add(size);
	}


	// Get the name or value of one of the attribute stored in this object
	/** Get the name of the nth attribute in this attribute group
	 * @param index the index of the attribute to get
	 * @return the attribute's value
	 */
	public String getAttributeName(int index) {
		return names.get(index);
	}


	/** Get the length of the array of the nth attribute in this attribute group.
	 * This assumes that the nth attribute contains an array data type.
	 * @param index the index of the attribute to get
	 * @return the length of the array belonging to the specified attribute index
	 */
	public int getAttributeDataArrayLength(int index) {
		return arraySize.get(index);
	}


	/** Get the boolean value of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the boolean value of the attribute at the specified index
	 */
	public boolean getAttributeBoolean(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Boolean.parseBoolean((String)obj);
		}
		else {
			return (Boolean)obj;
		}
	}


	/** Get the character value of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the character value of the attribute at the specified index
	 */
	public char getAttributeChar(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return ((String)obj).charAt(0);
		}
		else {
			return (Character)obj;
		}
	}


	/** Get the byte value of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the byte of the attribute at the specified index
	 */
	public byte getAttributeByte(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Byte.parseByte((String)obj, 10);
		}
		else {
			return (Byte)obj;
		}
	}


	/** Get the short value of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the short value of the attribute at the specified index
	 */
	public short getAttributeShort(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Short.parseShort((String)obj, 10);
		}
		else {
			return (Short)obj;
		}
	}


	/** Get the integer value of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the int value of the attribute at the specified index
	 */
	public int getAttributeInt(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Integer.parseInt((String)obj, 10);
		}
		else {
			return (Integer)obj;
		}
	}


	/** Get the long value of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the long value of the attribute at the specified index
	 */
	public long getAttributeLong(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Long.parseLong((String)obj, 10);
		}
		else {
			return (Long)obj;
		}
	}


	/** Get the float value of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the float value of the attribute at the specified index
	 */
	public float getAttributeFloat(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Float.parseFloat((String)obj);
		}
		else {
			return (Float)obj;
		}
	}


	/** Get the double value of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the double value of the attribute at the specified index
	 */
	public double getAttributeDouble(int index) {
		Object obj = values.get(index);
		if(obj instanceof String) {
			return Double.parseDouble((String)obj);
		}
		else {
			return (Double)obj;
		}
	}


	/** Get the value (as a string) of the specified attribute index
	 * @param index the index of the attribute to get the value from
	 * @return the String value of the attribute at the specified index
	 */
	public String getAttributeString(int index) {
		return (String)values.get(index);
	}


	/** Get the list of attribute names in this attribute group
	 * @return the unmodifiable list of attribute names in this attribute groups
	 */
	public List<String> getAttributeNames() {
		return namesIm;
	}


	/** Returns a list of objects which will be one of the classes:
	 * <code>Byte, Short, Integer, Long, Float, Double, Boolean, Char, String</code>
	 * @return the unmodifiable list of values in this attribute group
	 */
	public List<Object> getAttributeValues() {
		return valuesIm;
	}


	/** Returns a list of data types which will be one of the {@link XmlHandler} constants:
	 * <code>{@link XmlHandler#BYTE_TYPE BYTE_TYPE}, {@link XmlHandler#SHORT_TYPE SHORT_TYPE}, {@link XmlHandler#INT_TYPE INT_TYPE},
	 * {@link XmlHandler#LONG_TYPE LONG_TYPE}, {@link XmlHandler#FLOAT_TYPE FLOAT_TYPE}, {@link XmlHandler#DOUBLE_TYPE DOUBLE_TYPE},
	 * {@link XmlHandler#BOOLEAN_TYPE BOOLEAN_TYPE}, {@link XmlHandler#CHAR_TYPE CHAR_TYPE},
	 * {@link XmlHandler#STRING_TYPE STRING_TYPE},
	 * {@link XmlHandler#ARRAY_TYPE ARRAY_TYPE}</code>
	 * @return the list of types in this attribute group
	 */
	public List<Byte> getAttributeTypes() {
		return typesIm;
	}


	/** Returns a list of values corresponding to the array length of the data types stored in this attribute group, or null if the
	 * data element is not an {@link XmlHandler#ARRAY_TYPE ARRAY_TYPE}.
	 * @return a list of values corresponding to the array length of the data types stored in this attribute group, or null if the
	 * data element is not an {@link XmlHandler#ARRAY_TYPE ARRAY_TYPE}.
	 */
	public List<Integer> getAttributeArrayLengths() {
		return arraySizeIm;
	}


	/** Check if all of the values in the specified array are of the same type or subtype and
	 * return the {@link XmlHandler} value of the specified array.
	 * @param values the list of values to check the type of
	 * @return the data type of the list, for example: {@link XmlHandler#INT_TYPE}, {@link XmlHandler#STRING_TYPE}, etc.
	 */
	private byte checkType(List<Object> values) {
		Object obj = values.get(0);
		Class<?> clazz = obj.getClass();
		for(Object value : values) {
			if(!clazz.isInstance(value)) {
				throw new IllegalArgumentException("The array values to check are not all of the same class type");
			}
		}
		if(obj instanceof String) {
			return XmlHandler.STRING_TYPE;
		}
		else if(obj instanceof Integer) {
			return XmlHandler.INT_TYPE;
		}
		else if(obj instanceof Float) {
			return XmlHandler.FLOAT_TYPE;
		}
		else if(obj instanceof Boolean) {
			return XmlHandler.BOOLEAN_TYPE;
		}
		else if(obj instanceof Byte) {
			return XmlHandler.BYTE_TYPE;
		}
		else if(obj instanceof Short) {
			return XmlHandler.SHORT_TYPE;
		}
		else if(obj instanceof Long) {
			return XmlHandler.LONG_TYPE;
		}
		else if(obj instanceof Double) {
			return XmlHandler.DOUBLE_TYPE;
		}
		else if(obj instanceof Character) {
			return XmlHandler.CHAR_TYPE;
		}
		else {
			return XmlHandler.NO_TYPE;
		}
	}


	/** Converts a list of objects into an array of primitives based on the type of the item
	 * in the list at index <code>offset</code>.
	 * For example, if the object at index <code>offset</code> in the list is of type {@link Integer}
	 * a <code>new int[length]</code> array is created and the list of values starting
	 * at <code>offset</code> for <code>length</code> number of items are retrieved
	 * from the list and cast to an int and stored in the array.
	 * Valid types are <code>Byte, Short, Integer, Long, Float, Double, Character, Boolean, and String</code>.
	 * @param values the list of values to convert to an array
	 * @param offset the offset into the list of values at which to start converting to an array
	 * @param length the number of items from the list to convert to an array
	 * @return an array <code>length</code> long with the same class type as the item at
	 * index <code>offset</code> in the list.
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


	/** Clear all of the names and values in this XML attribute group
	 */
	public void clear() {
		names.clear();
		values.clear();
		types.clear();
	}


	/**
	 * @return the number of attributes in this XML attribute group
	 */
	public int size() {
		return names.size();
	}


	@Override
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
