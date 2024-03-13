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
package org.tweetyproject.logics.pcl.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.logics.commons.analysis.AbstractBeliefSetConsistencyTester;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.math.GeneralMathException;
import org.tweetyproject.math.opt.rootFinder.OptimizationRootFinder;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.FloatVariable;
import org.tweetyproject.math.term.IntegerConstant;
import org.tweetyproject.math.term.Term;
import org.tweetyproject.math.term.Variable;

/**
 * This class is capable of checking whether a given conditional knowledge base
 * is consistent by searching for the root of some equivalent multi-dimensional function.
 *
 * @author Matthias Thimm
 */
public class PclDefaultConsistencyTester extends AbstractBeliefSetConsistencyTester<ProbabilisticConditional> {

	private OptimizationRootFinder rootFinder;

	public PclDefaultConsistencyTester(OptimizationRootFinder rootFinder) {
		this.rootFinder = rootFinder;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.AbstractBeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<ProbabilisticConditional> formulas) {
		PclBeliefSet beliefSet = new PclBeliefSet(formulas);
		if(beliefSet.isEmpty()) return true;
		// Create variables for the probability of each possible world and
		// create a multi-dimensional function that has a root iff the belief base is consistent
		List<Term> functions = new ArrayList<Term>();
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PlSignature)beliefSet.getMinimalSignature());
		Map<PossibleWorld,Variable> worlds2vars = new HashMap<PossibleWorld,Variable>();
		int i = 0;
		Term normConstraint = null;
		for(PossibleWorld w: worlds){
			FloatVariable var = new FloatVariable("w" + i++,0,1);
			worlds2vars.put(w, var);
			if(normConstraint == null)
				normConstraint = var;
			else normConstraint = normConstraint.add(var);
		}
		normConstraint = normConstraint.add(new IntegerConstant(-1));
		functions.add(normConstraint);
		// add constraints implied by the conditionals
		for(ProbabilisticConditional c: beliefSet){
			Term leftSide = null;
			Term rightSide = null;
			if(c.isFact()){
				for(PossibleWorld w: worlds)
					if(w.satisfies(c.getConclusion())){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
				rightSide = new FloatConstant(c.getProbability().getValue());
			}else{
				PlFormula body = c.getPremise().iterator().next();
				PlFormula head_and_body = c.getConclusion().combineWithAnd(body);
				for(PossibleWorld w: worlds){
					if(w.satisfies(head_and_body)){
						if(leftSide == null)
							leftSide = worlds2vars.get(w);
						else leftSide = leftSide.add(worlds2vars.get(w));
					}
					if(w.satisfies(body)){
						if(rightSide == null)
							rightSide = worlds2vars.get(w);
						else rightSide = rightSide.add(worlds2vars.get(w));
					}
				}
				if(rightSide == null)
					rightSide = new FloatConstant(0);
				else rightSide = rightSide.mult(new FloatConstant(c.getProbability().getValue()));
			}
			if(leftSide == null)
				leftSide = new FloatConstant(0);
			if(rightSide == null)
				rightSide = new FloatConstant(0);
			functions.add(leftSide.minus(rightSide));
		}
		// Search for a root of "functions" using OpenOpt
		Map<Variable,Term> startingPoint = new HashMap<Variable,Term>();
		for(PossibleWorld w: worlds)
			startingPoint.put(worlds2vars.get(w), new IntegerConstant(1));
		try {
			this.rootFinder.randomRoot(functions,startingPoint);
		} catch (GeneralMathException e) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(org.tweetyproject.Formula)
	 */
	@Override
	public boolean isConsistent(ProbabilisticConditional formula) {
		PclBeliefSet bs = new PclBeliefSet();
		bs.add(formula);
		return this.isConsistent(bs);
	}
}
