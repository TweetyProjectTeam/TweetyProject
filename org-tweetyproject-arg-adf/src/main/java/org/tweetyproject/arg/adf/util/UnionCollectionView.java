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
import java.util.Objects;
import java.util.stream.Stream;

/**
 * UnionCollectionView class
 *
 * @author Mathias Hofer
 * @param <E> type
 */
public final class UnionCollectionView<E> extends AbstractUnmodifiableCollection<E> {

	/**
	 * The first collection in the union view.
	 */
	private final Collection<? extends E> c1;

	/**
	 * The second collection in the union view.
	 */
	private final Collection<? extends E> c2;

	/**
	 * Constructs a new {@code UnionCollectionView} that represents the union of the
	 * two specified collections.
	 *
	 * @param c1 the first collection to be included in the union view; must not be
	 *           {@code null}
	 * @param c2 the second collection to be included in the union view; must not be
	 *           {@code null}
	 * @throws NullPointerException if either {@code c1} or {@code c2} is
	 *                              {@code null}
	 */
	public UnionCollectionView(Collection<? extends E> c1, Collection<? extends E> c2) {
		this.c1 = Objects.requireNonNull(c1, "First collection must not be null");
		this.c2 = Objects.requireNonNull(c2, "Second collection must not be null");
	}

	/**
	 * Creates a new {@code UnionCollectionView} that represents the union of a
	 * collection and a single element.
	 *
	 * <p>
	 * This method provides a convenient way to create a union view when you have a
	 * collection
	 * and want to add a single additional element to it without modifying the
	 * original collection.
	 *
	 * @param <E>  the type of elements in the collection
	 * @param c    the collection to be included in the union view; must not be
	 *             {@code null}
	 * @param elem the single element to be included in the union view; must not be
	 *             {@code null}
	 * @return a {@code Collection<E>} representing the union of {@code c} and
	 *         {@code elem}
	 * @throws NullPointerException if either {@code c} or {@code elem} is
	 *                              {@code null}
	 */
	public static <E> Collection<E> of(Collection<? extends E> c, E elem) {
		return new UnionCollectionView<>(c, Collections.singleton(elem));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return Stream.concat(c1.stream(), c2.stream()).iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return c1.size() + c2.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return c1.isEmpty() && c2.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return c1.contains(o) || c2.contains(o);
	}

}
