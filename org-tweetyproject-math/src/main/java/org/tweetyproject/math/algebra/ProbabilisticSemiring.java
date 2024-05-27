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
 *  Copyright 20234 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.math.algebra;

/**
 * This class represents a probabilistic (Viterbi) Semiring, where elements range between [0.0, 1.0].The addition operation corresponds to finding the maximum, 0.0 represents the
 * additive identity, and 1.0 represents the multiplicative identity.
 * 
 * @author Sandra Hoffmann
 *
 */
public class ProbabilisticSemiring extends Semiring<Double>{

    /**
     * Constructs a ProbabilisticSemiring instance.
     */
	public ProbabilisticSemiring() {
		super((a, b) -> Math.max(a, b), (a, b) -> a * b, 0.0, 1.0);
	}
	

    /**
     * Validates and returns the given probabilistic value.
     * 
     * @param value The value to be validated.
     * @return The validated value.
     * @throws IllegalArgumentException If the value is outside the valid range [0.0, 1.0].
     */
    public Double validateAndReturn(Double value) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Value must be between 0.0 and 1.0");
        }
        return value;
    }

    /**
     * Generates a random Double element within the specified maximum value.
     *
     * @return A random Double element.
     */
    @Override
	public Double getRandomElement() {
		return random.nextDouble();
	}

    /**
     * Performs a custom division operation on two Double values within the context of a Probabilistic Semiring.
     * If the divisor is greater than or equal to the dividend, returns the multiplicative identity (oneElement).
     * Otherwise, returns the result of dividing the dividend by the divisor.
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
			return dividend /divisor;
		}
	}

}
