package net.sf.tweety.beliefdynamics.kernels;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This interface models an incision function for kernel contraction, ie
 * a function that incises each of the kernel sets of some set.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The formula this incision function works on
 */
public interface IncisionFunction<T extends Formula> {

	/**
	 * Selects from each collection in the given collection one element and
	 * returns the collection of all those elements.
	 * @param kernelSets a collection of kernel sets.
	 * @return the selected elements.
	 */
	public Collection<T> incise(Collection<Collection<T>> kernelSets);
}
