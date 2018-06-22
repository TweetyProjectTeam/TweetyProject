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
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.InterpretationIterator;
import net.sf.tweety.commons.analysis.InterpretationDistance;

/**
 * This class implements the d-hit inconsistency measure from  [Grant, Hunter. Distance-based Measures of Inconsistency, ECSQARU'13].
 * This implementation uses a slightly different characterization than the one used in the paper. This measure seeks an interpretation
 * I such that the number of formulas not satisfied by I is minimal. The value
 * of the inconsistency is than exactly this number. The distance can be parameterized.<br/>
 * NOTE: Currently, this algorithm uses a brute force approach (checking all interpretations) for computing the solution.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas supported
 */
public class DHitInconsistencyMeasure<T extends Interpretation<S>,S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The distance used by this measure. */
	private InterpretationDistance<T,S> distance;
	/** For iterating over interpretations. */
	private InterpretationIterator<S,T> it;
	
	/**
	 * Creates a new d-sum inconsistency measure using the given distance and interpretations
	 * provided from the given interpretation iterator.
	 * @param distance some distance measure
	 * @param it some interpretation iterator
	 */
	public DHitInconsistencyMeasure(InterpretationDistance<T,S> distance, InterpretationIterator<S,T> it){
		this.distance = distance;
		this.it = it;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		double val = Double.POSITIVE_INFINITY;
		// check every interpretation
		this.it = this.it.reset(formulas);
		T i;
		double tmp;
		while(this.it.hasNext()){
			i = it.next();
			tmp = 0;
			for(S f: formulas)
				if(this.distance.distance(f,i) > 0)
					tmp++;
			if(tmp < val)
				val = tmp;
		}
		return val;
	}
}
