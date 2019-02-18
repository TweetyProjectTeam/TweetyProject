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
import java.util.Set;

import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * The "contradiction" postulate for inconsistency measures: A knowledge base
 * is maximally inconsistent if all non-empty subsets are inconsistent. 
 * This postulate is supposed to be an extension of the "normalization" postulate,
 * meaning 1 is the maximum inconsistency value.
 * 
 * @author Anna Gessler
 * @see net.sf.tweety.logics.pl.postulates.ImNormalization
 */
public class ImContradiction extends ImPostulate {
	
	/**
	 * Protected constructor so one uses only the single instance ImPostulate.CONTRADICTION
	 */
	protected ImContradiction() {		
	}
	

	@Override
	public String getName() {
		return "Contradiction";
	}

	@Override
	public boolean isApplicable(Collection<PropositionalFormula> kb) {
		return true;
	}

	@Override
	public boolean isSatisfied(Collection<PropositionalFormula> kb,
			BeliefSetInconsistencyMeasure<PropositionalFormula> ev) {
		if(!this.isApplicable(kb))
			return true;
		double inconsistency = ev.inconsistencyMeasure(kb);
		Set<Set<PropositionalFormula>> subsets =  new SetTools<PropositionalFormula>().subsets(kb);
		SatSolver solver = SatSolver.getDefaultSolver();
		for (Set<PropositionalFormula> kbx : subsets) {
			if (!kbx.isEmpty() && solver.isConsistent(kbx)) 
				return (inconsistency != 1);
		}
		return (inconsistency == 1);
	}

}
