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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.beliefdynamics.kernels;

import java.util.*;

import org.tweetyproject.commons.*;

/**
 * This class implements an incision function that just randomly
 * selects a minimal incision.
 * <br>
 * NOTE: results of this function are not deterministic and may not be
 * reproduced (however each
 * result is a valid incision)
 *
 * @author Matthias Thimm
 *
 * @param <T> The formula this incision function works on
 */
public class RandomIncisionFunction<T extends Formula> implements IncisionFunction<T> {
	/** Default */
	public RandomIncisionFunction() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tweetyproject.beliefdynamics.kernels.IncisionFunction#incise(java.util.
	 * Collection)
	 */
	@Override
	public Collection<T> incise(Collection<Collection<T>> kernelSets) {
		Collection<T> toBeRemoved = new HashSet<T>();
		for (Collection<T> kernel : kernelSets)
			if (!kernel.isEmpty())
				toBeRemoved.add(kernel.iterator().next());
		// check for minimality
		boolean didRemove = false;
		do {
			// for each formula in toBeRemoved check whether it can be removed
			for (T formula : toBeRemoved) {
				Collection<T> newPossibleIncision = new HashSet<T>(toBeRemoved);
				newPossibleIncision.remove(formula);
				if (this.isIncision(kernelSets, newPossibleIncision)) {
					toBeRemoved.remove(formula);
					didRemove = true;
					break;
				}
			}
		} while (didRemove);
		return toBeRemoved;
	}

	/**
	 * Checks whether possibleIncision is an incision, ie. whether each kernel is
	 * incised.
	 *
	 * @param kernelSets       a set of kernels.
	 * @param possibleIncision a possible incision.
	 * @return "true" if the given set is an incision.
	 */
	private boolean isIncision(Collection<Collection<T>> kernelSets, Collection<T> possibleIncision) {
		for (Collection<T> kernel : kernelSets) {
			if (kernel.isEmpty())
				continue;
			Collection<T> testSet = new HashSet<T>(kernel);
			testSet.retainAll(possibleIncision);
			if (testSet.isEmpty())
				return false;
		}
		return true;
	}

}
