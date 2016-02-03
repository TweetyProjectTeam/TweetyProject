/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.agents.dialogues.oppmodels;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.sf.tweety.agents.dialogues.DialogueTrace;
import net.sf.tweety.agents.dialogues.ExecutableExtension;
import net.sf.tweety.agents.dialogues.ArgumentationEnvironment;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.commons.util.SetTools;

/**
 * This abstract class encapsulates the common characteristics of 
 * a belief state for arguing agents.
 * 
 * @author Matthias Thimm, Tjitze Rienstra
 */
public abstract class BeliefState {
	
	/** The set of arguments known by the agent. */
	private Extension knownArguments;
	/** The utility function of the agent. */
	private UtilityFunction<Argument,Extension> utilityFunction;
	
	/**
	 * Creates a new belief-state with the given parameters. 
	 * @param knownArguments the set of arguments known by the agent.
	 * @param utilityFunction the utility function of the agent.
	 */
	public BeliefState(Extension knownArguments, UtilityFunction<Argument,Extension> utilityFunction){
		this.knownArguments = knownArguments;
		this.utilityFunction = utilityFunction;
	}
	
	/** 
	 * Given the theory from the environment and the dialogue trace, returns true if
	 * move is a valid move. 
	 * @param env the environment (gives access to universal Dung theory)
	 * @param trace the trace to be considered.
	 * @param move a possible move
	 * @return "true" if the given move is legal.
	 */
	private boolean isLegal(ArgumentationEnvironment env, DialogueTrace<Argument,Extension> trace, Set<Argument> move) {		
		/* Moves of size 1 */
		if(move.size() != 1) return false;	
		/* Enforce that all but first move attacks a previous move */
		if(!trace.isEmpty() && !env.getPerceivedDungTheory(new Extension(trace.addAndCopy(new ExecutableExtension(move)).getElements())).isAttacked(new Extension(trace.getElements()),new Extension(move)))
			return false;		
		/* Enforce conflict free moves */
		//if(env.getPerceivedDungTheory(trace.getArguments()).isAttacked(new Extension(move), new Extension(move))) return false;		
		return true;
	}
	
	/**
	 * Returns the set of possible moves in the given situation and the given trace.
	 * @param env the environment (gives access to universal Dung theory)
	 * @param trace the trace to be considered.
	 * @return the set of possible moves in the given situation.
	 */
	protected Set<ExecutableExtension> getLegalMoves(ArgumentationEnvironment env, DialogueTrace<Argument,Extension> trace){
		Set<ExecutableExtension> moves = new HashSet<ExecutableExtension>();
		Set<Argument> arguments = new HashSet<Argument>(this.knownArguments);
		arguments.removeAll(trace.getElements());
		Set<Set<Argument>> allMoves = new SetTools<Argument>().subsets(arguments);
		for(Set<Argument> move: allMoves)
			if(this.isLegal(env, trace, move))
				moves.add(new ExecutableExtension(move));
		return moves;
	}
	
	/**
	 * Returns the set of known arguments.
	 * @return the set of known arguments.
	 */
	protected Extension getKnownArguments(){
		return this.knownArguments;
	}	
	
	/**
	 * Returns the utility function of this belief state.
	 * @return the utility function of this belief state.
	 */
	protected UtilityFunction<Argument,Extension> getUtilityFunction(){
		return this.utilityFunction;
	}
	
	/** 
	 * Updates the current belief state accordingly to
	 * the given dialogue trace.
	 * @param trace a dialogue trace
	 */
	public abstract void update(DialogueTrace<Argument,Extension> trace);
	
	/**
	 * Gives the set of all best next moves with their expected utility
	 * according to the belief state and the given trace.
	 * @param env the environment (gives access to the current trace)
	 * @param trace the dialogue trace.
	 * @return the set of all best next moves with their expected utility
	 */
	protected abstract Pair<Double,Set<ExecutableExtension>> doMove(ArgumentationEnvironment env, DialogueTrace<Argument,Extension> trace);
	
	/**
	 * Pretty print of this belief state.
	 * @return a string representation of this state.
	 */
	public abstract String display();
	
	/**
	 * Gives the next best move according to the
	 * belief state and the given trace.
	 * @param env the environment (gives access to the current trace)
	 * @return a set of arguments.
	 */
	public ExecutableExtension move(ArgumentationEnvironment env){
		Pair<Double,Set<ExecutableExtension>> move = this.doMove(env, env.getDialogueTrace());
		// for know, just select one action at random
		Random rand = new Random();
		ExecutableExtension[] extensions = new ExecutableExtension[move.getSecond().size()];
		extensions = move.getSecond().toArray(extensions);
		return extensions[rand.nextInt(extensions.length)];
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract Object clone();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((knownArguments == null) ? 0 : knownArguments.hashCode());
		result = prime * result
				+ ((utilityFunction == null) ? 0 : utilityFunction.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeliefState other = (BeliefState) obj;
		if (knownArguments == null) {
			if (other.knownArguments != null)
				return false;
		} else if (!knownArguments.equals(other.knownArguments))
			return false;
		if (utilityFunction == null) {
			if (other.utilityFunction != null)
				return false;
		} else if (!utilityFunction.equals(other.utilityFunction))
			return false;
		return true;
	}
}
