package net.sf.tweety.arg.aspic.syntax;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SimpleAspicOrder implements Comparator<AspicArgument> {
	
	private Map<AspicFormula, Integer> order = new HashMap<>();
	
	public SimpleAspicOrder() {
		
	}
	
	public SimpleAspicOrder(Collection<AspicFormula> rules) {
		int i  = 0;
		for (AspicFormula rule:rules) {
			order.put(rule, i++);
		}
			
	}

	@Override
	public int compare(AspicArgument a, AspicArgument b) {
		if(!order.containsKey(a.getTopRule())
				|| !order.containsKey(b.getTopRule()))
			return 0;
		int va = order.get(a.getTopRule()),
				vb = order.get(b.getTopRule());
		return va-vb;
		
	}

	@Override
	public String toString() {
		return "SimpleAspicOrder [" + order + "]";
	}
	
	

}
