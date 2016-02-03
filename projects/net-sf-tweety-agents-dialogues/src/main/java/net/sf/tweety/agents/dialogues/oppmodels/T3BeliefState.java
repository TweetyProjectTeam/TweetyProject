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
import java.util.Set;

import net.sf.tweety.agents.dialogues.DialogueTrace;
import net.sf.tweety.agents.dialogues.ExecutableExtension;
import net.sf.tweety.agents.dialogues.ArgumentationEnvironment;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.probability.ProbabilityFunction;

/**
 * This belief state consists of a probability distribution over 
 * other opponent models with virtual arguments.
 * 
 * @author Tjitze Rienstra, Matthias Thimm
 */
public class T3BeliefState extends BeliefState implements Comparable<T3BeliefState>{

	/** The set of virtual arguments assumed to exist. */
	private Set<Argument> virtualArguments;	
	/** The set of virtual attacks assumed to exist between
	 * virtual and ordinary arguments. */
	private Set<Attack> virtualAttacks;
	/** The recognition function for recognizing ordinary arguments. */
	private RecognitionFunction rec;
	/** The probability function on opponent models*/
	private ProbabilityFunction<T3BeliefState> prob;
		
	/**
	 * Creates a new T3-belief-state with the given parameters. 
	 * @param knownArguments the set of arguments known by the agent.
	 * @param utilityFunction the utility function of the agent.
	 * @param prob the probability function over opponent models.
	 */
	public T3BeliefState(Extension knownArguments, UtilityFunction<Argument,Extension> utilityFunction, Set<Argument> virtualArguments, Set<Attack> virtualAttacks, RecognitionFunction rec, ProbabilityFunction<T3BeliefState> prob){
		super(knownArguments, utilityFunction);
		this.virtualArguments = virtualArguments;
		this.virtualAttacks = virtualAttacks;
		this.rec = rec;
		this.prob = prob;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.BeliefState#update(net.sf.tweety.agents.argumentation.DialogueTrace)
	 */
	@Override
	public void update(DialogueTrace<Argument,Extension> trace) {
		this.getKnownArguments().addAll(trace.getElements());
		for(Argument a: trace.getElements())
			if(this.rec.get(a) != null)
				this.virtualArguments.removeAll(this.rec.get(a));
		Set<Attack> newVirtualAttacks = new HashSet<Attack>();
		for(Attack a: this.virtualAttacks)
			if(this.virtualArguments.contains(a.getAttacker()) || this.virtualArguments.contains(a.getAttacked()))
				newVirtualAttacks.add(a);
		this.virtualAttacks = newVirtualAttacks;
		ProbabilityFunction<T3BeliefState> newProb = new ProbabilityFunction<T3BeliefState>();
		for(T3BeliefState state: this.prob.keySet()){
			Probability p = this.prob.get(state);
			state.update(trace);
			if(newProb.keySet().contains(state))
				newProb.put(state, newProb.get(state).add(p));
			else newProb.put(state, p);			
		}
		this.prob = newProb;		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.BeliefState#doMove(net.sf.tweety.agents.argumentation.oppmodels.GroundedEnvironment, net.sf.tweety.agents.argumentation.DialogueTrace)
	 */
	@Override
	protected Pair<Double, Set<ExecutableExtension>> doMove(ArgumentationEnvironment env, DialogueTrace<Argument,Extension> trace) {
		double bestEU = this.getUtilityFunction().getUtility(env.getDialogueTrace(), this.virtualArguments, this.virtualAttacks);
		Set<ExecutableExtension> bestMoves = new HashSet<ExecutableExtension>();
		bestMoves.add(new ExecutableExtension());
		/* For every legal move newMove ... */		
		for(ExecutableExtension newMove: this.getLegalMoves(env,trace)){			
			DialogueTrace<Argument,Extension> t2 = trace.addAndCopy(newMove);
			double newMoveEU = 0;			
			/* For all possible opponent states oppState ... */
			if(this.prob.isEmpty())
				newMoveEU = this.getUtilityFunction().getUtility(t2);
			else
				for (T3BeliefState oppState: this.prob.keySet()) {
					Probability oppStateProb = this.prob.probability(oppState);				
					/* Get opponent's best responses to newMove */
					Set<ExecutableExtension> bestOppResponses = oppState.doMove(env,t2).getSecond();				
					/* If opponent has no response, then utility of newMove is determined by current trace / oppStateProb */
					if (bestOppResponses.isEmpty()) {
						newMoveEU += this.getUtilityFunction().getUtility(t2, this.virtualArguments, this.virtualAttacks) * oppStateProb.doubleValue();
					}else{
						/* There may be more than 1 opp response, we don't know which one is best, so 
						 * we assign equal probability to each response */
						float oppResponseProb = 1f / (float)bestOppResponses.size();	
						/* For every possible opponent response oppResponse ... */
						for (ExecutableExtension oppResponse: bestOppResponses) {
							// this avoids infinite loops
							// (if there are two consecutive noops the game is over anyway)
							if(newMove.isNoOperation() && oppResponse.isNoOperation()){
								newMoveEU += this.getUtilityFunction().getUtility(t2, this.virtualArguments, this.virtualAttacks) * oppStateProb.doubleValue() * oppResponseProb;
								continue;
							}
							DialogueTrace<Argument,Extension> t3 = t2.addAndCopy(oppResponse);		
							/* Get best response to oppResponse */
							Pair<Double, Set<ExecutableExtension>> r = this.doMove(env, t3);						
							/* Expected utility is utility of best response times probability of 
						   		opponent model times probability of opponent response */
							newMoveEU += r.getFirst() * oppStateProb.doubleValue() * oppResponseProb;							
						}
					}
				}			
			/* Keep track of the set of best responses */
			if (newMoveEU > bestEU) bestMoves.clear();
			if (newMoveEU >= bestEU) {
				bestMoves.add(newMove);
				bestEU = newMoveEU;
			}			
		}		
		return new Pair<Double, Set<ExecutableExtension>>(bestEU, bestMoves);
	}

	/**
	 * Returns a T2-belief state that is a projection of this belief state,
	 * i.e. all virtual arguments and virtual attacks are removed from
	 * all nested models.
	 * @return the T2-projection of this belief state.
	 */
	public T2BeliefState projectToT2BeliefState(){
		ProbabilityFunction<T2BeliefState> prob = new ProbabilityFunction<T2BeliefState>();
		for(T3BeliefState subState: this.prob.keySet())
			prob.put(subState.projectToT2BeliefState(), new Probability(this.prob.get(subState)));
		return new T2BeliefState(this.getKnownArguments(), this.getUtilityFunction(), prob);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.BeliefState#clone()
	 */
	public Object clone(){
		if(this.prob.isEmpty())
			return new T3BeliefState(new Extension(this.getKnownArguments()), this.getUtilityFunction(), new HashSet<Argument>(this.virtualArguments), new HashSet<Attack>(this.virtualAttacks),this.rec, new ProbabilityFunction<T3BeliefState>());
		ProbabilityFunction<T3BeliefState> prob = new ProbabilityFunction<T3BeliefState>();
		for(java.util.Map.Entry<T3BeliefState, Probability> entry: this.prob.entrySet())
			prob.put((T3BeliefState)entry.getKey().clone(), entry.getValue());
		return new T3BeliefState(new Extension(this.getKnownArguments()), this.getUtilityFunction(), new HashSet<Argument>(this.virtualArguments), new HashSet<Attack>(this.virtualAttacks),this.rec, prob);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.BeliefState#display()
	 */
	@Override
	public String display(){
		return this.display(0);
	}
	
	/**
	 * Aux method for pretty print();
	 * @param indent indentation for display, depending on recursion depth
	 * @return a string representation of this state.
	 */
	private String display(int indent){
		int origIndent = indent;
		String result = "";
		for(int i = 0; i < indent; i++) result += "  ";
		result += "<\n";
		indent++;
		for(int i = 0; i < indent; i++) result += "  ";
		result += this.getKnownArguments() + ",\n";
		for(int i = 0; i < indent; i++) result += "  ";
		result += "V: " + this.virtualArguments + ",\n";
		for(int i = 0; i < indent; i++) result += "  ";
		result += "V: " + this.virtualAttacks + ",\n";
		for(int i = 0; i < indent; i++) result += "  ";
		result += "V: " + this.rec + ",\n";
		for(int i = 0; i < indent; i++) result += "  ";
		result += this.getUtilityFunction() + ",\n";
		for(int i = 0; i < indent; i++) result += "  ";
		result += "Prob\n";
		indent++;
		for(T3BeliefState state: this.prob.keySet()){
			for(int i = 0; i < indent; i++) result += "  ";
			result += this.prob.get(state) + ":\n";
			result += state.display(indent+1) + "\n";
		}
		for(int i = 0; i < origIndent; i++) result += "  ";
		result += ">";
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((prob == null) ? 0 : prob.hashCode());
		result = prime * result + ((rec == null) ? 0 : rec.hashCode());
		result = prime
				* result
				+ ((virtualArguments == null) ? 0 : virtualArguments.hashCode());
		result = prime * result
				+ ((virtualAttacks == null) ? 0 : virtualAttacks.hashCode());
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
		T3BeliefState other = (T3BeliefState) obj;
		if (prob == null) {
			if (other.prob != null)
				return false;
		} else if (!prob.equals(other.prob))
			return false;
		if (rec == null) {
			if (other.rec != null)
				return false;
		} else if (!rec.equals(other.rec))
			return false;
		if (virtualArguments == null) {
			if (other.virtualArguments != null)
				return false;
		} else if (!virtualArguments.equals(other.virtualArguments))
			return false;
		if (virtualAttacks == null) {
			if (other.virtualAttacks != null)
				return false;
		} else if (!virtualAttacks.equals(other.virtualAttacks))
			return false;
		return true;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(T3BeliefState arg0) {
		if(this.hashCode() < arg0.hashCode())
			return -1;
		if(this.hashCode() > arg0.hashCode())
			return 1;
		return 0;
	}
}
