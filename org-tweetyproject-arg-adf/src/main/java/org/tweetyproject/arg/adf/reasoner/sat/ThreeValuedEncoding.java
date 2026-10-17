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
import java.util.function.IntSupplier;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.Link;
import org.tweetyproject.arg.adf.syntax.LinkType;
import org.tweetyproject.arg.adf.util.TseitinEncoder;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.sat.SatSolver;

/**
 * The encoding of three-valued interpretations of k++ADF: variables s_t and
 * s_f for the value of every argument s, and a variable p_rs for every link
 * (r, s), which represents the value of r in a completion considered for
 * phi_s. The clauses of the conflict-free, bipolar and k-bipolar encodings are
 * added separately.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 *
 * @author Lars Bengel
 */
final class ThreeValuedEncoding {

	private final AbstractDialecticalFramework adf;

	private final SatSolver solver;

	private final List<Argument> arguments;

	private final Map<Argument, Integer> trueVariables = new HashMap<>();

	private final Map<Argument, Integer> falseVariables = new HashMap<>();

	/** the variables p_rs, by s and r */
	private final Map<Argument, Map<Argument, Integer>> links = new HashMap<>();

	/** supplies a literal fixed to true, shared by all encoders of this encoding */
	private final IntSupplier top;

	/**
	 * Adds the variables, the clauses ¬s_t ∨ ¬s_f, and the clauses that fix p_rs
	 * to the value of r if r is decided.
	 *
	 * @param adf    an ADF, which must not be modified while the encoding is used
	 * @param solver the solver the clauses are added to
	 */
	ThreeValuedEncoding(AbstractDialecticalFramework adf, SatSolver solver) {
		this.adf = adf;
		this.solver = solver;
		this.top = TseitinEncoder.trueLiteral(solver);
		this.arguments = new ArrayList<>(adf.getNodes());
		for (Argument s : arguments) {
			trueVariables.put(s, solver.newVar());
			falseVariables.put(s, solver.newVar());
			solver.add(-trueVariables.get(s), -falseVariables.get(s));
		}
		for (Argument s : arguments) {
			Map<Argument, Integer> parents = new HashMap<>();
			for (Argument r : adf.getParents(s)) {
				parents.put(r, solver.newVar());
			}
			links.put(s, parents);
		}
		for (Argument s : arguments) {
			for (Argument r : adf.getChildren(s)) {
				int p = links.get(r).get(s);
				solver.add(-trueVariables.get(s), p);
				solver.add(-falseVariables.get(s), -p);
			}
		}
	}

	/**
	 * Adds the conflict-free clauses of all arguments.
	 */
	void addConflictFree() {
		for (Argument s : arguments) {
			addConflictFree(s);
		}
	}

	/**
	 * Adds the conflict-free clauses of s: phi_s is true (false) under the values p_rs if s_t (s_f).
	 *
	 * @param s an argument
	 */
	void addConflictFree(Argument s) {
		Map<Argument, Integer> parents = links.get(s);
		int phi = new TseitinEncoder(solver, q -> parents.get(adf.toArgument(q)), top).encode(adf.getAcceptanceCondition(s));
		solver.add(-trueVariables.get(s), phi);
		solver.add(-falseVariables.get(s), -phi);
	}

	/**
	 * Adds the bipolar clauses: for an attacking (supporting) link (r, s), p_rs takes the value
	 * of r that is worst for s_t and s_f unless r is decided. Redundant and
	 * dependent links are skipped.
	 *
	 * @param types the types of the links
	 */
	void addBipolar(Map<Link, LinkType> types) {
		for (Map.Entry<Link, LinkType> entry : types.entrySet()) {
			Argument r = entry.getKey().getParent(), s = entry.getKey().getChild();
			int p = links.get(s).get(r);
			int st = trueVariables.get(s), sf = falseVariables.get(s);
			int rt = trueVariables.get(r), rf = falseVariables.get(r);
			if (entry.getValue() == LinkType.ATTACKING) {
				solver.add(-st, rf, p);
				solver.add(-sf, rt, -p);
			} else if (entry.getValue() == LinkType.SUPPORTING) {
				solver.add(-st, rt, -p);
				solver.add(-sf, rf, p);
			}
		}
	}

	/**
	 * Adds the k-bipolar encoding of s in place of its conflict-free clauses: for
	 * every assignment of the dependent parents, phi_s under this assignment and the values p_rs of
	 * the other parents is true (false) if s_t (s_f) and no dependent parent is
	 * decided otherwise.
	 *
	 * @param s         an argument
	 * @param dependent the dependent parents of s, fewer than 31
	 */
	void addKBipolar(Argument s, List<Argument> dependent) {
		Map<Argument, Integer> parents = links.get(s);
		for (int j = 0; j < (1 << dependent.size()); j++) {
			Map<Argument, Integer> values = new HashMap<>(parents);
			int[] whenTrue = new int[dependent.size() + 2], whenFalse = new int[dependent.size() + 2];
			for (int i = 0; i < dependent.size(); i++) {
				Argument r = dependent.get(i);
				boolean value = (j & (1 << i)) != 0;
				values.put(r, value ? top.getAsInt() : -top.getAsInt());
				whenTrue[i] = value ? falseVariables.get(r) : trueVariables.get(r);
				whenFalse[i] = whenTrue[i];
			}
			int phi = new TseitinEncoder(solver, q -> values.get(adf.toArgument(q)), top).encode(adf.getAcceptanceCondition(s));
			whenTrue[dependent.size()] = -trueVariables.get(s);
			whenTrue[dependent.size() + 1] = phi;
			whenFalse[dependent.size()] = -falseVariables.get(s);
			whenFalse[dependent.size() + 1] = -phi;
			solver.add(whenTrue);
			solver.add(whenFalse);
		}
	}

	/**
	 * Adds the bipolar clauses and, for every argument s, the k-bipolar encoding
	 * over the dependent parents of s that are undecided in v, or the
	 * conflict-free clauses of s if there are none or the k-bipolar encoding is
	 * not used. Requires that the models are at least as informative as v.
	 *
	 * @param types    the types of the links
	 * @param v        an interpretation
	 * @param kBipolar whether the k-bipolar encoding is used
	 */
	void addAdmissible(Map<Link, LinkType> types, ThreeValuedInterpretation v, boolean kBipolar) {
		addBipolar(types);
		Map<Argument, List<Argument>> dependent = new HashMap<>();
		for (Map.Entry<Link, LinkType> entry : types.entrySet()) {
			Argument r = entry.getKey().getParent(), s = entry.getKey().getChild();
			if (entry.getValue() == LinkType.DEPENDENT && v.get(r) == TruthValue.UNDECIDED) {
				dependent.computeIfAbsent(s, a -> new ArrayList<>()).add(r);
			}
		}
		for (Argument s : arguments) {
			if (kBipolar && dependent.containsKey(s)) {
				addKBipolar(s, dependent.get(s));
			} else {
				addConflictFree(s);
			}
		}
	}

	/**
	 * Adds the clauses that complete interpretations satisfy in addition to the
	 * admissible ones: for every argument s, two copies of phi_s over fresh link
	 * variables, in which every parent takes the value of the interpretation if
	 * decided, and otherwise an attacking (supporting) parent is t (f) in the
	 * first and f (t) in the second copy. If the first copy is true, s_t holds;
	 * if the second copy is false, s_f holds.
	 *
	 * @param types the types of the links
	 */
	void addComplete(Map<Link, LinkType> types) {
		for (Argument s : arguments) {
			Map<Argument, Integer> first = new HashMap<>(), second = new HashMap<>();
			for (Argument r : adf.getParents(s)) {
				int q1 = solver.newVar(), q2 = solver.newVar();
				first.put(r, q1);
				second.put(r, q2);
				int rt = trueVariables.get(r), rf = falseVariables.get(r);
				solver.add(-rt, q1);
				solver.add(-rf, -q1);
				solver.add(-rt, q2);
				solver.add(-rf, -q2);
				LinkType type = types.get(new Link(r, s));
				if (type == LinkType.ATTACKING) {
					solver.add(rf, q1);
					solver.add(rt, -q2);
				} else if (type == LinkType.SUPPORTING) {
					solver.add(rt, -q1);
					solver.add(rf, q2);
				}
			}
			int phi1 = new TseitinEncoder(solver, q -> first.get(adf.toArgument(q)), top).encode(adf.getAcceptanceCondition(s));
			int phi2 = new TseitinEncoder(solver, q -> second.get(adf.toArgument(q)), top).encode(adf.getAcceptanceCondition(s));
			solver.add(-phi1, trueVariables.get(s));
			solver.add(phi2, falseVariables.get(s));
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
	 * Restricts the models to interpretations that are strictly more informative
	 * than v.
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
	 * Excludes v from the models.
	 *
	 * @param v an interpretation
	 */
	void exclude(ThreeValuedInterpretation v) {
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
	 * Excludes v and all less informative interpretations from the models.
	 *
	 * @param v an interpretation
	 */
	void excludeLessInformative(ThreeValuedInterpretation v) {
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
