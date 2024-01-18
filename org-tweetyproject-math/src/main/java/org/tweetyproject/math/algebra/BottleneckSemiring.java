/**
 * 
 */
package org.tweetyproject.math.algebra;

import java.util.function.BinaryOperator;

/**
 * This class represents a Bottleneck Semiring.
 * 
 * In this semiring, the multiplication operation corresponds to finding the maximum of two weights,
 * the addition operation corresponds to finding the minimum, 0.0 represents the
 * additive identity, and Double.Positive_Infitity represents the multiplicative identity.
 * 
 * @author Sandra Hoffmann
 */
public class BottleneckSemiring extends Semiring<Double>{
	private Double maxValue;
	
	/**
     * Constructs a BottleneckSemiring instance.
     *
	 * @param addition
	 * @param multiplication
	 * @param zeroElement
	 * @param oneElement
	 */
	public BottleneckSemiring() {
		super((a, b) -> Math.max(a, b), (a, b) -> Math.min(a, b), 0.0,
				Double.POSITIVE_INFINITY);
		maxValue = Double.MAX_VALUE - 1;
	}
	
    /**
     * Constructs a BottleneckSemiring instance with a max weight.
     */
    public BottleneckSemiring(double maxWeight) {
        super((a, b) -> Math.max(a, b), (a, b) -> Math.min(a, b), 0.0,
				Double.POSITIVE_INFINITY);
        maxValue = maxWeight;
    }
	
    /**
     * Validates and returns the given value if valid.
     * 
     * @param value The value to be validated.
     * @return The validated value.
     * @throws IllegalArgumentException If the value is outside the valid range [0.0, maxValue].
     */
    public Double validateAndReturn(Double value) {
        if (value < 0.0 || value > maxValue) {
            throw new IllegalArgumentException("Value must be between 0.0 and " + maxValue);
        }
        return value;
    }
	
	@Override
	public Double getRandomElement() {
		return random.nextDouble(maxValue);
	}

}
