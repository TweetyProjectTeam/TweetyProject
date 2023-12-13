package org.tweetyproject.math.algebra;

import java.util.function.BinaryOperator;

public class Semiring<T> {
    private BinaryOperator<T> addition;
    private BinaryOperator<T> multiplication;
    private T zeroElement;
    private T oneElement;

    public Semiring(BinaryOperator<T> addition, BinaryOperator<T> multiplication, T zeroElement, T oneElement) {
        this.addition = addition;
        this.multiplication = multiplication;
        this.zeroElement = zeroElement;
        this.oneElement = oneElement;
    }

    public BinaryOperator<T> getAddition() {
        return addition;
    }

    public BinaryOperator<T> getMultiplication() {
        return multiplication;
    }

    public T getZeroElement() {
        return zeroElement;
    }
    
    public T getOneElement() {
        return oneElement;
    }

    // Multiplication function
    public T multiply(T a, T b) {
        return multiplication.apply(a, b);
    }
    
    // Multiplication function
    public T add(T a, T b) {
        return addition.apply(a, b);
    }
    
    //returns true if value a is larger than or equal to b
    public Boolean largerOrSame(T a, T b) {
    	T larger = add(a,b);
    	return a == larger;
    }
    
    public T validateAndReturn(T value) {
        return value;
    }
}
