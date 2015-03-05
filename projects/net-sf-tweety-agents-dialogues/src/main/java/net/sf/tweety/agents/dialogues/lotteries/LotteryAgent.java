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
package net.sf.tweety.agents.dialogues.lotteries;

import java.util.Collection;
import java.util.Set;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.Executable;
import net.sf.tweety.agents.Perceivable;
import net.sf.tweety.agents.dialogues.ExecutableExtension;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.prob.lotteries.ArgumentationLottery;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;
import net.sf.tweety.arg.prob.lotteries.UtilityFunction;
import net.sf.tweety.commons.util.SetTools;

/**
 * An agent in a game of argumentation lotteries.
 * @author Matthias Thimm
 */
public class LotteryAgent extends Agent {

	/** Whether this agent is a dummy agent (does no action). */
	private boolean isDummy;
	
	/** The theory of this agent. */
	private DungTheory theory;
	
	/** Uncertainty on sub graphs. */
	private SubgraphProbabilityFunction prob;
	
	/** Utility function */
	private UtilityFunction util;
	
	/** The used semantics */
	private int semantics;
	
	/** Whether this agent should choose the next best action in a simpler manner. */
	private boolean isBaseline;
	
	/**
	 * Creates a new lottery agent
	 * @param name the name of the agent 
	 * @param theory some theory
	 * @param prob a probability function
	 * @param util a utility function
	 * @param isDummy whether it is a dummy agent.
	 * @param isBaseline whether this agent should choose the next best action in a simpler manner.
	 */
	public LotteryAgent(String name, DungTheory theory, SubgraphProbabilityFunction prob, UtilityFunction util, int semantics, boolean isDummy, boolean isBaseline) {
		super(name);
		this.theory = theory;
		this.prob = prob;
		this.semantics = semantics;
		this.isDummy = isDummy;
		this.util = util;
		this.isBaseline = isBaseline;
	}

	/**
	 * Returns the theory of this agent.
	 * @return the theory of this agent.
	 */
	public DungTheory getTheory(){
		return this.theory;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Agent#next(java.util.Collection)
	 */
	@Override
	public Executable next(Collection<? extends Perceivable> percepts) {
		if(this.isDummy)
			return new ExecutableExtension();
		if(this.isBaseline){
			Extension e = null;
			double bestUtility = Double.NEGATIVE_INFINITY;
			for(Set<Argument> posMove: new SetTools<Argument>().subsets(this.theory)){				
				Extension move = new Extension(posMove);
				DungTheory sub = new DungTheory(this.theory.getRestriction(move));
				Double d = this.util.getUtility(sub, this.semantics);
				if(d > bestUtility){
					bestUtility = d;
					e = move;
				}
			}		
			return new ExecutableExtension(e);
		}		
		Extension e = null;
		double bestUtility = Double.NEGATIVE_INFINITY;
		//int k = new SetTools<Argument>().subsets(this.theory).size();
		//int i = 0;
		for(Set<Argument> posMove: new SetTools<Argument>().subsets(this.theory)){
			//System.out.println( i++ + " of " + k + "\t" + bestUtility);
			Extension move = new Extension(posMove);
			SubgraphProbabilityFunction updFunc = this.prob.naiveUpdate(move);
			ArgumentationLottery lot = new ArgumentationLottery(this.util.keySet(), updFunc, this.semantics);
			Double d = this.util.getExpectedUtility(lot);
			if(d > bestUtility){
				bestUtility = d;
				e = move;
			}
		}		
		return new ExecutableExtension(e);
	}
	
	/** Returns "true" if this agent is a dummy agent.
	 * @return "true" if this agent is a dummy agent.
	 */
	public boolean isDummy(){
		return this.isDummy;
	}
	
	/**
	 * Returns the utility function of this agent.
	 * @return the utility function of this agent.
	 */
	public UtilityFunction getUtilityFunction(){
		return this.util;
	}
	
	/**
	 * Returns the semantics.
	 * @return the semantics.
	 */
	public int getSemantics(){
		return this.semantics;
	}
}
