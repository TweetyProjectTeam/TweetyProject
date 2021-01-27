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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.util.SetTools;

/**
 * This class implements the inconsistency measure I_CSP from 
 * [Said Jabbour. On Inconsistency Measuring and Resolving. ECAI 2016]
 *  
 * This implementation uses a brute force search approach which does
 * not scale well.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The specific type of formulas
 */
public class IsInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUS enumerator used for the measure. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new measure that uses the given MUS enumerator.
	 * 
	 * @param enumerator some MUS enumerator
	 */
	public IsInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Set<Collection<S>> mis = new HashSet<>(this.enumerator.minimalInconsistentSubsets(formulas));
		SetTools<S> settools = new SetTools<S>();
		double sum = 0;
		int card = 0;
		while(true){
			int num = settools.independentSets(mis, card++).size();
			if(num == 0)
				break;
			sum += num;
		}		
		return Math.log(sum);
	}

}
