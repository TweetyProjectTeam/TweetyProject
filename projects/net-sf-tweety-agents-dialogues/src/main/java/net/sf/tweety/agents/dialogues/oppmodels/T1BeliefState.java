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

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.agents.dialogues.DialogueTrace;
import net.sf.tweety.agents.dialogues.ExecutableExtension;
import net.sf.tweety.agents.dialogues.ArgumentationEnvironment;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.util.Pair;

/**
 * This belief state consists of a simple recursive opponent model.
 * 
 * @author Matthias Thimm, Tjitze Rienstra
 */
public class T1BeliefState extends BeliefState {
	
	/** The opponent model of the agent (as there are only
	 * two agents in the system the model always refers to 
	 * the other agent. */
	private T1BeliefState oppModel = null;
	
	/**
	 * Creates a new T1-belief-state with the given parameters. 
	 * @param knownArguments the set of arguments known by the agent.
	 * @param utilityFunction the utility function of the agent.
	 * @param oppModel the opponent model of the agent (null if no further model is given).
	 */
	public T1BeliefState(Extension knownArguments, UtilityFunction<Argument,Extension> utilityFunction, T1BeliefState oppModel){
		super(knownArguments, utilityFunction);
		this.oppModel = oppModel;
	}
	
	/**
	 * Creates a new T1-belief-state with the given parameters and without nesting. 
	 * @param knownArguments the set of arguments known by the agent.
	 * @param utilityFunction the utility function of the agent.	 
	 */
	public T1BeliefState(Extension knownArguments, UtilityFunction<Argument,Extension> utilityFunction){
		this(knownArguments, utilityFunction, null);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.BeliefState#update(net.sf.tweety.agents.argumentation.DialogueTrace)
	 */
	@Override
	public void update(DialogueTrace<Argument,Extension> trace) {
		this.getKnownArguments().addAll(trace.getElements());
		if(this.oppModel != null)
			this.oppModel.update(trace);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.BeliefState#doMove(net.sf.tweety.agents.argumentation.oppmodels.GroundedEnvironment, net.sf.tweety.agents.argumentation.DialogueTrace)
	 */
	@Override
	public Pair<Double,Set<ExecutableExtension>> doMove(ArgumentationEnvironment env, DialogueTrace<Argument,Extension> trace) {
		double maxUtility = this.getUtilityFunction().getUtility(trace);
		Set<ExecutableExtension> bestMoves = new HashSet<ExecutableExtension>();
		Set<ExecutableExtension> ee = this.getLegalMoves(env, trace);		
		bestMoves.add(new ExecutableExtension());
		for(ExecutableExtension move: ee){
			double eu = 0;
			if(this.oppModel == null)
				eu = this.getUtilityFunction().getUtility(trace.addAndCopy(move));
			else{			
				Pair<Double,Set<ExecutableExtension>> opponentMoves = this.oppModel.doMove(env,trace.addAndCopy(move));
				for(ExecutableExtension move2: opponentMoves.getSecond()){
					// this avoids infinite loops
					// (if there are two consecutive noops the game is over anyway)
					if(move.isNoOperation() && move2.isNoOperation()){
						eu += this.getUtilityFunction().getUtility(trace.addAndCopy(move)) * 1f/opponentMoves.getSecond().size();
						continue;						
					}
					Pair<Double,Set<ExecutableExtension>> myMoves = this.doMove(env, trace.addAndCopy(move).addAndCopy(move2));
					eu += myMoves.getFirst() * 1f/opponentMoves.getSecond().size();
				}				
			}
			if(eu > maxUtility){
				maxUtility = eu;
				bestMoves.clear();
				bestMoves.add(move);
			}else if(eu == maxUtility)
				bestMoves.add(move);
		}		
		return new Pair<Double,Set<ExecutableExtension>>(maxUtility,bestMoves);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.BeliefState#clone()
	 */
	public Object clone(){
		if(this.oppModel != null)
			return new T1BeliefState(new Extension(this.getKnownArguments()), this.getUtilityFunction(), (T1BeliefState) this.oppModel.clone());
		return new T1BeliefState(new Extension(this.getKnownArguments()), this.getUtilityFunction());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.BeliefState#display()
	 */
	@Override
	public String display(){
		return this.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "<" + this.getKnownArguments() + ", " + this.getUtilityFunction() + ", " + this.oppModel + ">";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((oppModel == null) ? 0 : oppModel.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		T1BeliefState other = (T1BeliefState) obj;
		if (oppModel == null) {
			if (other.oppModel != null)
				return false;
		} else if (!oppModel.equals(other.oppModel))
			return false;
		return true;
	}
}
