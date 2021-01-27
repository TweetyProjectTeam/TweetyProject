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
package org.tweetyproject.logics.commons.analysis;

import java.util.*;

import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;

/**
 * Classes extending this abstract class are capable of testing
 * whether a given belief set is consistent. 
 * 
 * @author Matthias Thimm
 * @param <T> The type of formulas in the belief set
 */
public abstract class AbstractBeliefSetConsistencyTester<T extends Formula> implements BeliefSetConsistencyTester<T> {
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.ConsistencyTester#isConsistent(org.tweetyproject.BeliefBase)
	 */
	@Override
	public boolean isConsistent(BeliefSet<T,?> beliefSet){
		return this.isConsistent((Collection<T>) beliefSet);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(org.tweetyproject.Formula)
	 */
	@Override
	public boolean isConsistent(T formula){
		Collection<T> c = new HashSet<T>();
		c.add(formula);
		return this.isConsistent(c);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	public abstract boolean isConsistent(Collection<T> formulas);
}