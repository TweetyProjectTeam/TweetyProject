package siwo.simulator;

import net.sf.tweety.logicprogramming.asplibrary.syntax.Atom;

/**
 * this interface models a handler for
 * managing actions committed by agents.
 * 
 * @author Thomas Vengels
 *
 */
public interface ActionHandler extends SimulationObserver {

	/**
	 * this method registers an action from
	 * an agent
	 * 
	 * @param agent unique agent identifier
	 * @param action fol representation of action
	 */
	public void addAction(String agent, Atom action);
	
	/**
	 * this method deletes all actions send so
	 * far from any agent.
	 */
	public void clearAcions();
}
