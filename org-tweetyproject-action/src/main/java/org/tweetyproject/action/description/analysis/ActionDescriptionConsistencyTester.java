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
package org.tweetyproject.action.description.analysis;

import org.tweetyproject.action.description.syntax.ActionDescription;
import org.tweetyproject.action.description.syntax.CausalLaw;
import org.tweetyproject.logics.commons.analysis.ConsistencyTester;

/**
 * Classes implementing this interface are capable of checking whether a given
 * action description is consistent according to some consistency measurements.
 *
 * @author Sebastian Homann
 * @author Tim Janus
 * @param <T> the type of causal law
 */
public interface ActionDescriptionConsistencyTester<T extends CausalLaw>
		extends ConsistencyTester<ActionDescription<T>> {

	/**
	 * Checks whether the given set of causal rules is consistent.
	 *
	 * @param causalRules a set of causal rules.
	 * @return true iff the given set of causal rules is consistent.
	 */
	boolean isConsistent(ActionDescription<T> causalRules);
}
