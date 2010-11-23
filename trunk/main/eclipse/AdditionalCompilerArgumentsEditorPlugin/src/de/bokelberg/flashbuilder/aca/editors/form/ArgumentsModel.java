package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.bokelberg.flashbuilder.aca.utils.StringUtil;


public class ArgumentsModel {

	private List<Argument> arguments = new ArrayList<Argument>();

	
	public void updateBoolean(String id, boolean value) {
		debug("updateBoolean <" + id + "><" + value + ">");
		Argument arg = addArg(id);
		arg.setValue(value + "");
	}
	
	public void updateString(String id, String value) {
		debug("updateString <" + id + "><" + value + ">");
		Argument arg = addArg(id);
		arg.setValue( StringUtil.removeOptionalQuotes( value ));
	}

	public void updateAssignmentOperator(String id, boolean append) {
		debug("updateAssignmentOperator <" + id + "><" + append + ">");
		Argument arg = addArg(id);
		arg.setAssignmentOperator(append ? "+=" : "=");
	}

	public Argument addArg(String id) {
		debug("addArg <" + id + ">");
		Argument arg = findArg(id);
		if (arg == null) {
			arg = new Argument();
			arg.setName(id);
			arg.setAssignmentOperator("="); //TODO Do we really need this? 
			arguments.add( arg );
			
			debug("addArg added new arg");
		}
		return arg;
	}


	public void clear() {
		arguments.clear();
	}
	
	public List<Argument> getArguments() {
		debug("getArguments size <" + arguments.size() + ">");
		return arguments;
	}

	private Argument findArg(String id) {
		for (Argument arg : arguments) {
			if (arg != null && arg.getName() != null && arg.getName().equals(id)) {
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
