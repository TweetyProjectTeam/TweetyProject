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
import org.tweetyproject.arg.dung.causal.syntax.StructuralCausalModel;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.Map;

/**
 * This class describes a counterfactual causal statement wrt. some {@link CausalKnowledgeBase} of the form:<br>
 * "Given phi, if 'v' had been 'x' then psi would be true"<br>
 * where 'v' is some atom and 'x' is a truth value
 * 
 * @see "Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm 'Argumentation-based Causal and Counterfactual Reasoning' 1st International Workshop on Argumentation for eXplainable AI, 2022"
 *
 * @author Julian Sander
 */
public class CounterfactualStatement extends InterventionalStatement {

	/**
	 * Initializes a new counterfactual causal statement.
	 * @param conclusions 	Conclusions, which would be true, iff this statement is true and the interventions were realized and the premises are met.
	 * @param interventions Maps explainable atoms to boolean values.
	 * @param premises 		PlFormulas which have to be true, so that the conclusions can be drawn.
	 */
	public CounterfactualStatement(Collection<PlFormula> conclusions, Map<Proposition, Boolean> interventions, Collection<PlFormula> premises) {
		super(conclusions, interventions, premises);
	}

	@Override
	public boolean holds(CausalKnowledgeBase ckbase) {
		for(PlFormula conclusion : this.getConclusions()) {
			if(!checkCounterFactualStatement(ckbase, conclusion)) {
				return false;
			}
		}
		return true;
	}
	
	/*@Override
	public void VisualizeHolds(CausalKnowledgeBase cKbase) {
		var causalKnowledgeBaseCopy = getIntervenedTwinModel(cKbase);
		causalKnowledgeBaseCopy.addAll(this.getObservations());
		var inducedAF = new InducedTheory(causalKnowledgeBaseCopy);
		DungTheoryPlotter.plotFramework(inducedAF, 3000, 2000,  
				"Premises: " + this.getObservations().toString()
				+ " \n Interventions: " + this.getInterventions().toString()
				+ " \n Conclusions: " + this.getConclusions().toString());
	}*/
	
	private boolean checkCounterFactualStatement(CausalKnowledgeBase ckbase, PlFormula conclusion) {
		StructuralCausalModel twinModel = ckbase.getCausalModel().getTwinModel();
		for (Proposition atom : this.interventions.keySet()) {
			twinModel = twinModel.intervene(new Proposition(atom.getName()+"*"), interventions.get(atom));
		}
		return new CausalKnowledgeBase(twinModel, ckbase.getAssumptions()).entails(this.getObservations(), conclusion);
	}
}
