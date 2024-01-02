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
 * This class represents a Fuzzy Semiring, where elements are interpreted as degrees of 
 * truth in the range [0.0, 1.0].The multiplication operation corresponds to finding the minimum of two weights,
 * while the addition operation corresponds to finding the maximum, 0.0 represents the
 * additive identity, and 1.0 represents the multiplicative identity.
 * 
 * @author Sandra Hoffmann
 */
public class FuzzySemiring extends Semiring<Double> {

    /**
     * Constructs a FuzzySemiring instance.
     */
    public FuzzySemiring() {
        super((a, b) -> Math.max(a, b), (a, b) -> Math.min(a, b), 0.0, 1.0);
    }

    /**
     * Validates and returns the given fuzzy value.
     * 
     * @param value The fuzzy value to be validated.
     * @return The validated fuzzy value.
     * @throws IllegalArgumentException If the value is outside the valid range [0.0, 1.0].
     */
    public Double validateAndReturn(Double value) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Value must be between 0.0 and 1.0");
        }
        return value;
    }

	@Override
	public Double getRandomElement() {
		return random.nextDouble();
	}
}
