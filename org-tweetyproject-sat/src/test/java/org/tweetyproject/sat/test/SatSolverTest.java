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
package org.tweetyproject.sat.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.lingeling.LingelingSatSolver;
import org.tweetyproject.sat.minisat.MinisatSatSolver;
import org.tweetyproject.sat.picosat.PicosatSatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

class SatSolverTest {

	static Stream<Arguments> solvers() {
		return Stream.of(Arguments.of("Sat4j", (Supplier<SatSolver>) Sat4jIncrementalSolver::new),
				Arguments.of("MiniSat", (Supplier<SatSolver>) MinisatSatSolver::new),
				Arguments.of("PicoSAT", (Supplier<SatSolver>) PicosatSatSolver::new),
				Arguments.of("Lingeling", (Supplier<SatSolver>) LingelingSatSolver::new));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("solvers")
	void tautology(String name, Supplier<SatSolver> solvers) {
		try (SatSolver solver = solvers.get()) {
			int a = solver.newVar();
			solver.add(a, -a);
			assertTrue(solver.satisfiable());
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("solvers")
	void unsatisfiable(String name, Supplier<SatSolver> solvers) {
		try (SatSolver solver = solvers.get()) {
			int a = solver.newVar();
			solver.add(a);
			solver.add(-a);
			assertFalse(solver.satisfiable());
			assertNull(solver.witness(new int[0]));
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("solvers")
	void witness(String name, Supplier<SatSolver> solvers) {
		try (SatSolver solver = solvers.get()) {
			int a = solver.newVar(), b = solver.newVar();
			solver.add(a);
			solver.add(-b);
			assertArrayEquals(new boolean[] { true, false }, solver.witness(new int[] { a, b }));
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("solvers")
	void witnessUnderAssumptions(String name, Supplier<SatSolver> solvers) {
		try (SatSolver solver = solvers.get()) {
			int a = solver.newVar(), b = solver.newVar();
			solver.add(a, b);
			assertArrayEquals(new boolean[] { false, true }, solver.witness(new int[] { a, b }, new int[] { -a }));
			assertArrayEquals(new boolean[] { true, false }, solver.witness(new int[] { a, b }, new int[] { -b }));
			assertNull(solver.witness(new int[] { a, b }, new int[] { -a, -b }));
			assertTrue(solver.satisfiable());
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("solvers")
	void closeTwice(String name, Supplier<SatSolver> solvers) {
		SatSolver solver = solvers.get();
		solver.close();
		solver.close();
	}

}
