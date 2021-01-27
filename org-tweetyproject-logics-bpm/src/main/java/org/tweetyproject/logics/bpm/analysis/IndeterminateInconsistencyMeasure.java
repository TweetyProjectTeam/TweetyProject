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
package org.tweetyproject.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.logics.bpm.syntax.BpmnModel;
import org.tweetyproject.logics.bpm.syntax.BpmnNode;
import org.tweetyproject.logics.bpm.syntax.EndEvent;
import org.tweetyproject.logics.bpm.syntax.StartEvent;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;

/**
 * A BPMN model has an IndeterminateInconsistency value of 1 if there is an activity in this model 
 * a) that is reachable from a start event via the model's sequence flow
 * b) from where no end event can possibly be reached, 
 * and 0 otherwise
 * @author Benedikt Knopp
 */
public class IndeterminateInconsistencyMeasure implements BpmnInconsistencyMeasure{

	/**
	 * the BPMN model for which the inconsistency value is to find
	 */
	private BpmnModel processModel;
	
	/**
	 * for each node in the process model, remember whether an end event can possibly be reached from that node
	 */
	private Map<BpmnNode, Boolean> terminations = new HashMap<>();
	
	/**
	 * for each node in the process model, remember which nodes may lead back to that node
	 */
	private Map<BpmnNode, Set<BpmnNode>> loopEntries = new HashMap<>();
	
	@Override
	public Double inconsistencyMeasure(BpmnModel processModel) {
		this.processModel = processModel;
		ArrayList<BpmnNode> startEvents = new ArrayList<>();
		startEvents.addAll( processModel.getNodesOfType(StartEvent.class) );
		boolean terminates = true;
		for(BpmnNode startEvent : startEvents) {
			LinkedList<BpmnNode> path = new LinkedList<>();
			path.add(startEvent);
			terminates = terminates && terminates(path);
		}
		return terminates ? 0d : 1d;
	} 
	

	/**
	 * @param path the sequence of nodes visited so far in the sequence flow of the BPMN model
	 * @return true iff an end event can always be reached from the current node,
	 * no matter the future course
	 */
	private boolean terminates(LinkedList<BpmnNode> path) {
				
		BpmnNode current = path.getLast();
		
		if(EndEvent.class.isAssignableFrom(current.getClass())){
			terminations.put(current, true);
			return true;			
		}

		if(terminations.containsKey(current)) {
			return terminations.get(current);
		}

		loopEntries.putIfAbsent(current, new HashSet<>());
		Set<BpmnNode> successors = processModel.getSequenceFlowSuccessors(current)
			.stream()
			// ignore loops
			.filter(successor -> !loopEntries.get(current).contains(successor))
			.collect(Collectors.toSet());
		
		if(successors.size() == 0) {
			terminations.put(current, false);
			return false;
		}
		
		boolean terminates = true;
		for(BpmnNode successor : successors) {
			if(path.contains(successor)) {
				// loop detection: we've been there before
				BpmnNode loopEntry = path.get(path.indexOf(successor) + 1 );
				loopEntries.get(successor).add(loopEntry);
			}
			LinkedList<BpmnNode> newPath = new LinkedList<>(path);
			newPath.add(successor);
			// depth-first
			terminates = terminates && terminates(newPath);
		}
		terminations.put(current, terminates);
		return terminates;
		
	}
	
}
