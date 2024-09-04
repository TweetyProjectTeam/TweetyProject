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
package org.tweetyproject.math.norm;

import java.util.Vector;

import org.tweetyproject.math.term.AbsoluteValue;
import org.tweetyproject.math.term.Term;

/**
 * The Manhattan norm.
 * @author Matthias Thimm
 */
public class ManhattanNorm extends AbstractRealVectorNorm{

	/** Constructor */
	public ManhattanNorm() {
	}


	/* (non-Javadoc)
	 * @see org.tweetyproject.math.norm.Norm#norm(java.lang.Object)
	 */
	@Override
	public double norm(Vector<Double> obj) {
		double norm = 0;
		for(Double d: obj)
			norm += Math.abs(d);
		return norm;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.norm.Norm#distance(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double distance(Vector<Double> obj1, Vector<Double> obj2) {
		if(obj1.size() != obj2.size())
			throw new IllegalArgumentException("Dimensions of vectors do not match.");
		double distance = 0;
		for(int i=0; i< obj1.size();i++){
			distance += Math.abs(obj1.get(i)-obj2.get(i));
		}
		return distance;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.norm.RealVectorNorm#normTerm(java.util.Vector)
	 */
	@Override
	public Term normTerm(Vector<Term> obj) {
		Term norm = null;
		for(Term t: obj)
			if(norm == null)
				norm = new AbsoluteValue(t);
			else norm = norm.add(new AbsoluteValue(t));
		return norm;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.norm.RealVectorNorm#distanceTerm(java.util.Vector, java.util.Vector)
	 */
	@Override
	public Term distanceTerm(Vector<Term> obj1, Vector<Term> obj2) {
		if(obj1.size() != obj2.size())
			throw new IllegalArgumentException("Dimensions of vectors do not match.");
		Term distance = null;
		for(int i=0; i< obj1.size();i++){
			if(distance == null)
				distance = new AbsoluteValue(obj1.get(i).minus(obj2.get(i)));
			else
				distance = distance.add(new AbsoluteValue(obj1.get(i).minus(obj2.get(i))));
		}
		return distance;
	}

}
