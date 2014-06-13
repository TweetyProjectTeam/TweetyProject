/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.agents;

import java.util.*;

/**
 * This class models a synchronous protocol for multi-agent systems.
 * This protocol asks all agents simultaneously for actions, and  
 * simultaneously executes these actions thereafter.
 * The protocol can optionally be defined as rigid, i.e. if at any time
 * an agent performs a "NO_OPERATION" he cannot perform any other
 * action thereafter. 
 * <br>
 * This protocol terminates when either<br>
 * - an optional number of maximal steps has been required or<br>
 * - every agent performs a NO_OPERATION at the same time or (if the
 *   protocol is defined to be rigid) after all agents performed one NO_OPERATION. 
 * 
 * @author Matthias Thimm
 */
public class SynchronousProtocol extends RigidProtocol {

	/**
	 * Indicates an unlimited number of execution steps
	 * of this protocol.
	 */
	public static final int UNLIMITED_EXECUTION = -1;
	
	/**
	 * This attribute indicates whether this protocol has terminated.
	 */
	private boolean hasTerminated = false;
	
	/**
	 * This attribute indicates the number of steps
	 * to be performed until termination of this protocol.
	 */
	private int numberOfSteps;
	
	/**
	 * Indicates whether this protocol is rigid. 
	 */
	private boolean isRigid;
	
	/**
	 * Creates a new (non-rigid) synchronous protocol for the given multi-agent system
	 * and unlimited number of steps.
	 * @param multiAgentSystem a multi-agent system.
	 */
	public SynchronousProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem) {
		this(multiAgentSystem,SynchronousProtocol.UNLIMITED_EXECUTION);
	}
	
	/**
	 * Creates a new (non-rigid) synchronous protocol for the given multi-agent system and the
	 * given number of steps.
	 * @param multiAgentSystem a multi-agent system.
	 * @param numberOfSteps the number of steps for this protocol.
	 */
	public SynchronousProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem, int numberOfSteps){
		this(multiAgentSystem,numberOfSteps,false);
	}
	
	/**
	 * Creates a new synchronous protocol for the given multi-agent system and unlimited number of steps.
	 * @param multiAgentSystem a multi-agent system.
	 * @param isRigid whether this protocol is rigid.
	 */
	public SynchronousProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem, boolean isRigid){
		this(multiAgentSystem,SynchronousProtocol.UNLIMITED_EXECUTION,isRigid);
	}
	
	/**
	 * Creates a new  synchronous protocol for the given multi-agent system.
	 * @param multiAgentSystem a multi-agent system.
	 * @param numberOfSteps the number of steps for this protocol.
	 * @param isRigid whether this protocol is rigid.
	 */
	public SynchronousProtocol(MultiAgentSystem<? extends Agent> multiAgentSystem, int numberOfSteps, boolean isRigid) {
		super(multiAgentSystem);
		if(numberOfSteps != SynchronousProtocol.UNLIMITED_EXECUTION && numberOfSteps < 0)
			throw new IllegalArgumentException("Number of steps must be greater or equal to zero.");
		this.numberOfSteps = numberOfSteps;
		if(numberOfSteps == 0) this.hasTerminated = true;
		this.isRigid = isRigid;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.RigidProtocol#hasTerminated()
	 */
	@Override
	protected boolean hasTerminated() {
		if(this.isRigid) return super.hasTerminated() && this.hasTerminated;
		return this.hasTerminated;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Protocol#doStep()
	 */
	@Override
	protected Set<ActionEvent> doStep() throws ProtocolTerminatedException{
		Set<Executable> actions = new HashSet<Executable>();
		Set<ActionEvent> actionEvents = new HashSet<ActionEvent>();
		Environment environment = this.getMultiAgentSystem().getEnvironment();
		for(Agent agent: this.getMultiAgentSystem()){
			Executable action = agent.next(environment.getPercepts(agent));
			if(!action.equals(Executable.NO_OPERATION)){
				this.setHasPerformedNoOperation(agent);
				actions.add(action);
			}
			actionEvents.add(new ActionEvent(agent,this.getMultiAgentSystem(),action));
		}
		if(!actions.isEmpty())
			environment.execute(actions);
		else this.hasTerminated = true;
		if(this.numberOfSteps != SynchronousProtocol.UNLIMITED_EXECUTION)
			this.numberOfSteps--;
		if(this.numberOfSteps == 0) this.hasTerminated = true;	
		return actionEvents;
	}
	
}
