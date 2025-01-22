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
package org.tweetyproject.logics.pl.reasoner;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Uses the default SAT reasoner to perform reasoning in propositional logic
 * @author Matthias Thimm
 */
public class SatReasoner extends AbstractPlReasoner {

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.reasoner.AbstractPropositionalLogicReasoner#query(org.tweetyproject.logics.pl.syntax.PlBeliefSet, org.tweetyproject.logics.pl.syntax.PropositionalFormula)
	 */
	@Override
	public Boolean query(PlBeliefSet beliefbase, PlFormula formula) {
		Collection<PlFormula> formulas = new HashSet<PlFormula>();
		formulas.add(new Negation((PlFormula)formula));
		formulas.addAll(beliefbase);
		return !SatSolver.getDefaultSolver().isConsistent(formulas);
	}

	@Override
	public boolean isInstalled() {
		return true;
	}


    /** Default Constructor */
    public SatReasoner(){}
}
