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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.bpm.syntax;

import java.util.HashSet;
import java.util.Set;

/**
 * A class to represent processes in a BPMN Model
 * @author Benedikt Knopp
 */
public class Process extends BpmnElement{

	/**
	 * all nodes in this process
	 */
	
	private Set<BpmnNode> nodes = new HashSet<>();
	/**
	 * all lanes in this process
	 */
	
	private Set<Lane> lanes = new HashSet<>();
	/**
	 * all processes in this process
	 */
	private Set<Process> subprocesses = new HashSet<>();
	
	/**
	 * Create a new instance
	 */
	public Process() {
		super();
	}
	
	/**
	 * Add a node to this process
	 * @param node a node
	 */
	public void addNode (BpmnNode node) {
		this.nodes.add(node);
	}
	
	/**
	 * Add a lane to this process
	 * @param lane a Lane
	 */
	public void addLane (Lane lane) {
		this.lanes.add(lane);
	}
	
	/**
	 * Add multiple lanes to this process
	 * @param lanes some Lanes
	 */
	public void addLanes (Set<Lane> lanes) {
		this.lanes.addAll(lanes);
	}
	
	/**
	 * Add a process to this process
	 * @param subprocess a Process
	 */
	public void addSubProcess(Process subprocess) {
		this.subprocesses.add(subprocess);
	}
	
}
