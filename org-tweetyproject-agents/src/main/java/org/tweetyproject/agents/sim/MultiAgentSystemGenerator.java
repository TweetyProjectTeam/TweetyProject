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
package org.tweetyproject.agents.sim;

import org.tweetyproject.agents.Agent;
import org.tweetyproject.agents.MultiAgentSystem;

/**
 * This interface defines a generator for multi-agent systems within a simulation framework.
 * It provides methods for generating a new multi-agent system and for setting a seed to
 * ensure reproducibility across different runs of the simulation.
 *
 *
 * @param <T> The type of the agent in the multi-agent system.
 * @param <S> The type of the multi-agent system.
 *
 * @author Matthias Thimm
 */
public interface MultiAgentSystemGenerator<T extends Agent, S extends MultiAgentSystem<T>> {

	/**
	 * Generates a new multi-agent system.
	 * @param params this object can be used for sharing parameters across
	 *  the generating components of a simulation.
	 * @return a multi-agent system.
	 */
	public S generate(SimulationParameters params);

	/**
	 * Set the seed for the generation. Every two
	 * runs of generations with the same seed
	 * are ensured to be identical.
	 * @param seed some seed.
	 */
	public void setSeed(long seed);
}
