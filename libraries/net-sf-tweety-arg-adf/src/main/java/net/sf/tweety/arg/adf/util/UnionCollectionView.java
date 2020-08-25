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
package net.sf.tweety.arg.adf.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Mathias Hofer
 *
 */
public final class UnionCollectionView<E> extends AbstractUnmodifiableCollection<E>{

	private final Collection<? extends E> c1;
	
	private final Collection<? extends E> c2;
	
	public UnionCollectionView(Collection<? extends E> c1, Collection<? extends E> c2) {
		this.c1 = Objects.requireNonNull(c1);
		this.c2 = Objects.requireNonNull(c2);
	}
	
	public static <E, T extends E> Collection<E> of(Collection<? extends E> c, T elem) {
		return new UnionCollectionView<>(c, Collections.singleton(elem));
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return Stream.concat(c1.stream(), c2.stream()).iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return c1.size() + c2.size();
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return c1.isEmpty() && c2.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return c1.contains(o) || c2.contains(o);
	}

}
