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
package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.agents.dialogues.ExecutableExtension;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.arg.saf.*;
import net.sf.tweety.logics.pl.syntax.*;


/**
 * This class represents an agent in a structured argumentation system.
 *  
 * @author Matthias Thimm
 */
public abstract class SasAgent extends Agent {

	/**
	 * Indicates whether this agent is a single-step argumentation agent,
	 * i.e. whether he may bring forward only one argument at a time
	 * or multiple.
	 */
	private boolean isSingleStep;
	
	/**
	 * The current view of this agent on the overall
	 * argumentation, i.e. its subjective beliefs on the
	 * all possible arguments and their relations.
	 */
	private StructuredArgumentationFramework view;
	
	/**
	 * The current state of the argumentation as perceived by all agents.
	 */
	private StructuredArgumentationFramework commonView;
	
	/**
	 * The utility function of this agent.
	 */
	private UtilityFunction utility;
	
	/**
	 * Creates a new (non-single-step) SasAgent with the given (local) view and utility function. 
	 * @param view the view of the agent on the argumentation.
	 * @param utility a utility function.
	 */
	public SasAgent(StructuredArgumentationFramework view, UtilityFunction utility){
		this(view,utility,false);
	}	
	
	/**
	 * Creates a new SasAgent with the given (local) view and utility function. 
	 * @param view the view of the agent on the argumentation.
	 * @param utility a utility function.
	 * @param isSingleStep indicates whether this agent is a single-step argumentation agent,
	 * i.e. whether he may bring forward only one argument at a time or multiple.
	 */
	public SasAgent(StructuredArgumentationFramework view, UtilityFunction utility, boolean isSingleStep){
		// no name needed
		super("");
		this.view = view;
		this.commonView = new StructuredArgumentationFramework();
		this.utility = utility;
		this.isSingleStep = isSingleStep;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Agent#next(java.util.Collection)
	 */
	@Override
	public Executable next(Collection<? extends Perceivable> percepts) {
		// In structured argumentation agents expect the percepts
		// to consist of a single structured argumentation framework,
		// the new common view.
		if(percepts.size() != 1 || !(percepts.iterator().next() instanceof StructuredArgumentationFramework))
			throw new IllegalArgumentException("Expecting single structured argumentation framework (the common view) as percept.");
		// update common view
		this.commonView = (StructuredArgumentationFramework) percepts.iterator().next();
		// update own view
		this.view.addAll(this.commonView);
		this.view.addAllAttacks(this.commonView.getAttacks());
		// prepare action (a set of arguments)
		// per default this is an empty set
		// extend this class and implement your own selection function
		return new ExecutableExtension();
	}
	
	/**
	 * Returns the view of this agent.
	 * @return the view of this agent.
	 */
	protected StructuredArgumentationFramework getView(){
		return this.view;
	}
	
	/**
	 * Returns the common view of this agent. 
	 * @return the common view of this agent.
	 */
	protected StructuredArgumentationFramework getCommonView(){
		return this.commonView;
	}
	
	/**
	 * Returns the utility function of this agent. 
	 * @return the utility function of this agent.
	 */
	protected UtilityFunction getUtilityFunction(){
		return this.utility;
	}
	
	/**
	 * Checks whether this agent is a single-step argumentation agent,
	 * i.e. whether he may bring forward only one argument at a time or multiple.
	 * @return "true" if this agent is a single-step agent.
	 */
	public boolean isSingleStep(){
		return this.isSingleStep;
	}
	
	/**
	 * Returns the set of arguments this agent may bring forward.
	 * @return The set of arguments this agent may bring forward.
	 */
	protected Set<Argument> getPossibleArguments(){
		Set<Argument> possibleArguments = new HashSet<Argument>(this.getView()); 
		possibleArguments.removeAll(this.getCommonView());
		return possibleArguments;
	}
	
	/**
	 * Ranks the given collection of propositions wrt. this agent's
	 * utility function.
	 * @param propositions a collection of propositions.
	 * @return the rank of the given collection of propositions wrt. this agent's
	 * utility function.
	 */
	public int rank(Collection<? extends Proposition> propositions){
		return this.utility.rank(propositions);
	}

}
