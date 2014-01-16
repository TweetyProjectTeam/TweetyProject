package net.sf.tweety.agents;

import java.util.*;

/**
 * A dummy agent is an agent that cannot act.
 * 
 * @author Matthias Thimm 
 */
public class DummyAgent extends Agent {

	/** Creates a new dummy agent with the given name.
	 * @param name some name.
	 */
	public DummyAgent(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Agent#next(java.util.Collection)
	 */
	@Override
	public Executable next(Collection<? extends Perceivable> percepts) {
		throw new UnsupportedOperationException("Dummy agents cannot act.");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.getName();
	}
	
}
