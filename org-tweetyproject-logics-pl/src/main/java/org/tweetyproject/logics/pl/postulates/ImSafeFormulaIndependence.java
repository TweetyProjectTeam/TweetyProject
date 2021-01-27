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

import java.util.Collection;
import java.util.List;

import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * The "safe-formula independence" postulate for inconsistency measures: Removing a safe
 * formula (i.e. a consistent formula whose signature is disjoint from the signature of the rest of the knowledge
 * base) from the knowledge base does not change its inconsistency value.
 * 
 * 
 * @author Anna Gessler
 */
public class ImSafeFormulaIndependence extends ImPostulate{

	/**
	 * Protected constructor so one uses only the single instance ImPostulate.SAFEFORMULAINDEPENDENCE
	 */
	protected ImSafeFormulaIndependence() {	
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util.Collection)
	 */
	@Override
	public boolean isApplicable(Collection<PlFormula> kb) {
		if(kb.isEmpty())
			return false;
		List<PlFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		PlFormula safeFormula = orderedKB.get(0);
		orderedKB.remove(0);
		if (!SatSolver.getDefaultSolver().isConsistent(safeFormula)) 
			return false;
		if (safeFormula.getSignature().isOverlappingSignature((new PlBeliefSet(orderedKB)).getMinimalSignature()))
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
		double inconsistency1 = ev.inconsistencyMeasure(kb);
		PlBeliefSet kb2 = new PlBeliefSet(kb);
		List<PlFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		PlFormula safeFormula = orderedKB.get(0);
		kb2.add(safeFormula);
		double inconsistency2 = ev.inconsistencyMeasure(kb2);
		return (inconsistency1 == inconsistency2);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Safe-formula independence";
	}
}
