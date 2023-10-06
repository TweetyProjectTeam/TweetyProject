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
 * This class describes an counterfactual causal statement like:
 * given phi, if v had been x then rho would be true
 * 
 * @see "Argumentation-based Causal and Counterfactual Reasoning" by
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, published at 1st International Workshop on Argumentation
 * for eXplainable AI (ArgXAI, co-located with COMMA ’22), September 12, 2022
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class CounterfactualStatement extends CausalStatement {

	/**
	 * Creates a new counterfactual causal statement.
	 * @param conclusions Conclusions, which would be true, iff this statement is true and the interventions were realized and the premises are met.
	 * @param interventions Maps explainable atoms to boolean values.
	 * @param premises PlFormulas which have to be true, so that the conclusions can be drawn.
	 */
	public CounterfactualStatement(HashSet<PlFormula> conclusions, HashMap<Proposition, Boolean> interventions,
			HashSet<PlFormula> premises) {
		super(conclusions, interventions, premises);
	}

	@Override
	public boolean holds(CausalKnowledgeBase cKbase) {
		for(var conclusion : this.getConclusions()) {
			if(!checkCounterFactualStatement(cKbase, conclusion)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkCounterFactualStatement(CausalKnowledgeBase cKbase, PlFormula conclusion) {
		var twin = cKbase.getCausalModel().getTwinModel();
		var interventions = this.getInterventions();
		for(var expAtom : interventions.keySet()) {
			twin.intervene( new Proposition(expAtom.getName() + "*"), interventions.get(expAtom).booleanValue());
		}
		
		var newKnowledgeBase = new CausalKnowledgeBase(twin, cKbase.getAssumptions());
		newKnowledgeBase.addAll(cKbase.getBeliefs());
		return newKnowledgeBase.entails(this.getPremises(), conclusion);
	}
}
