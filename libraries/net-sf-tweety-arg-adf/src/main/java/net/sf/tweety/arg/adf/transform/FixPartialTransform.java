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
package net.sf.tweety.arg.adf.transform;

import java.util.Collection;
import java.util.LinkedList;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.ConjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.ContradictionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.DisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.ExclusiveDisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.NegationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.TautologyAcceptanceCondition;

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
public class FixPartialTransform implements SimpleTransform<AcceptanceCondition> {

	private static final TautologyAcceptanceCondition TAUTOLOGY = new TautologyAcceptanceCondition();

	private static final ContradictionAcceptanceCondition CONTRADICTION = new ContradictionAcceptanceCondition();

	private Interpretation partialInterpretation;

	/**
	 * @param partialInterpretation
	 */
	public FixPartialTransform(Interpretation partialInterpretation) {
		this.partialInterpretation = partialInterpretation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.SimpleTransform#transformDisjunction(java
	 * .util.Collection)
	 */
	@Override
	public AcceptanceCondition transformDisjunction(Collection<AcceptanceCondition> subconditions) {
		Collection<AcceptanceCondition> filtered = new LinkedList<AcceptanceCondition>();
		for (AcceptanceCondition subcondition : subconditions) {
			if (subcondition.isTautology()) {
				return TAUTOLOGY;
			}
			if (!subcondition.isContradiction()) {
				filtered.add(subcondition);
			}
		}
		return new DisjunctionAcceptanceCondition(filtered.toArray(new AcceptanceCondition[0]));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.SimpleTransform#transformConjunction(java
	 * .util.Collection)
	 */
	@Override
	public AcceptanceCondition transformConjunction(Collection<AcceptanceCondition> subconditions) {
		Collection<AcceptanceCondition> filtered = new LinkedList<AcceptanceCondition>();
		for (AcceptanceCondition subcondition : subconditions) {
			if (subcondition.isContradiction()) {
				return CONTRADICTION;
			}
			if (!subcondition.isTautology()) {
				filtered.add(subcondition);
			}
		}
		return new ConjunctionAcceptanceCondition(filtered.toArray(new AcceptanceCondition[0]));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.SimpleTransform#transformImplication(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	public AcceptanceCondition transformImplication(AcceptanceCondition left, AcceptanceCondition right) {
		if (left.isContradiction() || right.isTautology()) {
			return TAUTOLOGY;
		} else if (left.isTautology() && right.isContradiction()) {
			return CONTRADICTION;
		} else if (left.isTautology()) {
			return right;
		} else if (right.isContradiction()) {
			return new NegationAcceptanceCondition(left);
		}
		return new ImplicationAcceptanceCondition(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.SimpleTransform#transformEquivalence(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	public AcceptanceCondition transformEquivalence(AcceptanceCondition left, AcceptanceCondition right) {
		if (left.equals(right)) {
			return TAUTOLOGY;
		} else if (left.isTautology()) {
			return right;
		} else if (right.isTautology()) {
			return left;
		} else if (left.isContradiction()) {
			return new NegationAcceptanceCondition(right);
		} else if (right.isContradiction()) {
			return new NegationAcceptanceCondition(left);
		}
		return new EquivalenceAcceptanceCondition(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.transform.SimpleTransform#
	 * transformExclusiveDisjunction(java.lang.Object, java.lang.Object)
	 */
	@Override
	public AcceptanceCondition transformExclusiveDisjunction(AcceptanceCondition left, AcceptanceCondition right) {
		if (left.equals(right)) {
			return CONTRADICTION;
		} else if (left.isTautology()) {
			return new NegationAcceptanceCondition(right);
		} else if (right.isTautology()) {
			return new NegationAcceptanceCondition(left);
		} else if (left.isContradiction()) {
			return right;
		} else if (right.isContradiction()) {
			return left;
		}
		return new ExclusiveDisjunctionAcceptanceCondition(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.SimpleTransform#transformNegation(java.
	 * lang.Object)
	 */
	@Override
	public AcceptanceCondition transformNegation(AcceptanceCondition sub) {
		if (sub.isContradiction()) {
			return TAUTOLOGY;
		} else if (sub.isTautology()) {
			return CONTRADICTION;
		}
		return new NegationAcceptanceCondition(sub);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.SimpleTransform#transformArgument(net.sf.
	 * tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public AcceptanceCondition transformArgument(Argument argument) {
		if (partialInterpretation.isSatisfied(argument)) {
			return TAUTOLOGY;
		} else if (partialInterpretation.isUnsatisfied(argument)) {
			return CONTRADICTION;
		}
		return argument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.SimpleTransform#transformContradiction()
	 */
	@Override
	public AcceptanceCondition transformContradiction() {
		return CONTRADICTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.transform.SimpleTransform#transformTautology()
	 */
	@Override
	public AcceptanceCondition transformTautology() {
		return TAUTOLOGY;
	}

}
