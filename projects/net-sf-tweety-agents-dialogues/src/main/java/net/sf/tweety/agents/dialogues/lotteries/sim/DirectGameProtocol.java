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
package net.sf.tweety.agents.dialogues.lotteries.sim;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.MultiAgentSystem;
import net.sf.tweety.agents.SynchronousProtocol;
import net.sf.tweety.agents.dialogues.ArgumentationEnvironment;
import net.sf.tweety.agents.dialogues.lotteries.LotteryAgent;
import net.sf.tweety.agents.sim.GameProtocol;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;

/**
 * This class implements a direct protocol for argumentation games.
 * 
 * @author Matthias Thimm
 */
public class DirectGameProtocol extends SynchronousProtocol implements GameProtocol{
	
	public DirectGameProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem) {
		super(multiAgentSystem,1);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.GameProtocol#hasWinner()
	 */
	@Override
	public boolean hasWinner() {
		return this.hasTerminated();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.GameProtocol#getWinner()
	 */
	@Override
	public Agent getWinner() {
		// the winner is always the pro agent by default
		for(Agent a: this.getMultiAgentSystem())
			if(!((LotteryAgent)a).isDummy())
				return a;
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.GameProtocol#getUtility(net.sf.tweety.agents.Agent)
	 */
	public Double getUtility(Agent agent){
		LotteryAgent a = (LotteryAgent) agent;
		if(a.isDummy()) return 0d;
		Collection<Argument> arguments = new HashSet<Argument>();
		for(Agent b: this.getMultiAgentSystem())
			if(((LotteryAgent)b).isDummy())
				arguments.addAll(((LotteryAgent)b).getTheory());
		arguments.addAll(((ArgumentationEnvironment)this.getMultiAgentSystem().getEnvironment()).getDialogueTrace().getElements());
		DungTheory theory = ((ArgumentationEnvironment)this.getMultiAgentSystem().getEnvironment()).getPerceivedDungTheory(new Extension(arguments));		
		return ((LotteryAgent)agent).getUtilityFunction().getUtility(theory, ((LotteryAgent)agent).getSemantics());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "DirectGameProtocol";
	}

}
