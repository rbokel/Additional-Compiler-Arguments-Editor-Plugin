package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.ArrayList;
import java.util.List;

public class FormElementList {

	private List<FormElement> elements = new ArrayList<FormElement>();
	

	public FormElement findElement(String id) {
		for (FormElement element : elements) {
			if (element != null && element.id != null && element.id.equals(id)) {
				return element;
			}
		}
		// ignore elements that we can not find
		return null;
	}

	public void addFormElement(String type, String id, String label,
			String tooltip) {
		FormElement element = new FormElement(type, id, label, tooltip);
		elements.add(element);
	}

	public Iterable<FormElement> iterator()
	{
		return elements;
	}

}
