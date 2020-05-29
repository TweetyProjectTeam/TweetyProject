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
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;

/**
 * The concept of a collector is similar to its sibling {@link Transformer}, we
 * want to transform an {@link AcceptanceCondition} into a different structure.
 * A collector is however more flexible since it may provide two different
 * results, one is computed while traversing through the acceptance condition
 * beginning from the root to its leaves and the other one is computed on the
 * way back from the leaves to the root.
 * <p>
 * This is best illustrated by an example, e.g. {@link TseitinTransformer} which
 * transforms an acceptance condition into a set of clauses. It starts top-down
 * by rewriting the root connective while the names of the sub-clauses are
 * computed bottom-up.
 * <p>
 * To minimize the effort for developers see {@link AbstractCollector}.
 * 
 * @author Mathias Hofer
 *
 * @param <U> the type of the additional result the collector may provide
 * @param <D> the type of the objects we want to collect
 */
public interface Collector<U, D> {

	/**
	 * Traverses through the given {@link AcceptanceCondition} and adds all the
	 * collected data to the provided collection.
	 * 
	 * @param acc
	 *            the acceptance condition
	 * @param collection
	 *            the collection which we use to store the collected data
	 * @return the result we may compute while collecting data
	 */
	default U collect(AcceptanceCondition acc, Collection<D> collection) {
		return collect(acc, collection::add);
	}

	/**
	 * Traverses through the given {@link AcceptanceCondition} and calls the
	 * provided consumer on all the collected data.
	 *  
	 * @param acc the acceptance condition
	 * @param consumer the consumer which is used as a callback for the collected data.
	 * @return the result we may compute while collecting data
	 */
	U collect(AcceptanceCondition acc, Consumer<D> consumer);

}
