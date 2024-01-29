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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.math.algebra;

/**
 * This class represents an example for a non numeric semiring. This implementation uses an example semiring from the following paper: Bistarelli, Stefano, and Francesco Santini. "Weighted Argumentation." FLAP 8.6 (2021): 1589-1622.
 * @author Sandra Hoffmann
 *
 */
import java.util.function.BinaryOperator;



public class NonNumericSemiring extends Semiring<org.tweetyproject.math.algebra.NonNumericSemiring.SemiringElement> {

	// Enum for the elements in the semiring
	public enum SemiringElement {
	    BAD, FAIR, GOOD
	}

    // Define the total ordering
    private static final BinaryOperator<SemiringElement> TOTAL_ORDERING = (a, b) -> {
        if (a == SemiringElement.BAD || (a == SemiringElement.FAIR && b == SemiringElement.GOOD))
            return b;
        return a;
    };

    public NonNumericSemiring() {
        super(TOTAL_ORDERING, (a, b) -> a == SemiringElement.BAD ? SemiringElement.BAD : b, SemiringElement.GOOD, SemiringElement.BAD);
    }
    
    
    @Override
    public SemiringElement getRandomElement() {
        // Concrete implementation for generating a random element in the semiring
        SemiringElement[] values = SemiringElement.values();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }
    
    @Override
    public double toNumericalValue(SemiringElement weight) {
        // Convert non-numeric weight to numeric value
        switch (weight) {
            case BAD:
                return 0.0;
            case FAIR:
                return 1.0;
            case GOOD:
                return 2.0;
            default:
                throw new IllegalArgumentException("Unknown SemiringElement: " + weight);
        }
    }


	@Override
	public SemiringElement divide(SemiringElement dividend, SemiringElement divisor) {
		double numDivisor = toNumericalValue(divisor);
		double numDividend = toNumericalValue(dividend);
		if (numDivisor < numDividend) {
			return SemiringElement.BAD;
		} else {
			return dividend;
		}
    }
}
