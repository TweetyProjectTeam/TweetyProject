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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.CardinalityConstraint;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * This class presents a sat-encoding-based implementation of the hit-distance 
 * measure of [Grant and Hunter, “Analysing inconsistent information using distance-based
 * measures” Int. J. Approx. Reasoning, 2017]. The hit-distance measure calculates an 
 * interpretation such that the number of distances greater than 0 to the models of each formula is minimal.
 * The value of the inconsistency is than exactly this number of distances.
 * 
 * <br> Note: This implementation may produce different results than 
 * {@link net.sf.tweety.logics.commons.analysis.DHitInconsistencyMeasure} in a few cases 
 * because the latter implements a slightly different interpretation of the measure.
 * 
 * @author Anna Gessler
 */
public class DHitSatInconsistencyMeasure extends SatBasedInconsistencyMeasure {
	/**
	 * Create a new DHitSatInconsistencyMeasure with the given SAT solver.
	 * 
	 * @param solver
	 */
	public DHitSatInconsistencyMeasure(SatSolver solver) {
		super(solver);
		maxIsInfinity = true;
	}

	/**
	 * Create a new DHitSatInconsistencyMeasure with the default SAT solver.
	 */
	public DHitSatInconsistencyMeasure() {
		super();
		maxIsInfinity = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#
	 * inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PlFormula> kb) {
		int max = kb.size();
		double result = super.binarySearchValue(kb, 0, max);
		if (result == (Double.POSITIVE_INFINITY)) 
			return (double) max; 
		return result;
	}

	@Override
	public PlBeliefSet getSATEncoding(Collection<PlFormula> kb, int upper_bound) {
		if (kb.isEmpty())
			return new PlBeliefSet();
		if (upper_bound == 0)
			return (PlBeliefSet) kb;
		if (upper_bound == kb.size())
			return new PlBeliefSet();

		PlBeliefSet encoding = new PlBeliefSet();
		int hi = 0;
		Set<Proposition> hits = new HashSet<Proposition>();
		for (PlFormula f : kb) {
			Proposition hit = new Proposition("HIT_" + hi);
			Disjunction d = new Disjunction();
			d.add(f, hit);
			encoding.add(d);
			hits.add(hit);
			hi++;
		}
		
		CardinalityConstraint c = new CardinalityConstraint(hits, upper_bound);
		PlBeliefSet cardinality_constraints = c.getSatEncoding();
		encoding.addAll(cardinality_constraints);
		return encoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "hit-distance (SAT-based)";
	}

}
