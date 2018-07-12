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
package net.sf.tweety.beliefdynamics.mas;

import net.sf.tweety.agents.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.graphs.orders.*;

/**
 * This class represents belief sets for credibility-based agents multi-agent systems.
 * Such a belief set contains a set of information objects and a credibility order among agents.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas in information objects.
 */
public class CrMasBeliefSet<T extends Formula> extends BeliefSet<InformationObject<T>> {

	/**
	 * The subjective credibility order of the agent who owns this belief set.
	 */
	private Order<Agent> credibilityOrder;
	
	/**
	 * Creates a new belief set with the given credibility order.
	 * @param credibilityOrder some credibility order.
	 */
	public CrMasBeliefSet(Order<Agent> credibilityOrder){
		this.credibilityOrder = credibilityOrder;
	}
	
	/**
	 * Returns the credibility order of this belief set.
	 * @return the credibility order of this belief set.
	 */
	public Order<Agent> getCredibilityOrder(){
		return this.credibilityOrder;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefSet#getSignature()
	 */
	@Override
	public Signature getSignature() {
		Signature sig = null;
		
		for(InformationObject<T> f: this)
			if(sig == null)
				sig = f.getSignature();
			else sig.addSignature(f.getSignature());
		return sig;
	}

}
