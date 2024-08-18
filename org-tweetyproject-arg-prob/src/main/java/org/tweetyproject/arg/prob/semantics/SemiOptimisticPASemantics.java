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
package org.tweetyproject.arg.prob.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.math.equation.Inequation;
import org.tweetyproject.math.equation.Statement;
import org.tweetyproject.math.probability.Probability;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.Term;

/**
 * P is semi-optimistic wrt. AF if P(A) \geq 1 - \sum_{B in Attackers(A)} P(B) for every A in Arg with Attackers(A)!= {}.
 * @author Matthias Thimm
 */
public class SemiOptimisticPASemantics extends AbstractPASemantics{


	/** Default */
	public SemiOptimisticPASemantics(){
		super();
	}
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.semantics.AbstractPASemantics#satisfies(org.tweetyproject.arg.prob.semantics.ProbabilisticExtension, org.tweetyproject.arg.dung.DungTheory)
	 */
	@Override
	public boolean satisfies(ProbabilisticExtension p, DungTheory theory) {
		for(Argument arg: theory){
			if(theory.getAttackers(arg).isEmpty())
				continue;
			double prob = 0;
			for(Argument attacker: theory.getAttackers(arg)){
				prob += p.probability(attacker).doubleValue();
			}
			if(p.probability(arg).doubleValue() < 1 - prob - Probability.PRECISION)
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.semantics.AbstractPASemantics#getSatisfactionStatement(org.tweetyproject.arg.dung.DungTheory, java.util.Map)
	 */
	@Override
	public Collection<Statement> getSatisfactionStatements(DungTheory theory, Map<Collection<Argument>, FloatVariable> worlds2vars) {
		Set<Statement> stats = new HashSet<Statement>();
		for(Argument arg: theory){
			if(theory.getAttackers(arg).isEmpty())
				continue;
			Term prob = null;
			for(Argument attacker: theory.getAttackers(arg)){
				if(prob == null)
					prob = this.probabilityTerm(attacker, worlds2vars);
				else
					prob = prob.add(this.probabilityTerm(attacker, worlds2vars));
			}
			stats.add(new Inequation(this.probabilityTerm(arg, worlds2vars),new FloatConstant(1).minus(prob),Inequation.GREATER_EQUAL));
		}
		return stats;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.prob.semantics.AbstractPASemantics#toString()
	 */
	@Override
	public String toString() {
		return "Semi-Optimistic Semantics";
	}
}