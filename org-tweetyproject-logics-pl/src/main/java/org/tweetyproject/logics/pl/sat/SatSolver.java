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
import java.util.HashSet;

import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester;
import org.tweetyproject.logics.commons.analysis.ConsistencyWitnessProvider;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Abstract class for specifying SAT solvers. Also includes methods to convert
 * knowledge bases into the Dimacs format.
 *
 * @author Matthias Thimm
 */
public abstract class SatSolver implements BeliefSetConsistencyTester<PlFormula>, ConsistencyWitnessProvider<PlBeliefSet, PlFormula> {

	/** The default SAT solver. */
	private static SatSolver defaultSatSolver = null;

	/**
	 * Sets the default SAT solver.
	 *
	 * @param solver some SAT solver
	 */
	public static void setDefaultSolver(SatSolver solver) {
		SatSolver.defaultSatSolver = solver;
	}

	/**
	 * Returns "true" if a default SAT solver is configured.
	 *
	 * @return "true" if a default SAT solver is configured.
	 */
	public static boolean hasDefaultSolver() {
		return SatSolver.defaultSatSolver != null;
	}

	/**
	 * Returns the default SAT solver.<br>
	 * <br>
	 * If a default SAT solver has been configured this solver is returned by this
	 * method. If no default solver is configured, the Sat4j solver
	 * (<code>org.tweetyproject.pl.sat.Sat4jSolver</code>) is returned as a fallback and
	 * a message is printed to stderr pointing out that no default SAT solver is
	 * configured.
	 *
	 * @return the default SAT solver.
	 */
	public static SatSolver getDefaultSolver() {
		if (SatSolver.defaultSatSolver != null)
			return SatSolver.defaultSatSolver;
		System.err.println(
				"No default SAT solver configured, using " + "'Sat4jSolver' with default settings as fallback. "
						+ "It is strongly advised that a default SAT solver is manually configured, see "
						+ "'http://tweetyproject.org/doc/sat-solvers.html' " + "for more information.");
		return new Sat4jSolver();
	}

	/**
	 * If the collection of formulas is consistent this method returns some model of
	 * it or, if it is inconsistent, null.
	 *
	 * @return some model of the formulas or null.
	 */
	public abstract Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas);

	/**
	 * Checks whether the given set of formulas is satisfiable.
	 *
	 * @param formulas a set of formulas.
	 * @return "true" if the set is consistent.
	 */
	public abstract boolean isSatisfiable(Collection<PlFormula> formulas);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent
	 * (org.tweetyproject.commons.BeliefSet)
	 */
	@Override
	public boolean isConsistent(BeliefSet<PlFormula, ?> beliefSet) {
		return this.isSatisfiable(beliefSet);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent
	 * (java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PlFormula> formulas) {
		return this.isSatisfiable(formulas);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent
	 * (org.tweetyproject.commons.Formula)
	 */
	@Override
	public boolean isConsistent(PlFormula formula) {
		Collection<PlFormula> formulas = new HashSet<PlFormula>();
		formulas.add(formula);
		return this.isSatisfiable(formulas);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.logics.commons.analysis.ConsistencyWitnessProvider#getWitness(
	 * org.tweetyproject.commons.Formula)
	 */
	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(PlFormula formula) {
		Collection<PlFormula> f = new HashSet<PlFormula>();
		f.add(formula);
		return this.getWitness(f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.logics.commons.analysis.ConsistencyWitnessProvider#getWitness(
	 * org.tweetyproject.commons.BeliefSet)
	 */
	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(BeliefSet<PlFormula, ?> bs) {
		return this.getWitness((Collection<PlFormula>) bs);
	}

	/**
	 *
	 * Return whether the solve ris installed
	 * @return whether the solve ris installed
	 */
	public abstract boolean isInstalled();

    /** Default Constructor */
    public SatSolver(){}
}
