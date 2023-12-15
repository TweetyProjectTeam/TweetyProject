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

import java.util.function.BinaryOperator;

/**
 * This class represents a generic Semiring, an algebraic structure with two binary operations
 * (addition and multiplication), and two corresponding identity elements (zeroElement and oneElement).
 * 
 * @param <T> The type of elements in the semiring.
 * 
 * @author Sandra Hoffmann
 */
public class Semiring<T> {
    private BinaryOperator<T> addition;
    private BinaryOperator<T> multiplication;
    private T zeroElement;
    private T oneElement;

    /**
     * Constructs a Semiring instance.
     * 
     * @param addition The binary addition operation.
     * @param multiplication The binary multiplication operation.
     * @param zeroElement The additive identity element.
     * @param oneElement The multiplicative identity element.
     */
    public Semiring(BinaryOperator<T> addition, BinaryOperator<T> multiplication, T zeroElement, T oneElement) {
        this.addition = addition;
        this.multiplication = multiplication;
        this.zeroElement = zeroElement;
        this.oneElement = oneElement;
    }

    /**
     * Retrieves the binary addition operation.
     * 
     * @return The binary addition operation.
     */
    public BinaryOperator<T> getAddition() {
        return addition;
    }

    /**
     * Retrieves the binary multiplication operation.
     * 
     * @return The binary multiplication operation.
     */
    public BinaryOperator<T> getMultiplication() {
        return multiplication;
    }

    /**
     * Retrieves the additive identity element.
     * 
     * @return The additive identity element.
     */
    public T getZeroElement() {
        return zeroElement;
    }
    
    /**
     * Retrieves the multiplicative identity element.
     * 
     * @return The multiplicative identity element.
     */
    public T getOneElement() {
        return oneElement;
    }

    /**
     * Performs the multiplication operation on two elements.
     * 
     * @param a The first operand.
     * @param b The second operand.
     * @return The result of the multiplication operation.
     */
    public T multiply(T a, T b) {
        return multiplication.apply(a, b);
    }
    
    /**
     * Performs the addition operation on two elements.
     * 
     * @param a The first operand.
     * @param b The second operand.
     * @return The result of the addition operation.
     */
    public T add(T a, T b) {
        return addition.apply(a, b);
    }
    
    /**
     * Returns true if value 'a' is larger than or equal to value 'b' in the semiring.
     * 
     * @param a The first operand.
     * @param b The second operand.
     * @return True if 'a' is larger than or equal to 'b'; otherwise, false.
     */
    public Boolean largerOrSame(T a, T b) {
        T larger = add(a, b);
        return a == larger;
    }

    /**
     * Validates and returns the given value.
     * 
     * @param value The value to be validated.
     * @return The validated value.
     */
    public T validateAndReturn(T value) {
        return value;
    }
}

