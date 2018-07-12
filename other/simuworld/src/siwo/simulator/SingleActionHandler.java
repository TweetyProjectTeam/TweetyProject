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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import siwo.syntax.WorldState;
import net.sf.tweety.logicprogramming.asplibrary.syntax.Atom;
import net.sf.tweety.logicprogramming.asplibrary.syntax.StdTerm;

/**
 * implementation of an actions handler that
 * accepts at most one action by an agent.
 * if an agent commits multiple actions per
 * turn, only the first action is stored,
 * others are discarded silently.
 * 
 * @author Thomas Vengels
 *
 */
public class SingleActionHandler implements ActionHandler {

	Map<String,Atom>	actions = new HashMap<String,Atom>();
	
	@Override
	public void onBeginStep(WorldState ws) {
		for(Atom a : actions.values()) {
			ws.I.add(a);
		}
	}

	@Override
	public void onEndStep(WorldState ws) {
		actions.clear();
	}

	@Override
	public void onBeginSimulation(WorldState ws) {
		// nothing to do here
	}

	@Override
	public void onEndSimulation(WorldState ws) {
		// nothing to do here
	}

	@Override
	public void addAction(String agent, Atom action) {
		if (!actions.keySet().contains(agent)) {
			actions.put(agent, action.rewrite("", new StdTerm(agent)));
		}
	}

	@Override
	public void clearAcions() {
		// TODO Auto-generated method stub
		this.actions.clear();
	}

}
