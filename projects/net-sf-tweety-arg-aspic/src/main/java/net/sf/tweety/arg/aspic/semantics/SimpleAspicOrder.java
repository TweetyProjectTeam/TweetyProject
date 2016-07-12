package net.sf.tweety.arg.aspic.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicFormula;

/**
 * @author Nils Geilen
 * A simple comparator for Aspic Arguments, that compares their top rules according to a given list of rules
 */
public class SimpleAspicOrder implements Comparator<AspicArgument> {
	
	/**
	 * The name of the rules ordered by size ascending
	 */
	private List<AspicFormula> rules = new ArrayList<>();
	
	/**
	 * Creates a comparator for AspicArguments, that always returns 0
	 */
	public SimpleAspicOrder() {
		
	}
	
	
	/**
	 * Creates a comparator for AspicArguments from a list of AspicInferneceRules
	 * This will return a value <0, ==0 or >0 if the first argument's top rule is <,=,> the second 
	 * argument's top rule
	 * @param rules	list of rules, ordered by their value ascending
	 */
	public SimpleAspicOrder(Collection<AspicFormula> rules) {
		this.rules.addAll(rules);
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(AspicArgument a, AspicArgument b) {
		int NULL = -1, val_a = NULL, val_b = NULL;
		for( int i = 0; i< rules.size(); i++) {
			if(rules.get(i).equals(a.getTopRule()))
				val_a = i;
			if(rules.get(i).equals(b.getTopRule()))
				val_b = i;
		}
		if(val_a == NULL || val_b == NULL) {
			System.out.println("www");
			return 0;
		}
			
		return val_a - val_b;	
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimpleAspicOrder [" + rules + "]";
	}
	
	

}
