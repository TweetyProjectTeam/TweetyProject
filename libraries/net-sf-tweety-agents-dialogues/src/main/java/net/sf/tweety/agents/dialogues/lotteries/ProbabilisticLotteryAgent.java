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
package net.sf.tweety.agents.dialogues.lotteries;

import java.util.Collection;
import java.util.Set;

import net.sf.tweety.agents.Perceivable;
import net.sf.tweety.agents.dialogues.ExecutableDungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.prob.lotteries.ArgumentationLottery;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;
import net.sf.tweety.arg.prob.lotteries.UtilityFunction;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.graphs.Graph;

/**
 * An agent in a game of argumentation lotteries.
 * @author Matthias Thimm
 */
public class ProbabilisticLotteryAgent extends AbstractLotteryAgent {
	
	/** Constant for denoting the naive update. */
	public static final byte UPDATE_NAIVE = 1;
	/** Constant for denoting the simple update. */
	public static final byte UPDATE_SIMPLE = 2;
	/** Constant for denoting the sticky update. */
	public static final byte UPDATE_STICKY = 3;
	/** Constant for denoting the rough update. */
	public static final byte UPDATE_ROUGH = 4;
	
	/** Stickyness coefficient for the sticky update. */
	private double stickynesscoefficient;
	
	/** The update strategy used. */
	private byte updatestrategy;
	
	/** Uncertainty on sub graphs. */
	private SubgraphProbabilityFunction prob;
	
	/** Utility function */
	private UtilityFunction util;
	
	/**
	 * Creates a new lottery agent
	 * @param name the name of the agent 
	 * @param theory some theory
	 * @param prob a probability function
	 * @param util a utility function
	 */
	public ProbabilisticLotteryAgent(String name, DungTheory theory, SubgraphProbabilityFunction prob, UtilityFunction util, Semantics semantics) {
		this(name,theory, prob,util, semantics, ProbabilisticLotteryAgent.UPDATE_NAIVE);
	}

	/**
	 * Creates a new lottery agent
	 * @param name the name of the agent 
	 * @param theory some theory
	 * @param prob a probability function
	 * @param util a utility function
	 * @param updatestrategy The update strategy used
	 */
	public ProbabilisticLotteryAgent(String name, DungTheory theory, SubgraphProbabilityFunction prob, UtilityFunction util, Semantics semantics, byte updatestrategy) {
		this(name,theory,prob, util,semantics, updatestrategy, 0.5);		
	} 
	
	/**
	 * Creates a new lottery agent
	 * @param name the name of the agent 
	 * @param theory some theory
	 * @param prob a probability function
	 * @param util a utility function
	 * @param updatestrategy The update strategy used
	 * @param Stickyness coefficient for the sticky update (only needed when updatestrategy==UPDATE_STICKY.
	 */
	public ProbabilisticLotteryAgent(String name, DungTheory theory, SubgraphProbabilityFunction prob, UtilityFunction util, Semantics semantics, byte updatestrategy, double stickynesscoefficient) {
		super(name,theory,semantics);
		this.prob = prob;
		this.util = util;
		this.updatestrategy = updatestrategy;
		this.stickynesscoefficient = stickynesscoefficient;
		if(stickynesscoefficient <0 || stickynesscoefficient > 1)
			throw new IllegalArgumentException("Stickyness coefficient has to be in [0,1].");
	}
	
	/**
	 * Returns the theory of this agent.
	 * @return the theory of this agent.
	 */
	public DungTheory getTheory(){
		return this.theory;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.dialogues.lotteries.AbstractLotteryAgent#next(java.util.Collection)
	 */
	@Override
	public ExecutableDungTheory next(Collection<? extends Perceivable> percepts) {
		Extension e = null;
		double bestUtility = Double.NEGATIVE_INFINITY;
		if(this.updatestrategy == ProbabilisticLotteryAgent.UPDATE_NAIVE){
			Set<Set<Argument>> subsets =  new SetTools<Argument>().subsets(this.theory);
			for(Set<Argument> posMove: subsets){
				Extension move = new Extension(posMove);
				SubgraphProbabilityFunction updFunc = this.prob.naiveUpdate(move);				
				ArgumentationLottery lot = new ArgumentationLottery(this.util.keySet(), updFunc, this.semantics);
				Double d = this.util.getExpectedUtility(lot);
				if(d > bestUtility){
					bestUtility = d;
					e = move;
				}				
			}		
			return new ExecutableDungTheory(new DungTheory(this.theory.getRestriction(e)));
		}else{
			DungTheory th = new DungTheory();
			Collection<Graph<Argument>> subgraphs = this.theory.getSubgraphs();
			for(Graph<Argument> subgraph: subgraphs){		
				DungTheory sub = new DungTheory(subgraph);				
				SubgraphProbabilityFunction updFunc;
				if(this.updatestrategy == ProbabilisticLotteryAgent.UPDATE_SIMPLE)
					updFunc = this.prob.simpleUpdate(sub);
				else if(this.updatestrategy == ProbabilisticLotteryAgent.UPDATE_ROUGH)
					updFunc = this.prob.roughUpdate(sub);
				else if(this.updatestrategy == ProbabilisticLotteryAgent.UPDATE_STICKY)
					updFunc = this.prob.stickyUpdate(sub, this.stickynesscoefficient);
				else throw new RuntimeException("Unrecognized update type");				
				ArgumentationLottery lot = new ArgumentationLottery(this.util.keySet(), updFunc, this.semantics);
				Double d = this.util.getExpectedUtility(lot);
				if(d > bestUtility){
					bestUtility = d;
					th = sub;
				}				
			}
			return new ExecutableDungTheory(th);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.dialogues.lotteries.AbstractLotteryAgent#getUtility(net.sf.tweety.arg.dung.DungTheory, int)
	 */
	public double getUtility(DungTheory theory, Semantics semantics){
		return this.util.getUtility(theory, semantics);
	}
}
