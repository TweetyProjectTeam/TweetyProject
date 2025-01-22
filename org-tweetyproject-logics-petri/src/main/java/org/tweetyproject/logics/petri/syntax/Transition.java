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

import java.util.ArrayList;
import java.util.List;

/**
 * A class to describe transitions in a Petri net
 * @author Benedikt Knopp
 */
public class Transition extends PetriNetNode {

	/**
	 * the arks leading from the preset to this transition
	 */
	private List<Ark> incomingArks = new ArrayList<>();
	/**
	 * the arks leading from this transition to its postset
	 */
	private List<Ark> outgoingArks = new ArrayList<>();
	/**
	 *  Specify whether a transition is "final", that is, it fixes the final state
	 */
	private boolean isFinal;

	/**
	 * Create a new transition
	 * @param id a unique identifier
	 */
	public Transition(String id) {
		super(id);
	}

	/**
	 * Create a new transition
	 * @param id a unique identifier
	 * @param name a pretty name
	 */
	public Transition(String id, String name) {
		super(id, name);
	}

	/**
	 * Add a new incoming ark to this transition
	 * @param ark the incoming ark
	 */
	public void addIncomingArk(Ark ark) {
		this.incomingArks.add(ark);
	}

	/**
	 * Add a new outgoing ark to this transition
	 * @param ark the outgoing ark
	 */
	public void addOutgoingArk(Ark ark) {
		this.outgoingArks.add(ark);
	}

	/**
	 * Return true if all incoming arks and all outgoing arks of this transition can fire
	 * @return true if all incoming arks and all outgoing arks of this transition can fire
	 */
	public boolean canFire() {
		for(Ark ark: this.incomingArks) {
			if(!ark.canFire()) {
				return false;
			};
		}
		for(Ark ark: this.outgoingArks) {
			if(!ark.canFire()) {
				return false;
			};
		}
		return true;
	}

	/**
	 * fire this transition, which means firing all incoming and all outgoing arks
	 * @throws IllegalStateException if this transition can not fire at the moment
	 */
	public void fire() {
		if(!this.canFire()) {
			throw new IllegalStateException("This transition can not fire, but it was called to fire");
		}
		for(Ark ark: this.incomingArks) {
			ark.fire();
		}
		for(Ark ark: this.outgoingArks) {
			ark.fire();
		}
	}

	/**
	 * Reverse fire transition for purposes of depth-first graph search
	 */
	public void revertFire() {
		for(Ark ark: this.incomingArks) {
			ark.revertFire();
		}
		for(Ark ark: this.outgoingArks) {
			ark.revertFire();
		}
	}

	/**
	 * Mark a transition as "final", that is, it fixes the final state
	 */
	public void setFinal() {
		this.isFinal = true;
	}

	/**
	 * Check if a transition is "final", that is, it fixes the final state
	 */
	public boolean isFinal() {
		return this.isFinal;
	}


}
