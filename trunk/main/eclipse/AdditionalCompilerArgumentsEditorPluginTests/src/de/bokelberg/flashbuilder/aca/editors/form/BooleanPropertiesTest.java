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
	private ArgumentsModelStringRenderer renderer;
	private ValueIsDifferentFromDefaultValuePredicate predicate;
	private DefaultValues defaultValues;

	@Before
	public void setUp() {
		model = new ArgumentsModel();
		parser = new ArgumentsParser(model);
		
		defaultValues = new DefaultValues();
		predicate = new ValueIsDifferentFromDefaultValuePredicate(defaultValues);
		renderer = new FilteringArgumentsModelStringRenderer(model, predicate);
	}

	@Test
	public void givenNoArgumentsAreSet_thenResultShouldBeEmpty() {
		parser.parse("");
		String result = renderer.render();
		assertEquals("", result);
	}

	@Test
	public void givenAOptionWithDefaultValueFalse_whenSelectingThisOption_thenResultShouldContainIt() {
		parser.parse("");
		model.updateBoolean("-test", true);		
		String result = renderer.render();
		assertEquals("-test=true", result);
	}

	@Test
	public void givenAOptionWithDefaultValueFalse_whenDeselectingThisOption_thenResultShouldBeEmpty() {
		parser.parse("");
		defaultValues.addDefaultValue("-test", false);
		model.updateBoolean("-test", true);
		model.updateBoolean("-test", false);
		String result = renderer.render();
		assertEquals("", result);
	}
	
	@Test
	public void givenAOptionWithDefaultValueFalse_whenOptionIsFalseInInput_thenResultShouldBeEmpty() {
		parser.parse("-test=false");
		defaultValues.addDefaultValue("-test", false);
		String result = renderer.render();
		assertEquals("", result);
	}

	@Test
	public void givenAOptionWithDefaultValueTrue_whenUnselectingThisOption_thenResultShouldContainIt() {
		parser.parse("");
		defaultValues.addDefaultValue("-test", true);
		model.updateBoolean("-test", false);		
		String result = renderer.render();
		assertEquals("-test=false", result);
	}
	
	@Test
	public void givenAOptionWithDefaultValueTrue_whenSelectingThisOption_thenResultShouldContainIt() {
		parser.parse("");
		defaultValues.addDefaultValue("-test", true);
		model.updateBoolean("-test", false);		
		model.updateBoolean("-test", true);		
		String result = renderer.render();
		assertEquals("", result);
	}
	
	@Test
	public void givenAOptionWithDefaultValueTrue_whenOptionIsTrueInInput_thenResultShouldBeEmpty() {
		parser.parse("-test=true");
		defaultValues.addDefaultValue("-test", true);
		String result = renderer.render();
		assertEquals("", result);
	}
	
}
