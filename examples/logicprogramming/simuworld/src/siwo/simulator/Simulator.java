/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
