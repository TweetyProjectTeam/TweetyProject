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
package net.sf.tweety.agents.dialogues.lotteries;

import net.sf.tweety.agents.MultiAgentSystem;
import net.sf.tweety.agents.AbstractProtocol;
import net.sf.tweety.agents.ProtocolTerminatedException;
import net.sf.tweety.agents.dialogues.LotteryArgumentationEnvironment;
import net.sf.tweety.arg.dung.DungTheory;

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
	 * @see net.sf.tweety.agents.MultiAgentSystem#add(net.sf.tweety.agents.Agent)
	 */
	@Override	
	public boolean add(AbstractLotteryAgent e) {
		if(this.size() >= 2)
			throw new IllegalArgumentException("The lottery game is only defined for two agents.");
		return super.add(e);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.MultiAgentSystem#execute(net.sf.tweety.agents.Protocol)
	 */
	@Override
	public void execute(AbstractProtocol protocol) throws ProtocolTerminatedException{
		if(this.size() != 2)		
			throw new IllegalArgumentException("The lottery game is only defined for two agents.");		
		super.execute(protocol);		
	}
}
