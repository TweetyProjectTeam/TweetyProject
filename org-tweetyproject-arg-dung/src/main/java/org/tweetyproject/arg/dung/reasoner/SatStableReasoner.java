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
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Uses a SAT solver to determine stable extensions.
 * 
 * @author Matthias Thimm
 *
 */
public class SatStableReasoner extends AbstractSatExtensionReasoner{

	/**
	 * Constructs a new stable reasoner.
	 * @param solver the SAT solver this reasoner uses.
	 */
	public SatStableReasoner(SatSolver solver) {
		super(solver);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractSatExtensionReasoner#getPropositionalCharacterisationBySemantics(org.tweetyproject.arg.dung.syntax.DungTheory, java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(DungTheory aaf, Map<Argument, Proposition> in, Map<Argument, Proposition> out, Map<Argument, Proposition> undec) {
		PlBeliefSet beliefSet = new PlBeliefSet();
		// an argument is in iff all attackers are out, and
		// no argument is undecided
		for(Argument a: aaf){
			if(aaf.getAttackers(a).isEmpty()){
				beliefSet.add(((PlFormula)in.get(a)));
			}else{
				beliefSet.add((PlFormula)undec.get(a).complement());
				Collection<PlFormula> attackersOr = new HashSet<PlFormula>();//new Contradiction();
				Collection<PlFormula> attackersNotOr = new HashSet<PlFormula>();//new Contradiction();
				for(Argument b: aaf.getAttackers(a)){
					attackersOr.add(in.get(b));
					attackersNotOr.add((PlFormula)out.get(b).complement());					
					beliefSet.add(((PlFormula)in.get(a).complement()).combineWithOr((PlFormula)out.get(b)));
				}
				beliefSet.add(new Disjunction(attackersOr).combineWithOr((PlFormula)out.get(a).complement()));
				beliefSet.add(new Disjunction(attackersNotOr).combineWithOr((PlFormula)in.get(a)));		
			}
		}
		return beliefSet;
	}
    /**
     * this method always returns true because the solver is native
     */
	@Override
	public boolean isInstalled() {
		return true;
	}
}
