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
package net.sf.tweety.math.norm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sf.tweety.math.func.AggregationFunction;
import net.sf.tweety.math.term.Term;

/**
 * This distance function uses an aggregator on a probabilistically normalized distance
 * for probabilities of each value.
 * 
 * @author Matthias Thimm
 */
public class ProbabilisticAggregatingNorm implements RealVectorNorm, Serializable {

	private static final long serialVersionUID = -114048201529519281L;

	/** The aggregation function used for computing the distance. */	
	private AggregationFunction aggregator;
	
	/** A parameter for adjusting the aggregation. */
	private int weight;
	
	/** Creates a new distance function with the given aggregator.
	 * @param aggregator some aggregation function.
	 * @param weight a parameter for adjusting the aggregation.
	 */
	public ProbabilisticAggregatingNorm(AggregationFunction aggregator, int weight){
		if(weight <= 2 || (weight % 2 == 0))
				throw new IllegalArgumentException("The weight must be odd and greater two.");
		this.aggregator = aggregator;
		this.weight = weight;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#distance(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double distance(Vector<Double> l1, Vector<Double> l2) {
		if(l1.size() != l2.size())
			throw new IllegalArgumentException("Lengths of lists must match.");
		List<Double> diff = new ArrayList<Double>();
		for(int i = 0; i< l1.size(); i++)
			diff.add(Math.abs( Math.pow(0.5-l1.get(i),this.weight)-Math.pow(0.5-l2.get(i),this.weight)));
		return this.aggregator.eval(diff);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.aggregator.toString()+"-pdist";
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#norm(java.lang.Object)
	 */
	@Override
	public double norm(Vector<Double> obj) {
		throw new UnsupportedOperationException("Implement me!");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#normTerm(java.util.Vector)
	 */
	@Override
	public Term normTerm(Vector<Term> obj) {
		throw new UnsupportedOperationException("Implement me!");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#normTerm(net.sf.tweety.math.term.Term[])
	 */
	@Override
	public Term normTerm(Term[] obj) {
		throw new UnsupportedOperationException("Implement me!");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#distanceTerm(java.util.Vector, java.util.Vector)
	 */
	@Override
	public Term distanceTerm(Vector<Term> obj1, Vector<Term> obj2) {
		throw new UnsupportedOperationException("Implement me!");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#distanceTerm(net.sf.tweety.math.term.Term[], net.sf.tweety.math.term.Term[])
	 */
	@Override
	public Term distanceTerm(Term[] obj1, Term[] obj2) {
		throw new UnsupportedOperationException("Implement me!");
	}
}
