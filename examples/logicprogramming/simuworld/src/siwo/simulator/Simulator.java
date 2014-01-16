package siwo.simulator;

import siwo.syntax.WorldState;

/**
 * this interface models a simulator, which
 * can compute the next world state given
 * a current world state and actions from
 * agents.
 * 
 * @author Thomas Vengels
 *
 */
public interface Simulator {

	/**
	 * this method computes a new world state from
	 * a given world state and actions committed by
	 * agents.
	 * 
	 * @param current_state current world state
	 * @return new world state
	 */
	public WorldState nextState(WorldState current_state);
	
	/**
	 * this method adds an observer to the simulator
	 * instance.
	 * 
	 * @param sl instance implementing {@link SimulationObserver}
	 */
	public void addObserver(SimulationObserver sl);
	
}
