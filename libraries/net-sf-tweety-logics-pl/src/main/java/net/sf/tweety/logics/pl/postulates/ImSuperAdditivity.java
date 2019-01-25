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
import net.sf.tweety.logics.pl.util.RandomSampler;

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
		return !kb.isEmpty();
	}


	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.postulates.ImPostulate#isSatisfied(java.util.Collection, net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure)
	 */
	@Override
	public boolean isSatisfied(Collection<PropositionalFormula> kb, BeliefSetInconsistencyMeasure<PropositionalFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		double inconsistency1 = ev.inconsistencyMeasure(kb);
		RandomSampler sampler = new RandomSampler(((PlBeliefSet)kb).getSignature(),0.2,2,4);
		PlBeliefSet kb2 = sampler.next();
		double inconsistency2 = ev.inconsistencyMeasure(kb2);
		kb.addAll(kb2);
		double inconsistency12 = ev.inconsistencyMeasure(kb);
		return (inconsistency12 >= (inconsistency1+inconsistency2));
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.postulates.Postulate#getName()
	 */
	public String getName() {
		return "Super-Additivity";
	}
}
