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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class implements a SAT encoding of the hitting set inconsistency measure,
 * originally proposed in [Thimm. "Stream-based inconsistency measurement". 2014].
 * <br>The inconsistency value is defined as one plus the minimal number of
 * interpretations s.t. every formula of the belief set is satisfied by at
 * least one interpretation. This is equivalent to the cardinality of a minimal
 * partitioning of the knowledge base such that each partition is consistent.
 * 
 * @author Anna Gessler
 */
public class HsSatInconsistencyMeasure extends SatBasedInconsistencyMeasure {
	/**
	 * Create a new HsSatInconsistencyMeasure with the given SAT solver.
	 * 
	 * @param solver
	 */
	public HsSatInconsistencyMeasure(SatSolver solver) {
		super(solver);
		this.maxIsInfinity = true;
		this.offset = 1;
	}

	/**
	 * Create a new HsSatInconsistencyMeasure with the default SAT solver.
	 */
	public HsSatInconsistencyMeasure() {
		super();
		this.maxIsInfinity = true;
		this.offset = 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure#
	 * inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PlFormula> kb) {
		if (kb.isEmpty())
			return 0.0;
		return super.binarySearchValue(kb, 1, kb.size());
	}

	@Override
	protected PlBeliefSet getSATEncoding(Collection<PlFormula> kb, int upper_bound) {
		if (upper_bound == 1)
			return (PlBeliefSet) kb;
		
		PlBeliefSet encoding = new PlBeliefSet();
		for (int i = 0; i < upper_bound; i++) {
			List<PlFormula> list = Arrays.asList(kb.toArray(new PlFormula[0]));
			for (int j = 0; j < kb.size(); j++) {
				// Create alibi versions for all formulas
				PlFormula fnew = list.get(j).clone();
				for (Proposition p : list.get(j).getAtoms()) {
					Proposition p_i = new Proposition(p.getName() + i);
					for (int ai = 0; ai < list.get(j).numberOfOccurrences(p); ai++) {
						fnew = fnew.replace(p, p_i, 1);
					}
				}
				// Create clauses that represent membership of a formula in a block
				// The alibis of all formulas inside each block are set to true
				Proposition pij = new Proposition("p_" + i + "_" + j);
				encoding.add(new Implication(pij,fnew));
				
				// every formula has to be a member of at least one partition
				Set<Proposition> exactly_one_of = new HashSet<Proposition>();
				for (int xi = 0; xi < upper_bound; xi++) {
					pij = new Proposition("p_" + xi + "_" + j);
					exactly_one_of.add(pij);
				}
				encoding.add(new Disjunction(exactly_one_of));
			}
		}
		return encoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "HS (SAT-based)";
	}

}
