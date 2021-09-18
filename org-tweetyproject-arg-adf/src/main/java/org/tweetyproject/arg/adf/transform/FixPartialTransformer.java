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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.transform;

import static org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition.CONTRADICTION;
import static org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition.TAUTOLOGY;

import java.util.Objects;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ContradictionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.NegationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.TautologyAcceptanceCondition;

/**
 * Syntactically rewrites the acceptance condition s.t. the arguments are
 * replaced with the corresponding constants
 * {@link TautologyAcceptanceCondition} or
 * {@link ContradictionAcceptanceCondition} according to the given (partial)
 * interpretation.
 * <p>
 * Some examples:<br>
 * and(a,b,c) with {t(a), u(b), u(c)} becomes and(b,c).<br>
 * or(a,b,c) with {t(a), u(b), u(c)} becomes T.<br>
 * or(a,b,c) with {f(a), u(b), u(c)} becomes or(b,c).<br>
 * 
 * @author Mathias Hofer
 *
 */
public final class FixPartialTransformer implements Transformer<AcceptanceCondition>{

	private final Interpretation interpretation;
	
	/**
	 * @param interpretation the interpretation which is used
	 */
	public FixPartialTransformer(Interpretation interpretation) {
		this.interpretation = Objects.requireNonNull(interpretation);
	}
	
	@Override
	public AcceptanceCondition transform(AcceptanceCondition acc) {
		if (acc instanceof BinaryAcceptanceCondition) {
			BinaryAcceptanceCondition bin = (BinaryAcceptanceCondition) acc;
			AcceptanceCondition left = transform(bin.getLeft());
			AcceptanceCondition right = transform(bin.getRight());
			if (acc instanceof ConjunctionAcceptanceCondition) {
				return transformConjunction(left, right);
			} else if (acc instanceof DisjunctionAcceptanceCondition) {
				return transformDisjunction(left, right);
			} else if (acc instanceof ImplicationAcceptanceCondition) {
				return transformImplication(left, right);
			} else if (acc instanceof EquivalenceAcceptanceCondition) {
				return transformEquivalence(left, right);
			} else if (acc instanceof ExclusiveDisjunctionAcceptanceCondition) {
				return transformExclusiveDisjunction(left, right);
			}
		} else if (acc instanceof NegationAcceptanceCondition) {
			AcceptanceCondition child = transform(((NegationAcceptanceCondition) acc).getChild());
			return transformNegation(child);
		} else if (acc instanceof Argument) {
			return transformArgument((Argument)acc);
		}
		return acc; // constants
	}

	private AcceptanceCondition transformDisjunction(AcceptanceCondition left, AcceptanceCondition right) {
		if (left == TAUTOLOGY || right == TAUTOLOGY) return TAUTOLOGY;
		if (left == CONTRADICTION) return right;
		if (right == CONTRADICTION) return left;
		return new DisjunctionAcceptanceCondition(left, right);
	}

	private AcceptanceCondition transformConjunction(AcceptanceCondition left, AcceptanceCondition right) {
		if (left == CONTRADICTION || right == CONTRADICTION) return CONTRADICTION;
		if (left == TAUTOLOGY) return right;
		if (right == TAUTOLOGY) return left;
		return new ConjunctionAcceptanceCondition(left, right);
	}

	private AcceptanceCondition transformImplication(AcceptanceCondition left, AcceptanceCondition right) {
		if (left == CONTRADICTION || right == TAUTOLOGY) return TAUTOLOGY;
		if (left == TAUTOLOGY) return right;
		if (right == CONTRADICTION) return new NegationAcceptanceCondition(left);
		return new ImplicationAcceptanceCondition(left, right);
	}

	private AcceptanceCondition transformEquivalence(AcceptanceCondition left, AcceptanceCondition right) {
		if (left == right) return TAUTOLOGY;
		if (left == TAUTOLOGY) return right;
		if (right == TAUTOLOGY) return left;
		if (left == CONTRADICTION) return new NegationAcceptanceCondition(right);
		if (right == CONTRADICTION) return new NegationAcceptanceCondition(left);
		return new ConjunctionAcceptanceCondition(left, right);
	}

	private AcceptanceCondition transformExclusiveDisjunction(AcceptanceCondition left, AcceptanceCondition right) {
		if (left == right) return CONTRADICTION;
		if (left == CONTRADICTION) return right;
		if (right == CONTRADICTION) return left;
		if (left == TAUTOLOGY) return new NegationAcceptanceCondition(right);
		if (right == TAUTOLOGY) return new NegationAcceptanceCondition(left);
		return new ExclusiveDisjunctionAcceptanceCondition(left, right);
	}

	private AcceptanceCondition transformNegation(AcceptanceCondition child) {
		if (child == CONTRADICTION) return TAUTOLOGY;
		if (child == TAUTOLOGY) return CONTRADICTION;
		return new NegationAcceptanceCondition(child);
	}

	private AcceptanceCondition transformArgument(Argument argument) {
		if (interpretation.unsatisfied(argument)) return CONTRADICTION;
		if (interpretation.satisfied(argument)) return TAUTOLOGY;
		return argument;
	}

}
