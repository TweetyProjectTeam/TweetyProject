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
 * This norm uses an aggregator on the 1-norm distance of each 
 * value.
 * 
 * @author Matthias Thimm
 */
public class AggregatingNorm implements RealVectorNorm, Serializable {

	private static final long serialVersionUID = 7393475547325748126L;

	/** The aggregation function used for computing the distance. */	
	private AggregationFunction aggregator;
	
	/** Creates a new distance function with the given aggregator.
	 * @param aggregator some aggregation function.
	 */
	public AggregatingNorm(AggregationFunction aggregator){
		this.aggregator = aggregator;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#distance(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double distance(Vector<Double> l1, Vector<Double> l2) {
		if(l1.size() != l2.size())
			throw new IllegalArgumentException("Lengths of vectors must match.");
		List<Double> diff = new ArrayList<Double>();
		for(int i = 0; i< l1.size(); i++)
			diff.add(Math.abs(l1.get(i)-l2.get(i)));
		return this.aggregator.eval(diff);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.aggregator.toString()+"-dist";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((aggregator == null) ? 0 : aggregator.hashCode());
		return result;
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
		AggregatingNorm other = (AggregatingNorm) obj;
		if (aggregator == null) {
			if (other.aggregator != null)
				return false;
		} else if (!aggregator.equals(other.aggregator))
			return false;
		return true;
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
