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

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ContradictionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.NegationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.TautologyAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.Visitor;

/**
 * AbstractTransformer class
 *
 * @author Mathias Hofer
 *
 * @param <U> the information that is passed bottom-up during the transformation
 * @param <D> the information that is passed top-down during the transformation
 * @param <R> the result of the transformation
 */
public abstract class AbstractTransformer<U, D, R> implements Transformer<R> {

	private final TransformerVisitor visitor = new TransformerVisitor();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.arg.adf.transform.Transformer#transform(org.tweetyproject.arg.
	 * adf.syntax.acc.AcceptanceCondition)
	 */
	@Override
	public R transform(AcceptanceCondition acc) {
		D userObject = initialize();
		U bottomUpData = transform(acc, userObject);
		return finish(bottomUpData, userObject);
	}

	protected U transform(AcceptanceCondition acc, D userObject) {
		return acc.accept(visitor, new TopDownData<D>(topLevelPolarity(), userObject));
	}

	protected int topLevelPolarity() {
		return 1;
	}

	/**
	 * Provides the initial top-down data.
	 *
	 * @return the initial top-down data
	 */
	protected abstract D initialize();

	protected abstract R finish(U bottomUpData, D topDownData);

	protected abstract U transformDisjunction(U left, U right, D topDownData, int polarity);

	protected abstract U transformConjunction(U left, U right, D topDownData, int polarity);

	protected abstract U transformImplication(U left, U right, D topDownData, int polarity);

	protected abstract U transformEquivalence(U left, U right, D topDownData, int polarity);

	protected abstract U transformExclusiveDisjunction(U left, U right, D topDownData, int polarity);

	protected abstract U transformNegation(U child, D topDownData, int polarity);

	protected abstract U transformArgument(Argument argument, D topDownData, int polarity);

	protected abstract U transformContradiction(D topDownData, int polarity);

	protected abstract U transformTautology(D topDownData, int polarity);

	/**
	 * We encapsulate the Visitor in a separate private class s.t. we do not
	 * pollute the public interface of AbstractTransformer.
	 */
	private final class TransformerVisitor implements Visitor<U, TopDownData<D>> {

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.acc.TautologyAcceptanceCondition, java.lang.Object)
		 */
		@Override
		public U visit(TautologyAcceptanceCondition acc, TopDownData<D> topDownData) {
			return transformTautology(topDownData.userObject, topDownData.polarity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.acc.ContradictionAcceptanceCondition, java.lang.Object)
		 */
		@Override
		public U visit(ContradictionAcceptanceCondition acc, TopDownData<D> topDownData) {
			return transformContradiction(topDownData.userObject, topDownData.polarity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.acc.ConjunctionAcceptanceCondition, java.lang.Object)
		 */
		@Override
		public U visit(ConjunctionAcceptanceCondition acc, TopDownData<D> topDownData) {
			U transformedLeft = acc.getLeft().accept(this, topDownData);
			U transformedRight = acc.getRight().accept(this, topDownData);
			return transformConjunction(transformedLeft, transformedRight, topDownData.userObject, topDownData.polarity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.acc.DisjunctionAcceptanceCondition, java.lang.Object)
		 */
		@Override
		public U visit(DisjunctionAcceptanceCondition acc, TopDownData<D> topDownData) {
			U transformedLeft = acc.getLeft().accept(this, topDownData);
			U transformedRight = acc.getRight().accept(this, topDownData);
			return transformDisjunction(transformedLeft, transformedRight, topDownData.userObject, topDownData.polarity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.acc.EquivalenceAcceptanceCondition, java.lang.Object)
		 */
		@Override
		public U visit(EquivalenceAcceptanceCondition acc, TopDownData<D> topDownData) {
			U transformedLeft = acc.getLeft().accept(this, new TopDownData<D>(0, topDownData.userObject));
			U transformedRight = acc.getRight().accept(this, new TopDownData<D>(0, topDownData.userObject));
			return transformEquivalence(transformedLeft, transformedRight, topDownData.userObject, topDownData.polarity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.acc.ExclusiveDisjunctionAcceptanceCondition, java.lang.Object)
		 */
		@Override
		public U visit(ExclusiveDisjunctionAcceptanceCondition acc, TopDownData<D> topDownData) {
			U transformedLeft = acc.getLeft().accept(this, topDownData);
			U transformedRight = acc.getRight().accept(this, topDownData);
			return transformExclusiveDisjunction(transformedLeft, transformedRight, topDownData.userObject, topDownData.polarity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.acc.ImplicationAcceptanceCondition, java.lang.Object)
		 */
		@Override
		public U visit(ImplicationAcceptanceCondition acc, TopDownData<D> topDownData) {
			U transformedLeft = acc.getLeft().accept(this, new TopDownData<D>(-topDownData.polarity, topDownData.userObject));
			U transformedRight = acc.getRight().accept(this, topDownData);
			return transformImplication(transformedLeft, transformedRight, topDownData.userObject,
					topDownData.polarity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.acc.NegationAcceptanceCondition, java.lang.Object)
		 */
		@Override
		public U visit(NegationAcceptanceCondition acc, TopDownData<D> topDownData) {
			U transformedChild = acc.getChild().accept(this, new TopDownData<D>(-topDownData.polarity, topDownData.userObject));
			return transformNegation(transformedChild, topDownData.userObject, topDownData.polarity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.tweetyproject.arg.adf.syntax.acc.Visitor#visit(org.tweetyproject.arg.adf.
		 * syntax.Argument, java.lang.Object)
		 */
		@Override
		public U visit(Argument acc, TopDownData<D> topDownData) {
			return transformArgument(acc, topDownData.userObject, topDownData.polarity);
		}

	}

	/**
	 * Used to store additional meta-data which may be useful.
	 */
	private static final class TopDownData<D> {

		private final int polarity;

		private final D userObject;

		/**
		 * @param polarity
		 * @param userObject
		 */
		public TopDownData(int polarity, D userObject) {
			this.polarity = polarity;
			this.userObject = userObject;
		}

	}

}
