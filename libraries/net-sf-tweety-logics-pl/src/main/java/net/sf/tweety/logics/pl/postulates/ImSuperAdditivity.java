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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * The "super-additivity" postulate for inconsistency measures: The sum of the 
 * inconsistency values of two disjoint knowledge bases is not larger
 * than the inconsistency value of the joint knowledge base.
 * 
 * @author Anna Gessler
 */
public class ImSuperAdditivity extends ImPostulate{

	/**
	 * Protected constructor so one uses only the single instance ImPostulate.ADDITIVITY
	 */
	protected ImSuperAdditivity() {		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util.Collection)
	 */
	@Override
	public boolean isApplicable(Collection<PropositionalFormula> kb) {
		if (kb.isEmpty())
			return false;
		List<PropositionalFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		List<PropositionalFormula> left = new ArrayList<PropositionalFormula>(orderedKB.subList(0,orderedKB.size()/2));
		List<PropositionalFormula> right = new ArrayList<PropositionalFormula>(orderedKB.subList(orderedKB.size()/2,orderedKB.size()));
		for (PropositionalFormula f : left)
			if (right.contains(f))
				return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.ImPostulate#isSatisfied(java.util.Collection, net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@Override
	public boolean isSatisfied(Collection<PropositionalFormula> kb, BeliefSetInconsistencyMeasure<PropositionalFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		List<PropositionalFormula> orderedKB = ((PlBeliefSet)kb).getCanonicalOrdering();
		List<PropositionalFormula> kb1 = new ArrayList<PropositionalFormula>(orderedKB.subList(0,orderedKB.size()/2));
		List<PropositionalFormula> kb2 = new ArrayList<PropositionalFormula>(orderedKB.subList(orderedKB.size()/2,orderedKB.size()));
		double inconsistency1 = ev.inconsistencyMeasure(kb1);
		double inconsistency2 = ev.inconsistencyMeasure(kb2);
		kb1.addAll(kb2);
		double inconsistency12 = ev.inconsistencyMeasure(kb1);
		return (inconsistency12 >= (inconsistency1+inconsistency2));
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Super-Additivity";
	}
}
