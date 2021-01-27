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
package org.tweetyproject.agents;

import java.util.*;

/**
 * This interface models an environment for agents, i.e.
 * an entity that reacts on actions with percepts.
 * 
 * @author Matthias Thimm
 */
public interface Environment {

	/**
	 * Executes the given action and returns the
	 * resulting percepts.
	 * @param action an executable
	 * @return a set of percepts.
	 */
	public Set<Perceivable> execute(Executable action);
	
	/**
	 * Executes the given actions simultaneously and returns
	 * the resulting percepts.
	 * @param actions a collection of executables.
	 * @return a set of percepts.
	 */
	public Set<Perceivable> execute(Collection<? extends Executable> actions);
	
	/**
	 * Retrieves the current percepts for the given agent.
	 * @param agent an agent.
	 * @return a set of percepts perceivable for the given agent.
	 */
	public Set<Perceivable> getPercepts(Agent agent);
	
	/** Resets the environment, i.e. brings it to
	 * its initialization state.
	 * @return "true" iff reset was successful.
	 */
	public boolean reset();
}
