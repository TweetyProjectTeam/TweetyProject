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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.logics.bpm.syntax.EndEvent;
import org.tweetyproject.logics.bpm.syntax.BpmnModel;
import org.tweetyproject.logics.bpm.syntax.BpmnNode;
import org.tweetyproject.logics.bpm.syntax.SequenceFlow;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;

/**
 * The DeadEndInconsistency value of a BPMN model is the number of activities without any successor activities
 * @author Benedikt Knopp
 */
public class DeadEndInconsistencyMeasure implements BpmnInconsistencyMeasure{

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
