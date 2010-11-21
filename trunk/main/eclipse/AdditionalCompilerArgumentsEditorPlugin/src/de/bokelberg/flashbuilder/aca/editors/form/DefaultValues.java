package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of String ids to a Object values 
 * It is used to capture the default values of the additional arguments
 *  
 * @author rbokel
 */
public class DefaultValues {

	private Map<String,Object> map = new HashMap<String, Object>();
	
	public void addDefaultValue( String id, Object value )
	{
		map.put( id, value);
	}
	
	public boolean hasDefaultValue(String id) {
		return map.containsKey(id);
	}

	public Object getDefaultValue(String id) {
		return map.get(id);
	}
}
