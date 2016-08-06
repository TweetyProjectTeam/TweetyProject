package net.sf.tweety.arg.aspic.syntax;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * This stands for an inference rule or for a premise if premises has length 0.
 * If this is a premise and defeasible it is an ordinary premise else it is an axiom.
 */
public abstract class InferenceRule<T extends Invertable> implements Rule<T, T> {
	
	/**
	 * The rule's conclusion
	 */
	private T conclusion;
	/**
	 * The rule's premises
	 */
	private Collection<T> premises = new ArrayList<>();
	
	private String name;
	
	public InferenceRule(){
		
	}
	
	/**
	 * Constructs a new infernence rule of rule p -> c if defeasible or p => c else
	 * @param defeasible	makes a rule with premises defeasible and makes a premise ordinary
	 * @param conclusion	^= p
	 * @param premise	^= c
	 */
	public InferenceRule(T conclusion, Collection<T> premise) {
		this.conclusion = conclusion;
		this.premises = premise;
	}

	/**
	 * @return	true iff this rule is defeasible
	 */
	public abstract boolean isDefeasible();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.syntax.AspicWord#toString()
	 */
	@Override
	public String toString() {
		StringWriter sw =  new StringWriter();
		sw.write("(");
		if(getName()!=null)
			sw.write(getName()+": ");
		Iterator<T> i = premises.iterator();
		if(i.hasNext())
			sw.write(i.next().toString());
		while(i.hasNext())
			sw.write(", "+i.next());
		if(isDefeasible())
			sw.write(" => ");
		else
			sw.write(" -> ");
		sw.write(conclusion+"");
		sw.write(")");
		return sw.toString();
		
	}

	public StrictInferenceRule<T> toStrict() {
		StrictInferenceRule<T> result = new StrictInferenceRule<>(conclusion, premises);
		result.setName(name);
		return result;
	}
	
	public DefeasibleInferenceRule<T> toDefeasible() {
		DefeasibleInferenceRule<T> result = new DefeasibleInferenceRule<>(conclusion, premises);
		result.setName(name);
		return result;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	public void setConclusion(T conclusion) {
		this.conclusion = conclusion;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremise(net.sf.tweety.commons.Formula)
	 */
	@Override
	public void addPremise(T premise) {
		this.premises.add(premise);	
	}
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremises(java.util.Collection)
	 */
	@Override
	public void addPremises(Collection<? extends T> premises) {
		this.premises.addAll(premises);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.syntax.AspicWord#getSignature()
	 */
	@Override
	public Signature getSignature() {
		Signature sig = conclusion.getSignature();
		for (T w: premises)
			sig.addSignature(w.getSignature());
		return sig;
	}
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getPremise()
	 */
	@Override
	public Collection<? extends T> getPremise() {
		return premises;
	}
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getConclusion()
	 */
	@Override
	public T getConclusion() {
		return conclusion;
	}

	
	
	
}
