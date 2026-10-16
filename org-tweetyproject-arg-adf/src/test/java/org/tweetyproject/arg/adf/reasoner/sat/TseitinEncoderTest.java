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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tweetyproject.arg.adf.AdfTestSupport.randomFormula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.adf.BruteForceOracle;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

class TseitinEncoderTest {

	private static final List<String> ATOMS = List.of("p0", "p1", "p2", "p3");

	@Test
	void literalIsEquivalentToFormula() {
		Random rng = new Random(1);
		List<PlFormula> formulas = new ArrayList<>(List.of(new Conjunction(), new Disjunction(), new ExclusiveDisjunction()));
		for (int i = 0; i < 300; i++) {
			formulas.add(randomFormula(rng, ATOMS, 1 + rng.nextInt(4)));
		}
		for (PlFormula formula : formulas) {
			try (SatSolver solver = new Sat4jIncrementalSolver()) {
				Map<String, Integer> variables = new HashMap<>();
				for (String atom : ATOMS) {
					variables.put(atom, solver.newVar());
				}
				int literal = new TseitinEncoder(solver, p -> variables.get(p.getName())).encode(formula);
				for (int mask = 0; mask < 1 << ATOMS.size(); mask++) {
					int[] assumptions = new int[ATOMS.size() + 1];
					Set<Argument> satisfied = new HashSet<>(), unsatisfied = new HashSet<>();
					for (int k = 0; k < ATOMS.size(); k++) {
						boolean value = (mask >> k & 1) == 1;
						assumptions[k] = value ? variables.get(ATOMS.get(k)) : -variables.get(ATOMS.get(k));
						(value ? satisfied : unsatisfied).add(new Argument(ATOMS.get(k)));
					}
					ThreeValuedInterpretation w = ThreeValuedInterpretation.of(satisfied, unsatisfied, ATOMS.stream().map(Argument::new).toList());
					boolean expected = BruteForceOracle.classical(w, formula) == TruthValue.TRUE;
					assumptions[ATOMS.size()] = literal;
					assertEquals(expected, solver.satisfiable(assumptions), formula + " under " + w);
					assumptions[ATOMS.size()] = -literal;
					assertEquals(!expected, solver.satisfiable(assumptions), "negation of " + formula + " under " + w);
				}
			}
		}
	}

}
