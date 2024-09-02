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
package org.tweetyproject.arg.adf.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * A view that represents the result of subtracting one set from another.
 * Specifically, this class models the set difference: {@code superset - subset}.
 * <p>
 * The class assumes that all elements of the {@code subset} are contained in
 * the {@code superset}. This property must be ensured by the caller; the class
 * does not perform any validation checks for this condition.
 * <p>
 * This is an unmodifiable view, and all mutating operations will throw an
 * {@link UnsupportedOperationException}.
 *
 * @param <E> the type of elements in this set
 *
 * @author Mathias Hofer
 */
public final class MinusSetView<E> extends AbstractUnmodifiableCollection<E> implements Set<E> {

	private final Set<E> superset;

	private final Set<E> subset;

	/**
	 * Constructs a view of the result of superset - subset.
	 * <p>
	 * As the name suggests, it is expected that all elements of
	 * <code>subset</code> are contained in <code>superset</code>, otherwise one
	 * has to expect unreasonable results.
	 * <p>
	 * It is up to the caller to ensure this property, this class performs no
	 * additional checks.
	 *
	 * @param superset the minuend
	 * @param subset the subtrahend
	 */
	public MinusSetView(Set<E> superset, Set<E> subset) {
		this.superset = Set.copyOf(superset);
		this.subset = Set.copyOf(subset);
	}


	/**
 * Returns a view of a set that excludes a specific element.
 *
 * <p>This method creates a new set view that represents the given {@code superset} with a specified
 * element excluded. The resulting set is a view, meaning that it does not create a copy of the
 * superset but instead excludes the specified element dynamically.
 *
 * @param <E>        The type of elements in the set.
 * @param superset   The original set from which an element is to be excluded.
 * @param subtrahend The element to be excluded from the {@code superset}.
 * @return A set view that represents the {@code superset} with the {@code subtrahend} excluded.
 */
	public static <E> Set<E> of(Set<E> superset, E subtrahend) {
		return new MinusSetView<E>(superset, Set.of(subtrahend));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		return superset.size() - subset.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return superset.contains(o) && !subset.contains(o);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		if (isEmpty()) {
			return Collections.emptyIterator();
		}

		return new Iterator<E>() {
			private final Iterator<E> iterator = superset.iterator();

			private int count = 0;

			@Override
			public boolean hasNext() {
				return count < size();
			}

			@Override
			public E next() {
				E next = null;
				while (iterator.hasNext() && next == null) {
					E candidate = iterator.next();
					if (!subset.contains(candidate)) {
						next = candidate;
						count++;
					}
				}

				return next;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		boolean subsetContainsAny = false;
		for (Object o : c) {
			if (subset.contains(o)) {
				subsetContainsAny = true;
				break;
			}
		}
		return superset.containsAll(c) && !subsetContainsAny;
	}

}
