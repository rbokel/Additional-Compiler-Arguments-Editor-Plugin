package de.bokelberg.flashbuilder.aca.editors.form;

public class FormElement {
	public String label;
	public String tooltip;
	public String id;
	public String type;

	public FormElement(String type, String id, String label, String tooltip) {
		this.type = type;
		this.id = id;
		this.label = label;
		this.tooltip = tooltip;
	}
}