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

import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.Term;

/**
 * A class to perform a stochastic walk on a reachability graph based on a probability function on that graph
 * @author Benedikt Knopp
 */
public class MarkovWalk {

	/**
	 * the graph to walk
	 */
	private ReachabilityGraph graph;
	/**
	 * the transition matrix of that graph, featuring the probabilities
	 */
	private Matrix transitionMatrix;
	/**
	 * the current state (probabiltiy distribution) during the stochastic walk
	 */
	private Matrix currentState;
	/**
	 * an upper limit of the number of discrete steps to take
	 */
	private final long MAX_ITERATIONS = 1000000;
	/**
	 * a lower limit to specify when a walk should be canceled due to minimal change in the state
	 */
	private final double TOLERANCE = 0.000001;
	
	/**
	 * Create a new instance
	 * @param graph the graph to walk
	 */
	public MarkovWalk(ReachabilityGraph graph) {
		this.graph = graph;
	}
	
	/**
	 * Initialize the walk by setting up an initial state that assigns equal probabilities
	 * to each of the graph's initial markings
	 */
	public void initializeWalk() {
		this.transitionMatrix = graph.getTransitionMatrix();
		setupInitialState();
	}
	
	private void setupInitialState() {
		this.currentState = new Matrix(1, transitionMatrix.getXDimension());
		int numberOfInitialMarkings = graph.getInitialMarkings().size();
		if(numberOfInitialMarkings == 0) {
			throw new IllegalStateException("The number of designated initial markings in this graph is zero.");
		}
		Term probability = new FloatConstant(1/Float.valueOf(numberOfInitialMarkings));
		int i = 0;
		for(Marking marking : graph.getNodes()) {
			if(graph.isInitial(marking)) {
				currentState.setEntry(0, i, probability);
			} else {
				currentState.setEntry(0, i, new FloatConstant(0));
			}
			i++;
		}
	}
	
	/**
	 * Walk the network until a stationary distribution is reached 
	 */
	public void performWalk() {
		Matrix newState;
		long iteration = 0;
		double delta = TOLERANCE + 1d;
		while(iteration < MAX_ITERATIONS &&  delta > TOLERANCE) {
			newState = this.transitionMatrix.mult(currentState).simplify();
			delta = getVectorDelta(currentState, newState);
			currentState = newState;
			iteration++;
		}
	}
	
	/**
	 * Calculate the distance between two vectors
	 * @param stateA the first vector
	 * @param stateB the second vector
	 * @return the distance
	 */
	private double getVectorDelta(Matrix stateA, Matrix stateB) {
		Matrix diff = stateA.minus(stateB);
		return diff.distanceToZero();
	}
	/**
	 * @return the currentState
	 */
	public Matrix getCurrentState() {
		return currentState;
	}

	/**
	 * @param currentState the currentState to set
	 */
	public void setCurrentState(Matrix currentState) {
		this.currentState = currentState;
	}


	
}
