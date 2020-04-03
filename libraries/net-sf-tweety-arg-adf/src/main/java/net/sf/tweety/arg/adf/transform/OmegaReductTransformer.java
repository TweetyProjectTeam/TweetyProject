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

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.NegationAcceptanceCondition;

/**
 * @author Mathias Hofer
 *
 */
public final class OmegaReductTransformer extends AbstractTransformer<AcceptanceCondition, Void, AcceptanceCondition>{

	private final Interpretation interpretation;
	
	/**
	 * @param interpretation
	 */
	public OmegaReductTransformer(Interpretation interpretation) {
		this.interpretation = interpretation;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#initialize()
	 */
	@Override
	protected Void initialize() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#finish(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected AcceptanceCondition finish(AcceptanceCondition bottomUpData, Void topDownData) {
		return bottomUpData;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformDisjunction(java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformDisjunction(Collection<AcceptanceCondition> children, Void topDownData,
			int polarity) {
		if (children.size() > 1) {
			return new DisjunctionAcceptanceCondition(children);			
		} 
		return children.iterator().next();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformConjunction(java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformConjunction(Collection<AcceptanceCondition> children, Void topDownData,
			int polarity) {
		if (children.size() > 1) {
			return new ConjunctionAcceptanceCondition(children);			
		}
		return children.iterator().next();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformImplication(java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformImplication(AcceptanceCondition left, AcceptanceCondition right,
			Void topDownData, int polarity) {
		return new ImplicationAcceptanceCondition(left, right);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformEquivalence(java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformEquivalence(Collection<AcceptanceCondition> children, Void topDownData,
			int polarity) {
		return new EquivalenceAcceptanceCondition(children);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformExclusiveDisjunction(java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformExclusiveDisjunction(AcceptanceCondition left, AcceptanceCondition right,
			Void topDownData, int polarity) {
		return new ExclusiveDisjunctionAcceptanceCondition(left, right);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformNegation(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformNegation(AcceptanceCondition child, Void topDownData, int polarity) {
		return new NegationAcceptanceCondition(child);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformArgument(net.sf.tweety.arg.adf.syntax.Argument, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformArgument(Argument argument, Void topDownData, int polarity) {
		return interpretation.unsatisfied(argument) ? AcceptanceCondition.CONTRADICTION : argument;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformContradiction(java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformContradiction(Void topDownData, int polarity) {
		return AcceptanceCondition.CONTRADICTION;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformTautology(java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformTautology(Void topDownData, int polarity) {
		return AcceptanceCondition.TAUTOLOGY;
	}

}
