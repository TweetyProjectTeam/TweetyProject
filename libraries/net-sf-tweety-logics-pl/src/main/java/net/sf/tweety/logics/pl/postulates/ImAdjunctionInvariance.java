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
import java.util.Iterator;

import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * The "adjunction invariance" postulate for inconsistency measures: The
 * set notation of knowledge bases should be equivalent
 * to the conjunction of its formulas in terms of inconsistency values.
 * 
 * @author Anna Gessler
 */
public class ImAdjunctionInvariance extends ImPostulate{

	/**
	 * Protected constructor so one uses only the single instance ImPostulate.ADJUNCTIONINVARIANCE
	 */
	protected ImAdjunctionInvariance() {		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.AbstractImPostulate#isApplicable(java.util.Collection)
	 */
	@Override
	public boolean isApplicable(Collection<PlFormula> kb) {
		return (!(kb.size()<2));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.ImPostulate#isSatisfied(java.util.Collection, net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@Override
	public boolean isSatisfied(Collection<PlFormula> kb, BeliefSetInconsistencyMeasure<PlFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		double inconsistency1 = ev.inconsistencyMeasure(kb);
		Iterator<PlFormula> it = ((PlBeliefSet)kb).getCanonicalOrdering().iterator();
		PlFormula f1 = it.next();
		PlFormula f2 = it.next();
		PlBeliefSet kb2 = new PlBeliefSet(kb);
		kb2.remove(f1);
		kb2.remove(f2);
		kb2.add(new Conjunction(f1,f2));
		double inconsistency2 = ev.inconsistencyMeasure(kb2);
		return (inconsistency1 == inconsistency2);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Adjunction-Invariance";
	}
}
