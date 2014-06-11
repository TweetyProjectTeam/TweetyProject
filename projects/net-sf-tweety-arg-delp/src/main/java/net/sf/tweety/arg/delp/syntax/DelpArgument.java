package net.sf.tweety.arg.delp.syntax;

import java.util.*;

import net.sf.tweety.arg.delp.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class models a DeLP argument which comprises of a set of defeasible rules (the support) and a literal (the conclusion).
 *
 * @author Matthias Thimm
 *
 */
public class DelpArgument implements Formula{

	/**
	 * The support of this argument
	 */
	Set<DefeasibleRule> support;

	/**
	 * The conclusion of this argument (must be a literal)
	 */
	FolFormula conclusion;

	/**
	 * constructor; initializes the conclusion of this argument with the given literal
	 * @param conclusion a literal
	 */
	public DelpArgument(FolFormula conclusion){
		this(new HashSet<DefeasibleRule>(),conclusion);
	}

	/**
	 * constructor; initializes this argument with the given parameters
	 * @param support a set of defeasible rules
	 * @param conclusion a literal
	 */
	public DelpArgument(Set<DefeasibleRule> support, FolFormula conclusion){
		if(!conclusion.isLiteral())
			throw new IllegalArgumentException("The conclusion of an argument must be a literal.");		
		this.support = support;
		this.conclusion = conclusion;
	}

	/**
	 * Checks whether this argument is a subargument of the given argument
	 * @param argument a DeLP argument
	 * @return <source>true</source> iff this argument is a subargument of the given argument
	 */
	public boolean isSubargumentOf(DelpArgument argument){
		return argument.getSupport().containsAll(this.support);
	}

	/**
	 * Checks whether this argument is a strong subargument of the given argument, i.e., if the
	 * support of this argument is a strict subset of the support of the given argument
	 * @param argument a DeLP argument
	 * @return <source>true</source> iff this argument is a strong subargument of the given argument
	 */
	public boolean isStrongSubargumentOf(DelpArgument argument){
		if(!isSubargumentOf(argument)) return false;
		if(argument.getSupport().size() > this.support.size()) return true;
		return false;
	}

	/**
	 * Computes the set of literals that disagree with the conclusion of a subargument of this argument
	 * @param delp a defeasible logic program
	 * @return  the set of literals that disagree with the conclusion of a subargument of this argument
	 */
	public Set<FolFormula> getAttackOpportunities(DefeasibleLogicProgram delp){
		Set<FolFormula> literals = new HashSet<FolFormula>();
		Iterator<DefeasibleRule> it = support.iterator();
		while(it.hasNext())
			literals.add((FolFormula)it.next().getConclusion());
		Set<FolFormula> strictClosure = delp.getStrictClosure();
		Set<FolFormula> strictClosureWithAP = delp.getStrictClosure(literals);
		strictClosureWithAP.removeAll(strictClosure);
		literals.addAll(strictClosureWithAP);
		Set<FolFormula> attackOpportunities = new HashSet<FolFormula>();
		Iterator<FolFormula> l_it = literals.iterator();
		while(l_it.hasNext())
			attackOpportunities.add((FolFormula) l_it.next().complement());
		return attackOpportunities;
	}

	/**
	 * Computes the disagreement subargument of this argument for the given literal
	 * @param lit a literal
	 * @param delp a defeasible logic program
	 * @return the disagreement subargument for <source>lit</source> or <source>null</source> if
	 * 	there is no disagreement subargument
	 */
	public DelpArgument getDisagreementSubargument(FolFormula lit, DefeasibleLogicProgram delp){
		Set<DelpArgument> arguments = delp.getArguments();
		Iterator<DelpArgument> it = arguments.iterator();
		while(it.hasNext()){
			DelpArgument argument = (DelpArgument) it.next();
			if(argument.isSubargumentOf(this)){
				Set<FolFormula> literals = new HashSet<FolFormula>();
				literals.add(lit);
				literals.add(argument.getConclusion());
				if(delp.disagree(literals))
					return argument;
			}
		}
		return null;
	}

	// Misc Methods

	/**
	 * Returns all defeasible rules of the support of this argument with the given literal as head
	 * @param l a literal
	 * @return a set defeasible rules
	 */
	public Set<DelpRule> getRulesWithHead(FolFormula l){
		Set<DelpRule> rules = new HashSet<DelpRule>();
		Iterator<DefeasibleRule> d_it = support.iterator();
		while(d_it.hasNext()){
			DefeasibleRule rule = d_it.next();
			if(rule.getConclusion().equals(l))
				rules.add(rule);
		}
		return rules;
	}

	/**
	 * returns the conclusion of this argument
	 * @return the conclusion of this argument
	 */
	public FolFormula getConclusion() {
		return conclusion;
	}

	/**
	 * returns the support of this argument
	 * @return the support of this argument
	 */
	public Set<DefeasibleRule> getSupport() {
		return support;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String str = "<{";
		Iterator<DefeasibleRule> it = support.iterator();
		if(it.hasNext())
			str += it.next().toString();
		while(it.hasNext())
			str += ","+it.next().toString();
		str += "},"+conclusion.toString()+">";
		return str;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.Formula#getSignature()
	 */
	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result + ((support == null) ? 0 : support.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DelpArgument other = (DelpArgument) obj;
		if (conclusion == null) {
			if (other.conclusion != null)
				return false;
		} else if (!conclusion.equals(other.conclusion))
			return false;
		if (support == null) {
			if (other.support != null)
				return false;
		} else if (!support.equals(other.support))
			return false;
		return true;
	}

}
