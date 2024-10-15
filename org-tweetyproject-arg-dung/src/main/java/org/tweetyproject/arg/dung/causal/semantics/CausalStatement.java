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
package org.tweetyproject.arg.dung.causal.semantics;

import org.tweetyproject.arg.dung.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.arg.dung.causal.syntax.InducedTheory;
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;
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
	private Collection<PlFormula> conclusions;
	
	private Collection<PlFormula> observations;

	/**
	 * Initializes a new causal statement.
	 *
	 * @param conclusions 	conclusions, which would be true, iff this statement is true and the interventions were realized and the premises are met.
	 * @param premises 		observations of the causal atoms
	 */
	public CausalStatement(Collection<PlFormula> conclusions, Collection<PlFormula> premises) {
		super();
		this.conclusions = conclusions;
		this.observations = premises;
	}

    /**
     * Retrieves the conclusions of this causal statement.
     * @return A new HashSet containing all the conclusions of this causal statement.
     */
	public Collection<PlFormula> getConclusions(){
		return new HashSet<>(this.conclusions);
	}
	
    /**
     * Retrieves the observations of this causal statement.
     * @return A new HashSet containing all the premises required for the conclusions to hold.
     */
	public Collection<PlFormula> getObservations(){
		return new HashSet<>(this.observations);
	}	
	
	/**
	 * Checks if this instance holds in the specified knowledge base.
	 * @param ckbase Causal knowledge base
	 * @return TRUE iff this instance holds in the specified knowledge base.
	 */
	public boolean holds(CausalKnowledgeBase ckbase) {
		for(var conclusion : this.getConclusions()) {
			if(!ckbase.entails(this.getObservations(), conclusion)) {
				return false;
			}
		}
		
		return true;
	}
	
    /**
     * Visualizes this causal statement within a given causal knowledge base. This method generates a visual representation
     * of the argument framework induced by adding the premises of this statement to the causal knowledge base, highlighting
     * the conclusions.
     * 
     * @param cKbase The causal knowledge base used for visualization.
     */
	public void VisualizeHolds(CausalKnowledgeBase cKbase)
	{
		var causalKnowledgeBaseCopy = cKbase.clone();
		causalKnowledgeBaseCopy.addAll(this.getObservations());
		var inducedAF = new InducedTheory(causalKnowledgeBaseCopy);
		DungTheoryPlotter.plotFramework(inducedAF, 3000, 2000, "Premises: " + this.getObservations().toString() + " \n Conclusions: " + this.getConclusions().toString());
	}
}
