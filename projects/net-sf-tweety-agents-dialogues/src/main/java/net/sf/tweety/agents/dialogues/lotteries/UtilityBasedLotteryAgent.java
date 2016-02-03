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
package net.sf.tweety.agents.dialogues.lotteries;

import java.util.Collection;

import net.sf.tweety.agents.Perceivable;
import net.sf.tweety.agents.dialogues.ExecutableDungTheory;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.prob.lotteries.UtilityFunction;
import net.sf.tweety.graphs.Graph;

public class UtilityBasedLotteryAgent extends AbstractLotteryAgent{

	/** The utility function. */
	private UtilityFunction util;
	
	public UtilityBasedLotteryAgent(String name, DungTheory theory, UtilityFunction util, int semantics) {
		super(name, theory, semantics);
		this.util = util;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.dialogues.lotteries.AbstractLotteryAgent#next(java.util.Collection)
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
	 * @see net.sf.tweety.agents.dialogues.lotteries.AbstractLotteryAgent#getUtility(net.sf.tweety.arg.dung.DungTheory, int)
	 */
	public double getUtility(DungTheory theory, int semantics){
		return this.util.getUtility(theory, semantics);
	}
	
}
