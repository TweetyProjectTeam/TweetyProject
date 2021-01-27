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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.qbf.reasoner;

import java.util.Set;

import org.tweetyproject.logics.pl.reasoner.AbstractPlReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.qbf.semantics.QbPossibleWorld;

/**
 * Naive classical inference for quantified boolean formulas (checks all interpretations for satisfiability).
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 */
public class NaiveQbfReasoner extends AbstractPlReasoner {
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.reasoner.AbstractPropositionalLogicReasoner#query(org.tweetyproject.logics.pl.syntax.PlBeliefSet, org.tweetyproject.logics.pl.syntax.PropositionalFormula)
	 */
	@Override
	public Boolean query(PlBeliefSet beliefbase, PlFormula formula) {
		PlSignature signature = new PlSignature();
		for(PlFormula f: beliefbase)
			signature.addAll(f.getAtoms());
		signature.addAll(formula.getAtoms());
		Set<QbPossibleWorld> possibleWorlds = QbPossibleWorld.getAllPossibleWorlds(signature);
		for(QbPossibleWorld w: possibleWorlds)
			if(w.satisfies((PlBeliefSet)beliefbase))
				if(!w.satisfies(formula))
					return false;
		return true;
	}

}
