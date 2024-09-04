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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A view that provides the union of two disjoint sets as a single set.
 * This class does not copy the elements from the sets but merely wraps them.
 * It is expected that the two sets are disjoint, as methods like
 * {@link #size()}
 * assume that there is no overlap between the elements.
 * <p>
 * This class provides an unmodifiable view and all modification operations
 * will throw {@link UnsupportedOperationException}.
 *
 * @param <E> the type of elements in this set
 *
 * @author Mathias Hofer
 */
public final class UnionSetView<E> extends AbstractUnmodifiableCollection<E> implements Set<E> {

	private final Set<? extends E> set1;

	private final Set<? extends E> set2;

	/**
	 * Expects the two sets to be disjoint, otherwise some methods, e.g.
	 * {@link #size()}, will return unreasonable results.
	 * <p>
	 * It is up to the caller to ensure this property, this class performs
	 * no additional checks.
	 *
	 * @param set1 the first set
	 * @param set2 the second set
	 */
	public UnionSetView(Set<? extends E> set1, Set<? extends E> set2) {
		this.set1 = Set.copyOf(set1);
		this.set2 = Set.copyOf(set2);
	}

	/**
	 * Creates a {@code UnionSetView} that represents the union of three sets.
	 *
	 * <p>
	 * This static method combines the given three sets into a single unified view,
	 * where all elements
	 * from the three sets appear as one set. It does not create a new set but
	 * provides a view representing
	 * the union of the specified sets.
	 *
	 * @param <E>  The type of elements contained in the sets.
	 * @param set1 The first set to be included in the union.
	 * @param set2 The second set to be included in the union.
	 * @param set3 The third set to be included in the union.
	 * @return A {@code UnionSetView} representing the union of the three sets.
	 * @throws NullPointerException if any of {@code set1}, {@code set2}, or
	 *                              {@code set3} is {@code null}.
	 */
	public static <E> Set<E> of(Set<? extends E> set1, Set<? extends E> set2, Set<? extends E> set3) {
		return new UnionSetView<E>(set1, new UnionSetView<E>(set2, set3));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		return set1.size() + set2.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return set1.isEmpty() && set2.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return set1.contains(o) || set2.contains(o);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return Stream.concat(set1.stream(), set2.stream()).iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		return toArray(new Object[0]);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#toArray(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		Object[] array1 = set1.toArray();
		Object[] array2 = set2.toArray();

		T[] union = a;
		if (a.length < array1.length + array2.length) {
			union = (T[]) Array.newInstance(a.getClass().getComponentType(), array1.length + array2.length);
		}

		System.arraycopy(array1, 0, union, 0, array1.length);
		System.arraycopy(array2, 0, union, array1.length, array2.length);

		return union;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!set1.contains(o) && !set2.contains(o)) {
				return false;
			}
		}
		return true;
	}

}