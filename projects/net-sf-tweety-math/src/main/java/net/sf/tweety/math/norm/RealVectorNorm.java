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

import java.util.Vector;

import net.sf.tweety.math.term.Term;

/**
 * A norm for real vector spaces.
 * @author Matthias Thimm
 *
 */
public interface RealVectorNorm extends Norm<Vector<Double>> {

	/**
	 * Returns the norm as a term of the given terms
	 * @param obj some term vector
	 * @return the term of the norm
	 */
	public Term normTerm(Vector<Term> obj);
	
	/**
	 * Returns the norm as a term of the given terms
	 * @param obj some term array
	 * @return the term of the norm
	 */
	public Term normTerm(Term[] obj);
			
	/**
	 * The distance between the two objects as a term.
	 * @param obj1 some terms
	 * @param obj2 some terms
	 * @return the distance between the two objects as a term
	 */
	public Term distanceTerm(Vector<Term> obj1, Vector<Term> obj2);
	
	/**
	 * The distance between the two objects as a term.
	 * @param obj1 some terms
	 * @param obj2 some terms
	 * @return the distance between the two objects as a term
	 */
	public Term distanceTerm(Term[] obj1, Term[] obj2);
}
