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
import net.sf.tweety.logics.pl.reasoner.SatReasoner;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.util.RandomSampler;

/**
 * The "dominance" postulate for inconsistency measures: Substituting a
 * consistent formula by a weaker formula should not increase the inconsistency
 * value.
 * 
 * @author Anna Gessler
 */
public class ImDominance extends ImPostulate {

	/**
	 * Protected constructor so one uses only the single instance
	 * ImPostulate.DOMINANCE
	 */
	protected ImDominance() {
	}

	private PropositionalFormula strongerFormula;
	private PropositionalFormula weakerFormula;

	@Override
	public String getName() {
		return "Dominance";
	}

	@Override
	public boolean isApplicable(Collection<PropositionalFormula> kb) {
		if (kb.isEmpty()) 
			return false;

		this.strongerFormula = kb.iterator().next();
		if (!SatSolver.getDefaultSolver().isConsistent(strongerFormula)) 
			return false;

		//Generate weaker formula
		RandomSampler sampler = new RandomSampler(strongerFormula.getSignature(),0.8,1,1);
		this.weakerFormula = new Disjunction(strongerFormula,sampler.next().iterator().next());
		if (!new SatReasoner().query(strongerFormula, weakerFormula)) 
			return false;
		
		return true;
	}

	@Override
	public boolean isSatisfied(Collection<PropositionalFormula> kb,
			BeliefSetInconsistencyMeasure<PropositionalFormula> ev) {
		if (!this.isApplicable(kb))
			return true;

		double inconsistency1 = ev.inconsistencyMeasure(kb);
		PlBeliefSet kb2 = new PlBeliefSet(kb);
		kb2.remove(strongerFormula);
		kb2.add(weakerFormula);
		double inconsistency2 = ev.inconsistencyMeasure(kb2);

		return (inconsistency1 >= inconsistency2);
	}

}
