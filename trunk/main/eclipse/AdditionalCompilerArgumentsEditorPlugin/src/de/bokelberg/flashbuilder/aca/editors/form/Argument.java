package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Argument {

	private String name;
	private String assignmentOperator;
	public List<String> values = null;

	public void setName(String value) {
		log().debug("setName <" + value + ">");
		this.name = value;
	}

	public String getName() {
		return name;
	}

	public void setAssignmentOperator(String value) {
		log().debug("setAssignmentOperator <" + value + ">");
		this.assignmentOperator = value;
	}

	public String getAssignmentOperator() {
		return assignmentOperator;
	}

	public void clearValues() {
		if (values == null) {
			values = new ArrayList<String>();
		}
		else
		{
			values.clear();
		}
	}

	public void addValue(String value) {
		log().debug("addValue <" + value + ">");
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(value);
	}

	public void setValue(String value) {
		log().debug("setValue <" + value + ">");
		clearValues();
		values.add(value);
	}

	public Object getValue() {
		return getValue( 0 );
	}
	
	public Object getValue( int index ) {
		return values != null && values.size() > index ? values.get(index) : null;
	}

	private Logger _log;
	
	private Logger log()
	{
		if( _log == null )
		{
			_log = Logger.getLogger( this.getClass() );
		}
		return _log;
	}

}
