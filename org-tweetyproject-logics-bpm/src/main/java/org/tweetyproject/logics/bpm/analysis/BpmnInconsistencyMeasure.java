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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.analysis;

import java.util.List;

import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;

/**
 * 
 * @author Matthias Thimm
 *
 */
public interface BpmnInconsistencyMeasure extends InconsistencyMeasure<ReachabilityGraph>{
	public List<String> getInfoStrings();
}
