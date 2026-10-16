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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;

/**
 * A lazy enumeration of the models of an ADF under some semantics.
 * <p>
 * The iterator owns the resources of its computation (e.g. solver state). It
 * releases them when it is exhausted, when it fails, and when {@link #close()}
 * is called; closing is idempotent. Callers that may stop early must close the
 * iterator, preferably with try-with-resources. After closing,
 * {@link #hasNext()} returns false.
 *
 * @author Lars Bengel
 */
public interface ModelIterator extends Iterator<ThreeValuedInterpretation>, AutoCloseable {

	/**
	 * Releases all resources of this enumeration. Calling it again has no effect.
	 */
	@Override
	void close();

	/**
	 * Returns a sequential stream over the remaining models. Closing the stream
	 * closes this iterator; terminal operations do not close it implicitly.
	 *
	 * @return the stream
	 */
	default Stream<ThreeValuedInterpretation> stream() {
		Spliterator<ThreeValuedInterpretation> spliterator = Spliterators.spliteratorUnknownSize(this,
				Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL);
		return StreamSupport.stream(spliterator, false).onClose(this::close);
	}

	/**
	 * @param iterator an iterator that holds no resources
	 * @return a model iterator over its elements
	 */
	static ModelIterator of(Iterator<ThreeValuedInterpretation> iterator) {
		return new ModelIterator() {
			private boolean closed;

			@Override
			public boolean hasNext() {
				return !closed && iterator.hasNext();
			}

			@Override
			public ThreeValuedInterpretation next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return iterator.next();
			}

			@Override
			public void close() {
				closed = true;
			}
		};
	}

}
