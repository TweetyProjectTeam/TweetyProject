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
package net.sf.tweety.arg.adf.syntax;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * An immutable representation of acceptance conditions for ADFs.
 * <p>
 * Mirrors the structure of {@link PlFormula}.
 * 
 * @author Mathias Hofer
 *
 */
public abstract class AcceptanceCondition {

	/**
	 * 
	 * @return a stream of all arguments of this acceptance condition and its
	 *         subconditions.
	 */
	public abstract Stream<Argument> arguments();

	/**
	 * A powerful method which allows us to collect arbitrary information
	 * provided by the given transform into arbitrary data-structures provided
	 * by the collector.
	 * <p>
	 * The information we collect is defined by the generic type C of the
	 * transform and is retrieved via a {@link Consumer} the transform
	 * implementation can use in order to write out further return values
	 * besides the one of its second generic type R. Which means that we can
	 * only collect values if the given {@link Transform} returns values via its
	 * consumer. This is not necessarily the case, since for simple transform
	 * operations, like the transform from {@link AcceptanceCondition} to
	 * {@link PlFormula}, there is no need for additional return values, since
	 * its just a 1:1 mapping of its structure. In cases like
	 * {@link DefinitionalCNFTransform} such a 1:1 mapping is not the case
	 * however, in this case the consumer is used to return the generated
	 * clauses which we can then collect.
	 * 
	 * @param <C>
	 *            the type we want to collect during the transform process, e.g.
	 *            the type of the generated clauses in the case of a
	 *            CNF-transform
	 * @param <R>
	 *            the return type of each transform step, or in other words the
	 *            type of the information we pass from the bottom to the top of
	 *            the structure
	 * @param <A>
	 *            the mutable accumulation type of the reduction operation (see
	 *            {@link Collector})
	 * @param <O>
	 *            the result type of the accumulation operation (see
	 *            {@link Collector})
	 * @param transform
	 *            the transform operation
	 * @param collector
	 *            the collector which is used for collecting the information we
	 *            get during the transform operation
	 * @return the collected transformed structure
	 */
	public <C, R, A, O> O collect(Transform<C, R> transform, Collector<C, A, O> collector) {
		A container = collector.supplier().get();
		BiConsumer<A, C> accumulator = collector.accumulator();
		Consumer<C> consumer = c -> accumulator.accept(container, c);
		transform(transform, consumer, 1);
		O output = collector.finisher().apply(container);
		return output;
	}

	/**
	 * Similar to {@link #collect(Transform, Collector)} but with the main
	 * difference that it allows us access to all returned values of the given
	 * transform. Hence, the C values given to us via the {@link Consumer}
	 * parameter of the transform methods in {@link Transform} and the directly
	 * returned bottom-up information.
	 * <p>
	 * Instead of an encapsulating {@link Collector} this method expects an
	 * already initialized container of type O and a {@link BiConsumer} which
	 * works as an abstraction to the add method of the container. This
	 * abstraction is needed since the container can be completely arbitrary.
	 * 
	 * @param <C>
	 *            the type we want to collect during the transform process, e.g.
	 *            the type of the generated clauses in the case of a
	 *            CNF-transform
	 * @param <R>
	 *            the return type of each transform step, or in other words the
	 *            type of the information we pass from the bottom to the top of
	 *            the structure
	 * @param <O>
	 *            the result type of the accumulation operation (see
	 *            {@link Collector})
	 * @param transform
	 *            the transform operation
	 * @param accumulator
	 * @param container
	 * @return the result of the transformed root acceptance condition
	 */
	public <C, R, O> R collect(Transform<C, R> transform, BiConsumer<O, C> accumulator, O container) {
		Consumer<C> consumer = c -> accumulator.accept(container, c);
		R output = transform(transform, consumer, 1);
		return output;
	}

	protected abstract <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, int polarity);

	/**
	 * A simple transform operation which is expected to be used in cases where
	 * we just want to transform this recursive structure into another recursive
	 * structure. No additional return values provided by the given transform is
	 * used.
	 * <p>
	 * More generally speaking, it can be used whenever we are only interested
	 * in the result of the transformation of the root acceptance condition.
	 * Which is usually the case for recursive structures, since they have a
	 * reference to their sub-structures.
	 * 
	 * @param <R>
	 *            the return type of each transform step, or in other words the
	 *            type of the information we pass from the bottom to the top of
	 *            the structure
	 * @param transform
	 *            the transform operation
	 * @return the result of the (simple) recursive transform operation
	 */
	public <R> R transform(Transform<?, R> transform) {
		return transform(transform, c -> {}, 1);
	}

}
