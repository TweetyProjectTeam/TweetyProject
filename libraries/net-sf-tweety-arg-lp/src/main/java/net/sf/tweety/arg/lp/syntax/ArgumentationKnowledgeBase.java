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
package net.sf.tweety.arg.lp.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.lp.asp.syntax.*;

/**
 * Instances of this class represent the set of minimal arguments from
 * a extended logic program 
 * @author Sebastian Homann
 *
 */
public class ArgumentationKnowledgeBase extends BeliefSet<Argument,FolSignature> {
	private Program program;
//	private Set<Argument> arguments = new HashSet<Argument>();
	
	public ArgumentationKnowledgeBase(Program program) {
		// program will be modified internally
		this.program = (Program)program.clone();
		
		// preprocessing: remove unnecessary rules, i.e. a <- a.
		for(ASPRule r : program) {
			if (r.getConclusion() instanceof AggregateHead)
				throw new IllegalArgumentException("Only literals are allowed as rule heads in this module.");
			ASPLiteral head = ((ClassicalHead)r.getConclusion()).iterator().next();
			if(r.getPremise().contains(head)) {
				this.program.remove(r);
			}
		}
	}
	
	/**
	 * Returns all minimal arguments constructible from the extended logic program 
	 * @return all minimal arguments constructible from the extended logic program
	 */
	public Set<Argument> getArguments() {
		Set<Argument> result = new HashSet<Argument>();
		
		for(ASPRule r : program) {			
			LinkedList<ASPRule> rules = new LinkedList<ASPRule>();
			rules.add(r);
			result.addAll(getArguments(rules));
		}
		
		return result;
	}
	
	/**
	 * Recursively constructions minimal arguments in a bottom-up fashion.
	 * 
	 * @param rules A set of rules already part of the argument
	 * @return a set of minimal arguments containing the given set of rules
	 */
	@SuppressWarnings("unchecked")
	private Set<Argument> getArguments(LinkedList<ASPRule> rules) {
		Set<Argument> result = new HashSet<Argument>();
		
		Set<ASPLiteral> openLiterals = getOpenLiterals(rules);
		
		if(openLiterals.isEmpty()) {
			// argument is complete
			result.add(new Argument(rules));
			return result;
		}
		
		// there is at least one unaccounted literal l, find a rule with head l
		for(ASPRule r : program) {
			if (r.getConclusion() instanceof AggregateHead)
				throw new IllegalArgumentException("Only literals are allowed as rule heads in this module.");
			ASPLiteral head = ((ClassicalHead)r.getConclusion()).iterator().next();
			if(openLiterals.contains(head)) {
				LinkedList<ASPRule> newRules = (LinkedList<ASPRule>)rules.clone();
				newRules.addLast(r);
				result.addAll(getArguments(newRules));
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the set of non-default-negated literals that are part of the premise
	 * of some rule but not the conclusion of some other rule
	 * @param rules a set of rules
	 * @return the set of non-default-negated literals
	 */
	private Set<ASPLiteral> getOpenLiterals(Collection<ASPRule> rules) {
		Set<ASPLiteral> result = new HashSet<ASPLiteral>();
		// add all non-default-negated premise literals
		for(ASPRule r : rules) {
			for(ASPBodyElement element : r.getPremise()) {
				if(element instanceof ASPLiteral) {
					result.add((ASPLiteral) element);
				}
			}
		}
		// remove all conclusions as they must have been accounted for
		for(ASPRule r : rules) {
			if (r.getConclusion() instanceof AggregateHead)
				throw new IllegalArgumentException("Only literals are allowed as heads in this module.");
			ASPLiteral head = ((ClassicalHead)r.getConclusion()).iterator().next();
			result.remove(head);
		}
		return result;
	}
	
	/**
	 * This method returns the set of conclusions of all rules in the collection
	 * of rules given.
	 * @param rules a collection of rules
	 * @return the set of conclusions of said rules 
	 */
	@SuppressWarnings("unused")
	private Set<ASPLiteral> getDerivableLiterals(Collection<ASPRule> rules) {
		Set<ASPLiteral> result = new HashSet<ASPLiteral>();
		boolean changed = true;
		while(changed) {
			changed = false;
			for(ASPRule r : rules) {
				if(isTrue(r,result)) {
					if (r.getConclusion() instanceof AggregateHead)
						throw new IllegalArgumentException("Only literals are allowed as heads in this module.");
					ASPLiteral head = ((ClassicalHead)r.getConclusion()).iterator().next();
					result.add(head);
					changed = true;
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns true iff each non-default-negated literal in the premise
	 * of rule is contained in the set of literals
	 * @param rule an elp rule
	 * @param literals a set of literals
	 * @return true iff each non-default-negated literal in the premise
	 * of rule is contained in the set of literals
	 */
	private boolean isTrue(ASPRule rule, Set<ASPLiteral> literals) {
		for(ASPBodyElement element : rule.getPremise()) {
			if(element instanceof ASPLiteral) {
				if(!literals.contains(element)) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.BeliefSet#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		return program.getMinimalSignature();
	}

	@Override
	protected FolSignature instantiateSignature() {
		return new FolSignature();
	}

	
}
