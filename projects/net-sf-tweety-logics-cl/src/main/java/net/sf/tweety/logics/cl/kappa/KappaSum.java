package net.sf.tweety.logics.cl.kappa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This constructs represents a sum of kappa values (or terms).
 * If it is empty its evaluated to zero.
 * 
 * @author Tim Janus
 */
public class KappaSum implements KappaTerm {

	/** the value of the kappa-sum, it is -1 as long as the sum cannot be evaluated */
	int value = -1;
	
	/** the elements that form the sum */
	List<KappaTerm> elements = new ArrayList<KappaTerm>();
	
	@Override
	public boolean evaluate() {
		if(value != -1 || elements.isEmpty())
			return true;
		
		// check if each element is resolvable
		boolean allResolvable = true;
		for(KappaTerm kt : elements) {
			if(!kt.evaluate()) {
				allResolvable = false;
				break;
			}
		}
		
		if(allResolvable) {
			value = 0;
			for(KappaTerm kt : elements) {
				value += kt.value();
			}
		}
			
		return value != -1;
	}

	/**
	 * The neutral element is zero, that means if the sum contains no elements this method returns zero
	 */
	@Override
	public int value() {
		return elements.isEmpty() ? 0 : value;
	}

	@Override
	public int greaterEqualThan() {
		int reval = 0;
		for(KappaTerm kt : elements) {
			if(kt.evaluate()) {
				reval += kt.value();
			} else {
				reval += kt.greaterEqualThan();
			}
		}
		return reval;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for(KappaTerm element : elements) {
			builder.append(element + " + ");
		}
		if(!elements.isEmpty())
			builder.delete(builder.length()-3, builder.length());
		builder.append(")");
		
		/*
		if(value == -1) {
			builder.append(" >= ");
			builder.append(greaterEqualThan());
		} else {
			builder.append(" = ");
			builder.append(value);
		}
		*/
		
		return builder.toString();
	}

	@Override
	public Set<KappaTerm> getSubTerms() {
		Set<KappaTerm> reval = new HashSet<KappaTerm>();
		for(KappaTerm element : elements) {
			reval.addAll(element.getSubTerms());
		}
		return reval;
	}
}
