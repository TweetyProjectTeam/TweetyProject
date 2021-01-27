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
import org.tweetyproject.agents.Protocol;

/**
 * Classes implementing this interface represent protocols 
 * for games in the game theoretic meaning.
 * @author Matthias Thimm
 */
public interface GameProtocol extends Protocol{
	
	/**
	 * Returns "true" if the game has finished and a winner
	 * is determined, otherwise it returns "false"
	 * @return "true" if the game has finished and a winner
	 * is determined, otherwise it returns "false" 
	 */
	public boolean hasWinner();
	
	/**
	 * If the game has a winner, this methods returns it.
	 * Otherwise it throws a RuntimeException.
	 * @return the winner of the game.
	 */
	public Agent getWinner();
	
	/**
	 * Returns the utility of the given agent for
	 * the final situation.
	 * @param agent some agent.
	 * @return the utility of the agent for the final
	 * 	situation.
	 */
	public Double getUtility(Agent agent);
}
