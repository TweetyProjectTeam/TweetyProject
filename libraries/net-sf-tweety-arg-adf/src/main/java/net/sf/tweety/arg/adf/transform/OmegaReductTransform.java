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
 * Replaces all unsatisfied arguments relative to a given interpretation with
 * falsum.
 * 
 * @author Mathias Hofer
 *
 */
public class OmegaReductTransform implements SimpleTransform<AcceptanceCondition> {

	private Interpretation interpretation;

	/**
	 * 
	 * @param interpretation
	 *            The interpretation for which we compute the reduct
	 */
	public OmegaReductTransform(Interpretation interpretation) {
		this.interpretation = interpretation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformDisjunction(java.
	 * util.Collection)
	 */
	@Override
	public AcceptanceCondition transformDisjunction(Collection<AcceptanceCondition> subconditions) {
		return new DisjunctionAcceptanceCondition(subconditions.toArray(new AcceptanceCondition[0]));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformConjunction(java.
	 * util.Collection)
	 */
	@Override
	public AcceptanceCondition transformConjunction(Collection<AcceptanceCondition> subconditions) {
		return new ConjunctionAcceptanceCondition(subconditions.toArray(new AcceptanceCondition[0]));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformImplication(java.
	 * lang.Object, java.lang.Object)
	 */
	@Override
	public AcceptanceCondition transformImplication(AcceptanceCondition left, AcceptanceCondition right) {
		return new ImplicationAcceptanceCondition(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformEquivalence(java.
	 * lang.Object, java.lang.Object)
	 */
	@Override
	public AcceptanceCondition transformEquivalence(AcceptanceCondition left, AcceptanceCondition right) {
		return new EquivalenceAcceptanceCondition(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.SimpleTransform#
	 * transformExclusiveDisjunction(java.lang.Object, java.lang.Object)
	 */
	@Override
	public AcceptanceCondition transformExclusiveDisjunction(AcceptanceCondition left, AcceptanceCondition right) {
		return new ExclusiveDisjunctionAcceptanceCondition(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformNegation(java.lang.
	 * Object)
	 */
	@Override
	public AcceptanceCondition transformNegation(AcceptanceCondition sub) {
		return new NegationAcceptanceCondition(sub);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformArgument(net.sf.
	 * tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public AcceptanceCondition transformArgument(Argument argument) {
		return interpretation.isUnsatisfied(argument) ? new ContradictionAcceptanceCondition() : argument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.SimpleTransform#transformContradiction()
	 */
	@Override
	public AcceptanceCondition transformContradiction() {
		return new ContradictionAcceptanceCondition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.SimpleTransform#transformTautology()
	 */
	@Override
	public AcceptanceCondition transformTautology() {
		return new TautologyAcceptanceCondition();
	}

}
