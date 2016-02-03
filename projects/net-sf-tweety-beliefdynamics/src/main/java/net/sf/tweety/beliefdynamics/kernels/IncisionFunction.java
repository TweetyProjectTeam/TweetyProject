/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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
