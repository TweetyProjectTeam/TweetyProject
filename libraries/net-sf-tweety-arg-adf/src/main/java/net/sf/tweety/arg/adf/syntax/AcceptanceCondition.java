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

/**
 * TODO make contract of collect s.t. it is consistent with the one of stream
 * 
 * @author Mathias Hofer
 *
 */
public abstract class AcceptanceCondition {

	public abstract Stream<Argument> arguments();

	/**
	 * 
	 * @param transform
	 * @param collector
	 * @return
	 */
	public <C, R, A, O> O collect(Transform<C, R> transform, Collector<C, A, O> collector) {
		A container = collector.supplier().get();
		BiConsumer<A,C> accumulator = collector.accumulator();
		Consumer<C> consumer = c -> accumulator.accept(container, c);
		transform(transform, consumer, 1);
		O output = collector.finisher().apply(container);
		return output;
	}
	
	/**
	 * 
	 * @param transform
	 * @param collector
	 * @param container
	 * @return
	 */
	public <C, R, O> R collect(Transform<C, R> transform, BiConsumer<O,C> accumulator, O container) {
		Consumer<C> consumer = c -> accumulator.accept(container, c);
		R output = transform(transform, consumer, 1);
		return output;
	}

	protected abstract <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, int polarity);

	/**
	 * 
	 * @param transform
	 * @return the result of the (simple) recursive transform operation
	 */
	public <R> R transform(Transform<?, R> transform) {
		return transform(transform, c -> {}, 1);
	}

}
