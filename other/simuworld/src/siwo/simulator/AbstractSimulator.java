/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
