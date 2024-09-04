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

import java.util.Objects;

/**
 * Pair class
 *
 * @author Mathias Hofer
 * @param <T1> type
 * @param <T2> type
 */
public final class Pair<T1, T2> {

	/** first */
	public final T1 first;

	/** second */
	public final T2 second;

	/**
	 * Constructor
	 *
	 * @param first  some element
	 * @param second some element
	 */
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Creates a new {@code Pair} consisting of two elements.
	 *
	 * <p>
	 * This method is a convenience factory method that constructs a {@code Pair}
	 * from
	 * the two provided elements.
	 *
	 * @param <T1> The type of the first element in the pair.
	 * @param <T2> The type of the second element in the pair.
	 * @param a    The first element of the pair.
	 * @param b    The second element of the pair.
	 * @return A {@code Pair<T1, T2>} containing the two specified elements.
	 */
	public static <T1, T2> Pair<T1, T2> of(T1 a, T2 b) {
		return new Pair<>(a, b);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(first, other.first) && Objects.equals(second, other.second);
	}

}
