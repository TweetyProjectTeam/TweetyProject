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
package org.tweetyproject.agents.dialogues.oppmodels;

import org.tweetyproject.agents.MultiAgentSystem;
import org.tweetyproject.agents.AbstractProtocol;
import org.tweetyproject.agents.ProtocolTerminatedException;
import org.tweetyproject.agents.dialogues.ArgumentationEnvironment;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This multi-agent system models a grounded dialogue game between
 * two agents.
 * @author Matthias Thimm
 */
public class GroundedGameSystem extends MultiAgentSystem<ArguingAgent> {

	/** The factions of agents in this game. */
	public enum AgentFaction {
    /** The faction that supports the argument or proposition (Pro position). */
    PRO,

    /** The faction that opposes the argument or proposition (Contra position). */
    CONTRA;

    /**
     * Returns the complement of the current faction.
     * If the faction is CONTRA, the complement is PRO.
     * If the faction is PRO, the complement is CONTRA.
     *
     * @return The complementary faction of this faction (either PRO or CONTRA).
     */
    public AgentFaction getComplement() {
        if (this.equals(CONTRA))
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
	 * @see org.tweetyproject.agents.MultiAgentSystem#add(org.tweetyproject.agents.Agent)
	 */
	@Override
	public boolean add(ArguingAgent e) {
		if(this.size() >= 2)
			throw new IllegalArgumentException("The grounded game is only defined for two agents.");
		return super.add(e);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.MultiAgentSystem#execute(org.tweetyproject.agents.Protocol)
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
