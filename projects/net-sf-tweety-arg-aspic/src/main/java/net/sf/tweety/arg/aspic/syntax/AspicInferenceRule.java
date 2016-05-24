package net.sf.tweety.arg.aspic.syntax;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;

public class AspicInferenceRule implements Rule<AspicWord, AspicWord> {
	
	boolean defeasible;
	AspicWord conclusion;
	Collection<AspicWord> premises = new ArrayList<>();
	
	public AspicInferenceRule(){}	
	
	public AspicInferenceRule(boolean defeasible, AspicWord conclusion, Collection<AspicWord> premise) {
		super();
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
		Iterator<AspicWord> i = premises.iterator();
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
	public void setConclusion(AspicWord conclusion) {
		this.conclusion = conclusion;
	}
	
	@Override
	public void addPremise(AspicWord premise) {
		this.premises.add(premise);	
	}
	@Override
	public void addPremises(Collection<? extends AspicWord> premises) {
		this.premises.addAll(premises);
	}
	
	@Override
	public Signature getSignature() {
		Signature sig = conclusion.getSignature();
		for (AspicWord w: premises)
			sig.addSignature(w.getSignature());
		return sig;
	}
	@Override
	public Collection<? extends AspicWord> getPremise() {
		return premises;
	}
	@Override
	public AspicWord getConclusion() {
		return conclusion;
	}
	
	
	
}
