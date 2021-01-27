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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.postulates;

import java.util.Arrays;
import java.util.Collection;

import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.sat.PlMusEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * The "attenuation" postulate for inconsistency measures: Minimal inconsistent
 * sets of smaller size should have a larger inconsistency value.
 * 
 * @author Anna Gessler
 */
public class ImAttenuation extends ImPostulate {

	/**
	 * Protected constructor so one uses only the single instance
	 * ImPostulate.ATTENUATION
	 */
	protected ImAttenuation() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util
	 * .Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isApplicable(Collection<PlFormula> kb) {
		if (kb.isEmpty())
			return false;
		Collection<Collection<PlFormula>> muses = PlMusEnumerator.getDefaultEnumerator().minimalInconsistentSubsets(kb);
		if (muses.size() < 2)
			return false;
		Object[] test = muses.toArray();
		Arrays.sort(test, new SimpleMUSComparator());
		PlBeliefSet mus1 = new PlBeliefSet((Collection<PlFormula>) test[0]);
		PlBeliefSet mus2 = new PlBeliefSet((Collection<PlFormula>) test[1]);
		if (mus1.size() != mus2.size())
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.logics.pl.postulates.AbstractImPostulate#isSatisfied(java.util.
	 * Collection,
	 * org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isSatisfied(Collection<PlFormula> kb, BeliefSetInconsistencyMeasure<PlFormula> ev) {
		if (!this.isApplicable(kb))
			return true;
		double inconsistency1, inconsistency2;
		Collection<Collection<PlFormula>> muses = PlMusEnumerator.getDefaultEnumerator().minimalInconsistentSubsets(kb);
		Object[] test = muses.toArray();
		Arrays.sort(test, new SimpleMUSComparator());
		PlBeliefSet mus1 = new PlBeliefSet((Collection<PlFormula>) test[0]);
		PlBeliefSet mus2 = new PlBeliefSet((Collection<PlFormula>) test[1]);
		if (mus1.size() > mus2.size()) {
			inconsistency1 = ev.inconsistencyMeasure(mus1);
			inconsistency2 = ev.inconsistencyMeasure(mus2);
		} else {
			inconsistency1 = ev.inconsistencyMeasure(mus2);
			inconsistency2 = ev.inconsistencyMeasure(mus1);
		}
		return (inconsistency1 < inconsistency2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Attenuation";
	}
	
	

}
