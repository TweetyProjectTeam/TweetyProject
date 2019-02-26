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
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * The "free-formula dilution" postulate for inconsistency measures: Removing a 
 * formula not participating in any minimal inconsistent set does not make the inconsistency 
 * value larger. 
 * <br> This postulate is a weaker version of "free formula independence"
 * and is intended to be used with normalized inconsistency measures 
 * (which may not fulfill free-formula independence in some cases).
 * 
 * @see net.sf.tweety.logics.pl.postulates.ImFreeFormulaIndependence
 * @author Anna Gessler
 */
public class ImFreeFormulaDilution extends ImPostulate{

	/**
	 * Protected constructor so one uses only the single instance ImPostulate.FREEFORMULADILUTION
	 */
	protected ImFreeFormulaDilution() {		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util.Collection)
	 */
	@Override
	public boolean isApplicable(Collection<PlFormula> kb) {
		if(kb.isEmpty())
			return false;
		List<PlFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		PlFormula f = orderedKB.get(0);
		AbstractMusEnumerator<PlFormula> e = PlMusEnumerator.getDefaultEnumerator();
		for(Collection<PlFormula> mus: e.minimalInconsistentSubsets(kb))
			if(mus.contains(f))
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.AbstractImPostulate#isSatisfied(java.util.Collection, net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@Override
	public boolean isSatisfied(Collection<PlFormula> kb, BeliefSetInconsistencyMeasure<PlFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		double inconsistency1 = ev.inconsistencyMeasure(kb);
		PlBeliefSet kb2 = new PlBeliefSet(kb);
		List<PlFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		PlFormula f = orderedKB.get(0);
		kb2.remove(f);
		double inconsistency2 = ev.inconsistencyMeasure(kb2);
		return inconsistency1 >= inconsistency2;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Free-formula dilution";
	}
}
