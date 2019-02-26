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
package net.sf.tweety.logics.pl.postulates;

import java.util.Collection;
import java.util.List;

import net.sf.tweety.logics.commons.analysis.AbstractMusEnumerator;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * The "penalty" postulate for inconsistency measures: Adding a formula that participates
 * in an inconsistency (i.e. a non-free formula) has a positive impact 
 * on the inconsistency value.
 * 
 * @see net.sf.tweety.logics.pl.postulates.ImFreeFormulaIndependence
 * @author Anna Gessler
 */
public class ImPenalty extends ImPostulate{

	/**
	 * Protected constructor so one uses only the single instance ImPostulate.PENALTY
	 */
	protected ImPenalty() {		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util.Collection)
	 */
	@Override
	public boolean isApplicable(Collection<PropositionalFormula> kb) {
		if(kb.isEmpty())
			return false;
		List<PropositionalFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		PropositionalFormula f = orderedKB.get(0);
		AbstractMusEnumerator<PropositionalFormula> e = PlMusEnumerator.getDefaultEnumerator();
		for(Collection<PropositionalFormula> mus: e.minimalInconsistentSubsets(kb))
			if(mus.contains(f))
				return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.AbstractImPostulate#isSatisfied(java.util.Collection, net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@Override
	public boolean isSatisfied(Collection<PropositionalFormula> kb, BeliefSetInconsistencyMeasure<PropositionalFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		double inconsistency1 = ev.inconsistencyMeasure(kb);
		PlBeliefSet kb2 = new PlBeliefSet(kb);
		List<PropositionalFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		PropositionalFormula f = orderedKB.get(0);
		kb2.remove(f);
		double inconsistency2 = ev.inconsistencyMeasure(kb2);
		return (inconsistency1 > inconsistency2);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Penalty";
	}
}
