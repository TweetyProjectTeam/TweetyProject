/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.agents.dialogues.lotteries.sim;


import org.tweetyproject.agents.Agent;
import org.tweetyproject.agents.MultiAgentSystem;
import org.tweetyproject.agents.SynchronousProtocol;
import org.tweetyproject.agents.dialogues.LotteryArgumentationEnvironment;
import org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.DummyLotteryAgent;
import org.tweetyproject.agents.sim.GameProtocol;
import org.tweetyproject.arg.dung.syntax.DungTheory;

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
	 * @see org.tweetyproject.agents.GameProtocol#hasWinner()
	 */
	@Override
	public boolean hasWinner() {
		return this.hasTerminated();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.GameProtocol#getWinner()
	 */
	@Override
	public Agent getWinner() {
		// the winner is always the pro agent by default
		for(Agent a: this.getMultiAgentSystem())
			if(!(a instanceof DummyLotteryAgent))
				return a;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.GameProtocol#getUtility(org.tweetyproject.agents.Agent)
	 */
	public Double getUtility(Agent agent){
		DungTheory theory = new DungTheory();
		// get theory of audience
		for(Agent b: this.getMultiAgentSystem())
			if(b instanceof DummyLotteryAgent){
				theory.addAll(((DummyLotteryAgent)b).getTheory());
				theory.addAllAttacks(((DummyLotteryAgent)b).getTheory().getAttacks());
				break;
			}
		// get disclosed arguments and attacks
		for(DungTheory action: ((LotteryArgumentationEnvironment)this.getMultiAgentSystem().getEnvironment()).getDialogueTrace().getElements()){
			theory.addAll(action);
			theory.addAllAttacks(action.getAttacks());
		}
		// get utility				
		return ((AbstractLotteryAgent)agent).getUtility(theory, ((AbstractLotteryAgent)agent).getSemantics());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "DirectGameProtocol";
	}

}
