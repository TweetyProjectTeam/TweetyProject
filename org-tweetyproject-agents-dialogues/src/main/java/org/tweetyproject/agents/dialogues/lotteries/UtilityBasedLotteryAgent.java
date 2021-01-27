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

import org.tweetyproject.agents.Perceivable;
import org.tweetyproject.agents.dialogues.ExecutableDungTheory;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.lotteries.UtilityFunction;
import org.tweetyproject.graphs.Graph;

public class UtilityBasedLotteryAgent extends AbstractLotteryAgent{

	/** The utility function. */
	private UtilityFunction util;
	
	public UtilityBasedLotteryAgent(String name, DungTheory theory, UtilityFunction util, Semantics semantics) {
		super(name, theory, semantics);
		this.util = util;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent#next(java.util.Collection)
	 */
	public ExecutableDungTheory next(Collection<? extends Perceivable> percepts) {
		double bestUtility = Double.NEGATIVE_INFINITY;
		DungTheory e = new DungTheory();
		for(Graph<Argument> subgraph: this.theory.getSubgraphs()){
			DungTheory sub = new DungTheory(subgraph);
			Double d = this.util.getUtility(sub, this.semantics);
			if(d > bestUtility){
				bestUtility = d;
				e = sub;
			}			
		}		
		return new ExecutableDungTheory(e);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent#getUtility(org.tweetyproject.arg.dung.DungTheory, int)
	 */
	public double getUtility(DungTheory theory, Semantics semantics){
		return this.util.getUtility(theory, semantics);
	}
	
}
