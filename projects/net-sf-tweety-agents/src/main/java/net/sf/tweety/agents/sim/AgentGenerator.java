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
package net.sf.tweety.agents.sim;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.MultiAgentSystem;

/**
 * This interface defines an agent template that is used to generate
 * agents following some common characteristics.
 * 
 * @author Matthias Thimm
 * @param <T> The actual agent type.
 * @param <S> The actual MAS type.
 */
public interface AgentGenerator<T extends Agent, S extends MultiAgentSystem<T>> {

	/**
	 * Generates a new agent for the given multi-agent system. 
	 * @param mas some multi-agent system.
	 * @param params this object can be used for sharing parameters across
	 *  the generating components of a simulation.
	 * @return an agent for type T
	 */
	public T generate(S mas, SimulationParameters params);
	
	/**
	 * Set the seed for the generation. Every two
	 * runs of generations with the same seed
	 * are ensured to be identical.
	 * @param seed some seed.
	 */
	public void setSeed(long seed);
}
