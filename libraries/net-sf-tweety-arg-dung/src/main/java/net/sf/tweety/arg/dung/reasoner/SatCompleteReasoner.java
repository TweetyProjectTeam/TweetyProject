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
package net.sf.tweety.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Uses a SAT solver to determine complete extensions.
 * 
 * @author Matthias Thimm
 *
 */
public class SatCompleteReasoner  extends AbstractSatExtensionReasoner {

	/**
	 * Constructs a new complete reasoner.
	 * @param solver the SAT solver this reasoner uses.
	 */
	public SatCompleteReasoner(SatSolver solver) {
		super(solver);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractSatExtensionReasoner#getPropositionalCharacterisationBySemantics(net.sf.tweety.arg.dung.syntax.DungTheory, java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	protected PlBeliefSet getPropositionalCharacterisationBySemantics(DungTheory aaf, Map<Argument, Proposition> in, Map<Argument, Proposition> out, Map<Argument, Proposition> undec) {
		PlBeliefSet beliefSet = new PlBeliefSet();
		// an argument is in iff all attackers are out
		for(Argument a: aaf){
			if(aaf.getAttackers(a).isEmpty()){
				beliefSet.add(((PlFormula)in.get(a)));
			}else{
				Collection<PlFormula> attackersAnd = new HashSet<PlFormula>();//new Tautology();
				Collection<PlFormula> attackersOr = new HashSet<PlFormula>();//new Contradiction();
				Collection<PlFormula> attackersNotAnd = new HashSet<PlFormula>();//new Tautology();
				Collection<PlFormula> attackersNotOr = new HashSet<PlFormula>();//new Contradiction();
				for(Argument b: aaf.getAttackers(a)){
					attackersAnd.add(out.get(b));
					attackersOr.add(in.get(b));
					attackersNotAnd.add((PlFormula)in.get(b).complement());
					attackersNotOr.add((PlFormula)out.get(b).complement());
				}
				beliefSet.add(((PlFormula)out.get(a).complement()).combineWithOr(new Disjunction(attackersOr)));
				beliefSet.add(((PlFormula)in.get(a).complement()).combineWithOr(new Conjunction(attackersAnd)));
				beliefSet.add(((PlFormula)undec.get(a).complement()).combineWithOr(new Conjunction(attackersNotAnd)));
				beliefSet.add(((PlFormula)undec.get(a).complement()).combineWithOr(new Disjunction(attackersNotOr)));
			}
		}		
		return beliefSet;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#query(net.sf.tweety.arg.dung.syntax.DungTheory,net.sf.tweety.arg.dung.syntax.Argument,net.sf.tweety.commons.InferenceMode)
	 */
	@Override
	public Boolean query(DungTheory beliefbase, Argument formula, InferenceMode inferenceMode) {
		if(inferenceMode.equals(InferenceMode.SKEPTICAL)){
			PlBeliefSet prop = this.getPropositionalCharacterisation(beliefbase);
			prop.add(new Negation(new Proposition("in_" + formula.getName())));
			PossibleWorld w = (PossibleWorld) super.solver.getWitness(prop);
			if(w == null)
				return true;
			return false;
		}
		// so its credulous semantics
		PlBeliefSet prop = this.getPropositionalCharacterisation(beliefbase);
		prop.add(new Proposition("in_" + formula.getName()));
		PossibleWorld w = (PossibleWorld) super.solver.getWitness(prop);
		if(w == null)
			return false;
		return true;
	}
}
