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

import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * The "monotony" postulate for inconsistency measures: adding information
 * to a belief base cannot decrease the inconsistency value.
 * 
 * @author Matthias Thimm
 */
public class ImMonotony extends ImPostulate{

	/**
	 * Protected constructor so one uses only the single instance ImPostulate.MONOTONY
	 */
	protected ImMonotony() {		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util.Collection)
	 */
	@Override
	public boolean isApplicable(Collection<PropositionalFormula> kb) {
		return !kb.isEmpty();
	}


	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.ImPostulate#isSatisfied(java.util.Collection, net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@Override
	public boolean isSatisfied(Collection<PropositionalFormula> kb, BeliefSetInconsistencyMeasure<PropositionalFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		double val1 = ev.inconsistencyMeasure(kb);
		PlBeliefSet kb2 = new PlBeliefSet(kb);
		kb2.remove(kb.iterator().next());
		double val2 = ev.inconsistencyMeasure(kb2);
		return val2 <= val1;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Monotony";
	}
}
