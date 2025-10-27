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
import java.util.HashMap;
import java.util.Map;

/**
 * This class describes an interventional causal statement wrt. some {@link CausalKnowledgeBase} of the form:<br>
 * "Given phi, if 'v' would be 'x' then psi would be true"<br>
 * where 'v' is some atom and 'x' is a truth value
 * 
 * @see "Reference: 'Argumentation-based Causal and Counterfactual Reasoning' by
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, published at
 * 1st International Workshop on Argumentation
 * for eXplainable AI (ArgXAI, co-located with COMMA ’22), September 12, 2022"
 * 
 * @author Julian Sander
 * @author Lars Bengel
 */
public class InterventionalStatement extends CausalStatement {
	/** Interventions of this causal statement */
	protected Map<Proposition, Boolean> interventions;

	/**
	 * Initializes a new interventional causal statement
	 *
	 * @param observations	 Observations of causal atoms
	 * @param interventions	 A set of interventions on causal atoms
	 * @param conclusion	 Conclusion of the causal statement
	 */
	public InterventionalStatement(Collection<PlFormula> observations, Map<Proposition, Boolean> interventions, PlFormula conclusion) {
		super(observations, conclusion);
		this.interventions = new HashMap<>(interventions);
	}

	/**
	 * Initializes a new interventional causal statement without interventions
	 *
	 * @param observations	Observations of causal atoms
	 * @param conclusion	Conclusion of the causal statement
	 */
	public InterventionalStatement(Collection<PlFormula> observations, PlFormula conclusion) {
		super(observations, conclusion);
		this.interventions = new HashMap<>();
	}

	/**
	 * Initializes a new empty interventional causal statement
	 */
	public InterventionalStatement() {
		super();
		this.interventions = new HashMap<>();
	}

	/**
	 * Add a new intervention which sets the given atom to the given truth value to the causal statement
	 * @param atom	some causal atom
	 * @param value	some truth value
	 * @return TRUE iff the intervention is added successfully
	 */
	public boolean addIntervention(Proposition atom, boolean value) {
		return Boolean.TRUE.equals(this.interventions.put(atom, value));
	}

    /**
     * Retrieves the interventions of this causal statement.
     * @return A Msp containing the interventions mapped from explainable atoms to their respective boolean values.
     */
	public Map<Proposition, Boolean> getInterventions(){
		return new HashMap<>(this.interventions);
	}

	/*
	private boolean checkInterventionalStatement(CausalKnowledgeBase ckbase, PlFormula conclusion) {
		CausalKnowledgeBase newBase = ckbase.clone();
		for (Proposition atom : interventions.keySet()) {
			newBase = new CausalKnowledgeBase(newBase.getCausalModel().intervene(atom, interventions.get(atom)), ckbase.getAssumptions());
		}
		return newBase.entails(this.getObservations(), conclusion);
	}

	 */
}
