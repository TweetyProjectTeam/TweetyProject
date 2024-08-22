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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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
 * Similar to {@link AbstractTransformer} but is designed for collections and
 * therefore provides {@link Collector} functionality for free.
 *
 * @author Mathias Hofer
 *
 * @param <U> the type of the additional result the collector may provide
 * @param <D> the type of the objects we want to collect
 * @param <R> the result of the transformation
 */
public abstract class AbstractCollector<U, D, R> implements Transformer<R>, Collector<U, D> {

	/** A visitor used for transforming acceptance conditions. */
	private final TransformerVisitor visitor = new TransformerVisitor();

	/** The top-level polarity for the transformation process. */
	private final int topLevelPolarity;

	/**
	 * Constructs an {@code AbstractCollector} with the specified top-level
	 * polarity.
	 *
	 * @param topLevelPolarity the polarity for the top-level transformation
	 */
	public AbstractCollector(int topLevelPolarity) {
		this.topLevelPolarity = topLevelPolarity;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.arg.adf.transform.Transformer#transform(org.tweetyproject.
	 * arg.adf.syntax.acc.AcceptanceCondition)
	 */
	@Override
	public R transform(AcceptanceCondition acc) {
		Collection<D> collection = initialize();
		U bottomUpData = transform(acc, collection::add);
		return finish(bottomUpData, collection);
	}

	protected U transform(AcceptanceCondition acc, Consumer<D> userObject) {
		return acc.accept(visitor, new TopDownData<D>(topLevelPolarity, userObject));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.arg.adf.transform.Collector#collect(org.tweetyproject.arg.
	 * adf.syntax.acc.AcceptanceCondition, java.util.function.Consumer)
	 */
	@Override
	public U collect(AcceptanceCondition acc, Consumer<D> consumer) {
		for (D data : initialize())
			consumer.accept(data);
		return transform(acc, consumer);
	}

	/**
	 * Provides the initial top-down data.
	 *
	 * @return the initial top-down data
	 */
	protected abstract Collection<D> initialize();

	/**
     * Finalizes the transformation process by processing the bottom-up data and the collected data.
     *
     * @param bottomUpData the bottom-up data obtained during the transformation
     * @param collection the collection of data accumulated during the transformation
     * @return the final result of the transformation
     */
	protected abstract R finish(U bottomUpData, Collection<D> collection);

	/**
	 * Transforms a disjunction of acceptance conditions.
	 *
	 * @param children   the collection of transformed child acceptance conditions
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the disjunction is being evaluated
	 * @return the result of the transformation for the disjunction
	 */
	protected abstract U transformDisjunction(Collection<U> children, Consumer<D> collection, int polarity);

	/**
	 * Transforms a conjunction of acceptance conditions.
	 *
	 * @param children   the collection of transformed child acceptance conditions
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the conjunction is being evaluated
	 * @return the result of the transformation for the conjunction
	 */
	protected abstract U transformConjunction(Collection<U> children, Consumer<D> collection, int polarity);

	/**
	 * Transforms an implication between two acceptance conditions.
	 *
	 * @param left       the transformed left-hand side of the implication
	 * @param right      the transformed right-hand side of the implication
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the implication is being evaluated
	 * @return the result of the transformation for the implication
	 */
	protected abstract U transformImplication(U left, U right, Consumer<D> collection, int polarity);

	/**
	 * Transforms an equivalence between acceptance conditions.
	 *
	 * @param children   the collection of transformed child acceptance conditions
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the equivalence is being evaluated
	 * @return the result of the transformation for the equivalence
	 */
	protected abstract U transformEquivalence(Collection<U> children, Consumer<D> collection, int polarity);

	/**
	 * Transforms an exclusive disjunction (XOR) between two acceptance conditions.
	 *
	 * @param left       the transformed left-hand side of the exclusive disjunction
	 * @param right      the transformed right-hand side of the exclusive
	 *                   disjunction
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the exclusive disjunction is being
	 *                   evaluated
	 * @return the result of the transformation for the exclusive disjunction
	 */
	protected abstract U transformExclusiveDisjunction(U left, U right, Consumer<D> collection, int polarity);

	/**
	 * Transforms a negation of an acceptance condition.
	 *
	 * @param child      the transformed child acceptance condition
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the negation is being evaluated
	 * @return the result of the transformation for the negation
	 */
	protected abstract U transformNegation(U child, Consumer<D> collection, int polarity);

	/**
	 * Transforms an atomic argument in an acceptance condition.
	 *
	 * @param argument   the argument to be transformed
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the argument is being evaluated
	 * @return the result of the transformation for the argument
	 */
	protected abstract U transformArgument(Argument argument, Consumer<D> collection, int polarity);

	/**
	 * Transforms a contradiction in an acceptance condition.
	 *
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the contradiction is being evaluated
	 * @return the result of the transformation for the contradiction
	 */
	protected abstract U transformContradiction(Consumer<D> collection, int polarity);

	/**
	 * Transforms a tautology in an acceptance condition.
	 *
	 * @param collection the consumer to collect the transformed data
	 * @param polarity   the polarity in which the tautology is being evaluated
	 * @return the result of the transformation for the tautology
	 */
	protected abstract U transformTautology(Consumer<D> collection, int polarity);

	/**
	 * We encapsulate the Visitor in a separate private class s.t. we do not
	 * pollute the public interface of AbstractTransformerCollector.
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
			Set<AcceptanceCondition> children = acc.getChildren();
			Set<U> transformedChildren = new HashSet<>(children.size());
			for (AcceptanceCondition child : children) {
				transformedChildren.add(child.accept(this, topDownData));
			}
			return transformConjunction(transformedChildren, topDownData.userObject, topDownData.polarity);
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
			Set<AcceptanceCondition> children = acc.getChildren();
			Set<U> transformedChildren = new HashSet<>(children.size());
			for (AcceptanceCondition child : children) {
				transformedChildren.add(child.accept(this, topDownData));
			}
			return transformDisjunction(transformedChildren, topDownData.userObject, topDownData.polarity);
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
			Set<AcceptanceCondition> children = acc.getChildren();
			Set<U> transformedChildren = new HashSet<>(children.size());
			for (AcceptanceCondition child : children) {
				transformedChildren.add(child.accept(this, new TopDownData<D>(0, topDownData.userObject)));
			}
			return transformEquivalence(transformedChildren, topDownData.userObject, topDownData.polarity);
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
			return transformExclusiveDisjunction(transformedLeft, transformedRight, topDownData.userObject,
					topDownData.polarity);
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
			U transformedLeft = acc.getLeft().accept(this,
					new TopDownData<D>(-topDownData.polarity, topDownData.userObject));
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
			U transformedChild = acc.getChild().accept(this,
					new TopDownData<D>(-topDownData.polarity, topDownData.userObject));
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

		private final Consumer<D> userObject;

		/**
		 * @param polarity
		 * @param userObject
		 */
		public TopDownData(int polarity, Consumer<D> userObject) {
			this.polarity = polarity;
			this.userObject = userObject;
		}

	}

}
