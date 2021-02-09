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
package org.tweetyproject.logics.petri.syntax.reachability_graph;

import org.tweetyproject.graphs.Edge;
import org.tweetyproject.logics.petri.syntax.Transition;

/**
 * A class to describe markings of a Petri net.
 * A MarkingEdge is a directed edge between two markings indicating that the target marking
 * can be reached from the source marking by firing some transition
 * @author Benedikt Knopp
 */
public class MarkingEdge extends Edge<Marking> implements Comparable<MarkingEdge> {

	/**
	 * the transition which, when fired, leads to the corresponding change of markings
	 */
	private Transition transition;

	/**
	 * Create a new instance
	 * @param from the original marking
	 * @param to the resulting marking
	 * @param transition the transition which, when fired, leads to this change of markings
	 */
	public MarkingEdge(Marking from, Marking to, Transition transition) {
		super(from, to);
		this.setTransition(transition);
	}
	
	/**
	 * @return the transition
	 */
	public Transition getTransition() {
		return transition;
	}

	/**
	 * @param transition the transition to set
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	@Override
	public int compareTo(MarkingEdge that) {
		// MarkingEdge implements Comparable in order to 
		// have a fixed (but arbitrary) order among all edges
		// for that purpose we just use the hash code.
		return this.hashCode() - that.hashCode();
	}


}
