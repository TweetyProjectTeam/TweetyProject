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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility;

import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * A state of the transition system for serialised semantics
 * It consists of an argumentation framework and a set of arguments
 *
 * @author Lars Bengel
 */
public class TransitionState {
	/**
	 * theory
	 */
	private DungTheory theory;
	/**
	 * extension
	 */
	private Extension<DungTheory> extension;
	/**
	 *
	 * @param theory a dung theory
	 * @param extension an extension
	 */
	public TransitionState(DungTheory theory, Extension<DungTheory> extension) {
		this.theory = theory;
		this.extension = extension;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TransitionState))
            return false;
		if (this == obj) {
			return true;
		}
		TransitionState other = (TransitionState) obj;

		return this.extension.equals(other.extension) && this.theory.equals(other.theory);
	}
	/**
	 *
	 * @return the extension
	 */
	public Extension<DungTheory> getExtension() {
		return this.extension;
	}

	/**
	 *
	 * @return the theory
	 */
	public DungTheory getTheory() {
		return this.theory;
	}
	
	/**
	 * Computes a new transition state by reducting the framework of this state by a specified extension
	 *
	 * @param transitionSet Set of arguments, used to reduct framework
	 * @return newly created transition state
	 */
	public TransitionState transitToNewState(Extension<DungTheory> transitionSet) {
		// reduct framework of the current state by the given extension newExt
		DungTheory reduct = this.theory.getReduct(transitionSet);
		Extension<DungTheory> newStateExtension = new Extension<>(this.extension);
		newStateExtension.addAll(transitionSet);
		return new TransitionState(reduct, newStateExtension);
	}
	
	/**
	 * Returns the next transition state, which is reached by reducting the framework of this state by the specified extension. 
	 * The method will only return a newly created state, if there's no equal in the collection of already existing states. 
	 * If there exists an equal state in the specified collection, then this one will be returned.
	 * 
	 * @param transitionSet Set of arguments, used to reduct framework
	 * @param existingStates States that have already been created in the associated process
	 * @return Transition State which will be reached by reducting the framework by the specified set
	 */
	public TransitionState transitToNewState(
			Extension<DungTheory> transitionSet, 
			Collection<TransitionState> existingStates){
		TransitionState output = this.transitToNewState(transitionSet);
		for (TransitionState existingState : existingStates) {
			if(existingState.equals(output)) output = existingState;
		}
		
		return output;
	}
}