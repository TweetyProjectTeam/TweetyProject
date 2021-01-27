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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.postulates;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.sat.PlMusEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * The "MI-separability" postulate for inconsistency measures: The sum of inconsistency values 
 * of two knowledge bases with non-interfering sets of minimal inconsistent subsets should
 * be the same as the inconsistency value of their union.
 * 
 * @author Anna Gessler
 */
public class ImMISeparability extends ImPostulate {

	/**
	 * Protected constructor so one uses only the single instance ImPostulate.MISEPARABILITY
	 */
	protected ImMISeparability() {		
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util.Collection)
	 */
	@Override
	public boolean isApplicable(Collection<PlFormula> kb) {
		if(kb.size() < 2)
			return false;
		
		List<PlFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		PlBeliefSet left = new PlBeliefSet(orderedKB.subList(0, orderedKB.size()/2));
		PlBeliefSet right = new PlBeliefSet(orderedKB.subList(orderedKB.size()/2, orderedKB.size()));
		Collection<Collection<PlFormula>> mus_union = PlMusEnumerator.getDefaultEnumerator().minimalInconsistentSubsets(kb);
		Collection<Collection<PlFormula>> mus_left = PlMusEnumerator.getDefaultEnumerator().minimalInconsistentSubsets(left);
		Collection<Collection<PlFormula>> mus_right = PlMusEnumerator.getDefaultEnumerator().minimalInconsistentSubsets(right);
		
		Set<Collection<PlFormula>> union_of_mus = new HashSet<Collection<PlFormula>>(mus_left);
		union_of_mus.addAll(mus_right);
		if (!union_of_mus.equals(mus_union))
			return false;
		mus_left.retainAll(mus_right);
		if (!mus_left.isEmpty())
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.postulates.AbstractImPostulate#isSatisfied(java.util.Collection, org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@Override
	public boolean isSatisfied(Collection<PlFormula> kb, BeliefSetInconsistencyMeasure<PlFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		List<PlFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		PlBeliefSet left = new PlBeliefSet(orderedKB.subList(0, orderedKB.size()/2));
		PlBeliefSet right = new PlBeliefSet(orderedKB.subList(orderedKB.size()/2, orderedKB.size()));
		double inconsistency_union = ev.inconsistencyMeasure(kb);
		double inconsistency_left = ev.inconsistencyMeasure(left);
		double inconsistency_right = ev.inconsistencyMeasure(right);
		return (inconsistency_union == inconsistency_left + inconsistency_right);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "MI-separability";
	}
}
