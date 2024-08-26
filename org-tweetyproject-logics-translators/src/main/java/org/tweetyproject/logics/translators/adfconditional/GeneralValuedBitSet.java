package org.tweetyproject.logics.translators.adfconditional;

/**
 * Similar to the ThreeValuedBitSet but with arbitrary values allowed
 * Can be used for 4-valued interpretations but also for anything above that!
 *
 * @author Jonas Schumacher
 */
public final class GeneralValuedBitSet {

	private final int[] bitSet;
	private final int maxValue;

	/**
	 * Creates a new bitset with the specified size and all values set to 0
	 *
	 * @param size the fixed size of the bitset
	 * @param maxValue maxValue
	 */
	public GeneralValuedBitSet(int size, int maxValue) {
		if (size <= 0) {
			throw new IllegalArgumentException("size > 0 required!");
		}
		this.maxValue = maxValue;
		this.bitSet = new int[size];
	}

	/**
	 * Increments the value at the specified position. If the value is already set to maxValue, it resets the current position and increments position i+1
	 * The order of the values is 0, 1, 2, etc.
	 *
	 * @param i the position
	 * @return the new value at position i
	 */
	public int increment(int i) {
		bitSet[i] = (bitSet[i] + 1) % (maxValue + 1);

		// In case of overflow, increment next position
		if (bitSet[i] == 0) {
			increment(i + 1);
		}

		return bitSet[i];
	}

	/**
	 * get the value of the bit at position i
	 * @param i = position
	 * @return the value of the position
	 */
	public int get(int i) {
		return bitSet[i];
	}

	/**
	 * sets the value of the bit at position i
	 * @param i = position
	 * @param value = value to be set
	 */
	public void set(int i, int value) {
		bitSet[i] = value;
	}

	/**
	 *
	 * Return true, if all values are set to max value
	 * @return true, if all values are set to max value
	 */
	public boolean allTrue() {
		for (int i = 0; i < bitSet.length; i++) {
			if (bitSet[i] != maxValue) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the value at given position to 0
	 * @param index the index of the bit to clear
	 */
	public void clear(int index) {
		bitSet[index] = 0;
	}
	/**
	 *
	 * Return size
	 * @return size
	 */
	public int size() {
		return bitSet.length;
	}
}
