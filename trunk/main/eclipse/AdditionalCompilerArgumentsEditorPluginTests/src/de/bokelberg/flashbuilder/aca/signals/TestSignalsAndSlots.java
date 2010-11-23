package de.bokelberg.flashbuilder.aca.signals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestSignalsAndSlots 
{

	private List<String> results;
	
	@Before
	public void setUp()
	{
		results = new ArrayList<String>();
	}
		
	
	@Test
	public void simpleSetUp()
	{
		Signal<String> s = new Signal<String>();
		Slot<String> sl = new Slot<String>() {

			public void onSignal(String payload) {
				results.add(payload);
			}
		};
		s.addSlot(sl);
		s.fire("test");
		
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("test", results.get(0));
	}
}
