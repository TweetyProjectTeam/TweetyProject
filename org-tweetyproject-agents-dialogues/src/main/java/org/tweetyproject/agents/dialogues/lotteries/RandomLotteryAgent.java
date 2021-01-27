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
package org.tweetyproject.agents.dialogues.lotteries;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.tweetyproject.agents.Perceivable;
import org.tweetyproject.agents.dialogues.ExecutableDungTheory;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.lotteries.UtilityFunction;
import org.tweetyproject.graphs.Graph;

/**
 * A baseline agent for argumentation games who always
 * returns some random move.
 * 
 * @author Matthias Thimm
 *
 */
public class RandomLotteryAgent extends AbstractLotteryAgent{
	
	/** The utility function. */
	private UtilityFunction util;
	
	/**
	 * Creates a new agent with the given name, theory, utility function and semantics
	 * @param name the name of the agent 
	 * @param theory some theory
	 * @param util the used utility function
	 * @param semantics the semantics underlying the theory
	 */
	public RandomLotteryAgent(String name, DungTheory theory, UtilityFunction util, Semantics semantics) {
		super(name, theory, semantics);	
		this.util = util;		
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent#next(java.util.Collection)
	 */
	@Override
	public ExecutableDungTheory next(Collection<? extends Perceivable> percepts) {
		List<Graph<Argument>> allSub = new LinkedList<Graph<Argument>>(this.theory.getSubgraphs());
		Random rand = new Random();
		return new ExecutableDungTheory(new DungTheory(allSub.get(rand.nextInt(allSub.size()))));
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent#getUtility(org.tweetyproject.arg.dung.DungTheory, int)
	 */
	@Override
	public double getUtility(DungTheory theory, Semantics semantics) {
		return this.util.getUtility(theory, semantics);
	}
}
