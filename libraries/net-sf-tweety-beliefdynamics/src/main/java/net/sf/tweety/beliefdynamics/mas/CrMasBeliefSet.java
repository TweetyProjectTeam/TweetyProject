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
 * This class represents belief sets for credibility-based agents multi-agent
 * systems. Such a belief set contains a set of information objects and a
 * credibility order among agents.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas in information objects.
 * @param <S> The type of signatures.
 */
public class CrMasBeliefSet<T extends Formula, S extends Signature> extends BeliefSet<InformationObject<T>,S> {

	/**
	 * The subjective credibility order of the agent who owns this belief set.
	 */
	private Order<Agent> credibilityOrder;

	/**
	 * The type of signature used in this system.
	 */
	private S type_of_signature;

	/**
	 * Creates a new belief set with the given credibility order.
	 * 
	 * @param credibilityOrder some credibility order.
	 * @param The              type of signature used in this system.
	 */
	public CrMasBeliefSet(Order<Agent> credibilityOrder, S sig) {
		super(sig);
		this.credibilityOrder = credibilityOrder;
		this.type_of_signature = sig;
	}

	/**
	 * Returns the credibility order of this belief set.
	 * 
	 * @return the credibility order of this belief set.
	 */
	public Order<Agent> getCredibilityOrder() {
		return this.credibilityOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.BeliefSet#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		Signature sigm = type_of_signature;
		for (InformationObject<T> f : this)
			if (sigm == null)
				sigm = f.getSignature();
			else
				sigm.addSignature(f.getSignature());
		return sigm;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected S instantiateSignature() {
		try {
			return (S) type_of_signature.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

}
