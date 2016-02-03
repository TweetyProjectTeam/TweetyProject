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
package net.sf.tweety.math.func.fuzzy;

import java.util.Collection;

import net.sf.tweety.math.func.BinaryFunction;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.Term;

/**
 * Represents a T-norm in fuzzy logic, i.e., a generalization of a logical
 * conjunction on values in [0,1].
 * 
 * @author Matthias Thimm
 *
 */
public abstract class TCoNorm implements BinaryFunction<Double,Double,Double>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.func.BinaryFunction#eval(java.lang.Object, java.lang.Object)
	 */
	@Override
	public abstract Double eval(Double val1, Double val2);

	/**
	 * Generalizes this conorm on sets of input parameters
	 * (as t-conorms are associative, the order is not important).
	 * @param vals a set of values
	 * @return the evaluation result on the input
	 */
	public Double eval(Collection<Double> vals){
		Double result = 0d;
		for(Double d: vals)
			result = this.eval(result, d);
		return result;
	}
	
	/**
	 * Gives a representation of this conorm as a mathematical term
	 * @param val1 the term denoting the first parameter
	 * @param val2 the term denoting the second parameter
	 * @return the term denoting this conorm evaluation on the two terms
	 */
	public abstract Term evalTerm(Term val1, Term val2);
	
	/**
	 * Gives a representation of this conorm as a mathematical term.
	 * Generalizes this conorm on sets of input parameters
	 * (as t-conorms are associative, the order is not important).
	 * @param vals a set of value terms
	 * @return the evaluation result on the input as a term
	 */
	public Term evalTerm(Collection<Term> vals){
		Term result = new FloatConstant(0);
		for(Term t: vals)
			result = this.evalTerm(result,t);
		return result;
	}
	
	/**
	 * Returns the dual T-norm of this T-conorm.
	 * @return
	 */
	public abstract TNorm getDualNorm();
	
	/**
	 * A T-conorm is nilpotent if there are x,y<1 with s(x,y)=1
	 * @return true if the conorm is nilpotent
	 */
	public abstract boolean isNilpotent();
	
}
