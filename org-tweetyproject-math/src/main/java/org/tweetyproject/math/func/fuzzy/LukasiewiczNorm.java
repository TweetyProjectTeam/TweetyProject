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
package org.tweetyproject.math.func.fuzzy;

import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.Maximum;
import org.tweetyproject.math.term.Term;

/**
 * Represents the Lukasiewicz-norm in fuzzy logic, i.e., T(x,y)=max(x+y-1,0)
 *
 * @author Matthias Thimm
 */
public class LukasiewiczNorm extends TNorm{


	/** Constructor */
	public LukasiewiczNorm() {
	}


	/* (non-Javadoc)
	 * @see org.tweetyproject.math.func.fuzzy.TNorm#eval(java.lang.Double, java.lang.Double)
	 */
	@Override
	public Double eval(Double val1, Double val2) {
		if(val1 < 0 || val1 > 1 || val2 < 0 || val2 > 1)
			throw new IllegalArgumentException("A T-norm is only defined for values in [0,1].");
		return Math.max(val1+val2-1, 0);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.func.fuzzy.TNorm#getDualCoNorm()
	 */
	@Override
	public TCoNorm getDualCoNorm(){
		return new BoundedSum();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.func.fuzzy.TNorm#evalTerm(org.tweetyproject.math.term.Term, org.tweetyproject.math.term.Term)
	 */
	@Override
	public Term evalTerm(Term val1, Term val2) {
		return new Maximum(val1.add(val2).minus(new FloatConstant(1)),new FloatConstant(0));
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.math.func.fuzzy.TNorm#isNilpotent()
	 */
	@Override
	public boolean isNilpotent() {
		return true;
	}
}
