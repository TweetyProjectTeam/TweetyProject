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

/**
 * This class implements an inconsistency measure based on "n-consistency" proposed in 
 * [Doder,Raskovic,Markovic,Ognjanovic. Measures of inconsistency and defaults. IJAR 51:832-845, 2010.]
 * A knowledge base K is called maximal n-consistent (for a natural number n) if every subset of K of size n is consistent,
 * and this is not true for n+1. As a measure of inconsistency we define I(K) = |K|-n (in order to have I(K)=0 for consistent K).
 * We use a simple characterization of this measure: K is maximal n-consistent iff n+1 is the size of the smallest minimal
 * (wrt. set cardinality) inconsistent subset of K (if K is inconsistent, otherwise n=|K|).
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 */
public class NConsInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S> {

	/** The MUs enumerator. */
	private MusEnumerator<S> enumerator;
	
	/**
	 * Creates a new inconsistency measure.
	 * @param enumerator some MUs enumerator
	 */
	public NConsInconsistencyMeasure(MusEnumerator<S> enumerator){
		this.enumerator = enumerator;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		Collection<Collection<S>> muses = this.enumerator.minimalInconsistentSubsets(formulas);
		if(muses.isEmpty()) return 0d;
		double minMus = (double) formulas.size();
		for(Collection<S> mus: muses)
			if(mus.size() < minMus)
				minMus = (double) mus.size();
		return formulas.size() - minMus + 1;
	}

}
