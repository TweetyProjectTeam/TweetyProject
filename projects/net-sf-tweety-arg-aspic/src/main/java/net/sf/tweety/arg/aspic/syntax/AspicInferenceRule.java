package net.sf.tweety.arg.aspic.syntax;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;

/**
 * @author Nils Geilen
 * 
 * This stands for an inference rule or for a premise if premises has length 0.
 * If this is a premise and defeasible it is an ordinary premise else it is an axiom.
 */
public class AspicInferenceRule extends AspicWord implements Rule<AspicFormula, AspicFormula> {
	
	/**
	 * This is true if the rule is defeasible.
	 */
	private boolean defeasible;
	/**
	 * The rule's conclusion
	 */
	private AspicFormula conclusion;
	/**
	 * The rule's premises
	 */
	private Collection<AspicFormula> premises = new ArrayList<>();
	
	/**
	 * Constructs an empty inference rule
	 */
	public AspicInferenceRule(){
		super(null);
		
	}	
	
	/**
	 * Constructs a new infernence rule of rule p -> c if defeasible or p => c else
	 * @param defeasible	makes a rule with premises defeasible and makes a premise ordinary
	 * @param conclusion	^= p
	 * @param premise	^= c
	 */
	public AspicInferenceRule(boolean defeasible, AspicFormula conclusion, Collection<AspicFormula> premise) {
		super(null);
		this.defeasible = defeasible;
		this.conclusion = conclusion;
		this.premises = premise;
	}

	/**
	 * @return	true iff this rule is defeasible
	 */
	public boolean isDefeasible() {
		return defeasible;
	}
	
	/**
	 * Makes rule defeasible or indefeasible
	 * @param d	new value for defeasible
	 */
	public void setDefeasible(boolean d){
		this.defeasible=d;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.syntax.AspicWord#toString()
	 */
	@Override
	public String toString() {
		StringWriter sw =  new StringWriter();
		sw.write("(");
		if(getID()!=null)
			sw.write(getID()+": ");
		Iterator<AspicFormula> i = premises.iterator();
		if(i.hasNext())
			sw.write(i.next().toString());
		while(i.hasNext())
			sw.write(", "+i.next());
		if(defeasible)
			sw.write(" => ");
		else
			sw.write(" -> ");
		sw.write(conclusion.toString());
		sw.write(")");
		return sw.toString();
		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#isFact()
	 */
	@Override
	public boolean isFact() {
		return premises.isEmpty() && conclusion != null;
	}
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#isConstraint()
	 */
	@Override
	public boolean isConstraint() {
		return false;
	}
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#setConclusion(net.sf.tweety.commons.Formula)
	 */
	@Override
	public void setConclusion(AspicFormula conclusion) {
		this.conclusion = conclusion;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremise(net.sf.tweety.commons.Formula)
	 */
	@Override
	public void addPremise(AspicFormula premise) {
		this.premises.add(premise);	
	}
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremises(java.util.Collection)
	 */
	@Override
	public void addPremises(Collection<? extends AspicFormula> premises) {
		this.premises.addAll(premises);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.syntax.AspicWord#getSignature()
	 */
	@Override
	public Signature getSignature() {
		Signature sig = conclusion.getSignature();
		for (AspicFormula w: premises)
			sig.addSignature(w.getSignature());
		return sig;
	}
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getPremise()
	 */
	@Override
	public Collection<? extends AspicFormula> getPremise() {
		return premises;
	}
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getConclusion()
	 */
	@Override
	public AspicFormula getConclusion() {
		return conclusion;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.syntax.AspicWord#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result + (defeasible ? 1231 : 1237);
		result = prime * result + ((premises == null) ? 0 : premises.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.syntax.AspicWord#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		
		// custom addition
		if(obj instanceof AspicWord&& !(obj instanceof AspicInferenceRule)) {
			if(getID()==null)
				return false;
			return ((AspicWord)obj).getID().equals(getID());
		}
		
		if (getClass() != obj.getClass())
			return false;
		AspicInferenceRule other = (AspicInferenceRule) obj;
		if (conclusion == null) {
			if (other.conclusion != null)
				return false;
		} else if (!conclusion.equals(other.conclusion))
			return false;
		if (defeasible != other.defeasible)
			return false;
		if (premises == null) {
			if (other.premises != null)
				return false;
		} else if (!premises.equals(other.premises))
			return false;
		return true;
	}
	
	
	
}
