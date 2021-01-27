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
package org.tweetyproject.commons.analysis;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Interpretation;


/**
 * This class models the drastic distance measure between interpretations,
 * see [Grant, Hunter. Distance-based Measures of Inconsistency, ECSQARU'13].
 * It returns 0 if the interpretations are equivalent and 1 otherwise.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The actual type of interpretation
 * @param <B> the type of belief bases
 * @param <S> the type of formulas
 */
public class DrasticDistance<T extends Interpretation<B,S>,B extends BeliefBase, S extends Formula> implements InterpretationDistance<T,B,S>{

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.analysis.InterpretationDistance#distance(org.tweetyproject.commons.Interpretation, org.tweetyproject.commons.Interpretation)
	 */
	@Override
	public double distance(T a, T b) {
		return a.equals(b) ? 0 : 1;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.analysis.InterpretationDistance#distance(org.tweetyproject.commons.Formula, org.tweetyproject.commons.Interpretation)
	 */
	@Override
	public double distance(S f, T b) {		
		return b.satisfies(f) ? 0 : 1;
	}

}
