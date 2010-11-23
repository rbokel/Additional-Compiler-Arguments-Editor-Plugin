package de.bokelberg.flashbuilder.aca.editors.form;

/**
 * Return true, if the argument has a non default value
 * 
 * @author rbokel
 * 
 */
public class ValueIsDifferentFromDefaultValuePredicate extends
		Predicate<Argument> {

	private DefaultValues defaultValues;

	public ValueIsDifferentFromDefaultValuePredicate(DefaultValues defaultValues) {
		this.defaultValues = defaultValues;
	}

	public boolean eval(Argument arg) {
		String id = arg.getName();
		return !hasDefaultValue(id) || !defaultValue(id).equals(getValue(arg));
	}

	private String getValue(Argument arg) {
		if (arg == null) {
			return "";
		}

		Object result = arg.getValue();
		if (result == null) {
			return "";
		}
		return result + "";
	}

	private boolean hasDefaultValue(String id) {
		return defaultValues.hasDefaultValue(id);
	}

	private String defaultValue(String id) {
		Object result = defaultValues.getDefaultValue(id);
		return result != null ? result + "" : "";
	}

}
