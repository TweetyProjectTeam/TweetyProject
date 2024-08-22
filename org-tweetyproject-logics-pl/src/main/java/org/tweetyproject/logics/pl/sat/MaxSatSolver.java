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
package org.tweetyproject.logics.pl.sat;

import java.util.Collection;
import java.util.Map;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * 
 * Provides a generic class for implementing MaxSAT solvers, i.e. solvers that get as input a set
 * of hard constraints (=propositional formulas that need to be satisfied) and a set of soft constraints
 * (=clauses with weights) whose satisfaction should be maximized (=sum of weights should be maximized). 
 * 
 * @author Matthias Thimm
 *
 */
public abstract class MaxSatSolver extends SatSolver{
	
	/**
	 * Returns an interpretation with maximal weight on the soft constraints
	 * (or null if the hard constraints are not satisfiable) 
	 * @param hardConstraints a set of propositional formulas
	 * @param softConstraints a map mapping clauses to weights (if there is a formula, which
	 *    is not a clause, i.e. a disjunction of literals), an exception is thrown.
	 * @return an interpretation with maximal weight on the soft constraints
	 * (or null if the hard constraints are not satisfiable) 
	 */
	public abstract Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints);

	@Override
	public abstract boolean isSatisfiable(Collection<PlFormula> formulas);
	
	/**
	 * Returns the cost of the given interpretation, i.e. the sum of the weights
	 * of all violated soft constraints. If the interpretation does not satisfy the
	 * hard constraints -1 is returned; 
	 * @param interpretation some interpretation 
	 * @param hardConstraints a set of hard constraints
	 * @param softConstraints a set of soft constraints
	 * @return the cost of the interpretation
	 */
	public static int costOf(Interpretation<PlBeliefSet, PlFormula> interpretation, Collection<PlFormula> hardConstraints, Map<PlFormula, Integer> softConstraints){
		for(PlFormula f: hardConstraints)
			if(!interpretation.satisfies(f))
				return -1;
		int costs = 0;
		for(PlFormula f: softConstraints.keySet())
			if(!interpretation.satisfies(f))
				costs += softConstraints.get(f);
		return costs;
	}

    /** Default Constructor */
    public MaxSatSolver(){}
}
