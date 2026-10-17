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
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;
import org.tweetyproject.sat.SatSolver;

/**
 * Tseitin encoding of propositional formulas into the clauses of a
 * {@link SatSolver}. Every formula is represented by a literal that is
 * equivalent to it in every model of the added clauses, so the literal can be
 * used under both polarities. Subformulas are encoded once per encoder. An
 * n-ary exclusive disjunction is true iff an odd number of its operands is
 * true.
 *
 * @author Lars Bengel
 */
public final class TseitinEncoder {

	private final SatSolver solver;

	private final ToIntFunction<Proposition> atoms;

	private final Map<PlFormula, Integer> literals = new HashMap<>();

	/** a literal fixed to true, created on first use */
	private int top;

	/**
	 * @param solver the solver the clauses are added to
	 * @param atoms  the solver variable of each proposition
	 */
	public TseitinEncoder(SatSolver solver, ToIntFunction<Proposition> atoms) {
		this.solver = solver;
		this.atoms = atoms;
	}

	/**
	 * @param formula a formula
	 * @return a literal that is equivalent to the formula
	 */
	public int encode(PlFormula formula) {
		if (formula instanceof Proposition) {
			return atoms.applyAsInt((Proposition) formula);
		}
		Integer literal = literals.get(formula);
		if (literal == null) {
			literal = define(formula);
			literals.put(formula, literal);
		}
		return literal;
	}

	private int define(PlFormula formula) {
		if (formula instanceof Tautology) {
			return top();
		} else if (formula instanceof Contradiction) {
			return -top();
		} else if (formula instanceof Negation) {
			return -encode(((Negation) formula).getFormula());
		} else if (formula instanceof Conjunction) {
			return and(encodeAll((Conjunction) formula));
		} else if (formula instanceof Disjunction) {
			return -and(negateAll(encodeAll((Disjunction) formula)));
		} else if (formula instanceof Implication) {
			Implication i = (Implication) formula;
			return -and(new int[] { encode(i.getFormulas().getFirst()), -encode(i.getFormulas().getSecond()) });
		} else if (formula instanceof Equivalence) {
			Equivalence e = (Equivalence) formula;
			return -xor(encode(e.getFormulas().getFirst()), encode(e.getFormulas().getSecond()));
		} else if (formula instanceof ExclusiveDisjunction) {
			int[] operands = encodeAll((ExclusiveDisjunction) formula);
			if (operands.length == 0) {
				return -top();
			}
			int result = operands[0];
			for (int i = 1; i < operands.length; i++) {
				result = xor(result, operands[i]);
			}
			return result;
		}
		throw new IllegalArgumentException("Unsupported formula: " + formula);
	}

	private int[] encodeAll(Iterable<PlFormula> operands) {
		List<Integer> result = new ArrayList<>();
		for (PlFormula operand : operands) {
			result.add(encode(operand));
		}
		return result.stream().mapToInt(Integer::intValue).toArray();
	}

	private static int[] negateAll(int[] literals) {
		int[] result = new int[literals.length];
		for (int i = 0; i < literals.length; i++) {
			result[i] = -literals[i];
		}
		return result;
	}

	/** x ↔ (l_1 ∧ ... ∧ l_n) */
	private int and(int[] operands) {
		if (operands.length == 0) {
			return top();
		} else if (operands.length == 1) {
			return operands[0];
		}
		int x = solver.newVar();
		int[] back = new int[operands.length + 1];
		back[0] = x;
		for (int i = 0; i < operands.length; i++) {
			solver.add(-x, operands[i]);
			back[i + 1] = -operands[i];
		}
		solver.add(back);
		return x;
	}

	/** x ↔ (a ⊕ b) */
	private int xor(int a, int b) {
		int x = solver.newVar();
		solver.add(-x, a, b);
		solver.add(-x, -a, -b);
		solver.add(x, -a, b);
		solver.add(x, a, -b);
		return x;
	}

	private int top() {
		if (top == 0) {
			top = solver.newVar();
			solver.add(top);
		}
		return top;
	}

}
