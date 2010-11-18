package de.bokelberg.flashbuilder.aca.editors.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.bokelberg.flashbuilder.aca.editors.form.ArgumentsModel;
import de.bokelberg.flashbuilder.aca.editors.form.ArgumentsParser;
import de.bokelberg.flashbuilder.aca.editors.form.Argument;


public class ArgumentsParserTest {

	
	private ArgumentsParser parser;
	private ArgumentsModel model;
	
	@Before
	public void setUp()
	{
		model = new ArgumentsModel();
		parser = new ArgumentsParser( model );
	}
	
	@Test
	public void testParseSimpleOption() 
	{
		String input = "-option";
		parser.parse(input);
		List<Argument> result = model.arguments;
		
		assertNotNull( result );
		assertEquals( 1, result.size());
		
		Argument arg = result.get(0);
		assertNotNull( arg );
		assertEquals( input, arg.name );
		assertNull( arg.assignmentOperator );
		assertNull( arg.values );
	}

	@Test
	public void testParseOptionWithSingleValue() 
	{
		String expectedOption = "-option";
		String expectedValue = "one";
		String input = expectedOption + " " + expectedValue;
		parser.parse(input);
		List<Argument> result = model.arguments;
		
		assertNotNull( result );
		assertEquals( 1, result.size());
		
		Argument arg = result.get(0);
		assertNotNull( arg );
		assertEquals( expectedOption, arg.name );
		assertNull( arg.assignmentOperator );
		assertNotNull( arg.values );
		assertEquals( 1, arg.values.size());
		assertEquals( expectedValue, arg.values.get(0));
	}
	
	@Test
	public void testParseOptionWithMultipleValues() 
	{
		String expectedOption = "-option";
		String expectedValues = "one two three";
		String input = expectedOption + " " + expectedValues;
		parser.parse(input);
		List<Argument> result = model.arguments;
		
		assertNotNull( result );
		assertEquals( 1, result.size());
		
		Argument arg = result.get(0);
		assertNotNull( arg );
		assertEquals( expectedOption, arg.name );
		assertNull( arg.assignmentOperator );
		assertNotNull( arg.values );
		assertEquals( 3, arg.values.size());
		assertEquals( "one", arg.values.get(0));
		assertEquals( "two", arg.values.get(1));
		assertEquals( "three", arg.values.get(2));
	}
	
	@Test
	public void testParseOptionWithSimpleStringValue() 
	{
		String expectedOption = "-option";
		String expectedValues = "\"one\"";
		String input = expectedOption + " " + expectedValues;
		parser.parse(input);
		List<Argument> result = model.arguments;
		
		assertNotNull( result );
		assertEquals( 1, result.size());
		
		Argument arg = result.get(0);
		assertNotNull( arg );
		assertEquals( expectedOption, arg.name );
		assertNull( arg.assignmentOperator );
		assertNotNull( arg.values );
		assertEquals( 1, arg.values.size());
		assertEquals( expectedValues, arg.values.get(0));
	}
	
	@Test
	public void testParseOptionWithComplexStringValue() 
	{
		String expectedOption = "-option";
		String expectedValues = "\"one two three\"";
		String input = expectedOption + " " + expectedValues;
		parser.parse(input);
		List<Argument> result = model.arguments;
		
		assertNotNull( result );
		assertEquals( 1, result.size());
		
		Argument arg = result.get(0);
		assertNotNull( arg );
		assertEquals( expectedOption, arg.name );
		assertNull( arg.assignmentOperator );
		assertNotNull( arg.values );
		assertEquals( 1, arg.values.size());
		assertEquals( expectedValues, arg.values.get(0));
	}
	
	@Test
	public void testParseOptionWithAssignment() 
	{
		String expectedOption = "-option";
		String expectedAssignment = "=";
		String expectedValues = "one";
		String input = expectedOption + expectedAssignment + expectedValues;
		parser.parse(input);
		List<Argument> result = model.arguments;
		
		assertNotNull( result );
		assertEquals( 1, result.size());
		
		Argument arg = result.get(0);
		assertNotNull( arg );
		assertEquals( expectedOption, arg.name );
		assertEquals( expectedAssignment, arg.assignmentOperator );
		assertNotNull( arg.values );
		assertEquals( 1, arg.values.size());
		assertEquals( expectedValues, arg.values.get(0));
	}
	
	@Test
	public void testParseOptionWithIncrementAssignment() 
	{
		String expectedOption = "-option";
		String expectedAssignment = "+=";
		String expectedValues = "one";
		String input = expectedOption + expectedAssignment + expectedValues;
		parser.parse(input);
		List<Argument> result = model.arguments;
		
		assertNotNull( result );
		assertEquals( 1, result.size());
		
		Argument arg = result.get(0);
		assertNotNull( arg );
		assertEquals( expectedOption, arg.name );
		assertEquals( expectedAssignment, arg.assignmentOperator );
		assertNotNull( arg.values );
		assertEquals( 1, arg.values.size());
		assertEquals( expectedValues, arg.values.get(0));
	}
	
	@Test
	public void testParseTwoOptions() 
	{
		String expectedOption1 = "-option1";
		String expectedOption2 = "-option2";
		String input = expectedOption1 + " " + expectedOption2;
		parser.parse(input);
		List<Argument> result = model.arguments;
		
		assertNotNull( result );
		assertEquals( 2, result.size());
		
		Argument arg = result.get(0);
		assertNotNull( arg );
		assertEquals( expectedOption1, arg.name );
		
		arg = result.get(1);
		assertNotNull( arg );
		assertEquals( expectedOption2, arg.name );
		
	}
}
