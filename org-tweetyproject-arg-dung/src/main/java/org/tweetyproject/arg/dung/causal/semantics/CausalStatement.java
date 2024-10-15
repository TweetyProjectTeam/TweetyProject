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
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.dung.causal.semantics;

import java.util.HashSet;

import org.tweetyproject.arg.dung.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.arg.dung.causal.syntax.InducedTheory;
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class describes a causal statement, such as an interventional or counterfactual statement.
 * 
 * @author Julian Sander
 */
public abstract class CausalStatement {
	private HashSet<PlFormula> conclusions;
	
	private HashSet<PlFormula> premises;

	/**
	 * Creates a new causal statement.
	 * @param conclusions Conclusions, which would be true, iff this statement is true and the interventions were realized and the premises are met.
	 * @param premises PlFormulas which have to be true, so that the conclusions can be drawn.
	 */
	public CausalStatement(HashSet<PlFormula> conclusions, HashSet<PlFormula> premises) {
		super();
		this.conclusions = conclusions;
		this.premises = premises;
	}

    /**
     * Retrieves the conclusions of this causal statement.
     * 
     * @return A new HashSet containing all the conclusions of this causal statement.
     */
	public HashSet<PlFormula> getConclusions(){
		return new HashSet<PlFormula>(this.conclusions);
	}
	
    /**
     * Retrieves the premises of this causal statement.
     * 
     * @return A new HashSet containing all the premises required for the conclusions to hold.
     */
	public HashSet<PlFormula> getPremises(){
		return new HashSet<PlFormula>(this.premises);
	}	
	
	/**
	 * Checks if this instance holds in the specified knowledge base.
	 * @param cKbase Causal knowledge base
	 * @return TRUE iff this instance holds in the specified knowledge base.
	 */
	public boolean holds(CausalKnowledgeBase cKbase) {
		for(var conclusion : this.getConclusions()) {
			if(!checkStatement(cKbase, conclusion)) {
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
		causalKnowledgeBaseCopy.addAll(this.getPremises());
		var inducedAF = new InducedTheory(causalKnowledgeBaseCopy);
		DungTheoryPlotter.plotFramework(inducedAF, 3000, 2000, "Premises: " + this.getPremises().toString() + " \n Conclusions: " + this.getConclusions().toString());
	}
	
	    /**
     * Helper method to check if a single conclusion is entailed by the premises in the given causal knowledge base.
     * 
     * @param cKbase The causal knowledge base in which the conclusion is to be checked.
     * @param conclusion The conclusion to check against the premises.
     * @return true if the causal knowledge base entails the conclusion given the premises, otherwise false.
     */
	private boolean checkStatement(CausalKnowledgeBase cKbase, PlFormula conclusion) {
		return cKbase.entails(this.getPremises(), conclusion);
	}
}
