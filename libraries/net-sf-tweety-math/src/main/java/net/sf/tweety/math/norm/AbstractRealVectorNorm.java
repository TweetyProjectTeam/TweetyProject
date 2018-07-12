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

import java.util.Vector;

import net.sf.tweety.math.term.Term;

/**
 * Abstract class for real vector norms.
 * 
 * @author Matthias Thimm
 *
 */
public abstract class AbstractRealVectorNorm implements RealVectorNorm {
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#normTerm(java.util.Vector)
	 */
	public abstract Term normTerm(Vector<Term> obj);
		
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
	 * @see net.sf.tweety.math.norm.RealVectorNorm#distanceTerm(java.util.Vector, java.util.Vector)
	 */
	public abstract Term distanceTerm(Vector<Term> obj1, Vector<Term> obj2);
		
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
}
