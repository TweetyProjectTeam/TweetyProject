package net.sf.tweety.commons.analysis;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Interpretation;

/**
 * Classes implementing this interface represent distance functions
 * between two interpretations.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The actual type of interpretation used
 */
public interface InterpretationDistance<T extends Interpretation,S extends Formula> {

	/**
	 * Measures the distance between the two given interpretations.
	 * @param a some interpretation
	 * @param b some interpretation
	 * @return the distance between the two given interpretations.
	 */
	public double distance(T a, T b);
	
	/**
	 * Measures the distance between a formula and some
	 * interpretation by taking the minimal distance from all models
	 * of the formula to the given interpretation.
	 * @param f some formula
	 * @param b some interpretation.
	 * @return the distance between the set of models of the formula to the
	 * 	given interpretation.
	 */
	public double distance(S f, T b);
}
