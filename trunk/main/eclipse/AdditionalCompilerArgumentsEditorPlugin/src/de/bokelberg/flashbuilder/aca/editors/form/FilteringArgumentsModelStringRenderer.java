package de.bokelberg.flashbuilder.aca.editors.form;

import java.util.ArrayList;
import java.util.List;

/**
 * Remove items from the result, 
 * which don't fulfill the predicate
 * @author rbokel
 */
public class FilteringArgumentsModelStringRenderer extends
		ArgumentsModelStringRenderer {

	private Predicate<Argument> includeInArgumentsPredicate;
	
	public FilteringArgumentsModelStringRenderer(ArgumentsModel model, Predicate<Argument> includeInArguments ) {
		super(model);
		
		this.includeInArgumentsPredicate = includeInArguments;
	}
	
	protected List<Argument> getArguments()
	{
		List<Argument> result = new ArrayList<Argument>();
		for( Argument arg : super.getArguments())
		{
			if( includeInArgumentsPredicate.eval( arg ))
			{
				result.add( arg );
			}
		}
		return result;
	}
}
