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
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class describes an interventional causal statement wrt. some {@link CausalKnowledgeBase} of the form:<br>
 * "Given phi, if 'v' would be 'x' then psi would be true"<br>
 * where 'v' is some atom and 'x' is a truth value
 * 
 * @see "Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm 'Argumentation-based Causal and Counterfactual Reasoning' 1st International Workshop on Argumentation for eXplainable AI, 2022"
 * 
 * @author Julian Sander
 * @author Lars Bengel
 */
public class InterventionalStatement extends CausalStatement {

	protected Map<Proposition, Boolean> interventions;
	/**
	 * Initializes a new interventional causal statement.
	 *
	 * @param conclusions	 Conclusions, which would be true, iff this statement is true and the interventions were realized and the premises are met.
	 * @param interventions	 Maps explainable atoms to boolean values.
	 * @param observations	 PlFormulas which have to be true, so that the conclusions can be drawn.
	 */
	public InterventionalStatement(Collection<PlFormula> conclusions, Map<Proposition, Boolean> interventions, Collection<PlFormula> observations) {
		super(conclusions, observations);
		this.interventions = interventions;
	}
	
    /**
     * Retrieves the interventions of this causal statement.
     * @return A HashMap containing the interventions mapped from explainable atoms to their respective boolean values.
     */
	public Map<Proposition, Boolean> getInterventions(){
		return new HashMap<>(this.interventions);
	}

	@Override
	public boolean holds(CausalKnowledgeBase ckbase) {
		for(var conclusion : this.getConclusions()) {
			if(!checkInterventionalStatement(ckbase, conclusion)) {
				return false;
			}
		}
		
		return true;
	}
	
	/*@Override
	public void VisualizeHolds(CausalKnowledgeBase cKbase)
	{
		var causalKnowledgeBaseCopy = getIntervenedCopy(cKbase);
		causalKnowledgeBaseCopy.addAll(this.getObservations());
		var inducedAF = new InducedTheory(causalKnowledgeBaseCopy);
		DungTheoryPlotter.plotFramework(inducedAF, 3000, 2000,  
				"Premises: " + this.getObservations().toString()
				+ " \n Interventions: " + this.getInterventions().toString()
				+ " \n Conclusions: " + this.getConclusions().toString());
	}*/
	
	private boolean checkInterventionalStatement(CausalKnowledgeBase ckbase, PlFormula conclusion) {
		CausalKnowledgeBase newBase = ckbase.clone();
		for (Proposition atom : interventions.keySet()) {
			newBase = new CausalKnowledgeBase(newBase.getCausalModel().intervene(atom, interventions.get(atom)), ckbase.getAssumptions());
		}
		return newBase.entails(this.getObservations(), conclusion);
	}
}
