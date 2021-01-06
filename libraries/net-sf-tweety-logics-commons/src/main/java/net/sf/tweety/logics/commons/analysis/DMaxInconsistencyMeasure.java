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
package net.sf.tweety.logics.commons.analysis;

import java.util.Collection;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.InterpretationIterator;
import net.sf.tweety.commons.analysis.InterpretationDistance;

/**
 * This class implements the d-max inconsistency measure from  [Grant, Hunter. Distance-based Measures of Inconsistency, ECSQARU'13].
 * This implementation uses a slightly different characterization than the one used in the paper. This measure seeks an interpretation
 * I such that the the maximal distance between every formula of the knowledge base and I is minimal. The value
 * of the inconsistency is than exactly this value. The distance can be parameterized.<br>
 * NOTE: Currently, this algorithm uses a brute force approach (checking all interpretations) for computing the solution.
 * 
 * @author Matthias Thimm
 * @param <T> the type of interpretations
 * @param <B> the type of belief bases
 *
 * @param <S> The type of formulas supported
 */
public class DMaxInconsistencyMeasure<T extends Interpretation<B,S>,B extends BeliefBase,S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The distance used by this measure. */
	private InterpretationDistance<T,B,S> distance;
	/** For iterating over interpretations. */
	private InterpretationIterator<S,B,T> it;
	
	/**
	 * Creates a new d-max inconsistency measure using the given distance and interpretations
	 * provided from the given interpretation iterator.
	 * @param distance some distance measure
	 * @param it some interpretation iterator
	 */
	public DMaxInconsistencyMeasure(InterpretationDistance<T,B,S> distance, InterpretationIterator<S,B,T> it){
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
				tmp = Math.max(tmp, this.distance.distance(f,i));
			if(tmp < val)
				val = tmp;
		}
		return val;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "max-distance";
	}

}
