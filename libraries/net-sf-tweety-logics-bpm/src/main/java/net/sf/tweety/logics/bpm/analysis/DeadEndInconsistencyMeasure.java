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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.tweety.logics.bpm.syntax.EndEvent;
import net.sf.tweety.logics.bpm.syntax.BpmnModel;
import net.sf.tweety.logics.bpm.syntax.BpmnNode;
import net.sf.tweety.logics.bpm.syntax.SequenceFlow;

/**
 * @author Benedikt Knopp
 */
// number of sequence flow nodes (events and activities, apart from end-events)
// that do not have an outgoing sequence flow
public class DeadEndInconsistencyMeasure implements InconsistencyMeasure<BpmnModel>{

	@Override
	public Double inconsistencyMeasure(BpmnModel processModel) {
		Set<BpmnNode> nodes = processModel.getNodes()
				.stream()
				.filter(node -> !(EndEvent.class.isAssignableFrom(node.getClass())))
				.collect(Collectors.toSet());
		Set<SequenceFlow> sequenceFlows = processModel.getSequenceFlows();
		Collection<BpmnNode> nodesWithOutgoingEdges = new HashSet<BpmnNode>();
		for( SequenceFlow edge : sequenceFlows ) {
			BpmnNode source = edge.getNodeA();
			if(source != null) {
				nodesWithOutgoingEdges.add(source);				
			}
		}
		int numberOfProblematicNodes = nodes.size() - nodesWithOutgoingEdges.size();	
		return numberOfProblematicNodes + 0d;
	}

}
