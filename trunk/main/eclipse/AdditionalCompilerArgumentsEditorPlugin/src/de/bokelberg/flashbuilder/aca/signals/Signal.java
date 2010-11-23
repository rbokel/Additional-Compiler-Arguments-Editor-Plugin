package de.bokelberg.flashbuilder.aca.signals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Signal<T> {

	
	private List<Slot<T>> slots = new ArrayList<Slot<T>>();
	
	public void addSlot(Slot<T> slot) {
		if( slots.contains( slot ))
		{
			return;
		}
		slots.add( slot );
		
	}

	public void fire(T payload) {
		for( Slot<T> slot : slots )
		{
			try{
				slot.onSignal(payload);
			}
			catch(Throwable t)
			{
				Logger.getLogger(this.getClass()).error("Error in slot <" + slot + ">");
			}
		}
	}

}
