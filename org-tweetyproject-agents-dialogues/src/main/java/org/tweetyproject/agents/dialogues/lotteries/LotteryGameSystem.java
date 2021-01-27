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
package org.tweetyproject.agents.dialogues.lotteries;

import org.tweetyproject.agents.MultiAgentSystem;
import org.tweetyproject.agents.AbstractProtocol;
import org.tweetyproject.agents.ProtocolTerminatedException;
import org.tweetyproject.agents.dialogues.LotteryArgumentationEnvironment;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This multi-agent system models a lottery dialogue game between
 * a lottery agent and a dummy agent
 * @author Matthias Thimm
 */
public class LotteryGameSystem extends MultiAgentSystem<AbstractLotteryAgent> {
	
	/**
	 * Creates a new grounded game system.
	 * @param universalTheory the universal Dung theory used for argumentation.
	 */
	public LotteryGameSystem(DungTheory universalTheory) {
		super(new LotteryArgumentationEnvironment(universalTheory));
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.MultiAgentSystem#add(org.tweetyproject.agents.Agent)
	 */
	@Override	
	public boolean add(AbstractLotteryAgent e) {
		if(this.size() >= 2)
			throw new IllegalArgumentException("The lottery game is only defined for two agents.");
		return super.add(e);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.MultiAgentSystem#execute(org.tweetyproject.agents.Protocol)
	 */
	@Override
	public void execute(AbstractProtocol protocol) throws ProtocolTerminatedException{
		if(this.size() != 2)		
			throw new IllegalArgumentException("The lottery game is only defined for two agents.");		
		super.execute(protocol);		
	}
}
