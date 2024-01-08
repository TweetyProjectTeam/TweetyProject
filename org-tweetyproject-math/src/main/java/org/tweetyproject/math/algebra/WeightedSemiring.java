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
        maxValue = Double.MAX_VALUE - 1;
    }
    
    /**
     * Constructs a WeightedSemiring instance with a max weight.
     */
    public WeightedSemiring(double maxWeight) {
        super((a, b) -> Math.min(a, b), (a, b) -> a + b, maxWeight, 0.0);
        maxValue = maxWeight;
    }

	@Override
	public Double getRandomElement() {
		return random.nextDouble(maxValue);
	}
}

