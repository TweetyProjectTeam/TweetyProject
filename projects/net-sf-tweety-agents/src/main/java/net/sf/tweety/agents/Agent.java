/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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
