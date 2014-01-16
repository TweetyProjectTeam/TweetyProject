package siwo.simulator;

import java.util.LinkedList;
import java.util.List;

import siwo.syntax.WorldState;

/**
 * this class implemens a basic simulation engine, handling
 * listeners and providing a skeleton for concrete simulator
 * implementations.
 * 
 * @author Thomas Vengels
 *
 */
public abstract class AbstractSimulator implements Simulator {

	List<SimulationObserver>	simLis = new LinkedList<SimulationObserver>();
	
	@Override
	public WorldState nextState(WorldState current_state) {
		for (SimulationObserver sl : simLis)
			sl.onBeginStep(current_state);
		
		WorldState ret = performStep(current_state);
		
		for (SimulationObserver sl : simLis)
			sl.onEndStep(ret);
		
		return ret;
	}

	@Override
	public void addObserver(SimulationObserver sl) {
		assert(sl != null);
		simLis.add(sl);
	}
	
	public abstract WorldState performStep(WorldState ws);

}
