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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.semantics.CharacteristicOperator;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * SAT-based variant of the {@link CharacteristicOperator}: Γ_D(v)(a) is t if
 * phi_a has no falsifying completion of v, f if it has no satisfying one, and
 * u otherwise. Both checks are calls to one incremental solver, with the
 * decided arguments of v as assumptions. As in the brute-force variant, the
 * calls are skipped if the Kleene value of phi_a under v is already decided.
 * The operator works on a copy of the ADF and must be closed to release the
 * solver.
 *
 * @author Lars Bengel
 */
public final class SatCharacteristicOperator implements AutoCloseable {

	private final AbstractDialecticalFramework adf;

	private final SatSolver solver;

	private final AdfEncoding encoding;

	private final Map<Argument, PlFormula> conditions = new HashMap<>();

	/**
	 * Creates the operator with the {@link Sat4jIncrementalSolver}.
	 *
	 * @param adf an ADF
	 */
	public SatCharacteristicOperator(AbstractDialecticalFramework adf) {
		this(adf, Sat4jIncrementalSolver::new);
	}

	/**
	 * @param adf     an ADF
	 * @param solvers creates the solver used by this operator
	 */
	public SatCharacteristicOperator(AbstractDialecticalFramework adf, Supplier<SatSolver> solvers) {
		this.adf = new AbstractDialecticalFramework(adf);
		this.solver = solvers.get();
		this.encoding = new AdfEncoding(this.adf, solver);
	}

	/**
	 * @param v an interpretation over the arguments of the ADF
	 * @return Γ_D(v)
	 * @throws IllegalArgumentException if the domain of v differs from the arguments of the ADF
	 */
	public ThreeValuedInterpretation apply(ThreeValuedInterpretation v) {
		requireDomain(v);
		int[] assumptions = encoding.assumptions(v);
		Map<Argument, TruthValue> result = new LinkedHashMap<>();
		for (Argument a : encoding.arguments()) {
			result.put(a, gamma(v, assumptions, a));
		}
		return ThreeValuedInterpretation.of(result);
	}

	/**
	 * @param v        an interpretation over the arguments of the ADF
	 * @param argument an argument of the ADF
	 * @return Γ_D(v)(a)
	 * @throws IllegalArgumentException if the domain of v differs from the
	 *                                  arguments of the ADF or the argument is unknown
	 */
	public TruthValue apply(ThreeValuedInterpretation v, Argument argument) {
		requireDomain(v);
		if (!adf.contains(argument)) {
			throw new IllegalArgumentException("Not an argument of the ADF: " + argument);
		}
		return gamma(v, encoding.assumptions(v), argument);
	}

	private TruthValue gamma(ThreeValuedInterpretation v, int[] assumptions, Argument a) {
		TruthValue kleene = v.evaluate(conditions.computeIfAbsent(a, adf::getAcceptanceCondition));
		if (kleene.isDecided()) {
			return kleene;
		}
		int condition = encoding.condition(a);
		int[] extended = Arrays.copyOf(assumptions, assumptions.length + 1);
		extended[assumptions.length] = -condition;
		boolean canBeFalse = solver.satisfiable(extended);
		extended[assumptions.length] = condition;
		boolean canBeTrue = solver.satisfiable(extended);
		if (canBeTrue && canBeFalse) {
			return TruthValue.UNDECIDED;
		}
		return canBeTrue ? TruthValue.TRUE : TruthValue.FALSE;
	}

	private void requireDomain(ThreeValuedInterpretation v) {
		if (!v.getDomain().equals(adf.getNodes())) {
			throw new IllegalArgumentException("The interpretation is not defined over the arguments of the ADF.");
		}
	}

	@Override
	public void close() {
		solver.close();
	}

}
