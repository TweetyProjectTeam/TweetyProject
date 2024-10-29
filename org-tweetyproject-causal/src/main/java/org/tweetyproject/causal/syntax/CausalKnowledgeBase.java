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
package org.tweetyproject.causal.syntax;

import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class describes a causal knowledge base which consists of a {@link StructuralCausalModel} and a set of background assumptions.
 *
 * @see "Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, 'Argumentation-based Causal and Counterfactual Reasoning', 1st International Workshop on Argumentation for eXplainable AI (ArgXAI), 2022"
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class CausalKnowledgeBase extends PlBeliefSet {
	
	/** Explicit storage of causal model of this causal knowledge base */
	protected StructuralCausalModel model;
	/** The set of background assumptions */
	protected Collection<PlFormula> assumptions;

	/**
	 * Initializes a causal knowledge from the given causal model without assumptions
	 * @param model some causal model
	 */
	public CausalKnowledgeBase(StructuralCausalModel model) {
		super(model);
		this.model = model.clone();
		this.assumptions = new HashSet<>();
	}

	/**
	 * Initialize a new causal knowledge base
	 * @param model some causal model
	 * @param assumptions Set of assumptions about the background atoms of the causal model.
	 */
	public CausalKnowledgeBase(StructuralCausalModel model, Collection<PlFormula> assumptions) {
		this(model);
		for(PlFormula assumption : assumptions) {
			this.addAssumption(assumption);
		}

		for(Proposition u : model.getBackgroundAtoms()) {
			if(!assumptions.contains(u) && !assumptions.contains(new Negation(u))) {
				throw new IllegalArgumentException("There must be at least one assumption for each background atom");
			}
		}
	}

	/**
	 * Initializes an empty causal knowledge base
	 */
	public CausalKnowledgeBase() {
		this.model = new StructuralCausalModel();
		this.assumptions = new HashSet<>();
	}

	/**
	 * Adds an assumption to this knowledge base.
	 *
	 * @param assumption The PlFormula representing the assumption to be added
	 * @return "True" iff the assumption was successfully added
	 */
	public boolean addAssumption(PlFormula assumption) {
		if (!assumption.isLiteral()) throw new IllegalArgumentException("Assumption must be literal");
		if (!model.getBackgroundAtoms().containsAll(assumption.getAtoms())) throw new IllegalArgumentException("Can only assume background atoms");
		return this.assumptions.add(assumption);
	}

	/**
	 * Removes an assumption from this knowledge base.
	 *
	 * @param assumption The assumption to be removed.
	 * @return true if the assumption was successfully removed, false otherwise.
	 */
	public boolean removeAssumption(PlFormula assumption) {
		// TODO cannot remove the only assumption for a background atom
		return this.assumptions.remove(assumption);
	}

	/**
	 * @return Set of {@link PlFormula}, which are the assumptions of this instance.
	 */
	public Collection<PlFormula> getAssumptions() {
		return new HashSet<>(this.assumptions);
	}

	public StructuralCausalModel getCausalModel() {
		return this.model.clone();
	}

	public CausalKnowledgeBase getTwinVersion() {
        return new CausalKnowledgeBase(this.getCausalModel().getTwinModel(), this.getAssumptions());
	}

	/**
	 * Returns all propositional formulas of this knowledge base, i.e.,
	 * the structural equations of the corresponding causal model
	 *
	 * @return the set of structural equations
	 */
	public Collection<PlFormula> getBeliefs() {
		Collection<PlFormula> result = new HashSet<>(formulas);
		result.addAll(model.getStructuralEquations());
		return result;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", getCausalModel(), getAssumptions());
	}

	@Override
	public CausalKnowledgeBase clone() {
        return new CausalKnowledgeBase(this.model, this.getAssumptions());
	}
}
