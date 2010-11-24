package de.bokelberg.flashbuilder.aca.editors.form;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test whether the boolean on/off properties work as expected
 * 
 * @author rbokel
 */
public class MultipleStringsPropertiesTest {

	private ArgumentsParser parser;
	private ArgumentsModel model;
	private ArgumentsModelStringRenderer renderer;
	private ValueIsDifferentFromDefaultPredicate predicate;
	private DefaultValues defaultValues;

	@Before
	public void setUp() {
		model = new ArgumentsModel();
		parser = new ArgumentsParser(model);
		
		defaultValues = new DefaultValues();
		predicate = new ValueIsDifferentFromDefaultPredicate(defaultValues);
		renderer = new FilteringArgumentsModelStringRenderer(model, predicate);
	}

	@Test
	public void givenNoArgumentsAreSet_thenResultShouldBeEmpty() {
		parser.parse("");
		String result = renderer.render();
		assertEquals("", result);
	}

	@Test
	public void whenOneStringIsAddedToAMultiString_thenResultContainsOneString() {
		parser.parse("");
		model.updateMultipleStrings("-test", "value");		
		String result = renderer.render();
		assertEquals("-test=value", result);
	}

	@Test
	public void whenMultipleStringsAreAddedToAMultiString_thenResultContainsTheseStrings() {
		parser.parse("");
		model.updateMultipleStrings("-test", "value1,value2,value3");		
		String result = renderer.render();
		assertEquals("-test=value1,value2,value3", result);
	}

	@Test
	public void whenOneStringContainsBlanks_thenItIsQuotedInTheResult() {
		parser.parse("");
		model.updateMultipleStrings("-test", "value1,value 2,value3");		
		String result = renderer.render();
		assertEquals("-test=value1,\"value 2\",value3", result);
	}
		
}
