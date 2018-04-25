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

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.postulates.PostulateEvaluatable;

/**
 * Classes extending this abstract class represent inconsistency measures
 * on belief sets.
 * 
 * @author Matthias Thimm
 * @param <S> The type of formulas this measure supports.
 * @param <T> The type of belief sets this measure supports.
 */
public abstract class BeliefSetInconsistencyMeasure<S extends Formula> implements InconsistencyMeasure<BeliefSet<S>>, PostulateEvaluatable<S> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.InconsistencyMeasure#inconsistencyMeasure(net.sf.tweety.BeliefBase)
	 */
	public Double inconsistencyMeasure(BeliefSet<S> beliefBase){
		return this.inconsistencyMeasure((Collection<S>) beliefBase);
	}
	
	/**
	 * This method measures the inconsistency of the given set of formulas.
	 * @param formulas a collection of formulas.
	 * @return a Double indicating the degree of inconsistency.
	 */
	public abstract Double inconsistencyMeasure(Collection<S> formulas);
}
