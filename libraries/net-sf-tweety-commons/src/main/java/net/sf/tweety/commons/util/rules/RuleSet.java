/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons.util.rules;

import java.util.*;

import net.sf.tweety.commons.*;

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
	
	/**
	 * Returns the maximal subset of this rule set that is closed under
	 * "syntactic" neighbourhood relationship for the given formula. A formula/rule has
	 * a "syntactic" neighbourhood relationship with a rule iff they share vocabulary
	 * elements.
	 * @param f some formula
	 * @return a rule set.
	 */
	public RuleSet<T> getSyntacticModule(Formula f){
		RuleSet<T> ruleset = new RuleSet<T>();
		Signature sig = f.getSignature();
		boolean changed;
		do {
			changed = false;
			for(T rule: this) {
				if(!ruleset.contains(rule) && rule.getSignature().isOverlappingSignature(sig)) {
					ruleset.add(rule);
					changed = true;
					sig.addSignature(rule.getSignature());
				}
			}
		}while(changed);		
		return ruleset;
	}
}
