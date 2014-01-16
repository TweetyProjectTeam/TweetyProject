package net.sf.tweety.util.rules;

import java.util.*;

import net.sf.tweety.*;

/**
 * This class represents a set of rules and provides several
 * auxiliary methods for accessing such a set.
 * 
 * @param <T extends Rule> the specific rule class
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class RuleSet<T extends Rule<?,?>> extends HashSet<T>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new empty rule set.
	 */
	public RuleSet(){
		super();
	}
	
	/**
	 * Creates a new rule set with the given rules
	 * @param rules a collection of rules.
	 */
	public RuleSet(Collection<? extends T> rules){
		super(rules);
	}
	
	/**
	 * Returns all rules this set with the given conclusion
	 * @param f a formula
	 * @return all rules this set with the given conclusion
	 */
	public Set<T> getRulesWithConclusion(Formula f){
		Set<T> rules = new HashSet<T>();
		for(T rule: this)
			if(((Rule<?,?>)rule).getConclusion().equals(f))
				rules.add(rule);		
		return rules;
	}
	
	/**
	 * Returns all conclusions of all rules of this rule set.
	 * @return all conclusions of all rules of this rule set.
	 */
	public Set<Formula> getConclusions(){
		Set<Formula> conclusions = new HashSet<Formula>();
		for(T rule: this)
			conclusions.add(((Rule<?,?>)rule).getConclusion());
		return conclusions;
	}
	
	/**
	 * Returns all premises appearing in this rule set.
	 * @return all premises appearing in this rule set.
	 */
	public Set<Formula> getPremises(){
		Set<Formula> premises = new HashSet<Formula>();
		for(T rule: this)
			premises.addAll(((Rule<?,?>)rule).getPremise());
		return premises;		
	}
}
