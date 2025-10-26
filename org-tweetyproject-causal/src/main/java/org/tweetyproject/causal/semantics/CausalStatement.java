/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.causal.semantics;

import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class describes a basic causal statement wrt. some {@link CausalKnowledgeBase} of the form:<br>
 * "Given phi, it follows that psi holds"
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class CausalStatement {
	/** Conclusion or effect of the causal statement */
	private PlFormula conclusion;
	/** Observations or premises of the causal statement */
	private Collection<PlFormula> observations;

	/**
	 * Initializes a new empty causal statement
	 */
	public CausalStatement() {
		this.observations = new HashSet<>();
		this.conclusion = null;
	}

	/**
	 * Initializes a new causal statement
	 *
	 * @param observations 	observations of the causal atoms
	 * @param conclusion 	the conclusion of the causal statement
	 */
	public CausalStatement(Collection<PlFormula> observations, PlFormula conclusion) {
		this.conclusion = conclusion;
		this.observations = new HashSet<>(observations);
	}

	/**
	 * Add a new observation to the causal statement
	 *
	 * @param observation some observation of a causal atom
	 * @return TRUE iff observation is added successfully
	 */
	public boolean addObservation(PlFormula observation) {
		return this.observations.add(observation);
	}

	/**
	 * Set a new conclusion of the causal statement
	 * @param conclusion the new conclusion
	 */
	public void setConclusion(PlFormula conclusion) {
		this.conclusion = conclusion;
	}

    /**
     * Retrieves the conclusion of this causal statement
     * @return The conclusion of this causal statement.
     */
	public PlFormula getConclusion(){
		return this.conclusion;
	}
	
    /**
     * Retrieves the observations of this causal statement
     * @return A copy of the observations of this causal statement
     */
	public Collection<PlFormula> getObservations(){
		return new HashSet<>(this.observations);
	}
}
