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
package org.tweetyproject.arg.adf.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.util.TseitinEncoder;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.AcceptanceConditions;
import org.tweetyproject.arg.adf.syntax.Link;
import org.tweetyproject.arg.adf.syntax.LinkType;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Computes the {@link LinkType}s of the links of an ADF.
 * <p>
 * A link (b, a) is supporting if phi_a is monotone in b (making b true never makes
 * phi_a false), attacking if phi_a is antitone in b, redundant if both, and
 * dependent if neither.
 *
 * @see "Thomas Linsbichler, Marco Maratea, Andreas Niskanen, Johannes P. Wallner, Stefan Woltran. 'Novel Algorithms for Abstract Dialectical Frameworks based on Complexity Analysis of Subclasses and SAT Solving.' IJCAI 2018."
 *
 * @author Lars Bengel
 */
public final class LinkTypeClassifier {

	private LinkTypeClassifier() {}

	/**
	 * @param adf an ADF
	 * @return the exact type of every link of the ADF
	 */
	public static Map<Link, LinkType> classify(AbstractDialecticalFramework adf) {
		return classify(adf, Sat4jIncrementalSolver::new);
	}

	/**
	 * @param adf     an ADF
	 * @param solvers creates the solver used for the classification
	 * @return the exact type of every link of the ADF
	 */
	public static Map<Link, LinkType> classify(AbstractDialecticalFramework adf, Supplier<SatSolver> solvers) {
		try (Context ctx = new Context(adf, solvers.get())) {
			Map<Link, LinkType> result = new LinkedHashMap<>();
			for (Link link : adf.getLinks()) {
				result.put(link, ctx.classify(link.getParent(), link.getChild(), true));
			}
			return Collections.unmodifiableMap(result);
		}
	}

	/**
	 * @param adf    an ADF
	 * @param parent an argument occurring in the condition of the child
	 * @param child  an argument of the ADF
	 * @return the exact type of the link (parent, child)
	 * @throws IllegalArgumentException if (parent, child) is not a link of the ADF
	 */
	public static LinkType classify(AbstractDialecticalFramework adf, Argument parent, Argument child) {
		if (!adf.areAdjacent(parent, child)) {
			throw new IllegalArgumentException("(" + parent + ", " + child + ") is not a link of the ADF.");
		}
		try (Context ctx = new Context(adf, new Sat4jIncrementalSolver())) {
			return ctx.classify(parent, child, true);
		}
	}

	/**
	 * @param adf an ADF
	 * @return true if every link is supporting, attacking or redundant
	 */
	public static boolean isBipolar(AbstractDialecticalFramework adf) {
		try (Context ctx = new Context(adf, new Sat4jIncrementalSolver())) {
			for (Link link : adf.getLinks()) {
				if (!ctx.classify(link.getParent(), link.getChild(), false).isBipolar()) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * @param adf      an ADF
	 * @param argument an argument of the ADF
	 * @return the number k_a of dependent parents of the argument
	 */
	public static int dependentParents(AbstractDialecticalFramework adf, Argument argument) {
		try (Context ctx = new Context(adf, new Sat4jIncrementalSolver())) {
			int k = 0;
			for (Argument parent : adf.getParents(argument)) {
				if (!ctx.classify(parent, argument, false).isBipolar()) {
					k++;
				}
			}
			return k;
		}
	}

	/** classification state for one snapshot of a framework */
	private static final class Context implements AutoCloseable {
		private final AbstractDialecticalFramework adf;

		private final SatSolver solver;

		private final Map<Argument, Integer> variables = new HashMap<>();

		/** supplies a literal fixed to true */
		private final IntSupplier top;

		Context(AbstractDialecticalFramework adf, SatSolver solver) {
			this.adf = adf;
			this.solver = solver;
			this.top = TseitinEncoder.trueLiteral(solver);
		}

		/**
		 * @param exact if false, a syntactic certificate for one direction suffices
		 *              and a bipolar (non-dependent) result may be SUPPORTING or
		 *              ATTACKING although the link is in fact REDUNDANT
		 */
		LinkType classify(Argument parent, Argument child, boolean exact) {
			PlFormula condition = adf.getAcceptanceCondition(child);
			int polarity = polarity(condition, parent.getName(), true);
			boolean monotone = (polarity & NEGATIVE) == 0;
			boolean antitone = (polarity & POSITIVE) == 0;
			if (!exact && (monotone || antitone)) {
				return LinkType.of(monotone, antitone);
			}
			boolean[] checked = check(condition, parent, child, monotone, antitone);
			return LinkType.of(checked[0], checked[1]);
		}

		/** @return {monotone, antitone}, both decided exactly; known properties are not checked again */
		private boolean[] check(PlFormula condition, Argument parent, Argument child, boolean monotone, boolean antitone) {
			int t = top.getAsInt();
			int withFalse = new TseitinEncoder(solver, p -> atom(p, parent, -t), top).encode(condition);
			int withTrue = new TseitinEncoder(solver, p -> atom(p, parent, t), top).encode(condition);
			monotone = monotone || !solver.satisfiable(new int[] { withFalse, -withTrue });
			antitone = antitone || !solver.satisfiable(new int[] { withTrue, -withFalse });
			return new boolean[] { monotone, antitone };
		}

		/** @return the literal of p, where the parent is replaced by the given constant */
		private int atom(Proposition p, Argument parent, int constant) {
			Argument argument = adf.toArgument(p);
			if (argument.equals(parent)) {
				return constant;
			}
			Integer variable = variables.get(argument);
			if (variable == null) {
				variable = solver.newVar();
				variables.put(argument, variable);
			}
			return variable;
		}

		@Override
		public void close() {
			solver.close();
		}
	}

	private static final int POSITIVE = 1, NEGATIVE = 2;

	/** @return the polarities (bit set of POSITIVE, NEGATIVE) in which the atom occurs */
	private static int polarity(PlFormula formula, String atom, boolean positive) {
		if (formula instanceof Proposition) {
			return ((Proposition) formula).getName().equals(atom) ? (positive ? POSITIVE : NEGATIVE) : 0;
		} else if (formula instanceof Negation) {
			return polarity(((Negation) formula).getFormula(), atom, !positive);
		} else if (formula instanceof Implication) {
			Implication i = (Implication) formula;
			return polarity(i.getFirstFormula(), atom, !positive) | polarity(i.getSecondFormula(), atom, positive);
		} else if (formula instanceof Equivalence || formula instanceof ExclusiveDisjunction) {
			// operands of ↔ and ⊕ occur in both polarities
			int result = 0;
			for (PlFormula child : AcceptanceConditions.children(formula)) {
				result |= polarity(child, atom, true) | polarity(child, atom, false);
			}
			return result;
		}
		// constants, conjunction and disjunction keep the polarity
		int result = 0;
		for (PlFormula child : AcceptanceConditions.children(formula)) {
			result |= polarity(child, atom, positive);
		}
		return result;
	}

}
