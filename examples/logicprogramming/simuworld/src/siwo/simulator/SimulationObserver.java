package siwo.simulator;

import siwo.syntax.WorldState;

/**
 * this interfaces defines an observer for
 * world state computations.
 * 
 * @author Thomas Vengels
 *
 */
public interface SimulationObserver {

	/**
	 * this method is called right before a world state
	 * transition takes place
	 * 
	 * @param ws world state undergoing an update
	 */
	public void onBeginStep(WorldState ws);
	
	
	/**
	 * this method is called right after a world
	 * state transition took place
	 * 
	 * @param ws new world state
	 */
	public void onEndStep(WorldState ws);
	
	/**
	 * this method is called whenever a simulation
	 * is started
	 * 
	 * @param ws initial world state description
	 */
	public void onBeginSimulation(WorldState ws);
	
	/**
	 * this method is called whenever a simulation
	 * is finished.
	 * 
	 * @param ws final world state
	 */
	public void onEndSimulation(WorldState ws);
}
