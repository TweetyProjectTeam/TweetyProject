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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A multi-agent system is a collection of agents with some environment.
 * 
 * @author Matthias Thimm
 * 
 * @param T the agent class.
 */
public class MultiAgentSystem<T extends Agent> implements Collection<T>{

	/**
	 * Indicates that the execution of this system's protocol should 
	 * be repeated until it has terminated.
	 */
	public static final int EXECUTE_TILL_TERMINATION = -1;
	
	/**
	 * The agents in this multi-agent system.
	 */
	private Set<T> agents;
	
	/**
	 * The environment of this multi-agent system.
	 */
	private Environment environment;
	
	/**
	 * Creates a new empty multi-agent system with the given environment.
	 * @param environment some environment.
	 */
	public MultiAgentSystem(Environment environment){
		this(environment, new HashSet<T>());
	}
	
	/**
	 * Creates a new multi-agent system with the given collection of agents.
	 * @param agents a collection of agents.
	 * @param environment some environment.
	 */
	public MultiAgentSystem(Environment environment, Collection<? extends T> agents){
		this.agents = new HashSet<T>(agents);
		this.environment = environment;
	}
	
	/**
	 * Returns the environment of this multi-agent system.
	 * @return the environment of this multi-agent system.
	 */
	public Environment getEnvironment(){
		return this.environment;
	}
	
	/**
	 * Executes the given protocol for the given number of steps.
	 * Set steps to Protocol.EXECUTE_TILL_TERMINATION to have the
	 * protocol executed till termination.
	 * @param protocol a protocol.
	 * @param numOfSteps the number of steps to be executed.
	 * @return "true" if the protocol has terminated after executing the given
	 * 		number of steps.	 
	 * @throws ProtocolTerminatedException if the protocol terminates before all
	 * 		steps could be executed.
	 * @throws IllegalArgumentException if the given number of steps is zero or
	 * 	negative and not equal to Protocol.EXECUTE_TILL_TERMINATION.
	 */
	public boolean execute(AbstractProtocol protocol, int numOfSteps) throws ProtocolTerminatedException, IllegalArgumentException{
		if(numOfSteps <= 0 && numOfSteps != EXECUTE_TILL_TERMINATION)
			throw new IllegalArgumentException("Illegal number of steps: " + numOfSteps);
		if(numOfSteps == EXECUTE_TILL_TERMINATION){
			while(!protocol.hasTerminated())
				protocol.step();			
			return true;
		}
		for(int i = 0; i < numOfSteps;i++){
			protocol.step();
		}
		return protocol.hasTerminated();
	}
	
	/**
	 * Executes the given protocol till it terminates.
	 * @param protocol a protocol.
	 * @throws ProtocolTerminatedException if the protocol has already terminated.
	 */
	public void execute(AbstractProtocol protocol) throws ProtocolTerminatedException{
		this.execute(protocol, EXECUTE_TILL_TERMINATION);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(T e) {
		return this.agents.add(e);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return this.agents.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.agents.clear();		
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {		
		return this.agents.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.agents.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.agents.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.agents.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.agents.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.agents.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.agents.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return this.agents.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.agents.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <S> S[] toArray(S[] a) {
		return this.agents.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agents == null) ? 0 : agents.hashCode());
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
		MultiAgentSystem<?> other = (MultiAgentSystem<?>) obj;
		if (agents == null) {
			if (other.agents != null)
				return false;
		} else if (!agents.equals(other.agents))
			return false;
		return true;
	}
	
}
