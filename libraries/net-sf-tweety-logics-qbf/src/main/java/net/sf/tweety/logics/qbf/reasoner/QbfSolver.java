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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.qbf.reasoner;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Abstract QBF sat solver to be implemented by concrete solvers.
 * 
 * @author Anna Gessler
 *
 */
public abstract class QbfSolver implements BeliefSetConsistencyTester<PlFormula> {
	/**
	 * Shell for running solvers.
	 */
	protected Shell bash;
	
	/**
	 * Checks whether the given set of formulas is satisfiable.
	 * @param formulas a set of formulas.
	 * @return "true" if the set is consistent.
	 */
	public abstract boolean isSatisfiable(Collection<PlFormula> formulas);

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(net.sf.tweety.commons.BeliefSet)
	 */
	@Override
	public boolean isConsistent(BeliefSet<PlFormula,?> beliefSet) {
		return this.isSatisfiable(beliefSet);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PlFormula> formulas) {
		return this.isSatisfiable(formulas);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(net.sf.tweety.commons.Formula)
	 */
	@Override
	public boolean isConsistent(PlFormula formula) {
		Collection<PlFormula> formulas = new HashSet<PlFormula>();
		formulas.add(formula);
		return this.isSatisfiable(formulas);
	}
}
