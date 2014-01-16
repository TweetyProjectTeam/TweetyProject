package net.sf.tweety.agents.sim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.tweety.agents.AbstractProtocol;
import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.MultiAgentSystem;

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
	/** The average utility of each agent generator. */
	private Map<AgentGenerator<T,R>, Double> avgUtility;
	
	/**
	 * Creates a new SimulationResult for the given set of agent generators.
	 * @param agentGenerators a set of agent generators.
	 */
	public SimulationResult(List<AgentGenerator<T,R>> agentGenerators){
		this.runs = 0;
		this.wins = new HashMap<AgentGenerator<T,R>, Integer>();
		this.avgUtility = new HashMap<AgentGenerator<T,R>, Double>();
		for(AgentGenerator<T,R> aGen: agentGenerators){
			this.wins.put(aGen, 0);
			this.avgUtility.put(aGen, 0d);
		}
	}
	
	/**
	 * Adds a new entry of a simulation run.
	 * @param winner the winner of the run.
	 * @param utilities the utilities of each agent.
	 */
	public void addEntry(AgentGenerator<T,R> winner, Map<AgentGenerator<T,R>,Double> utilities){		
		for(AgentGenerator<T,R> ag: utilities.keySet()){
			this.avgUtility.put(ag, (this.avgUtility.get(ag)*this.runs + utilities.get(ag))/(this.runs+1));			
		}
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
		str += "Avg utilty:\n";
		for(AgentGenerator<T,R> ag: this.avgUtility.keySet())
			str += "\t" + ag.toString() + "=" + this.avgUtility.get(ag) +"\n";
		return str;
	}
	
	/**
	 * Returns a CSV representation of the result.
	 * @return a CSV representation of the result.
	 */
	public String csvDisplay(){
		String str = "100;";
		for(AgentGenerator<T,R> ag: this.wins.keySet())
			str += ag.toString() + ";" + this.wins.get(ag) +";";
		for(AgentGenerator<T,R> ag: this.avgUtility.keySet())
			str += ag.toString() + ";" + this.avgUtility.get(ag) +";";
		return str;
	}
	
}
