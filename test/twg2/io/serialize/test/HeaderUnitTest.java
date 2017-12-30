package twg2.io.serialize.test;

import org.junit.Assert;
import org.junit.Test;

import twg2.io.serialize.base.DataElementImpl;

public class HeaderUnitTest {

	@Test
	public void testDataElementEquality() {
		String name = "name";
		int id = 2;
		String nam0 = null;
		int i0 = -1;
		// Truth table for the equals method in DataHeader and implementations
		// Default headers
		Assert.assertTrue(DataElementImpl.equals(name, id, name, id));
		Assert.assertTrue(DataElementImpl.equals(name, id, name, i0));
		Assert.assertTrue(DataElementImpl.equals(name, i0, name, i0));
		Assert.assertTrue(DataElementImpl.equals(name, id, nam0, id));
		Assert.assertFalse(DataElementImpl.equals(name, id, nam0, i0));
		Assert.assertFalse(DataElementImpl.equals(name, i0, nam0, i0));
		Assert.assertTrue(DataElementImpl.equals(nam0, id, nam0, id));
		Assert.assertFalse(DataElementImpl.equals(nam0, id, nam0, i0));
		Assert.assertTrue(DataElementImpl.equals(nam0, i0, nam0, i0));
	}

}
