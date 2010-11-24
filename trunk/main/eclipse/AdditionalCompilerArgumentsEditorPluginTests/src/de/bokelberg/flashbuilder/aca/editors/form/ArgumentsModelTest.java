package de.bokelberg.flashbuilder.aca.editors.form;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.bokelberg.flashbuilder.aca.editors.form.ArgumentsModel;

public class ArgumentsModelTest {

	private ArgumentsModel model;
	private ArgumentsModelStringRenderer renderer;
	
	@Before
	public void setUp() throws Exception {
		model = new ArgumentsModel();
		renderer = new ArgumentsModelStringRenderer(model);
	}

	@Test
	public void testUpdateBoolean() {
		String expectedId = "-test";
		model.updateBoolean( expectedId, true);
		String result = renderer.render();
		assertEquals( expectedId + "=true", result);
	}
	
	@Test
	public void testUpdateString() {
		String expectedId = "-test";
		String expectedValue = "some value";
		model.updateSingleString( expectedId, expectedValue );
		String result = renderer.render();
		assertEquals( expectedId + "=\"" + expectedValue + "\"", result);
	}
	

}
