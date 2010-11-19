package de.bokelberg.flashbuilder.aca.editors.form;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.bokelberg.flashbuilder.aca.editors.form.ArgumentsModel;

public class ArgumentsModelTest {

	private ArgumentsModel model;
	
	@Before
	public void setUp() throws Exception {
		model = new ArgumentsModel();
	}

	@Test
	public void testUpdateBoolean() {
		String expectedId = "-test";
		model.updateBoolean( expectedId, true, false);
		String result = model.getString();
		assertEquals( expectedId + "=true", result);
	}
	
	@Test
	public void testUpdateString() {
		String expectedId = "-test";
		String expectedValue = "some value";
		model.updateString( expectedId, expectedValue );
		String result = model.getString();
		assertEquals( expectedId + "=\"" + expectedValue + "\"", result);
	}
	

}
