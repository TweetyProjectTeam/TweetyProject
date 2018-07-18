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
package net.sf.tweety.logics.pl.reasoner;

import java.util.Set;

import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Naive classical inference  (checks all interpretations for satisfiability).
 * 
 * @author Matthias Thimm
 */
public class NaiveReasoner extends AbstractPropositionalLogicReasoner {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.reasoner.AbstractPropositionalLogicReasoner#query(net.sf.tweety.logics.pl.syntax.PlBeliefSet, net.sf.tweety.logics.pl.syntax.PropositionalFormula)
	 */
	@Override
	public Boolean query(PlBeliefSet beliefbase, PropositionalFormula formula) {
		PropositionalSignature signature = new PropositionalSignature();
		for(PropositionalFormula f: beliefbase)
			signature.addAll(f.getAtoms());
		signature.addAll(formula.getAtoms());
		Set<PossibleWorld> possibleWorlds = PossibleWorld.getAllPossibleWorlds(signature);
		for(PossibleWorld w: possibleWorlds)
			if(w.satisfies((PlBeliefSet)beliefbase))
				if(!w.satisfies(formula))
					return false;
		return true;
	}

}
