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
package org.tweetyproject.logics.petri.syntax;

import org.tweetyproject.graphs.Edge;

/**
 * A class to describe arks in a Petri net
 * @author Benedikt Knopp
 */
public class Ark extends Edge<PetriNetNode> {

	/**
	 * the direction of this ark; either from its place to its transition or vice versa
	 */
	private Direction direction;
	/**
	 * the positive and finite weight of this ark
	 */
	private int weight;
	/**
	 * the place of this ark
	 */
	private Place place;
	/**
	 * the transition of this ark
	 */
	private Transition transition;
	
	/**
	 * 
	 * @author Matthias Timm
	 *
	 */
	public enum Direction {
		/** PLACE_TO_TRANSITION */
		/** TRANSITION_TO_PLACE */
		PLACE_TO_TRANSITION, TRANSITION_TO_PLACE
	}
	
	/**
	 * Create a new ark from a place to a transition
	 * @param place the place
	 * @param transition the transition
	 */
	public Ark(Place place, Transition transition) {
		super(place, transition);
		this.init(place, transition, Direction.PLACE_TO_TRANSITION, 1);
	}

	/**
	 * Create a new ark from a transition to a place
	 * @param transition the transition
	 * @param place the place	
	 */
	public Ark(Transition transition, Place place) {
		super(transition, place);
		this.init(place, transition, Direction.TRANSITION_TO_PLACE, 1);
	}
	
	/**
	 * Create a new ark from a place to a transition with a certain weight
	 * @param place the place
	 * @param transition the transition
	 * @param weight the ark weight
	 */
	public Ark(Place place, Transition transition, int weight) {
		super(place, transition);
		this.init(place, transition, Direction.PLACE_TO_TRANSITION, weight);
	}
	
	/**
	 * Create a new ark from a transition to a place with a certain weight
	 * @param transition the transition
	 * @param place the place	
	 * @param weight the ark weight	 
	 */
	public Ark(Transition transition, Place place, int weight) {
		super(transition, place);
		this.init(place, transition, Direction.TRANSITION_TO_PLACE, weight);
	}
	
	/**
	 * Initialize basic properties of this ark
	 * @param place the place
	 * @param transition the transition	
	 * @param direction the ark direction
	 * @param weight the ark weight	
	 */
	private void init(Place place, Transition transition, Direction direction, int weight) {
		this.place = place;
		this.transition = transition;
		this.direction = direction;
		this.weight = weight;
	}
	
	/**
	 * @return the ark direction, from place to transition or vice versa
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * @return the place of this ark
	 */
	public Place getPlace() {
		return place;
	}
	
	/**
	 * @return the transition of this ark
	 */
	public Transition getTransition() {
		return transition;
	}

	/**
	 * @return the ark weight
	 */
	public int getWeight() {
		return weight;
	}

	
	/**
	 * 
	 * @param weight setWeight
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	/**
	 * @return true if this ark can fire based on its weight and properties of the connected place
	 */
	public boolean canFire() {
		if(this.direction == Direction.PLACE_TO_TRANSITION) {
			return this.place.canRemoveTokens(this.weight);
		}
		return this.place.canAddTokens(this.weight);
	}
	
	/**
	 * Fire this ark and conduct respective token propagations
	 * @throws IllegalStateException if this ark can not be fired at the moment
	 * */
	public void fire() {
		if(!this.canFire()) {
			throw new IllegalStateException("This ark can not fire, but it was called to fire");
		}
		if(this.direction == Direction.PLACE_TO_TRANSITION) {
			this.place.removeTokens(this.weight);
			return;
		}
		this.place.addTokens(this.weight);
	}

	/**
	 * reverse transition firing for purposes of graph search
	 */
	public void revertFire() {
		if(this.direction == Direction.PLACE_TO_TRANSITION) {
			this.place.addTokens(this.weight);
			return;
		}
		this.place.removeTokens(this.weight);
	}


}