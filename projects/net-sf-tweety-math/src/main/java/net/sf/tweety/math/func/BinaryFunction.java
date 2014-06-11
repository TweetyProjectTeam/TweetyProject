package net.sf.tweety.math.func;

/**
 * Encapsulates common methods of mathematical functions with a two parameters.
 * 
 * @author Matthias Thimm
 * @param <T> The type of the domain of the first parameter
 * @param <T> The type of the domain of the second parameter
 * @param <S> The type of the co-domain
 */
public interface BinaryFunction<T extends Object,S extends Object,R extends Object> {

	/**
	 * Evaluates the function for the given elements.
	 * @param val1 some element
	 * @param val2 some element
	 * @return the value of the element.
	 */
	public R eval(T val1, S val2);
}
