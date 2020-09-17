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
package net.sf.tweety.logics.bpm.analysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.tweety.logics.bpm.syntax.BpmnModel;
import net.sf.tweety.logics.bpm.syntax.BpmnNode;
import net.sf.tweety.logics.bpm.syntax.EndEvent;
import net.sf.tweety.logics.bpm.syntax.StartEvent;

/**
 * @author Benedikt Knopp
 */
// 1 if there is a "dead end" in the sequence flow (including loops that can never terminate),
// 0 otherwise
public class IndeterminateInconsistencyMeasure implements InconsistencyMeasure<BpmnModel>{

	private BpmnModel processModel;
	// tree spanned by process model nodes,
	// in the end, all leaves must represent end events,
	// only then model is consistent
	private Map<BpmnNode, Boolean> terminations = new HashMap<>();
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
