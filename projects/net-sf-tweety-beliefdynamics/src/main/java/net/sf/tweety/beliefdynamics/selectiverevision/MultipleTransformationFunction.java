package net.sf.tweety.beliefdynamics.selectiverevision;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This interface represents a multiple transformation function for selective revision [Kruempelmann:2011,Ferme:1999].
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas this transformation function works on.
 */
public interface MultipleTransformationFunction<T extends Formula> {

	/**
	 * Transforms the given set of formulas for selective revision.
	 * @param formulas a set of formulas.
	 * @return a set of formulas.
	 */
	public Collection<T> transform(Collection<T> formulas);
}
