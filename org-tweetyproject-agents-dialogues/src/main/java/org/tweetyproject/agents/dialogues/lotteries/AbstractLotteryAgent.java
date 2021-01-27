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

import org.tweetyproject.agents.Agent;
import org.tweetyproject.agents.Perceivable;
import org.tweetyproject.agents.dialogues.ExecutableDungTheory;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * An agent in a game of argumentation lotteries.
 * @author Matthias Thimm
 */
public abstract class AbstractLotteryAgent extends Agent {

	/** The theory of this agent. */
	protected DungTheory theory;
	
	/** The used semantics */
	protected Semantics semantics;
		
	/**
	 * Creates a new lottery agent
	 * @param name the name of the agent 
	 * @param theory some theory
	 * @param semantics the semantics underlying the theory
	 */
	public AbstractLotteryAgent(String name, DungTheory theory, Semantics semantics) {
		super(name);
		this.theory = theory;		
		this.semantics = semantics;		
	}

	/**
	 * Returns the theory of this agent.
	 * @return the theory of this agent.
	 */
	public DungTheory getTheory(){
		return this.theory;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.Agent#next(java.util.Collection)
	 */
	@Override
	public abstract ExecutableDungTheory next(Collection<? extends Perceivable> percepts);
	
	/**
	 * Returns the utility of the agent wrt. the given theory.
	 * @param theory some theory
	 * @param semantics some semantics
	 * @return the utility of this agent wrt. the given theory.
	 */
	public abstract double getUtility(DungTheory theory, Semantics semantics);
	
	/**
	 * Returns the semantics.
	 * @return the semantics.
	 */
	public Semantics getSemantics(){
		return this.semantics;
	}
}
