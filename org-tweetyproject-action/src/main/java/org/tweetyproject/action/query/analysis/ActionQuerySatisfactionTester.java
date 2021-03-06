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
package org.tweetyproject.action.query.analysis;

import java.util.Set;

import org.tweetyproject.action.query.syntax.ActionQuery;
import org.tweetyproject.action.transitionsystem.TransitionSystem;
import org.tweetyproject.commons.BeliefBase;

/**
 * Classes implementing this interface are capable of checking whether a
 * transition system satisfies an action query.
 * 
 * @author Sebastian Homann
 */
public interface ActionQuerySatisfactionTester {
	/**
	 * Checks whether the given transition system satisfies the given action
	 * queries.
	 * 
	 * @param transitionSystem the transition system that will be checked for
	 *                         satisfaction.
	 * @param actionQuery      a belief base containing action queries, all of which
	 *                         have to be satisfied by the transition system.
	 * @return true iff the transition system satisfies all action queries in the
	 *         given belief base, false otherwise.
	 */
	public boolean isSatisfied(TransitionSystem transitionSystem, BeliefBase actionQuery);

	/**
	 * Checks whether the given transition system satisfies the given action
	 * queries.
	 * 
	 * @param transitionSystem the transition system that will be checked for
	 *                         satisfaction.
	 * @param actionQuery      a set of action queries, which have to be satisfied
	 *                         by the transition system.
	 * @return true iff the transition system satisfies all action queries in the
	 *         given set, false otherwise.
	 */
	public boolean isSatisfied(TransitionSystem transitionSystem, Set<ActionQuery> actionQuery);
}
