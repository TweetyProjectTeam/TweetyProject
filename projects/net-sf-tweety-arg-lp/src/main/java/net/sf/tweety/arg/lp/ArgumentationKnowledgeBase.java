package net.sf.tweety.arg.lp;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.sf.tweety.arg.lp.syntax.Argument;
import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.lp.asp.syntax.*;

/**
 * Instances of this class represent the set of minimal arguments from
 * a extended logic program 
 * @author Sebastian Homann
 *
 */
public class ArgumentationKnowledgeBase extends BeliefSet<Argument> {
	private Program program;
//	private Set<Argument> arguments = new HashSet<Argument>();
	
	public ArgumentationKnowledgeBase(Program program) {
		// program will be modified internally
		this.program = (Program)program.clone();
		
		// preprocessing: remove unnecessary rules, i.e. a <- a.
		for(Rule r : program) {
			DLPLiteral head = r.getConclusion().iterator().next();
			if(r.getPremise().contains(head)) {
				this.program.remove(r);
			}
		}
	}
	
	/**
	 * Returns all minimal arguments constructible from the extended logic program 
	 */
	public Set<Argument> getArguments() {
		Set<Argument> result = new HashSet<Argument>();
		
		for(Rule r : program) {			
			LinkedList<Rule> rules = new LinkedList<Rule>();
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
	private Set<Argument> getArguments(LinkedList<Rule> rules) {
		Set<Argument> result = new HashSet<Argument>();
		
		Set<DLPLiteral> openLiterals = getOpenLiterals(rules);
		
		if(openLiterals.isEmpty()) {
			// argument is complete
			result.add(new Argument(rules));
			return result;
		}
		
		// there is at least one unaccounted literal l, find a rule with head l
		for(Rule r : program) {
			DLPLiteral head = r.getConclusion().iterator().next();
			if(openLiterals.contains(head)) {
				LinkedList<Rule> newRules = (LinkedList<Rule>)rules.clone();
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
	 */
	private Set<DLPLiteral> getOpenLiterals(Collection<Rule> rules) {
		Set<DLPLiteral> result = new HashSet<DLPLiteral>();
		// add all non-default-negated premise literals
		for(Rule r : rules) {
			for(DLPElement element : r.getPremise()) {
				if(element instanceof DLPLiteral) {
					result.add((DLPLiteral) element);
				}
			}
		}
		// remove all conclusions as they must have been accounted for
		for(Rule r : rules) {
			DLPLiteral head = r.getConclusion().iterator().next();
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
	private Set<DLPLiteral> getDerivableLiterals(Collection<Rule> rules) {
		Set<DLPLiteral> result = new HashSet<DLPLiteral>();
		boolean changed = true;
		while(changed) {
			changed = false;
			for(Rule r : rules) {
				if(isTrue(r,result)) {
					DLPLiteral head = r.getConclusion().iterator().next();
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
	 */
	private boolean isTrue(Rule rule, Set<DLPLiteral> literals) {
		for(DLPElement element : rule.getPremise()) {
			if(element instanceof DLPLiteral) {
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
	public Signature getSignature() {
		return program.getSignature();
	}

	
}
