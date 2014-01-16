package net.sf.tweety.agents;

import java.util.*;

/**
 * An agent is a possibly proactive entity situated in some environment.
 * 
 * @author Matthias Thimm
 */
public abstract class Agent {
	
	/**
	 * The name of the agent. 
	 */
	private String name;
	
	/**
	 * Creates a new agent with the given name.
	 * @param name some string.
	 */
	public Agent(String name){
		this.name = name;
	}
	
	/**
	 * Returns the name of this agent.
	 * @return the name of this agent.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Determines the next action of this agent wrt. the given percepts.
	 * @param percepts a collection of percepts.
	 * @return an action.
	 */
	public abstract Executable next(Collection<? extends Perceivable> percepts);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.name;
	}
	
}
