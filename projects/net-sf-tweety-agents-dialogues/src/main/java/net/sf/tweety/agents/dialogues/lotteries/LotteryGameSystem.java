package net.sf.tweety.agents.dialogues.lotteries;

import net.sf.tweety.agents.MultiAgentSystem;
import net.sf.tweety.agents.AbstractProtocol;
import net.sf.tweety.agents.ProtocolTerminatedException;
import net.sf.tweety.agents.dialogues.ArgumentationEnvironment;
import net.sf.tweety.arg.dung.DungTheory;

/**
 * This multi-agent system models a lottery dialogue game between
 * a lottery agent and a dummy agent
 * @author Matthias Thimm
 */
public class LotteryGameSystem extends MultiAgentSystem<LotteryAgent> {
	
	/**
	 * Creates a new grounded game system.
	 * @param universalTheory the universal Dung theory used for argumentation.
	 */
	public LotteryGameSystem(DungTheory universalTheory) {
		super(new ArgumentationEnvironment(universalTheory));
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.MultiAgentSystem#add(net.sf.tweety.agents.Agent)
	 */
	@Override	
	public boolean add(LotteryAgent e) {
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
