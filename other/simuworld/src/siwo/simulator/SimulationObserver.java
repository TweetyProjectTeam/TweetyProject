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
