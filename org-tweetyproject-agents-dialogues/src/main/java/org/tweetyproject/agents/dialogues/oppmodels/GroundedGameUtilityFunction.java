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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.agents.dialogues.oppmodels;

import java.util.Set;

import org.tweetyproject.agents.dialogues.DialogueTrace;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;


/**
 * The grounded game utility function u_a^g. See definition in paper.
 * This function can either function as the proponent's utility or the
 * opposite opponent's utility.
 * 
 * @author Tjitze Rienstra, Matthias Thimm
 *
 */
public class GroundedGameUtilityFunction extends UtilityFunction<Argument,Extension<DungTheory>> {
	
	
	/** The argument which is played for or against. */
	private final Argument argument;
	/** The faction of this utility function */
	private GroundedGameSystem.AgentFaction faction;
	/** The underlying Dung theory*/ 
	private final DungTheory theory;
	/** The epsilon value. */
	private final Double epsilon = 0.01d;	
	
	/**
	 * Construct utility function.
	 * 
	 * @param theory A Dung theory
	 * @param argument the argument which is played for or against.
	 * @param faction the type of this utility function.
	 */
	public GroundedGameUtilityFunction(DungTheory theory, Argument argument, GroundedGameSystem.AgentFaction faction) {
		this.theory = theory;
		this.argument = argument;
		this.faction = faction;
	}
	
	/**
	 * Determines the utility of the given trace with
	 * the given grounded extension.
	 * @param groundedExtension a grounded extension.
	 * @param trace some trace
	 * @return a utility
	 */
	private double getUtility(Extension<DungTheory> groundedExtension, DialogueTrace<Argument,Extension<DungTheory>> trace){
		double utility = 0;
		switch(this.faction){
			case PRO:
				if(groundedExtension.contains(this.argument))
					utility = 1d - (this.epsilon * (float)trace.size());				
				else//if(theory.isAttacked(this.argument, groundedExtension))
					utility = -1d - (this.epsilon * (float)trace.size());				
				break;
			case CONTRA:
				if(groundedExtension.contains(this.argument)) 
					utility = -1d - (this.epsilon * (float)trace.size());
				else//if(theory.isAttacked(this.argument, groundedExtension))
					utility = 1d - (this.epsilon * (float)trace.size());			
				break;
		}

		return utility;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.argumentation.oppmodels.UtilityFunction#getUtility(org.tweetyproject.agents.argumentation.DialogueTrace)
	 */
	@Override
	public double getUtility(DialogueTrace<Argument,Extension<DungTheory>> trace) {		
		DungTheory theory = new DungTheory(this.theory.getRestriction(trace.getElements()));
		Extension<DungTheory> groundedExtension = new SimpleGroundedReasoner().getModel(theory);
		return this.getUtility(groundedExtension, trace);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.argumentation.oppmodels.UtilityFunction#getUtility(org.tweetyproject.agents.argumentation.DialogueTrace, java.util.Set, java.util.Set)
	 */
	@Override
	public double getUtility(DialogueTrace<Argument,Extension<DungTheory>> trace, Set<Argument> additionalArguments, Set<Attack> additionalAttacks){
		DungTheory theory = new DungTheory(this.theory.getRestriction(trace.getElements()));
		theory.addAll(additionalArguments);
		theory.addAllAttacks(additionalAttacks);
		Extension<DungTheory> groundedExtension = new SimpleGroundedReasoner().getModel(theory);
		return this.getUtility(groundedExtension, trace);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "GUF";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argument == null) ? 0 : argument.hashCode());
		result = prime * result + ((epsilon == null) ? 0 : epsilon.hashCode());
		result = prime * result + ((theory == null) ? 0 : theory.hashCode());
		result = prime * result + ((faction == null) ? 0 : faction.hashCode());
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
		GroundedGameUtilityFunction other = (GroundedGameUtilityFunction) obj;
		if (argument == null) {
			if (other.argument != null)
				return false;
		} else if (!argument.equals(other.argument))
			return false;
		if (epsilon == null) {
			if (other.epsilon != null)
				return false;
		} else if (!epsilon.equals(other.epsilon))
			return false;
		if (theory == null) {
			if (other.theory != null)
				return false;
		} else if (!theory.equals(other.theory))
			return false;
		if (faction != other.faction)
			return false;
		return true;
	}
}
