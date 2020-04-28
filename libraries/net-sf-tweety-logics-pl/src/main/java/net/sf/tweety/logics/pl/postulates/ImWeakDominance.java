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
import java.util.Random;

import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.reasoner.SimplePlReasoner;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * A weaker variant of the "dominance" postulate using prime implicates,
 * proposed in [Jabbour et al. On the characterization of inconsistency measures: 
 * A prime implicates based framework. ICTAI'14] in response to the problem that many syntactic
 * inconsistency measures do not satisfy the "dominance" postulate.
 * 
 * @author Anna Gessler
 */
public class ImWeakDominance extends ImPostulate {

	/**
	 * Protected constructor so one uses only the single instance
	 * ImPostulate.WEAKDOMINANCE
	 */
	protected ImWeakDominance() {
	}

	@Override
	public String getName() {
		return "Weak Dominance";
	}

	@Override
	public boolean isApplicable(Collection<PlFormula> kb) {
		if (kb.size() < 2)
			return false;
		List<PlFormula> orderedKB = ((PlBeliefSet) kb).getCanonicalOrdering();
		Random rand = new Random(orderedKB.hashCode());
		int i = rand.nextInt(orderedKB.size());
		PlFormula strongerFormula = orderedKB.get(0);
		PlFormula weakerFormula = orderedKB.get(i);
		if (!SatSolver.getDefaultSolver().isConsistent(strongerFormula))
			return false;
		if (!(new SimplePlReasoner().query(strongerFormula, weakerFormula)
				&& strongerFormula.getPrimeImplicants().containsAll(weakerFormula.getPrimeImplicants())))
			return false;
		return true;
	}

	@Override
	public boolean isSatisfied(Collection<PlFormula> kb, BeliefSetInconsistencyMeasure<PlFormula> ev) {
		if (!this.isApplicable(kb))
			return true;
		PlBeliefSet kb1 = new PlBeliefSet(kb);
		PlBeliefSet kb2 = new PlBeliefSet(kb);
		List<PlFormula> orderedKB = ((PlBeliefSet) kb).getCanonicalOrdering();
		Random rand = new Random(orderedKB.hashCode());
		int i = rand.nextInt(orderedKB.size());
		PlFormula strongerFormula = orderedKB.get(0);
		PlFormula weakerFormula = orderedKB.get(i);
		kb1.remove(weakerFormula);
		kb2.remove(strongerFormula);
		double inconsistency1 = ev.inconsistencyMeasure(kb1);
		double inconsistency2 = ev.inconsistencyMeasure(kb2);
		return (inconsistency1 >= inconsistency2);
	}
}
