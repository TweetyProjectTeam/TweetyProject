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
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Equivalence;
import net.sf.tweety.logics.pl.syntax.ExclusiveDisjunction;
import net.sf.tweety.logics.pl.syntax.Implication;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * Transforms an {@link AcceptanceCondition} into a {@link PlFormula}.
 * <p>
 * Most of the transformation is a 1:1 mapping between the structures since they are quite
 * similar, except for {@link EquivalenceAcceptanceCondition} which allows for a
 * compact representation of pairwise equivalences.
 * 
 * @author Mathias Hofer
 *
 */
public final class PropositionalTransformer extends AbstractTransformer<PlFormula, Void, PlFormula> {

	private final Function<Argument, Proposition> argumentMapping;

	/**
	 * @param argumentMapping the argument to proposition mapping
	 */
	public PropositionalTransformer(Function<Argument, Proposition> argumentMapping) {
		this.argumentMapping = Objects.requireNonNull(argumentMapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#initialize()
	 */
	@Override
	protected Void initialize() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractTransformer#finish(java.lang.
	 * Object, java.lang.Object)
	 */
	@Override
	protected PlFormula finish(PlFormula bottomUpData, Void topDownData) {
		return bottomUpData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractTransformer#transformDisjunction(
	 * java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformDisjunction(Collection<PlFormula> children, Void topDownData, int polarity) {
		return new Disjunction(children);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractTransformer#transformConjunction(
	 * java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformConjunction(Collection<PlFormula> children, Void topDownData, int polarity) {
		return new Conjunction(children);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractTransformer#transformImplication(
	 * java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformImplication(PlFormula left, PlFormula right, Void topDownData, int polarity) {
		return new Implication(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractTransformer#transformEquivalence(
	 * java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformEquivalence(Collection<PlFormula> children, Void topDownData, int polarity) {
		Iterator<PlFormula> iterator = children.iterator();
		if (children.size() == 2) {
			// most of the time we expect it to only have two children
			// in these cases we want to use the more concise Equivalence
			return new Equivalence(iterator.next(), iterator.next());
		} else {
			// there currently is no constructor of Equivalence which expects a
			// list, we therefore model equivalence as a circle of implications
			PlFormula first = iterator.next();
			PlFormula left = first;
			Conjunction conjunction = new Conjunction();
			while (iterator.hasNext()) {
				PlFormula right = iterator.next();
				conjunction.add(new Implication(left, right));
				left = right;
			}
			// left is now the last child
			// complete the circle
			conjunction.add(new Implication(left, first));
			return conjunction;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#
	 * transformExclusiveDisjunction(java.lang.Object, java.lang.Object,
	 * java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformExclusiveDisjunction(PlFormula left, PlFormula right, Void topDownData, int polarity) {
		return new ExclusiveDisjunction(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractTransformer#transformNegation(
	 * java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformNegation(PlFormula child, Void topDownData, int polarity) {
		return new Negation(child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractTransformer#transformArgument(net
	 * .sf.tweety.arg.adf.syntax.Argument, java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformArgument(Argument argument, Void topDownData, int polarity) {
		return argumentMapping.apply(argument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#
	 * transformContradiction(java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformContradiction(Void topDownData, int polarity) {
		return new Contradiction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractTransformer#transformTautology(
	 * java.lang.Object, int)
	 */
	@Override
	protected PlFormula transformTautology(Void topDownData, int polarity) {
		return new Tautology();
	}

}
