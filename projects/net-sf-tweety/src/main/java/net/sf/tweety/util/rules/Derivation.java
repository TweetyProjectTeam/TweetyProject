package net.sf.tweety.util.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.sf.tweety.Formula;
import net.sf.tweety.util.Triple;

/**
 * This class models a derivation, i.e. a minimal (with respect to set
 * inclusion) sequence of rules [R1, ... ,Rn] such that for any
 * Ri and for any p in the premise of Ri there is an Rj with j > i
 * and the conclusion of Rj equals p.
 * 
 * @param T the specific rule class 
 * 
 * @author Matthias Thimm
 */
public class Derivation<T extends Rule<?,?>> extends ArrayList<T>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new derivation with the given sequence of rules.
	 * @param rules a sequence of rules.
	 */
	public Derivation(List<T> rules){
		super(rules);
	}
	
	/**
	 * Returns the conclusion of this derivation.
	 * @return the conclusion of this derivation.
	 */
	public Formula getConclusion(){
		RuleSet<T> ruleSet = new RuleSet<T>(this);
		Set<Formula> conclusions = ruleSet.getConclusions();
		Set<Formula> premises = ruleSet.getPremises();
		conclusions.removeAll(premises);
		return conclusions.iterator().next();
	}
	
	/**
	 * Returns the set of all possible derivations from the set of rules. 
	 * @param rules a set of rules
	 * @return the set of all possible derivations
	 */
	public static <S extends Rule<?,?>> Set<Derivation<S>> allDerivations(Collection<? extends S> rules){
		RuleSet<S> theRules = new RuleSet<S>(rules);
		Set<Derivation<S>> allDerivations = new HashSet<Derivation<S>>();
		for(Formula f: theRules.getConclusions())
			allDerivations.addAll(Derivation.allDerivations(rules, f));
		return allDerivations;
	}
	
	/**
	 * Returns the set of all possible derivations with the given
	 * conclusion from the set of rules. 
	 * @param rules a set of rules
	 * @return the set of all possible derivations with the given conclusion
	 */
	public static <S extends Rule<?,?>> Set<Derivation<S>> allDerivations(Collection<? extends S> rules, Formula conclusion){
		RuleSet<S> theRules = new RuleSet<S>(rules);
		// each element (A,B,C) of this stack describes a (partial) derivation with
		// A - being the current derivation (a list of rules
		// B - being the set of all formulas that have to be proven
		// C - being the set of rules that can be used to construct the rest of the derivation
		Stack<Triple<List<S>,Set<Formula>,RuleSet<S>>> stack = new Stack<Triple<List<S>,Set<Formula>,RuleSet<S>>>();
		for(S r : theRules.getRulesWithConclusion(conclusion)){
			Triple<List<S>,Set<Formula>,RuleSet<S>> derivation = new Triple<List<S>,Set<Formula>,RuleSet<S>>();
			derivation.setFirst(new ArrayList<S>());
			derivation.getFirst().add(r);
			derivation.setSecond(new HashSet<Formula>());
			derivation.getSecond().addAll(r.getPremise());
			derivation.setThird(new RuleSet<S>(rules));
			derivation.getThird().removeAll(theRules.getRulesWithConclusion(conclusion));
			stack.add(derivation);
		}
		Set<Derivation<S>> derivations = new HashSet<Derivation<S>>();
		while(!stack.isEmpty()){
			Triple<List<S>,Set<Formula>,RuleSet<S>> derivation = stack.pop();
			if(derivation.getSecond().isEmpty())
				derivations.add(new Derivation<S>(derivation.getFirst()));
			else{
				for(Formula f: derivation.getSecond()){
					for(S r: derivation.getThird().getRulesWithConclusion(f)){
						Triple<List<S>,Set<Formula>,RuleSet<S>> newDerivation = new Triple<List<S>,Set<Formula>,RuleSet<S>>();
						newDerivation.setFirst(new ArrayList<S>(derivation.getFirst()));
						newDerivation.getFirst().add(r);
						newDerivation.setSecond(new HashSet<Formula>(derivation.getSecond()));
						newDerivation.getSecond().remove(f);
						newDerivation.getSecond().addAll(r.getPremise());
						newDerivation.setThird(new RuleSet<S>(derivation.getThird()));
						newDerivation.getThird().removeAll(theRules.getRulesWithConclusion(f));
						stack.add(newDerivation);
					}
				}
			}
		}	
		return derivations;
	}

	/**
	 * Checks whether this derivation is founded, i.e.
	 * whether every formula appearing in the premise of
	 * a rule is also the conclusion of a previous rule.
	 * @return "true" if this derivation is founded.
	 */
	public boolean isFounded(){
		Set<Formula> toProve = new HashSet<Formula>();
		Iterator<T> rules = this.iterator();
		toProve.addAll(rules.next().getPremise());
		while(rules.hasNext()){
			T rule = rules.next();
			toProve.remove(rule.getConclusion());
			toProve.addAll(rule.getPremise());
		}
		return toProve.isEmpty();
	}
	
	/**
	 * Checks whether this derivation is minimal with
	 * respect to set inclusion. This is equivalent to checking
	 * whether every conclusion besides the first is used
	 * in a premise and no conclusion appear twice. 
	 * @return "true" if this derivation is minimal.
	 */
	public boolean isMinimal(){
		RuleSet<T> ruleSet = new RuleSet<T>(this);
		for(Formula f: ruleSet.getConclusions())
			if(ruleSet.getRulesWithConclusion(f).size()>1)
				return false;
		Set<Formula> conclusions = ruleSet.getConclusions();
		Set<Formula> premises = ruleSet.getPremises();
		conclusions.removeAll(premises);
		return conclusions.size() == 1;
	}	
	
	/* (non-Javadoc)
	 * @see java.util.AbstractList#hashCode()
	 */
	@Override
	public int hashCode(){
		// for comparing a derivation is treated as a set
		Set<T> thisSet = new HashSet<T>(this);
		return thisSet.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractList#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		// for comparing a derivation is treated as a set
		Set<T> thisSet = new HashSet<T>(this);
		return thisSet.equals(new HashSet<T>((Derivation<T>)obj));
	}
	
}
