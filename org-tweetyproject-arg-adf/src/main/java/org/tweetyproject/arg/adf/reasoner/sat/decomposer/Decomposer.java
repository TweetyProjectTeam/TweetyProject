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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat.decomposer;

import java.util.Collection;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;

/**
 * The {@code Decomposer} interface defines a contract for decomposing an
 * argumentation framework or a logic system into multiple parts based on a
 * desired criterion.
 *
 * @author Mathias Hofer
 */
public interface Decomposer {

	/**
	 * Decomposes the framework or system into a collection of {@link Interpretation}
	 * objects based on the provided criterion.
	 *
	 * @param desired an integer representing the desired criterion for decomposition;
	 *                the meaning of this parameter is implementation-specific
	 *                (e.g., it could represent the number of desired interpretations
	 *                or a specific threshold for decomposition)
	 * @return a collection of decomposed {@link Interpretation} objects based on the
	 *         specified criterion
	 */
	Collection<Interpretation> decompose(int desired);

}
