package twg2.io.serialize.test;

import static org.junit.Assert.*;

import org.junit.Test;

import twg2.io.serialize.base.DataProxy;
import twg2.simpleTypes.ioPrimitives.PrimitiveOrString;

/**
 * @author TeamworkGuy2
 * @since 2017-12-30
 */
public class DataProxyTest {

	@Test
	public void primitiveTest() {
		DataProxy p = null;

		p = DataProxy.newBoolean("a", true);
		assertTrue(p.getBoolean() == true);
		p.setBoolean("a2", false);
		assertTrue(p.getBoolean() == false);
		assertEquals("a2", p.getName());

		p = DataProxy.newByte("b", (byte)0);
		assertEquals(p.getByte(), 0);
		p.setByte("b2", (byte)120);
		assertEquals(p.getByte(), 120);
		assertEquals("b2", p.getName());

		p = DataProxy.newChar("c", (char)0);
		assertEquals(p.getChar(), 0);
		p.setChar("c2", 'Z');
		assertEquals(p.getChar(), 'Z');
		assertEquals("c2", p.getName());

		p = DataProxy.newShort("d", (short)0);
		assertEquals(p.getShort(), 0);
		p.setShort("d2", (short)1400);
		assertEquals(p.getShort(), 1400);
		assertEquals("d2", p.getName());

		p = DataProxy.newInt("e", 0);
		assertEquals(p.getInt(), 0);
		p.setInt("e2", 70800);
		assertEquals(p.getInt(), 70800);
		assertEquals("e2", p.getName());

		p = DataProxy.newLong("f", 0);
		assertEquals(p.getLong(), 0);
		p.setLong("f2", 5400300100L);
		assertEquals(p.getLong(), 5400300100L);
		assertEquals("f2", p.getName());

		p = DataProxy.newFloat("g", 0f);
		assertEquals(p.getFloat(), 0f, 0);
		p.setFloat("g2", 12.135f);
		assertEquals(p.getFloat(), 12.135f, 0);
		assertEquals("g2", p.getName());

		p = DataProxy.newDouble("h", 0);
		assertEquals(p.getDouble(), 0, 0);
		p.setDouble("h2", 1234.56789);
		assertEquals(p.getDouble(), 1234.56789, 0);
		assertEquals("h2", p.getName());
	}


	@Test
	public void arrayTest() {
		DataProxy p = null;

		p = DataProxy.newBooleanArray("a", new boolean[0], 0, 0);
		assertArrayEquals(p.getBooleanArray(), new boolean[0]);
		p.setBooleanArray("a2", new boolean[] { true, false, false, true }, 1, 3);
		assertArrayEquals(p.getBooleanArray(), new boolean[] { true, false, false, true });
		assertTrue(p.getBooleanFromArray(0) == false);

		p = DataProxy.newByteArray("b", new byte[0], 0, 0);
		assertArrayEquals(p.getByteArray(), new byte[0]);
		p.setByteArray("b2", new byte[] { (byte)0, (byte)1, (byte)2, (byte)5 }, 1, 3);
		assertArrayEquals(p.getByteArray(), new byte[] { (byte)0, (byte)1, (byte)2, (byte)5 });
		assertEquals(p.getByteFromArray(0), 1);

		p = DataProxy.newCharArray("c", new char[0], 0, 0);
		assertArrayEquals(p.getCharArray(), new char[0]);
		p.setCharArray("c2", new char[] { 'A', '3', 'B', 'X' }, 1, 3);
		assertArrayEquals(p.getCharArray(), new char[] { 'A', '3', 'B', 'X' });
		assertEquals(p.getCharFromArray(0), '3');

		p = DataProxy.newShortArray("d", new short[0], 0, 0);
		assertArrayEquals(p.getShortArray(), new short[0]);
		p.setShortArray("d2", new short[] { (short)0, (short)1, (short)2, (short)5 }, 1, 3);
		assertArrayEquals(p.getShortArray(), new short[] { (short)0, (short)1, (short)2, (short)5 });
		assertEquals(p.getShortFromArray(0), 1);

		p = DataProxy.newIntArray("e", new int[0], 0, 0);
		assertArrayEquals(p.getIntArray(), new int[0]);
		p.setIntArray("e2", new int[] { 0, 1, 2, 5 }, 1, 3);
		assertArrayEquals(p.getIntArray(), new int[] { 0, 1, 2, 5 });
		assertEquals(p.getIntFromArray(0), 1);

		p = DataProxy.newLongArray("f", new long[0], 0, 0);
		assertArrayEquals(p.getLongArray(), new long[0]);
		p.setLongArray("f2", new long[] { 0, 1, 2, 5 }, 1, 3);
		assertArrayEquals(p.getLongArray(), new long[] { 0, 1, 2, 5 });
		assertEquals(p.getLongFromArray(0), 1);

		p = DataProxy.newFloatArray("g", new float[0], 0, 0);
		assertArrayEquals(p.getFloatArray(), new float[0], 0);
		p.setFloatArray("g2", new float[] { 0, 1.1f, 2, 5 }, 1, 3);
		assertArrayEquals(p.getFloatArray(), new float[] { 0, 1.1f, 2, 5 }, 0);
		assertEquals(p.getFloatFromArray(0), 1.1f, 0);

		p = DataProxy.newDoubleArray("h", new double[0], 0, 0);
		assertArrayEquals(p.getDoubleArray(), new double[0], 0);
		p.setDoubleArray("h2", new double[] { 0, 1.2345, 2, 5 }, 1, 3);
		assertArrayEquals(p.getDoubleArray(), new double[] { 0, 1.2345, 2, 5 }, 0);
		assertEquals(p.getDoubleFromArray(0), 1.2345, 0);
	}


	@Test
	public void arrayAddTest() {
		DataProxy p = null;

		p = new DataProxy(PrimitiveOrString.INT, true);
		p.addIntToArray(0);
		assertEquals(1, p.getArrayLength());
		p.addIntToArray(1);
		p.addIntToArray(2);
		assertArrayEquals(new int[] { 0, 1, 2 }, (int[])p.getArrayCopy());
	}

}
