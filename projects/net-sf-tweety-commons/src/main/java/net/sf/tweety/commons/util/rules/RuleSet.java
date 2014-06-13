/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
}
