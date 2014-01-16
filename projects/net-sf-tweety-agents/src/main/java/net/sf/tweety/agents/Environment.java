package net.sf.tweety.agents;

import java.util.*;

/**
 * This interface models an environment for agents, i.e.
 * an entity that reacts on actions with percepts.
 * 
 * @author Matthias Thimm
 */
public interface Environment {

	/**
	 * Executes the given action and returns the
	 * resulting percepts.
	 * @param action an executable
	 * @return a set of percepts.
	 */
	public Set<Perceivable> execute(Executable action);
	
	/**
	 * Executes the given actions simultaneously and returns
	 * the resulting percepts.
	 * @param actions a collection of executables.
	 * @return a set of percepts.
	 */
	public Set<Perceivable> execute(Collection<? extends Executable> actions);
	
	/**
	 * Retrieves the current percepts for the given agent.
	 * @param agent an agent.
	 * @return a set of percepts perceivable for the given agent.
	 */
	public Set<Perceivable> getPercepts(Agent agent);
	
	/** Resets the environment, i.e. brings it to
	 * its initialization state.
	 * @return "true" iff reset was successful.
	 */
	public boolean reset();
}
