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
	private PlFormula conclusion;
	
	private Collection<PlFormula> observations;

	/**
	 * Initializes a new causal statement.
	 *
	 * @param conclusion 	conclusions, which would be true, iff this statement is true and the interventions were realized and the premises are met.
	 * @param premises 		observations of the causal atoms
	 */
	public CausalStatement(PlFormula conclusion, Collection<PlFormula> premises) {
		super();
		this.conclusion = conclusion;
		this.observations = premises;
	}

    /**
     * Retrieves the conclusions of this causal statement.
     * @return A new HashSet containing all the conclusions of this causal statement.
     */
	public PlFormula getConclusion(){
		return this.conclusion;
	}
	
    /**
     * Retrieves the observations of this causal statement.
     * @return A new HashSet containing all the premises required for the conclusions to hold.
     */
	public Collection<PlFormula> getObservations(){
		return new HashSet<>(this.observations);
	}
}
