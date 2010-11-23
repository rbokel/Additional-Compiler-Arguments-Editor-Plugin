package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Widget;

public class WidgetLocator {

	final private Map<String, Widget> idToFormElement = new HashMap<String, Widget>();

	public void addWidget( String id, Widget widget )
	{
		idToFormElement.put( id, widget );
	}
	
	public void addSubWidget( String id, String subId, Widget widget )
	{
		idToFormElement.put( createId(id, subId), widget );
	}
	
	public Widget getWidget( String id )
	{
		return idToFormElement.get( id );
	}
	
	public Widget getSubWidget( String id, String subId )
	{
		return idToFormElement.get(createId(id, subId) );
	}

	private String createId(String id, String subId) {
		return id + "_" + subId;
	}
}
