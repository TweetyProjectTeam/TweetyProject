package net.sf.tweety.agents.dialogues.oppmodels.sim;

import java.util.Random;

import net.sf.tweety.agents.dialogues.oppmodels.ArguingAgent;
import net.sf.tweety.agents.dialogues.oppmodels.BeliefState;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameSystem;
import net.sf.tweety.agents.sim.AgentGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;

/**
 * Generates new arguing agents for a grounded game. Encapsulates common 
 * characteristics for specific models. 
 * @author Matthias Thimm
 */
public abstract class GroundedGameAgentGenerator implements AgentGenerator<ArguingAgent,GroundedGameSystem> {

	/** The faction of the agents to be generated */
	private GroundedGameSystem.AgentFaction faction;
	
	/** Random numbers generator. */
	private Random random = new Random();
	
	/**
	 * Creates a new agent generator.
	 * @param faction the type of the agents to be generated.
	 */
	public GroundedGameAgentGenerator(GroundedGameSystem.AgentFaction faction){
		this.faction = faction;
	}
	
	/**
	 * Returns the faction of the generated agents.
	 * @return the faction of the generated agents.
	 */
	public GroundedGameSystem.AgentFaction getFaction(){
		return this.faction;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.AgentGenerator#generate(net.sf.tweety.agents.MultiAgentSystem, net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	public ArguingAgent generate(GroundedGameSystem mas, SimulationParameters params) {
		return new ArguingAgent(this.faction,this.generateBeliefState(mas, params));
	}
		
	/**
	 * Generates a belief state for the agent wrt. the given MAS
	 * @param mas a grounded game system.
	 * @param params shared simulation parameters.
	 * @return a belief state
	 */	
	protected abstract BeliefState generateBeliefState(GroundedGameSystem mas, SimulationParameters params);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.faction.toString();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.AgentGenerator#setSeed(long)
	 */
	public void setSeed(long seed){
		this.random = new Random(seed);
	}
	
	/**
	 * Returns the random number generator.
	 * @return the random number generator.
	 */
	protected Random getRandom(){
		return this.random;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((faction == null) ? 0 : faction.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroundedGameAgentGenerator other = (GroundedGameAgentGenerator) obj;
		if (faction != other.faction)
			return false;
		return true;
	}
}
