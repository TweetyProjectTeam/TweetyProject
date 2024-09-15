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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.math.algebra;

/**
 * This class represents a Weighted Semiring, where elements are interpreted as weights or costs.
 * 
 * In this semiring, the multiplication operation corresponds to finding the minimum of two weights,
 * the addition operation corresponds to adding weights, Double.POSITIVE_INFINITY represents the
 * additive identity, and 0.0 represents the multiplicative identity.
 * 
 * @author Sandra Hoffmann
 */
public class WeightedSemiring extends Semiring<Double> {
	private Double maxValue;

    /**
     * Constructs a WeightedSemiring instance.
     */
    public WeightedSemiring() {
        super((a, b) -> Math.min(a, b), (a, b) -> a + b, Double.POSITIVE_INFINITY, 0.0);
        maxValue = Double.POSITIVE_INFINITY;
        //maxValue = Double.MAX_VALUE - 1;
    }
    
    /**
     * Constructs a WeightedSemiring instance with a max weight.
     */
    public WeightedSemiring(double maxWeight) {
        super((a, b) -> Math.min(a, b), (a, b) -> a + b, maxWeight, 0.0);
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


    /**
     * Generates a random element from the semiring within the specified maximum value.
     *
     * @return A randomly chosen Double element.
     */
    @Override
	public Double getRandomElement() {
		return 0.0;//random.nextDouble(maxValue);
	}

    /**
     * Performs a custom division operation on two Double values.
     * If the divisor is greater than or equal to the dividend, returns the multiplicative identity (oneElement).
     * Otherwise, returns the result of subtracting the divisor from the dividend.
     *
     * @param dividend The Double dividend.
     * @param divisor  The Double divisor.
     * @return The result of the custom division operation.
     */
    @Override
	public Double divide(Double dividend, Double divisor) {
		if (divisor >= dividend) {
			return this.oneElement;
		} else {
			return dividend - divisor;
		}

	}
	
}

