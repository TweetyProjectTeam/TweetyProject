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
package net.sf.tweety.math.probability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

/**
 * This class represents a probability distribution over some set of objects
 * @author Matthias Thimm
 * @param <T> The class of the objects used.
 */
public class ProbabilityFunction<T extends Comparable<T>> implements Map<T,Probability> {
	
		/** For random sampling. */
		private static Random random = new Random();
	
		/**
		 * The probabilities of the objects.
		 */
		private Map<T,Probability> probabilities;
		
		/**
		 * Creates a new probability function.
		 */
		public ProbabilityFunction(){
			this.probabilities = new HashMap<T,Probability>();
		}
			
		/**
		 * Creates a new probability function by copying the given one.
		 * @param other another probability function
		 */
		public ProbabilityFunction(ProbabilityFunction<T> other){
			this();
			for(Entry<T, Probability> entry: other.entrySet())
				this.probabilities.put(entry.getKey(), new Probability(entry.getValue()));
		}
		
		/**
		 * Gets the probability of the given object.
		 * @param w some object.
		 * @return the probability of the given object.
		 * @throws IllegalArgumentException if the given object has no probability
		 */
		public Probability probability(T w) throws IllegalArgumentException{
			return this.get(w);
		}
		
		/**
		 * Gets the probability of the given object.
		 * @param objects some object.
		 * @return the probability of the given object.
		 * @throws IllegalArgumentException if the given object has no probability
		 */
		public Probability probability(Collection<? extends T> objects) throws IllegalArgumentException{
			double prob = 0;
			for(Object o: objects)
				prob += this.get(o).doubleValue();
			return new Probability(prob);
		}
		
		/**
		 * Normalizes the given list of probabilities, i.e. divides
		 * each probability by the sum of all probabilities.
		 * @param probabilities a list of probabilities
		 */
		protected static void normalize(List<Double> probabilities){
			double sum = 0;
			for(Double p : probabilities)
				sum += p;
			for(int i = 0; i < probabilities.size(); i++)
				probabilities.set(i, probabilities.get(i)/sum);		
		}
		
		/**
		 * Checks whether this probability function is normalized, i.e.
		 * the sum of all probabilities is 1.
		 * @return "true" if this probability function is normalized.
		 */
		public boolean isNormalized(){
			if(this.isEmpty())
				return true;
			double sum = 0;
			for(Probability p : this.probabilities.values())
				sum += p.doubleValue();		
			return sum >= 1- Probability.PRECISION && sum <= 1+Probability.PRECISION;
		}
		
		/**
		 * Normalizes this probability function to have mass 1.
		 */
		public void normalize(){
			double sum = 0;
			for(Probability p : this.probabilities.values())
				sum += p.doubleValue();
			for(T key: this.keySet())
				this.put(key, new Probability(this.probability(key).doubleValue()/sum));
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
		public ProbabilityFunction<T> convexCombination(double d, ProbabilityFunction<T> other){
			if(d < 0 || d > 1)
				throw new IllegalArgumentException("The combination parameter must be between 0 and 1.");
			Set<T> objects = this.keySet();
			if(!objects.equals(other.keySet()))
				throw new IllegalArgumentException("The distributions cannot be combined as they differ in their definitions.");			
			ProbabilityFunction<T> p = new ProbabilityFunction<T>();
			for(T i: objects)
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
		public ProbabilityFunction<T> linearCombination(double d1, double d2, ProbabilityFunction<T> other){
			if(!this.keySet().equals(other.keySet()))
				throw new IllegalArgumentException("The distributions cannot be combined as they differ in their definitions.");
			List<T> objects = new ArrayList<T>(this.keySet());
			List<Double> probabilities = new LinkedList<Double>();
			for(T i: objects)
				probabilities.add(d1 * this.get(i).getValue() + d2 * other.get(i).getValue());
			ProbabilityFunction.normalize(probabilities);		
			ProbabilityFunction<T> p = new ProbabilityFunction<T>();
			Iterator<Double> iterator = probabilities.iterator();
			for(T i: objects)
				p.put(i, new Probability(iterator.next()));
			return p;
		}

		/**
		 * Computes the convex combination of the
		 * given probability distributions P1,...,PN with parameters factors, i.e.
		 * it returns a P with P(i)=d1 P1(i) + d2 P2(i)+ ... + dN PN(i) for every object i
		 * (with d1,...,dN=factors).
		 * @param <S> The object class
		 * @param factors a vector of doubles.
		 * @param creators a vector of probability distributions.
		 * @return the convex combination of the given distributions with parameters factors.
		 * @throws IllegalArgumentException if either the sum of factors d is not in 1, or this and
		 * the given probability distributions are not defined on the same set of objects, or
		 * the lengths of creators and factors differ.
		 */
		public static <S extends Comparable<S>> ProbabilityFunction<S> convexCombination(double[] factors, ProbabilityFunction<S>[] creators) throws IllegalArgumentException{
			if(factors.length != creators.length)
				throw new IllegalArgumentException("Length of factors and creators does not coincide.");
			double sum = 0;
			for(double d: factors)
				sum += d;
			if(sum < 1-Probability.PRECISION || sum > 1+Probability.PRECISION)
				throw new IllegalArgumentException("Factors do not sum up to one.");
			Set<S> objects = creators[0].keySet();
			for(int i = 1; i < creators.length; i++)
				if(!objects.equals(creators[i].keySet()))				
					throw new IllegalArgumentException("The distributions cannot be combined as they differ in their definitions.");			
			ProbabilityFunction<S> p = new ProbabilityFunction<S>();
			for(S i: objects){
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
		 * @param objects set of interpretations of type S.
		 * @return the uniform distribution on the given interpretations.
		 */
		public static <S extends Comparable<S>> ProbabilityFunction<S> getUniformDistribution(Set<S> objects){
			ProbabilityFunction<S> p = new ProbabilityFunction<S>();
			double size = objects.size();
			for(S i: objects)
				p.put(i, new Probability(1d/size));
			return p;
		}
		
		/**
		 * Samples one element from the domain of this
		 * probability function, depending on its probability.
		 * @return a sample from this probability function.
		 */
		public T sample(){
			return this.sample(ProbabilityFunction.random);
		}
		
		/**
		 * Samples one element from the domain of this
		 * probability function, depending on its probability.
		 * @param random the number generator used.
		 * @return a sample from this probability function.
		 */
		public T sample(Random random){
			if(this.isEmpty()) return null;
			double p = random.nextDouble();
			Probability prob = new Probability(0d);
			for(Entry<T, Probability> entry: this.entrySet()){
				prob = prob.add(entry.getValue());
				if(p <= prob.doubleValue())
					return entry.getKey();
			}
			// this should not happen
			// ... but sometimes this happens
			// until I figure it out just return the first element
			// TODO fix this
			System.out.println("X");
			return this.keySet().iterator().next();
			//throw new RuntimeException("Mass of this probability function is larger than one!");
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

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((probabilities == null) ? 0 : probabilities.hashCode());
			return result;
		}

		/**
		 * Returns the vector of probabilities, depending on the order
		 * of the domain elements (which can be ordered as they
		 * implement Comparable). 
		 * @return the vector of probabilities
		 */
		public Vector<Probability> getProbabilityVector(){
			List<T> keys = new LinkedList<T>(this.keySet());
			Collections.sort(keys);
			Vector<Probability> vec = new Vector<Probability>();
			for(T key: keys)
				vec.add(this.get(key));
			return vec;
		}
		
		/**
		 * Returns the vector of probabilities, depending on the order
		 * of the domain elements (which can be ordered as they
		 * implement Comparable). 
		 * @return the vector of probabilities as doubles
		 */
		public Vector<Double> getProbabilityVectorAsDoubles(){
			List<T> keys = new LinkedList<T>(this.keySet());
			Collections.sort(keys);
			Vector<Double> vec = new Vector<Double>();
			for(T key: keys)
				vec.add(this.get(key).getValue());
			return vec;
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
			ProbabilityFunction<?> other = (ProbabilityFunction<?>) obj;
			if (probabilities == null) {
				if (other.probabilities != null)
					return false;
			} else if (!probabilities.equals(other.probabilities))
				return false;
			return true;
		}
	}

