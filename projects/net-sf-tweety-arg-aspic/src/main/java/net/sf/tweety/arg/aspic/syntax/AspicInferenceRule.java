package net.sf.tweety.arg.aspic.syntax;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;

public class AspicInferenceRule extends AspicWord implements Rule<AspicFormula, AspicFormula> {
	
	boolean defeasible;
	AspicFormula conclusion;
	Collection<AspicFormula> premises = new ArrayList<>();
	
	public AspicInferenceRule(){
		super(null);
		
	}	
	
	public AspicInferenceRule(boolean defeasible, AspicFormula conclusion, Collection<AspicFormula> premise) {
		super(null);
		this.defeasible = defeasible;
		this.conclusion = conclusion;
		this.premises = premise;
	}

	public boolean isDefeasible() {
		return defeasible;
	}
	
	public void setDefeasible(boolean d){
		this.defeasible=d;
	}
	
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
	
	@Override
	public boolean isFact() {
		return premises.isEmpty() && conclusion != null;
	}
	@Override
	public boolean isConstraint() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setConclusion(AspicFormula conclusion) {
		this.conclusion = conclusion;
	}
	
	@Override
	public void addPremise(AspicFormula premise) {
		this.premises.add(premise);	
	}
	@Override
	public void addPremises(Collection<? extends AspicFormula> premises) {
		this.premises.addAll(premises);
	}
	
	@Override
	public Signature getSignature() {
		Signature sig = conclusion.getSignature();
		for (AspicFormula w: premises)
			sig.addSignature(w.getSignature());
		return sig;
	}
	@Override
	public Collection<? extends AspicFormula> getPremise() {
		return premises;
	}
	@Override
	public AspicFormula getConclusion() {
		return conclusion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conclusion == null) ? 0 : conclusion.hashCode());
		result = prime * result + (defeasible ? 1231 : 1237);
		result = prime * result + ((premises == null) ? 0 : premises.hashCode());
		return result;
	}

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
