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

	/**
	 * Transform
	 * @param acc condition
	 * @param userObject object
	 * @return transformed
	 */
	protected U transform(AcceptanceCondition acc, D userObject) {
		return acc.accept(visitor, new TopDownData<D>(topLevelPolarity(), userObject));
	}

	/**
	 *
	 * Return topLevelPolarity
	 * @return topLevelPolarity
	 */
	protected int topLevelPolarity() {
		return 1;
	}

	/**
	 * Provides the initial top-down data.
	 *
	 * @return the initial top-down data
	 */
	protected abstract D initialize();

/**
 * Finalizes the transformation process and returns the result.
 *
 * @param bottomUpData The data accumulated during the bottom-up traversal.
 * @param topDownData  The data provided during the top-down traversal.
 * @return The final result of the transformation process, of type {@code R}.
 */
protected abstract R finish(U bottomUpData, D topDownData);

/**
 * Transforms a disjunction of two sub-expressions.
 *
 * @param left         The transformed result of the left sub-expression.
 * @param right        The transformed result of the right sub-expression.
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the disjunction, of type {@code U}.
 */
protected abstract U transformDisjunction(U left, U right, D topDownData, int polarity);

/**
 * Transforms a conjunction of two sub-expressions.
 *
 * @param left         The transformed result of the left sub-expression.
 * @param right        The transformed result of the right sub-expression.
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the conjunction, of type {@code U}.
 */
protected abstract U transformConjunction(U left, U right, D topDownData, int polarity);

/**
 * Transforms an implication between two sub-expressions.
 *
 * @param left         The transformed result of the left sub-expression.
 * @param right        The transformed result of the right sub-expression.
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the implication, of type {@code U}.
 */
protected abstract U transformImplication(U left, U right, D topDownData, int polarity);

/**
 * Transforms an equivalence between two sub-expressions.
 *
 * @param left         The transformed result of the left sub-expression.
 * @param right        The transformed result of the right sub-expression.
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the equivalence, of type {@code U}.
 */
protected abstract U transformEquivalence(U left, U right, D topDownData, int polarity);

/**
 * Transforms an exclusive disjunction (XOR) between two sub-expressions.
 *
 * @param left         The transformed result of the left sub-expression.
 * @param right        The transformed result of the right sub-expression.
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the exclusive disjunction, of type {@code U}.
 */
protected abstract U transformExclusiveDisjunction(U left, U right, D topDownData, int polarity);

/**
 * Transforms a negation of a sub-expression.
 *
 * @param child        The transformed result of the sub-expression being negated.
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the negation, of type {@code U}.
 */
protected abstract U transformNegation(U child, D topDownData, int polarity);

/**
 * Transforms an argument within the logical structure.
 *
 * @param argument     The argument to be transformed.
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the argument, of type {@code U}.
 */
protected abstract U transformArgument(Argument argument, D topDownData, int polarity);

/**
 * Transforms a contradiction in the logical structure.
 *
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the contradiction, of type {@code U}.
 */
protected abstract U transformContradiction(D topDownData, int polarity);

/**
 * Transforms a tautology in the logical structure.
 *
 * @param topDownData  The data provided during the top-down traversal.
 * @param polarity     The polarity (e.g., positive or negative) to guide the transformation.
 * @return The result of transforming the tautology, of type {@code U}.
 */
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
