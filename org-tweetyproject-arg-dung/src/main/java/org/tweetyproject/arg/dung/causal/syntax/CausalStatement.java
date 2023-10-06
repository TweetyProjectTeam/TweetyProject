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
package org.tweetyproject.arg.dung.causal.syntax;

import java.util.HashMap;
import java.util.HashSet;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * This class is describes a causal statement, such as an interventional or counterfactual statement.
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public abstract class CausalStatement {
	private HashSet<PlFormula> conclusions;
	private HashMap<Proposition, Boolean> interventions;
	private HashSet<PlFormula> premises;
	
	
	
	/**
	 * Creates a new causal statement.
	 * @param conclusions Conclusions, which would be true, iff this statement is true and the interventions were realized and the premises are met.
	 * @param interventions Maps explainable atoms to boolean values.
	 * @param premises PlFormulas which have to be true, so that the conclusions can be drawn.
	 */
	public CausalStatement(HashSet<PlFormula> conclusions, HashMap<Proposition, Boolean> interventions,
			HashSet<PlFormula> premises) {
		super();
		this.conclusions = conclusions;
		this.interventions = interventions;
		this.premises = premises;
	}

	public HashSet<PlFormula> getConclusions(){
		return new HashSet<PlFormula>(this.conclusions);
	}
	
	public HashMap<Proposition, Boolean> getInterventions(){
		return new HashMap<Proposition, Boolean>(this.interventions);
	}
	
	public HashSet<PlFormula> getPremises(){
		return new HashSet<PlFormula>(this.premises);
	}	
	
	/**
	 * Checks if this instance holds in the specified knowledge base.
	 * @param cKbase Causal knowledge base
	 * @return TRUE iff this instance holds in the specified knowledge base.
	 */
	public abstract boolean holds(CausalKnowledgeBase cKbase);
}
