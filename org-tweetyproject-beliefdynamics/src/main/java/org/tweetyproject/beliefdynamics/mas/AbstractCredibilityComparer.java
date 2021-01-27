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
package org.tweetyproject.beliefdynamics.mas;

import java.util.*;

import org.tweetyproject.agents.*;
import org.tweetyproject.graphs.orders.*;
import org.tweetyproject.logics.pl.syntax.*;

/**
 * This class provides for auxiliary functions used to compare
 * formulas and proofs of formulas wrt. the credibility of the
 * agents.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractCredibilityComparer {

	/**
	 *  The credibility order used to guide the comparison.
	 */
	private Order<Agent> credOrder;
	
	/**
	 * The information objects that hold the information which agents
	 * uttered the formulas.
	 */
	private Collection<InformationObject<PlFormula>> formulas;
	
	/**
	 * Creates a new credibility comparer that is guided by the giving information which
	 * agents uttered the formulas and the credibility order. 
	 * @param formulas The information objects that hold the information which agents
	 * 		uttered the formulas.
	 * @param credOrder The credibility order used to guide the comparison.
	 */
	public AbstractCredibilityComparer(Collection<InformationObject<PlFormula>> formulas, Order<Agent> credOrder){
		this.formulas = formulas;
		this.credOrder = credOrder;
	}
	
	/**
	 * Checks whether col1 is at least as preferred as col2 wrt. the credibility order.
	 * @param col1 a set of formulas
	 * @param col2 a set of formulas
	 * @return "true" iff col1 is at least as preferred as cl2 wrt. the credibility order. 
	 */
	protected boolean isAtLeastAsPreferredAs(Collection<? extends PlFormula> col1, Collection<? extends PlFormula> col2){
		for(PlFormula f: col1){
			if(!this.isAtLeastAsPreferredAs(f, col2))
				return false;
		}		
		return true;
	}	
	
	/**
	 * Checks whether f is at least as preferred as some formula in "formulas"
	 * @param f some formula
	 * @param formulas a set of formulas
	 * @return "true" iff f is at least as preferred as each formula in "formulas"
	 */
	protected boolean isAtLeastAsPreferredAs(PlFormula f, Collection<? extends PlFormula> formulas){
		for(PlFormula f2: formulas){
			if(this.isAtLeastAsPreferredAs(f, f2))
				return true;
		}		
		return false;
	}
	
	/**
	 * Checks whether f is at least as preferred as f2
	 * @param f some formula
	 * @param f2 some formula
	 * @return "true" iff f is at least as preferred as f2
	 */
	protected boolean isAtLeastAsPreferredAs(PlFormula f, PlFormula f2){
		// Retrieve all agents that uttered f
		Set<Agent> agents1 = new HashSet<Agent>();
		for(InformationObject<PlFormula> i: this.formulas)
			if(i.getFormula().equals(f))
				agents1.add(i.getSource());
		// Retrieve all agents that uttered f2
		Set<Agent> agents2 = new HashSet<Agent>();
		for(InformationObject<PlFormula> i: this.formulas)
			if(i.getFormula().equals(f2))
				agents2.add(i.getSource());
		// f is at least as preferred as f2 if there is one agent in agents1 such that no agent in
		// agents2 is more credible than that one.
		for(Agent a: agents1){
			boolean mostCredible = true;
			for(Agent b: agents2){
				if(this.credOrder.isOrderedBefore(b, a) && !this.credOrder.isOrderedBefore(a, b)){
					mostCredible = false;
					break;
				}
			}
			if(mostCredible)
				return true;
		}		
		return false;
	}
}
