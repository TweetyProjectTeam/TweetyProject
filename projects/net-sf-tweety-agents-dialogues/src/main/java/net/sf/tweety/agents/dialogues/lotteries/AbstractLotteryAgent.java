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

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.Perceivable;
import net.sf.tweety.agents.dialogues.ExecutableDungTheory;
import net.sf.tweety.arg.dung.DungTheory;

/**
 * An agent in a game of argumentation lotteries.
 * @author Matthias Thimm
 */
public abstract class AbstractLotteryAgent extends Agent {

	/** The theory of this agent. */
	protected DungTheory theory;
	
	/** The used semantics */
	protected int semantics;
		
	/**
	 * Creates a new lottery agent
	 * @param name the name of the agent 
	 * @param theory some theory
	 * @param prob a probability function
	 * @param util a utility function
	 * @param isDummy whether it is a dummy agent.
	 */
	public AbstractLotteryAgent(String name, DungTheory theory, int semantics) {
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
	 * @see net.sf.tweety.agents.Agent#next(java.util.Collection)
	 */
	@Override
	public abstract ExecutableDungTheory next(Collection<? extends Perceivable> percepts);
	
	/**
	 * Returns the utility of the agent wrt. the given theory.
	 * @param theory some theory
	 * @param semantics some semantics
	 * @return the utility of this agent wrt. the given theory.
	 */
	public abstract double getUtility(DungTheory theory, int semantics);
	
	/**
	 * Returns the semantics.
	 * @return the semantics.
	 */
	public int getSemantics(){
		return this.semantics;
	}
}
