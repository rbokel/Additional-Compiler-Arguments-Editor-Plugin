package de.bokelberg.flashbuilder.aca.signals;

public interface Slot<T> 
{
	public void onSignal(T payload);
}
