package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class ArgumentsModel {

	public List<Argument> arguments = new ArrayList<Argument>();

	
	public void updateBoolean(String id, boolean value, boolean defaultValue) {
		if (value != defaultValue) {
			Argument arg = addArg(id);
			arg.setValue(value + "");
			arg.assignmentOperator="=";
		} else {
			removeArg(id);
		}
	}
	
	public void updateString(String id, String text) {
		updateString( id, text, "=");
	}

	public void updateString(String id, String text, String assignmentOperator) {
		debug("updateString <" + id + "><" + assignmentOperator + "><" + text + ">");
		if( text == null || text.trim().length() == 0 )
		{
			removeArg( id );
		}
		else 
		{
			Argument arg = addArg(id);
			arg.assignmentOperator = assignmentOperator;
			if( text.startsWith("\"") && text.endsWith("\""))
			{
				text = text.substring(0,text.length()-1);
			}
			arg.setValue( text );
		}
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

	private Argument findArg(String id) {
		for (Argument arg : arguments) {
			if (arg != null && arg.name != null && arg.name.equals(id)) {
				return arg;
			}
		}
		return null;
	}

	private void removeArg(String id) {
		Argument arg = findArg(id);
		if (arg != null) {
			arguments.remove(arg);
		}
	}

	/**
	 * Create a string which can be used as a value for the
	 * additionalCompilerArgumentsAttribute in .actionScriptProperties
	 * 
	 * @return
	 */
	public String getString() {
		String prefix = "";
		StringBuffer buf = new StringBuffer();
		for (Argument arg : arguments) {
			buf.append(prefix);
			buf.append(argToString(arg));
			prefix = " ";
		}
		return buf.toString();
	}

	private String argToString(Argument arg) {
		String prefix;
		StringBuffer buf = new StringBuffer( arg.name );
		if( arg.assignmentOperator != null )
		{
			buf.append( arg.assignmentOperator );
			prefix = "";
		}
		else
		{
			prefix = " ";
		}
		if( arg.values != null && arg.values.size() >= 1)
		{
			for( String value : arg.values )
			{
				buf.append( prefix );
				if( value.contains(" "))
				{
					buf.append("\"");
					buf.append( value );
					buf.append("\"");
				}
				else
				{
					buf.append( value );
				}
				prefix = " ";
			}
		}
		return buf.toString();
	}

	public void clear() {
		arguments.clear();
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
