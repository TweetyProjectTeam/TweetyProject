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

import java.util.Collection;

import org.tweetyproject.agents.Agent;
import org.tweetyproject.agents.Executable;
import org.tweetyproject.agents.Perceivable;
import org.tweetyproject.agents.dialogues.DialogueTrace;
import org.tweetyproject.agents.dialogues.ArgumentationEnvironment;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * This class represent a general arguing agent with an belief state. 
 * @author Matthias Thimm
 *
 */
public class ArguingAgent extends Agent {
	
	/** The belief state of the agent. */
	private BeliefState beliefState;
	/** The faction of the agent. */
	private GroundedGameSystem.AgentFaction faction;
	
	/**
	 * Create a new agent with the given name and belief state.
	 * @param faction the type of the agent.
	 * @param beliefState a belief state
	 */
	public ArguingAgent(GroundedGameSystem.AgentFaction faction, BeliefState beliefState) {
		super(faction.toString());
		this.faction = faction;
		this.beliefState = beliefState;
	}

	/**
	 * Returns the faction of the agent.
	 * @return the faction of the agent.
	 */
	public GroundedGameSystem.AgentFaction getFaction(){
		return this.faction;
	}
	
	/**
	 * Returns the belief state of the agent.
	 * @return the belief state of the agent.
	 */
	public BeliefState getBeliefState(){
		return this.beliefState;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.Agent#next(java.util.Collection)
	 */
	@Override
	public Executable next(Collection<? extends Perceivable> percepts) {
		// There should be just a single percept and that should be a dialogue trace
		if(percepts.size()!=1)
			throw new IllegalArgumentException("Only one percept expected.");
		if(!(percepts.iterator().next() instanceof ArgumentationEnvironment))
			throw new IllegalArgumentException("Object of type GroundedEnvironment expected.");
		ArgumentationEnvironment env = (ArgumentationEnvironment)percepts.iterator().next();
		this.beliefState.update(env.getDialogueTrace());		
		return this.beliefState.move(env);
	}
	
	/**
	 * Assess the given dialogue trace with the belief states utility function.
	 * @param trace a dialogue trace
	 * @return the utility of this agent for this dialog trace
	 */
	protected double getUtility(DialogueTrace<Argument,Extension> trace){
		return this.beliefState.getUtilityFunction().getUtility(trace);
	}

}
