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
 * @author Lars Bengel
 */
public class CounterfactualStatement extends InterventionalStatement {
	/**
	 * Initializes a new counterfactual causal statement
	 *
	 * @param observations	 Observations of causal atoms
	 * @param interventions	 A set of interventions on causal atoms
	 * @param conclusion	 Conclusion of the causal statement
	 */
	public CounterfactualStatement(Collection<PlFormula> observations, Map<Proposition, Boolean> interventions, PlFormula conclusion) {
		super(observations, interventions, conclusion);
	}

	/**
	 * Initializes a new counterfactual causal statement without interventions
	 *
	 * @param observations	 Observations of causal atoms
	 * @param conclusion	 Conclusion of the causal statement
	 */
	public CounterfactualStatement(Collection<PlFormula> observations, PlFormula conclusion) {
		super(observations, conclusion);
	}

	/**
	 * Initializes a new empty counterfactual causal statement
	 */
	public CounterfactualStatement() {
		super();
	}

	/**
	 * Add a new intervention which sets the counterfactual copy of the given atom to the given truth value
	 * @param atom	some causal atom
	 * @param value	some truth value
	 * @return TRUE iff the intervention is added successfully
	 */
	public boolean addCounterfactualIntervention(Proposition atom, boolean value) {
		Proposition cAtom = new Proposition(atom.getName()+"*");
		return Boolean.TRUE.equals(this.interventions.put(cAtom, value));
	}

	/*
	private boolean checkCounterFactualStatement(CausalKnowledgeBase ckbase, PlFormula conclusion) {
		StructuralCausalModel twinModel = ckbase.getCausalModel().getTwinModel();
		for (Proposition atom : this.interventions.keySet()) {
			twinModel = twinModel.intervene(new Proposition(atom.getName()+"*"), interventions.get(atom));
		}
		return new CausalKnowledgeBase(twinModel, ckbase.getAssumptions()).entails(this.getObservations(), conclusion);
	}

	 */
}
