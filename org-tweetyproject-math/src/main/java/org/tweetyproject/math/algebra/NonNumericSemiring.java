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

    /**
     * Enum representing the elements in the non-numeric semiring: BAD, FAIR, and GOOD.
     */
	public enum SemiringElement {
	    BAD, FAIR, GOOD
	}

    // Define the total ordering
    private static final BinaryOperator<SemiringElement> TOTAL_ORDERING = (a, b) -> {
        if (a == SemiringElement.BAD || (a == SemiringElement.FAIR && b == SemiringElement.GOOD))
            return b;
        return a;
    };

 // Define the multiplication operation
    private static final BinaryOperator<SemiringElement> MULTIPLICATION = (a, b) -> {
        if (a == SemiringElement.BAD || (a == SemiringElement.FAIR && b == SemiringElement.GOOD))
            return a;
        return b;
    };
    
    /**
     * Constructs a NonNumericSemiring with the specified total ordering, multiplication, and identity elements.
     */
    public NonNumericSemiring() {
        super(TOTAL_ORDERING, MULTIPLICATION, SemiringElement.GOOD, SemiringElement.BAD);
    }
    
    
    /**
     * Generates a random element from the semiring.
     *
     * @return A randomly chosen SemiringElement.
     */
    @Override
    public SemiringElement getRandomElement() {
        // Concrete implementation for generating a random element in the semiring
        SemiringElement[] values = SemiringElement.values();
        int randomIndex = random.nextInt(values.length);
        return values[randomIndex];
    }
    

    /**
     * Converts a non-numeric weight to its numerical representation.
     *
     * @param weight The SemiringElement to be converted.
     * @return The numerical value associated with the SemiringElement.
     */
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


    /**
     * Performs a custom division operation on two SemiringElements.
     * If the divisor has a greater or equal numerical value, returns the dividend.
     * Otherwise, returns the divisor.
     *
     * @param dividend The SemiringElement dividend.
     * @param divisor  The SemiringElement divisor.
     * @return The result of the custom division operation.
     */
    @Override
	public SemiringElement divide(SemiringElement dividend, SemiringElement divisor) {
		double numDivisor = toNumericalValue(divisor);
		double numDividend = toNumericalValue(dividend);
		if (numDivisor >= numDividend) {
			return dividend;
		} else {
			return divisor;
		}
    }

}
