package de.bokelberg.flashbuilder.aca.editors.form;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test whether the boolean on/off properties work as expected
 * 
 * @author rbokel
 */
public class BooleanPropertiesTest {

	private ArgumentsParser parser;
	private ArgumentsModel model;

	@Before
	public void setUp() {
		model = new ArgumentsModel();
		parser = new ArgumentsParser(model);
	}

	@Test
	public void givenNoArgumentsAreSet_thenResultShouldBeEmpty() {
		parser.parse("");
		String result = model.getString();
		assertEquals("", result);
	}

	@Test
	public void givenAOptionWithDefaultValueFalse_whenSelectingThisOption_thenResultShouldContainIt() {
		parser.parse("");
		model.updateBoolean("test", true, false);		
		String result = model.getString();
		assertEquals("test=true", result);
	}

	@Test
	public void givenAOptionWithDefaultValueFalse_whenDeselectingThisOption_thenResultShouldBeEmpty() {
		parser.parse("");
		model.updateBoolean("test", true, false);
		model.updateBoolean("test", false, false);
		String result = model.getString();
		assertEquals("", result);
	}
	
	@Test
	public void givenAOptionWithDefaultValueFalse_whenOptionIsFalseInInput_thenResultShouldBeEmpty() {
		parser.parse("test=false");
		String result = model.getString();
		assertEquals("", result);
	}

	@Test
	public void givenAOptionWithDefaultValueTrue_whenUnselectingThisOption_thenResultShouldContainIt() {
		parser.parse("");
		model.updateBoolean("test", false, true);		
		String result = model.getString();
		assertEquals("test=false", result);
	}
	
	@Test
	public void givenAOptionWithDefaultValueTrue_whenSelectingThisOption_thenResultShouldContainIt() {
		parser.parse("");
		model.updateBoolean("test", false, true);		
		model.updateBoolean("test", true, true);		
		String result = model.getString();
		assertEquals("", result);
	}
	
	@Test
	public void givenAOptionWithDefaultValueTrue_whenOptionIsTrueInInput_thenResultShouldBeEmpty() {
		parser.parse("test=true");
		String result = model.getString();
		assertEquals("", result);
	}
	
}
