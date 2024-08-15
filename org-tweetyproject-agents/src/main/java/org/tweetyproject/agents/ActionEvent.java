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

/**
 * An action event encapsulates a multi agent system, an agent in this system, and
 * an action (which was performed by the agent).
 *
 * @author Matthias Thimm
 */
public class ActionEvent {

	private Agent agent;
	private MultiAgentSystem<? extends Agent> multiAgentSystem;
	private Executable action;

	/**
	 * Creates a new ActionEvent.
	 * @param agent an agent.
	 * @param multiAgentSystem a multi-agent system.
	 * @param action an executable.
	 */
	public ActionEvent(Agent agent, MultiAgentSystem<? extends Agent> multiAgentSystem, Executable action){
		this.action = action;
		this.agent = agent;
		this.multiAgentSystem = multiAgentSystem;
	}

	/**
	 * Return the agent.
	 * @return the agent
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * Return the  multiAgentSystem
	 * @return the multiAgentSystem
	 */
	public MultiAgentSystem<? extends Agent> getMultiAgentSystem() {
		return multiAgentSystem;
	}

	/**
	 * Return the action
	 * @return the action
	 */
	public Executable getAction() {
		return action;
	}
}
