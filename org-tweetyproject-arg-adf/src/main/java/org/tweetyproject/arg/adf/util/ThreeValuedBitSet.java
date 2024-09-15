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

import java.util.BitSet;

/**
 * Inspired by {@link BitSet} but with three values.
 *
 * @author Mathias Hofer
 *
 */
public final class ThreeValuedBitSet {

	private final Boolean[] bitSet;

	/**
	 * Creates a new bitset with the specified size and all values undefined.
	 *
	 * @param size the fixed size of the bitset
	 */
	public ThreeValuedBitSet(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("size > 0 required!");
		}
		this.bitSet = new Boolean[size];
	}

	/**
	 * Increments the value at the specified position. If the value is already
	 * true, it resets the current position and increments the one at i + 1.
	 * <p>
	 * The order of the values is Undefined &lt; False &lt; True.
	 *
	 * @param i the position
	 * @return the new value at position i
	 */
	public Boolean increment(int i) {
		Boolean oldValue = bitSet[i];
		bitSet[i] = next(oldValue);

		if (oldValue != null && oldValue) {
			increment(i + 1);
		}

		return bitSet[i];
	}

	/**
	 * Getter
	 *
	 * @param i index
	 * @return bit at index
	 */
	public Boolean get(int i) {
		return bitSet[i];
	}

	/**
	 * Setter value
	 *
	 * @param i     index
	 * @param value value to set
	 */
	public void set(int i, Boolean value) {
		bitSet[i] = value;
	}

	/**
	 * Checks if all elements in the {@code bitSet} are {@code true}.
	 *
	 * @return {@code true} if all elements in the {@code bitSet} are {@code true},
	 *         otherwise {@code false}.
	 */
	public boolean allTrue() {
		// TODO make O(1)
		for (int i = 0; i < bitSet.length; i++) {
			if (bitSet[i] == null || !bitSet[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the value at position to undecided resp. <code>null</code>
	 *
	 * @param index the index of the bit to clear
	 */
	public void clear(int index) {
		bitSet[index] = null;
	}

	/**
	 *
	 * Return size
	 * @return size
	 */
	public int size() {
		return bitSet.length;
	}

	private Boolean next(Boolean bool) {
		if (bool == null) {
			return false;
		} else if (bool) {
			return null;
		} else {
			return true;
		}
	}

	private static char toChar(Boolean bool) {
		if (bool == null) {
			return 'u';
		} else if (bool) {
			return 't';
		} else {
			return 'f';
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		builder.append(toChar(bitSet[0]));
		for (int i = 1; i < bitSet.length; i++) {
			builder.append(",");
			builder.append(toChar(bitSet[i]));
		}
		builder.append("]");
		return builder.toString();
	}

}
