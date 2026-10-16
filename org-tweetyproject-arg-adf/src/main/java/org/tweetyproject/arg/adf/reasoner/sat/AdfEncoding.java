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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.sat.SatSolver;

/**
 * The encoding of an ADF in a {@link SatSolver}: a variable x_a for the
 * (two-valued) truth value of every argument a, and the Tseitin literal of
 * every acceptance condition phi_a over these variables, encoded on first use.
 *
 * @author Lars Bengel
 */
final class AdfEncoding {

	private final AbstractDialecticalFramework adf;

	private final List<Argument> arguments;

	private final Map<Argument, Integer> variables = new HashMap<>();

	private final Map<Argument, Integer> conditions = new HashMap<>();

	private final TseitinEncoder encoder;

	/**
	 * @param adf    an ADF, which must not be modified while the encoding is used
	 * @param solver the solver the clauses are added to
	 */
	AdfEncoding(AbstractDialecticalFramework adf, SatSolver solver) {
		this.adf = adf;
		this.arguments = new ArrayList<>(adf.getNodes());
		for (Argument a : arguments) {
			variables.put(a, solver.newVar());
		}
		this.encoder = new TseitinEncoder(solver, p -> variables.get(adf.toArgument(p)));
	}

	List<Argument> arguments() {
		return arguments;
	}

	/** @return the variable x_a */
	int variable(Argument a) {
		return variables.get(a);
	}

	/** @return the variables x_a in the order of {@link #arguments()} */
	int[] variables() {
		int[] result = new int[arguments.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = variables.get(arguments.get(i));
		}
		return result;
	}

	/** @return a literal equivalent to phi_a over the variables x_b */
	int condition(Argument a) {
		Integer literal = conditions.get(a);
		if (literal == null) {
			literal = encoder.encode(adf.getAcceptanceCondition(a));
			conditions.put(a, literal);
		}
		return literal;
	}

	/** @return the literals that fix the decided arguments of v */
	int[] assumptions(ThreeValuedInterpretation v) {
		List<Integer> result = new ArrayList<>();
		for (Argument a : arguments) {
			TruthValue value = v.get(a);
			if (value.isDecided()) {
				result.add(value == TruthValue.TRUE ? variables.get(a) : -variables.get(a));
			}
		}
		return result.stream().mapToInt(Integer::intValue).toArray();
	}

	/**
	 * @param values the values of {@link #variables()}
	 * @return the corresponding two-valued interpretation
	 */
	ThreeValuedInterpretation decode(boolean[] values) {
		Set<Argument> satisfied = new HashSet<>(), unsatisfied = new HashSet<>();
		for (int i = 0; i < values.length; i++) {
			(values[i] ? satisfied : unsatisfied).add(arguments.get(i));
		}
		return ThreeValuedInterpretation.of(satisfied, unsatisfied, arguments);
	}

}
