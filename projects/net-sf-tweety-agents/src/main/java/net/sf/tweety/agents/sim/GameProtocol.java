package net.sf.tweety.agents.sim;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.Protocol;

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
