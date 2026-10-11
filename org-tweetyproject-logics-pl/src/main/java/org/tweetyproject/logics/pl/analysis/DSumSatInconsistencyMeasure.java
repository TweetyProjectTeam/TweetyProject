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
package org.tweetyproject.logics.pl.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.util.CardinalityConstraintEncoder;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class presents a sat-encoding-based implementation of the sum-distance 
 * measure of [Grant and Hunter, “Analysing incThis measure seeks an interpretation
 * I such that the the sum of the distances between every formula of the knowledge base and I is minimal. 
 * The value of the inconsistency is than exactly this value.
 * 
 * @author Anna Gessler
 */
public class DSumSatInconsistencyMeasure extends SatBasedInconsistencyMeasure {
	/**
	 * Create a new DSumSatInconsistencyMeasure with the given SAT solver.
	 * 
	 * @param solver  some SAT solver
	 */
	public DSumSatInconsistencyMeasure(SatSolver solver) {
		super(solver);
		maxIsInfinity = true;
	}

	/**
	 * Create a new DSumSatInconsistencyMeasure with the default SAT solver.
	 */
	public DSumSatInconsistencyMeasure() {
		super();
		maxIsInfinity = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure#
	 * inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PlFormula> kb) {
		return super.binarySearchValue(kb, 0, ((PlBeliefSet) kb).getMinimalSignature().size() * kb.size());
	}

	@Override
	public PlBeliefSet getSATEncoding(Collection<PlFormula> kb, int upper_bound) {
		if (kb.isEmpty())
			return new PlBeliefSet();
		if (upper_bound == 0)
			return (PlBeliefSet) kb;

		PlBeliefSet encoding = new PlBeliefSet();
		List<PlFormula> list = Arrays.asList(kb.toArray(new PlFormula[0]));
		for (int i = 0; i < kb.size(); i++) {
			PlFormula fnew = copyOf(list.get(i), i);
			encoding.add(fnew);
		}
		
		List<Proposition> atoms = new ArrayList<Proposition>((new PlBeliefSet(kb)).getSignature().toCollection());
		for (int i = 0; i < atoms.size(); i++) {
			for (int j = 0; j < kb.size(); j++) {
				Proposition at = atoms.get(i);

				Disjunction right = new Disjunction();
				right.add(copyOf(at, "s"));
				Conjunction c = new Conjunction();
				c.add(auxiliary("j" + i + "_" + j));
				c.add(new Negation(copyOf(at, "s")));
				right.add(c);
				Implication imp_p = new Implication(copyOf(at, j), right);

				right = new Disjunction();
				right.add(new Negation(copyOf(at, "s")));
				c = new Conjunction();
				c.add(auxiliary("j" + i + "_" + j));
				c.add(copyOf(at, "s"));
				right.add(c);
				Implication imp_n = new Implication(new Negation(copyOf(at, j)), right);

				encoding.add(imp_p);
				encoding.add(imp_n);
			}
		}

		Set<Proposition> inverters = new HashSet<Proposition>();
		for (int j = 0; j < kb.size(); j++) 
			for (int i = 0; i < atoms.size(); i++) 
				inverters.add(auxiliary("j" + i + "_" + j));
		
		CardinalityConstraintEncoder c = new CardinalityConstraintEncoder(inverters, upper_bound);
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
		return "sum-distance (SAT-based)";
	}

}
