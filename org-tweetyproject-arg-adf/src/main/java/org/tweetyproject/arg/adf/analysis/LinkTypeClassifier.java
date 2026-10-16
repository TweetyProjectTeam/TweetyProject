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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.AcceptanceConditions;
import org.tweetyproject.arg.adf.syntax.Link;
import org.tweetyproject.arg.adf.syntax.LinkType;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * Computes the {@link LinkType}s of the links of an ADF.
 * <p>
 * A link (b, a) is supporting if phi_a is monotone in b (making b true never makes
 * phi_a false), attacking if phi_a is antitone in b, redundant if both, and
 * dependent if neither. Each property is decided as follows:
 * <ol>
 * <li>If b occurs in phi_a only positively (only negatively), phi_a is monotone
 * (antitone) in b. This syntactic certificate does not exclude redundancy, so
 * {@link #classify(AbstractDialecticalFramework)} still checks the other
 * direction.</li>
 * <li>Otherwise, the property is checked exactly: by a truth table over the
 * parents of a if there are at most {@value #TRUTH_TABLE_LIMIT} of them, and by
 * an unsatisfiability check of phi_a[b/⊥] ∧ ¬phi_a[b/⊤] (resp. phi_a[b/⊤] ∧
 * ¬phi_a[b/⊥]) with the default SAT solver of {@code logics-pl} otherwise.</li>
 * </ol>
 * Results are computed on demand from the current state of the framework and
 * are not cached.
 *
 * @author Lars Bengel
 */
public final class LinkTypeClassifier {

	/** the maximal number of parents for which truth tables are used instead of SAT */
	public static final int TRUTH_TABLE_LIMIT = 20;

	private LinkTypeClassifier() {}

	/**
	 * @param adf an ADF
	 * @return the exact type of every link of the ADF
	 */
	public static Map<Link, LinkType> classify(AbstractDialecticalFramework adf) {
		Context ctx = new Context(adf);
		Map<Link, LinkType> result = new LinkedHashMap<>();
		for (Link link : adf.getLinks()) {
			result.put(link, ctx.classify(link.getParent(), link.getChild(), true));
		}
		return Collections.unmodifiableMap(result);
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
		return new Context(adf).classify(parent, child, true);
	}

	/**
	 * @param adf an ADF
	 * @return true if every link is supporting, attacking or redundant
	 */
	public static boolean isBipolar(AbstractDialecticalFramework adf) {
		Context ctx = new Context(adf);
		for (Link link : adf.getLinks()) {
			if (!ctx.classify(link.getParent(), link.getChild(), false).isBipolar()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param adf      an ADF
	 * @param argument an argument of the ADF
	 * @return the number k_a of dependent parents of the argument
	 */
	public static int dependentParents(AbstractDialecticalFramework adf, Argument argument) {
		Context ctx = new Context(adf);
		int k = 0;
		for (Argument parent : adf.getParents(argument)) {
			if (!ctx.classify(parent, argument, false).isBipolar()) {
				k++;
			}
		}
		return k;
	}

	/** classification state for one snapshot of a framework */
	private static final class Context {
		private final AbstractDialecticalFramework adf;

		Context(AbstractDialecticalFramework adf) {
			this.adf = adf;
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
			if (monotone && antitone) {
				return LinkType.REDUNDANT; // cannot happen for an occurring atom, kept for safety
			}
			boolean[] checked = check(condition, parent, child, monotone, antitone);
			return LinkType.of(checked[0], checked[1]);
		}

		/** @return {monotone, antitone}, both decided exactly; known properties are not checked again */
		private boolean[] check(PlFormula condition, Argument parent, Argument child, boolean monotone, boolean antitone) {
			if (adf.getParents(child).size() <= TRUTH_TABLE_LIMIT) {
				boolean[] table = truthTable(adf, condition, parent, child);
				return new boolean[] { monotone || table[0], antitone || table[1] };
			}
			PlFormula withFalse = AcceptanceConditions.substitute(condition, Map.of(parent.getName(), new Contradiction()));
			PlFormula withTrue = AcceptanceConditions.substitute(condition, Map.of(parent.getName(), new Tautology()));
			SatSolver solver = SatSolver.hasDefaultSolver() ? SatSolver.getDefaultSolver() : new Sat4jSolver();
			monotone = monotone || !solver.isSatisfiable(List.of(withFalse, new Negation(withTrue)));
			antitone = antitone || !solver.isSatisfiable(List.of(withTrue, new Negation(withFalse)));
			return new boolean[] { monotone, antitone };
		}
	}

	private static boolean[] truthTable(AbstractDialecticalFramework adf, PlFormula condition, Argument parent, Argument child) {
		Set<Argument> others = new LinkedHashSet<>(adf.getParents(child));
		others.remove(parent);
		boolean monotone = true, antitone = true;
		Iterator<ThreeValuedInterpretation> it = ThreeValuedInterpretation.allUndecided(adf.getNodes()).completions(others);
		while (it.hasNext() && (monotone || antitone)) {
			ThreeValuedInterpretation w = it.next();
			boolean withFalse = w.with(parent, TruthValue.FALSE).evaluate(condition) == TruthValue.TRUE;
			boolean withTrue = w.with(parent, TruthValue.TRUE).evaluate(condition) == TruthValue.TRUE;
			monotone &= !withFalse || withTrue;
			antitone &= !withTrue || withFalse;
		}
		return new boolean[] { monotone, antitone };
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
