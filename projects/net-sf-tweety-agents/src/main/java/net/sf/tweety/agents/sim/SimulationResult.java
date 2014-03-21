package net.sf.tweety.agents.sim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.tweety.agents.AbstractProtocol;
import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.MultiAgentSystem;
import net.sf.tweety.util.MathTools;

/**
 * Instances of this class summarize information
 * on a performed simulation.
 * 
 * @author Matthias Thimm
 * 
 * @param <T> The actual type of agents.
 * @param <S> The actual type of protocols.
 * @param <R> The actual type of the multi-agent system.
 */
public class SimulationResult<S extends AbstractProtocol & GameProtocol, T extends Agent, R extends MultiAgentSystem<T>> {

	/** The number of simulation runs. */
	private int runs;
	/** The number of wins of each agent generator. */
	private Map<AgentGenerator<T,R>, Integer> wins;
	/** all utilities. */
	private Map<AgentGenerator<T,R>, List<Double>> utilities;
	
	/**
	 * Creates a new SimulationResult for the given set of agent generators.
	 * @param agentGenerators a set of agent generators.
	 */
	public SimulationResult(List<AgentGenerator<T,R>> agentGenerators){
		this.runs = 0;
		this.wins = new HashMap<AgentGenerator<T,R>, Integer>();
		this.utilities = new HashMap<AgentGenerator<T,R>, List<Double>>();
		
		for(AgentGenerator<T,R> aGen: agentGenerators){
			this.wins.put(aGen, 0);
			this.utilities.put(aGen, new LinkedList<Double>());
		}
	}
	
	/**
	 * Adds a new entry of a simulation run.
	 * @param winner the winner of the run.
	 * @param utilities the utilities of each agent.
	 */
	public void addEntry(AgentGenerator<T,R> winner, Map<AgentGenerator<T,R>,Double> utilities){		
		for(AgentGenerator<T,R> ag: utilities.keySet())
			this.utilities.get(ag).add(utilities.get(ag));		
		this.runs++;
		this.wins.put(winner, this.wins.get(winner)+1);
	}
	
	/**
	 * Gives a pretty print of the results.
	 * @return a pretty print of the results.
	 */
	public String display(){
		String str = "#Runs:\t" + this.runs + "\n";
		str += "#Wins:\n";
		for(AgentGenerator<T,R> ag: this.wins.keySet())
			str += "\t" + ag.toString() + "=" + this.wins.get(ag) +"\n";
		str += "Avg utilty/variance:\n";
		for(AgentGenerator<T,R> ag: this.utilities.keySet())
			str += "\t" + ag.toString() + "=" + MathTools.averageAndVariance(this.utilities.get(ag)) +"\n";		
		return str;
	}
	
	/**
	 * Returns a CSV representation of the result.
	 * @return a CSV representation of the result.
	 */
	public String csvDisplay(){
		String str = "";
		for(AgentGenerator<T,R> ag: this.wins.keySet())
			str += ag.toString() + ";" + this.wins.get(ag) +";";
		for(AgentGenerator<T,R> ag: this.utilities.keySet())
			str += ag.toString() + ";" + MathTools.averageAndVariance(this.utilities.get(ag)) +";";
		return str;
	}
	
}
