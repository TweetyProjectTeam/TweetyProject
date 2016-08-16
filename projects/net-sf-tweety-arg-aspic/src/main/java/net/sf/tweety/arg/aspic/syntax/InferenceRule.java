/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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
 * 
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
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
	
	/**
	 * Identifying name. used e.g. for formula generation
	 */
	private String name;
	
	/**
	 * Creates an empty instance
	 */
	public InferenceRule(){
		
	}
	
	/**
	 * Constructs a new inference rule of rule p -> c if strict or p => c else
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

	/**
	 * @return	a strict instance of this rule
	 */
	public StrictInferenceRule<T> toStrict() {
		StrictInferenceRule<T> result = new StrictInferenceRule<>(conclusion, premises);
		result.setName(name);
		return result;
	}
	
	/**
	 * @return	a defeasible instance of this rule
	 */
	public DefeasibleInferenceRule<T> toDefeasible() {
		DefeasibleInferenceRule<T> result = new DefeasibleInferenceRule<>(conclusion, premises);
		result.setName(name);
		return result;
	}
	
	public String getIdentifier() {
		if (getName() == null)
			return ""+hashCode();
		else
			return getName();
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
