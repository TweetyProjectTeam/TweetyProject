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
package net.sf.tweety.agents.dialogues.oppmodels;

import net.sf.tweety.agents.MultiAgentSystem;
import net.sf.tweety.agents.AbstractProtocol;
import net.sf.tweety.agents.ProtocolTerminatedException;
import net.sf.tweety.agents.dialogues.ArgumentationEnvironment;
import net.sf.tweety.arg.dung.DungTheory;

/**
 * This multi-agent system models a grounded dialogue game between
 * two agents.
 * @author Matthias Thimm
 */
public class GroundedGameSystem extends MultiAgentSystem<ArguingAgent> {
	
	/** The factions of agents in this game. */
	public enum AgentFaction {
		PRO, CONTRA;
		public AgentFaction getComplement(){
			if(this.equals(CONTRA))
				return PRO;
			return CONTRA;
		}
	};
	
	/**
	 * Creates a new grounded game system.
	 * @param universalTheory the universal Dung theory used for argumentation.
	 */
	public GroundedGameSystem(DungTheory universalTheory) {
		super(new ArgumentationEnvironment(universalTheory));
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.MultiAgentSystem#add(net.sf.tweety.agents.Agent)
	 */
	@Override	
	public boolean add(ArguingAgent e) {
		if(this.size() >= 2)
			throw new IllegalArgumentException("The grounded game is only defined for two agents.");
		return super.add(e);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.MultiAgentSystem#execute(net.sf.tweety.agents.Protocol)
	 */
	@Override
	public void execute(AbstractProtocol protocol) throws ProtocolTerminatedException{
		if(this.size() != 2)		
			throw new IllegalArgumentException("The grounded game is only defined for two agents.");
		ArguingAgent a1 = (ArguingAgent)this.toArray()[0];
		ArguingAgent a2 = (ArguingAgent)this.toArray()[1];
		if(!a1.getFaction().equals(AgentFaction.PRO) && !a2.getFaction().equals(AgentFaction.PRO))		
			throw new IllegalArgumentException("The grounded game is only defined if there is one PRO agent.");
		if(!a1.getFaction().equals(AgentFaction.CONTRA) && !a2.getFaction().equals(AgentFaction.CONTRA))
			throw new IllegalArgumentException("The grounded game is only defined if there is one CONTRA agent.");
		super.execute(protocol);
	}
}
