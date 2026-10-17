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
 * The encoding Φ_cf of conflict-free interpretations of k++ADF: variables s_t
 * and s_f for the value of every argument s, and a variable p_rs for every link
 * (r, s), which guesses the value of r in a completion that satisfies (s_t) or
 * refutes (s_f) phi_s. Also provides the clauses φ_{&lt;_i}, Refine_≠ and
 * Refine_&gt;.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 *
 * @author Lars Bengel
 */
final class ConflictFreeEncoding {

	private final SatSolver solver;

	private final List<Argument> arguments;

	private final Map<Argument, Integer> trueVariables = new HashMap<>();

	private final Map<Argument, Integer> falseVariables = new HashMap<>();

	/**
	 * Adds Φ_cf for the given ADF to the solver.
	 *
	 * @param adf    an ADF, which must not be modified while the encoding is used
	 * @param solver the solver the clauses are added to
	 */
	ConflictFreeEncoding(AbstractDialecticalFramework adf, SatSolver solver) {
		this.solver = solver;
		this.arguments = new ArrayList<>(adf.getNodes());
		for (Argument s : arguments) {
			trueVariables.put(s, solver.newVar());
			falseVariables.put(s, solver.newVar());
			solver.add(-trueVariables.get(s), -falseVariables.get(s));
		}
		Map<Argument, Map<Argument, Integer>> links = new HashMap<>();
		for (Argument s : arguments) {
			Map<Argument, Integer> parents = new HashMap<>();
			for (Argument r : adf.getParents(s)) {
				parents.put(r, solver.newVar());
			}
			links.put(s, parents);
		}
		for (Argument s : arguments) {
			int st = trueVariables.get(s), sf = falseVariables.get(s);
			// φ^pr_s
			for (Argument r : adf.getChildren(s)) {
				int p = links.get(r).get(s);
				solver.add(-st, p);
				solver.add(-sf, -p);
			}
			// φ^Γ_s
			Map<Argument, Integer> parents = links.get(s);
			int phi = new TseitinEncoder(solver, q -> parents.get(adf.toArgument(q))).encode(adf.getAcceptanceCondition(s));
			solver.add(-st, phi);
			solver.add(-sf, -phi);
		}
	}

	/** @return the variable s_t */
	int trueVariable(Argument s) {
		return trueVariables.get(s);
	}

	/** @return the variables s_t and s_f of all arguments */
	int[] variables() {
		int[] result = new int[2 * arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			result[2 * i] = trueVariables.get(arguments.get(i));
			result[2 * i + 1] = falseVariables.get(arguments.get(i));
		}
		return result;
	}

	/** @return the literals that fix the interpretation to v */
	int[] assumptions(ThreeValuedInterpretation v) {
		int[] result = new int[2 * arguments.size()];
		for (int i = 0; i < arguments.size(); i++) {
			Argument s = arguments.get(i);
			TruthValue value = v.get(s);
			result[2 * i] = value == TruthValue.TRUE ? trueVariables.get(s) : -trueVariables.get(s);
			result[2 * i + 1] = value == TruthValue.FALSE ? falseVariables.get(s) : -falseVariables.get(s);
		}
		return result;
	}

	/**
	 * Adds φ_{&lt;_i}, which restricts the models to interpretations that are
	 * strictly more informative than v.
	 *
	 * @param v an interpretation that is not two-valued
	 */
	void addMoreInformativeThan(ThreeValuedInterpretation v) {
		List<Integer> undecided = new ArrayList<>();
		for (Argument s : arguments) {
			TruthValue value = v.get(s);
			if (value == TruthValue.TRUE) {
				solver.add(trueVariables.get(s));
			} else if (value == TruthValue.FALSE) {
				solver.add(falseVariables.get(s));
			} else {
				undecided.add(trueVariables.get(s));
				undecided.add(falseVariables.get(s));
			}
		}
		solver.add(undecided.stream().mapToInt(Integer::intValue).toArray());
	}

	/**
	 * Adds Refine_≠, which excludes v.
	 *
	 * @param v an interpretation
	 */
	void addRefineUnequal(ThreeValuedInterpretation v) {
		List<Integer> clause = new ArrayList<>();
		for (Argument s : arguments) {
			TruthValue value = v.get(s);
			if (value == TruthValue.TRUE) {
				clause.add(-trueVariables.get(s));
			} else if (value == TruthValue.FALSE) {
				clause.add(-falseVariables.get(s));
			} else {
				clause.add(trueVariables.get(s));
				clause.add(falseVariables.get(s));
			}
		}
		solver.add(clause.stream().mapToInt(Integer::intValue).toArray());
	}

	/**
	 * Adds Refine_&gt;, which excludes v and all less informative interpretations.
	 *
	 * @param v an interpretation
	 */
	void addRefineLarger(ThreeValuedInterpretation v) {
		List<Integer> clause = new ArrayList<>();
		for (Argument s : arguments) {
			TruthValue value = v.get(s);
			if (value == TruthValue.TRUE) {
				clause.add(falseVariables.get(s));
			} else if (value == TruthValue.FALSE) {
				clause.add(trueVariables.get(s));
			} else {
				clause.add(trueVariables.get(s));
				clause.add(falseVariables.get(s));
			}
		}
		solver.add(clause.stream().mapToInt(Integer::intValue).toArray());
	}

	/**
	 * @param values the values of {@link #variables()}
	 * @return the corresponding interpretation
	 */
	ThreeValuedInterpretation decode(boolean[] values) {
		Set<Argument> satisfied = new HashSet<>(), unsatisfied = new HashSet<>();
		for (int i = 0; i < arguments.size(); i++) {
			if (values[2 * i]) {
				satisfied.add(arguments.get(i));
			} else if (values[2 * i + 1]) {
				unsatisfied.add(arguments.get(i));
			}
		}
		return ThreeValuedInterpretation.of(satisfied, unsatisfied, arguments);
	}

}
