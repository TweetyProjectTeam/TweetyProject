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
package net.sf.tweety.logics.rpcl.semantics;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.fol.semantics.*;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.rcl.syntax.*;
import net.sf.tweety.logics.rpcl.*;
import net.sf.tweety.logics.rpcl.syntax.*;
import net.sf.tweety.math.probability.*;


/**
 * Objects of this class represent probability distributions on the interpretations
 * of an underlying first-order signature for a relational probabilistic conditional knowledge base.
 * @author Matthias Thimm
 */
public class RpclProbabilityDistribution<T extends Interpretation<FolFormula>> extends AbstractInterpretation<RelationalProbabilisticConditional> implements Map<T,Probability> {
	
	/**
	 * The probabilities of the interpretations.
	 */
	private Map<T,Probability> probabilities;
	
	/**
	 * The semantics used for this probability distribution.
	 */
	private RpclSemantics semantics;
	
	/**
	 * The used FOL signature 
	 */
	private FolSignature signature;
	
	/**
	 * Creates a new probability distribution for the given signature.
	 * @param signature a fol signature.
	 */
	public RpclProbabilityDistribution(RpclSemantics semantics, FolSignature signature){
		this.probabilities = new HashMap<T,Probability>();
		this.signature = signature;
		this.semantics = semantics;		
	}
	
	/**
	 * Returns the semantics of this distribution.
	 * @return the semantics of this distribution.
	 */
	public RpclSemantics getSemantics(){
		return this.semantics;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.kr.Formula)
	 */	
	@Override
	public boolean satisfies(RelationalProbabilisticConditional formula) throws IllegalArgumentException {
		return semantics.satisfies(this, (RelationalProbabilisticConditional)formula);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.kr.BeliefBase)
	 */
	@Override
	public boolean satisfies(BeliefBase beliefBase)	throws IllegalArgumentException {
		if(!(beliefBase instanceof RpclBeliefSet))
			throw new IllegalArgumentException("Relational probabilistic conditional knowledge base expected.");
		RpclBeliefSet kb = (RpclBeliefSet) beliefBase;
		for(RelationalProbabilisticConditional f: kb)
			if(!this.satisfies(f)) return false;
		return true;
	}
	
	
	/**
	 * Gets the probability of the given closed formula, i.e. the sum of the
	 * probabilities of all interpretations satisfying it.
	 * @param f a closed fol formula.
	 * @return a probability.
	 */
	public Probability probability(FolFormula f){
		if(!f.isClosed()) throw new IllegalArgumentException("Formula '" + f + "' is not closed.");
		Probability result = new Probability(0d);
		for(Interpretation<FolFormula> i: this.keySet())
			if(i.satisfies(f))
				result = result.add(this.get(i));
		return result;
	}
	
	/**
	 * Gets the probability of the given closed relational conditional "re", i.e.
	 * the probability of the head of "re" given its body.
	 * @param re a closed relational conditional.
	 * @return a probability.
	 */
	public Probability probability(RelationalConditional re){
		if(!re.isClosed()) throw new IllegalArgumentException("Conditional '" + re + "' is not closed.");
		FolFormula head = re.getConclusion();
		if(re.isFact())
			return this.probability(head);
		FolFormula body = re.getPremise().iterator().next();		
		return this.probability(head.combineWithAnd(body)).divide(this.probability(body));
	}
	
	/**
	 * Returns the entropy of this probability distribution.
	 * @return the entropy of this probability distribution.
	 */
	public double entropy(){
		double entropy = 0;
		for(T i : this.keySet())
			if(this.get(i).getValue() != 0)
				entropy -= this.get(i).getValue() * Math.log(this.get(i).getValue());
		return entropy;
	}
	
	/**
	 * Computes the convex combination of this P1 and the
	 * given probability distribution P2 with parameter d, i.e.
	 * it returns a P with P(i)=d P1(i) + (1-d) P2(i) for every interpretation i.
	 * @param d a double
	 * @param other a probability distribution
	 * @return the convex combination of this P1 and the
	 * 	given probability distribution P2 with parameter d.
	 * @throws IllegalArgumentException if either d is not in [0,1] or this and
	 * the given probability distribution are not defined on the same set of interpretations.
	 */
	public RpclProbabilityDistribution<T> convexCombination(double d, RpclProbabilityDistribution<T> other){
		if(d < 0 || d > 1)
			throw new IllegalArgumentException("The combination parameter must be between 0 and 1.");
		Set<T> interpretations = this.keySet();
		if(!interpretations.equals(other.keySet())|| !this.getSignature().equals(other.getSignature()))
			throw new IllegalArgumentException("The distributions cannot be combined as they differ in their definitions.");			
		RpclProbabilityDistribution<T> p = new RpclProbabilityDistribution<T>(this.semantics, (FolSignature) this.getSignature());
		for(T i: interpretations)
			p.put(i, this.get(i).mult(d).add(other.get(i).mult(1-d)));
		return p;
	}
	
	/**
	 * Returns the uniform distribution on the given signature.
	 * @param semantics the semantics for the distribution.
	 * @param signature a fol signature
	 * @return the uniform distribution on the given signature.
	 */
	public static RpclProbabilityDistribution<HerbrandInterpretation> getUniformDistribution(RpclSemantics semantics, FolSignature signature){
		RpclProbabilityDistribution<HerbrandInterpretation> p = new RpclProbabilityDistribution<HerbrandInterpretation>(semantics,signature);
		Set<HerbrandInterpretation> interpretations = new HerbrandBase(signature).getAllHerbrandInterpretations(); 
		double size = interpretations.size();
		for(HerbrandInterpretation i: interpretations)
			p.put(i, new Probability(1/size));
		return p;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.probabilities.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		this.probabilities.clear();		
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return this.probabilities.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return this.probabilities.containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<T, Probability>> entrySet() {
		return this.probabilities.entrySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Probability get(Object key) {
		return this.probabilities.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.probabilities.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Probability put(T key, Probability value) {
		return this.probabilities.put(key, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends T, ? extends Probability> m) {
		this.probabilities.putAll(m);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Probability remove(Object key) {
		return this.probabilities.remove(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.probabilities.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Probability> values() {
		return this.probabilities.values();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<T> keySet() {
		return this.probabilities.keySet();
	}
	
	/** Returns the signature of the underlying language.
	 * @return the signature of the underlying language.
	 */
	public Signature getSignature(){
		return this.signature;
	}
}
