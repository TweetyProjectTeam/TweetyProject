/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.agents.dialogues.oppmodels;

import java.util.Set;

import net.sf.tweety.agents.dialogues.DialogueTrace;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.GroundReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The grounded game utility function u_a^g. See definition in paper.
 * This function can either function as the proponent's utility or the
 * opposite opponent's utility.
 * 
 * @author Tjitze Rienstra, Matthias Thimm
 *
 */
public class GroundedGameUtilityFunction extends UtilityFunction<Argument,Extension> {
	
	/** Logger */
	static private Logger log = LoggerFactory.getLogger(GroundedGameUtilityFunction.class);
	
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
	private double getUtility(Extension groundedExtension, DialogueTrace<Argument,Extension> trace){
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
		log.trace("Utility of " + this.faction + " for " + trace + ": " + utility);
		return utility;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.UtilityFunction#getUtility(net.sf.tweety.agents.argumentation.DialogueTrace)
	 */
	@Override
	public double getUtility(DialogueTrace<Argument,Extension> trace) {		
		DungTheory theory = new DungTheory(this.theory.getRestriction(trace.getElements()));
		Extension groundedExtension = new GroundReasoner(theory).getExtensions().iterator().next();
		return this.getUtility(groundedExtension, trace);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.UtilityFunction#getUtility(net.sf.tweety.agents.argumentation.DialogueTrace, java.util.Set, java.util.Set)
	 */
	@Override
	public double getUtility(DialogueTrace<Argument,Extension> trace, Set<Argument> additionalArguments, Set<Attack> additionalAttacks){
		DungTheory theory = new DungTheory(this.theory.getRestriction(trace.getElements()));
		theory.addAll(additionalArguments);
		theory.addAllAttacks(additionalAttacks);
		Extension groundedExtension = new GroundReasoner(theory).getExtensions().iterator().next();
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
