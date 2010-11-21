package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.List;

import de.bokelberg.flashbuilder.aca.utils.StringUtil;

/**
 * Render the contents of the arguments model to a string
 * so it can be used for the additionalCompilerArguments attribute 
 * in the .actionScriptProperties xml file
 * @author rbokel
 * 
 */
public class ArgumentsModelStringRenderer {

	private ArgumentsModel model;

	public ArgumentsModelStringRenderer(ArgumentsModel model) {
		this.model = model;
	}

	public String render() {
		String prefix = "";
		StringBuffer buf = new StringBuffer();
		for (Argument arg : getArguments()) {
			buf.append(prefix);
			buf.append(argToString(arg));
			prefix = " ";
		}
		return buf.toString();
	}
	
	protected List<Argument> getArguments()
	{
		return model.getArguments();
	}
	
	protected String argToString(Argument arg) {
		String prefix;
		StringBuffer buf = new StringBuffer(arg.name);
		if (arg.assignmentOperator != null) {
			buf.append(arg.assignmentOperator);
			prefix = "";
		} else {
			prefix = " ";
		}
		if (arg.values != null && arg.values.size() >= 1) {
			for (String value : arg.values) {
				buf.append(prefix);
				buf.append(StringUtil.addQuotesIfStringContainsBlanks(value));
				prefix = " ";
			}
		}
		return buf.toString();
	}



}
