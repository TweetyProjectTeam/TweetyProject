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
package net.sf.tweety.logics.pcl.semantics;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pcl.syntax.*;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.math.probability.Probability;

/**
 * This class represents a probability distribution on some logical language
 * @author Matthias Thimm
 * @param <T> The actual interpretation class used for this distribution.
 */
public class ProbabilityDistribution<T extends Interpretation<PlBeliefSet,PropositionalFormula>> extends AbstractInterpretation<PclBeliefSet,ProbabilisticConditional> implements Map<T,Probability>{

	/**
	 * The probabilities of the interpretations.
	 */
	private Map<T,Probability> probabilities;
	
	/**
	 * The signature of the underlying language.
	 */
	private Signature signature;
	
	/**
	 * Creates a new probability distribution.
	 */
	public ProbabilityDistribution(Signature signature){
		this.probabilities = new HashMap<T,Probability>();
		this.signature = signature;
	}
		
	/** Returns the signature of the underlying language.
	 * @return the signature of the underlying language.
	 */
	public Signature getSignature(){
		return this.signature;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Interpretation#satisfies(net.sf.tweety.Formula)
	 */
	@Override
	public boolean satisfies(ProbabilisticConditional formula) throws IllegalArgumentException {
		return formula.getProbability().isWithinTolerance(this.conditionalProbability(formula));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.Interpretation#satisfies(net.sf.tweety.BeliefBase)
	 */
	@Override
	public boolean satisfies(PclBeliefSet beliefBase)	throws IllegalArgumentException {
		for(ProbabilisticConditional f: beliefBase)
			if(!this.satisfies(f)) return false;
		return true;
	}
	
	/**
	 * Gets the probability of the given Herbrand interpretation (this is just an
	 * alias for get(Object o).
	 * @param w a Herbrand interpretation.
	 * @return the probability of the given Herbrand interpretation.
	 */
	public Probability probability(Interpretation<PlBeliefSet,PropositionalFormula> w) throws IllegalArgumentException{
		return this.get(w);
	}
	
	/** Returns the probability of the given formula
	 * @param f a formula
	 * @return a probability.
	 */
	public Probability probability(PropositionalFormula f){
		double p = 0;
		for(T i: this.probabilities.keySet())
			if(i.satisfies(f))
				p += this.probability(i).doubleValue();
		return new Probability(p);
	}
		
	/** Returns the probability of the given conditional
	 * @param c a conditional
	 * @return a probability.
	 */
	public Probability conditionalProbability(Conditional c){
		PropositionalFormula head = c.getConclusion();
		if(c.isFact())
			return this.probability(head);
		PropositionalFormula body = c.getPremise().iterator().next();		
		return this.probability(head.combineWithAnd(body)).divide(this.probability(body));
		
	}
		
	/**
	 * Normalizes the given list of probabilities, i.e. divides
	 * each probability by the sum of all probabilities.
	 */
	public static void normalize(List<Double> probabilities){
		double sum = 0;
		for(Double p : probabilities)
			sum += p;
		for(int i = 0; i < probabilities.size(); i++)
			probabilities.set(i, probabilities.get(i)/sum);		
	}
	
	/**
	 * Returns the entropy of this probability distribution.
	 * @return the entropy of this probability distribution.
	 */
	public double entropy(){
		double entropy = 0;
		for(Interpretation<PlBeliefSet,PropositionalFormula> i : this.probabilities.keySet())
			if(this.probability(i).getValue() != 0)
				entropy -= this.probability(i).getValue() * Math.log(this.probability(i).getValue());
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
	public ProbabilityDistribution<T> convexCombination(double d, ProbabilityDistribution<T> other){
		if(d < 0 || d > 1)
			throw new IllegalArgumentException("The combination parameter must be between 0 and 1.");
		Set<T> interpretations = this.keySet();
		if(!interpretations.equals(other.keySet())|| !this.signature.equals(other.signature))
			throw new IllegalArgumentException("The distributions cannot be combined as they differ in their definitions.");			
		ProbabilityDistribution<T> p = new ProbabilityDistribution<T>(this.signature);
		for(T i: interpretations)
			p.put(i, this.probability(i).mult(d).add(other.probability(i).mult(1-d)));
		return p;
	}
	
	/**
	 * Makes a linear combination of this distribution "p1" and the given distribution "other" and
	 * the given parameters, i.e. it returns a P with P(i)=d1 P1(i) + d2 P2(i) for every interpretation i.
	 * NOTE: P is normalized after combination.
	 * @param d1 a double.
	 * @param d2 a double.
	 * @param other a probability distribution.
	 * @return a probability distribution.
	 */
	public ProbabilityDistribution<T> linearCombination(double d1, double d2, ProbabilityDistribution<T> other){
		if(!this.keySet().equals(other.keySet()) || !this.signature.equals(other.signature))
			throw new IllegalArgumentException("The distributions cannot be combined as they differ in their definitions.");
		List<T> interpretations = new ArrayList<T>(this.keySet());
		List<Double> probabilities = new LinkedList<Double>();
		for(T i: interpretations)
			probabilities.add(d1 * this.get(i).getValue() + d2 * other.get(i).getValue());
		ProbabilityDistribution.normalize(probabilities);		
		ProbabilityDistribution<T> p = new ProbabilityDistribution<T>(this.signature);
		Iterator<Double> iterator = probabilities.iterator();
		for(T i: interpretations)
			p.put(i, new Probability(iterator.next()));
		return p;
	}

	/**
	 * Computes the convex combination of the
	 * given probability distributions P1,...,PN with parameters factors, i.e.
	 * it returns a P with P(i)=d1 P1(i) + d2 P2(i)+ ... + dN PN(i) for every interpretation i
	 * (with d1,...,dN=factors).
	 * @param <S> The interpretation class
	 * @param factors a vector of doubles.
	 * @param creators a vector of probability distributions.
	 * @return the convex combination of the given distributions with parameters factors.
	 * @throws IllegalArgumentException if either the sum of factors d is not in 1, or this and
	 * the given probability distributions are not defined on the same set of interpretations, or
	 * the lengths of creators and factors differ.
	 */
	public static <S extends Interpretation<PlBeliefSet,PropositionalFormula>> ProbabilityDistribution<S> convexCombination(double[] factors, ProbabilityDistribution<S>[] creators) throws IllegalArgumentException{
		if(factors.length != creators.length)
			throw new IllegalArgumentException("Length of factors and creators does not coincide.");
		double sum = 0;
		for(double d: factors)
			sum += d;
		if(sum < 1-Probability.PRECISION || sum > 1+Probability.PRECISION)
			throw new IllegalArgumentException("Factors do not sum up to one.");
		Set<S> interpretations = creators[0].keySet();
		Signature sig = creators[0].signature;
		for(int i = 1; i < creators.length; i++)
			if(!interpretations.equals(creators[i].keySet()) ||	!sig.equals(creators[i].signature))				
				throw new IllegalArgumentException("The distributions cannot be combined as they differ in their definitions.");
		
		ProbabilityDistribution<S> p = new ProbabilityDistribution<S>(sig);
		for(S i: interpretations){
			double prob = 0;
			for(int k =0; k < creators.length; k++)
				prob += factors[k] * creators[k].probability(i).getValue();
			p.put(i, new Probability(prob));
		}
		return p;
	}
	
	/**
	 * Returns the uniform distribution on the given interpretations.
	 * @param <S> The interpretation class
	 * @param interpretations some interpretations.
	 * @param sig a signature
	 * @return the uniform distribution on the given interpretations.
	 */
	public static <S extends Interpretation<PlBeliefSet,PropositionalFormula>> ProbabilityDistribution<S> getUniformDistribution(Set<S> interpretations, Signature sig){
		ProbabilityDistribution<S> p = new ProbabilityDistribution<S>(sig);
		double size = interpretations.size();
		for(S i: interpretations)
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
}
