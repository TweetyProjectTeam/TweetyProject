package net.sf.tweety.arg.aspic.order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public class RuleComparator <T extends Invertable> implements Comparator<InferenceRule<T>> {

	/**
	 * The name of the rules ordered by size ascending
	 */
	private List<String> rules = new ArrayList<>();
	
	

	public RuleComparator(List<String> rules) {
		super();
		this.rules = rules;
	}



	@Override
	public int compare(InferenceRule<T> o1, InferenceRule<T> o2) {
		int NULL = -1, val_a = NULL, val_b = NULL;
		for( int i = 0; i< rules.size(); i++) {
			if(rules.get(i).equals(o1.getName()))
				val_a = i;
			if(rules.get(i).equals(o2.getName()))
				val_b = i;
		}
		if(val_a == NULL || val_b == NULL) {
			return 0;
		}
		
		int result = val_a - val_b;
		
		//System.out.println(a +" - "+b+" = "+result);
		
		return result;
	}

}
