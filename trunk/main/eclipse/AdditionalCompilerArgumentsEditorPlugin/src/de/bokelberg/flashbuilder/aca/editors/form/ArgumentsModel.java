package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.bokelberg.flashbuilder.aca.utils.StringUtil;


public class ArgumentsModel {

	public List<Argument> arguments = new ArrayList<Argument>();

	
	public void updateBoolean(String id, boolean value, String assignmentOperator ) {
		debug("updateBoolean <" + id + "><" + assignmentOperator + "><" + value + ">");
		Argument arg = addArg(id);
		arg.setValue(value + "");
		arg.assignmentOperator=assignmentOperator;
	}
	
	public void updateBoolean(String id, boolean value) {
		updateBoolean(id, value, "=");
	}
	
	public void updateString(String id, String value, String assignmentOperator) {
		debug("updateString <" + id + "><" + assignmentOperator + "><" + value + ">");
		Argument arg = addArg(id);
		arg.assignmentOperator = assignmentOperator;
		arg.setValue( StringUtil.removeOptionalQuotes( value ));
	}

	public void updateString(String id, String text) {
		updateString( id, text, "=");
	}


	public Argument addArg(String id) {
		debug("addArg <" + id + ">");
		Argument arg = findArg(id);
		if (arg == null) {
			arg = new Argument();
			arg.name = id;
			arguments.add( arg );
			debug("addArg added new arg");
		}
		return arg;
	}


	public void clear() {
		arguments.clear();
	}
	
	public List<Argument> getArguments() {
		return arguments;
	}

	private Argument findArg(String id) {
		for (Argument arg : arguments) {
			if (arg != null && arg.name != null && arg.name.equals(id)) {
				return arg;
			}
		}
		return null;
	}

	private Logger log = null;
	
	private void debug(String msg) {
		if( log == null )
		{
			log = Logger.getLogger( this.getClass() );
		}
		log.debug(msg);
	}


}
