/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.math.norm;

import java.util.Vector;

import net.sf.tweety.math.func.EntropyFunction;
import net.sf.tweety.math.probability.ProbabilityFunction;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.Fraction;
import net.sf.tweety.math.term.Logarithm;
import net.sf.tweety.math.term.Term;

/**
 * The entropy norm. Uses the entropy of a vector of doubles
 * (=probability function) as a measure of norm and the relative entropy of two 
 * probability distributions as distance.
 * Note that entropy is not actually a norm!
 * @author Matthias Thimm
 */
public class EntropyNorm<T extends Comparable<T>> extends EntropyFunction implements RealVectorNorm{

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#norm(java.lang.Object)
	 */
	@Override
	public double norm(Vector<Double> obj) {
		return this.eval(obj);		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#norm(java.lang.Object)
	 */
	public double norm(ProbabilityFunction<T> prob) {
		return this.norm(prob.getProbabilityVectorAsDoubles());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#normTerm(net.sf.tweety.math.term.Term[])
	 */
	public Term normTerm(Term[] obj){
		Vector<Term> v = new Vector<Term>();
		for(Term t: obj)
			v.add(t);
		return this.normTerm(v);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#distanceTerm(net.sf.tweety.math.term.Term[], net.sf.tweety.math.term.Term[])
	 */
	public Term distanceTerm(Term[] obj1, Term[] obj2){
		Vector<Term> v1 = new Vector<Term>();
		for(Term t: obj1)
			v1.add(t);
		Vector<Term> v2 = new Vector<Term>();
		for(Term t: obj2)
			v2.add(t);
		return this.distanceTerm(v1,v2);
		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#distance(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double distance(Vector<Double> obj1,	Vector<Double> obj2) {
		if(obj1.size() != obj2.size())
			throw new IllegalArgumentException("Dimensions of vectors do not match.");
		double distance = 0;
		for(int i=0; i< obj1.size();i++){
			distance -= obj1.get(i) * Math.log(obj1.get(i)/obj2.get(i));
		}
		return distance;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#distance(java.lang.Object, java.lang.Object)
	 */
	public double distance(ProbabilityFunction<T> prob1, ProbabilityFunction<T> prob2) {
		return this.distance(prob1.getProbabilityVectorAsDoubles(), prob2.getProbabilityVectorAsDoubles());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#normTerm(java.util.Vector)
	 */
	@Override
	public Term normTerm(Vector<Term> obj) {
		return this.getTerm(obj);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#distanceTerm(java.util.Vector, java.util.Vector)
	 */
	@Override
	public Term distanceTerm(Vector<Term> obj1, Vector<Term> obj2) {
		if(obj1.size() != obj2.size())
			throw new IllegalArgumentException("Dimensions of vectors do not match.");
		Term distance = new FloatConstant(0);
		for(int i=0; i< obj1.size();i++)
			distance = distance.minus(obj1.get(i).mult(new Logarithm(new Fraction(obj1.get(i),obj2.get(i)))));
		return distance;
	}
	
}
