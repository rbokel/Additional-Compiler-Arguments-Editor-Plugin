package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.ArrayList;
import java.util.List;

public class Argument {

	public String name;
	public String assignmentOperator;
	public List<String> values = null;

	public void addValue(String value) {
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(value);
	}

	public void setValue(String value) {
		values = new ArrayList<String>();
		values.add(value);
	}
}
