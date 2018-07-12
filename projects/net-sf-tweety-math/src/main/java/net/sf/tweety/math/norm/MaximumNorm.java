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
package net.sf.tweety.math.norm;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sf.tweety.math.term.AbsoluteValue;
import net.sf.tweety.math.term.Maximum;
import net.sf.tweety.math.term.Term;

/**
 * The Maximum norm.
 * @author Nico Potyka
 */
public class MaximumNorm extends AbstractRealVectorNorm{

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#norm(java.lang.Object)
	 */
	@Override
	public double norm(Vector<Double> obj) {
		double norm = 0;
		for(Double d: obj) {
			double v =  Math.abs(d);
		    if(v>norm) norm = v;
		}
		return norm;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.Norm#distance(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double distance(Vector<Double> obj1, Vector<Double> obj2) {
		if(obj1.size() != obj2.size())
			throw new IllegalArgumentException("Dimensions of vectors do not match.");
		double distance = 0;
		for(int i=0; i< obj1.size();i++){
			double v = Math.abs(obj1.get(i)-obj2.get(i));
			if(v>distance) distance = v;
		}
		return distance;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#normTerm(java.util.Vector)
	 */
	@Override
	public Term normTerm(Vector<Term> obj) {	
		if(obj.size() == 1)
			return obj.get(0);
		return new Maximum(new ArrayList<Term>(obj));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#distanceTerm(java.util.Vector, java.util.Vector)
	 */
	@Override
	public Term distanceTerm(Vector<Term> obj1, Vector<Term> obj2) {
		if(obj1.size() != obj2.size())
			throw new IllegalArgumentException("Dimensions of vectors do not match.");
		List<Term> terms = new ArrayList<Term>();
		for(int i=0; i< obj1.size();i++){
			terms.add(new AbsoluteValue(obj1.get(i).minus(obj2.get(i))));
			
		}
		return new Maximum(terms);
	}

}
