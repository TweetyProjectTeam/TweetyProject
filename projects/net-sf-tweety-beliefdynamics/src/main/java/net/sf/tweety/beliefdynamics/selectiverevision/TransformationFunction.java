package net.sf.tweety.beliefdynamics.selectiverevision;

import net.sf.tweety.*;

/**
 * This interface represents a transformation function for selective revision [Ferme:1999].
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas this transformation function works on.
 */
public interface TransformationFunction<T extends Formula> {

	/**
	 * Transforms the given formula for selective revision.
	 * @param formula some formula.
	 * @return a formula.
	 */
	public T transform(T formula);
}
