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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * This class describes a causal knowledge base.
 *
 * @see "Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, 'Argumentation-based Causal and Counterfactual Reasoning', 1st International Workshop on Argumentation for eXplainable AI (ArgXAI), 2022"
 *
 * @author Julian Sander, Lars Bengel
 */
public class CausalKnowledgeBase extends PlBeliefSet {
	
	/**
	 * Explicit storage of causal model of this causal knowledge base
	 */
	protected StructuralCausalModel model;
	protected Collection<PlFormula> assumptions;

	public CausalKnowledgeBase(StructuralCausalModel model) {
		this.model = model.clone();
		this.assumptions = new HashSet<>();
	}

	/**
	 * Initializes a new causal knowledge base
	 * @param model some causal model
	 * @param assumptions Set of assumptions about the background atoms of the causal model.
	 */
	public CausalKnowledgeBase(StructuralCausalModel model, Collection<PlFormula> assumptions) {
		this(model);
		//this.formulas.addAll(model);
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
	 * @return true if the assumption was successfully removed, false if it was not found in the set.
	 */
	public boolean removeAssumption(PlFormula assumption) {
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

	/**
	 * Returns all propositional formulas of this knowledge base, i.e.,
	 * the structural equations of the corresponding causal model
	 *
	 * @return the set of structural equations
	 */
	public Collection<PlFormula> getBeliefs(){
		Collection<PlFormula> result = new HashSet<>(formulas);
		result.addAll(model.getStructuralEquations());
		return result;
	}
	
    /**
     * Retrieves all propositional formulas of this knowledge base excluding the structural equations.
     * 
     * @return a set of beliefs without structural equations
     */
	public Collection<PlFormula> getObservations(){
		return new HashSet<>(formulas);
	}

	/**
	 * Determines whether the specified conclusion can be inferred from the given premises via this knowledge base.
	 *
	 * @param premises Set of formulas, which will be added to this knowledge base
	 * @param conclusion Formula, which is checked to be a conclusion of the combination of this instance and the specified premises or not
	 * @return "True" iff the specified formula is a conclusion of this knowledge base and the specified set of premises.
	 */
	public boolean entails(Collection<PlFormula> premises, PlFormula conclusion) {
		Collection<PlFormula> beliefs = this.getBeliefs();
		beliefs.addAll(premises);
		PlBeliefSet beliefSet = new PlBeliefSet(beliefs);
		return new SimplePlReasoner().query(beliefSet, conclusion);
	}

	/**
	 * Returns all 1-atom-conclusions of this instance if the specified set of formulas is
	 * used as premises.
	 * @param premises Set of formulas which are added to this knowledge base to get to the returned conlusions.
	 * @return Set of formulas, that can be concluded from this knowledge base, if the specified formulas are added.
	 */
	public Collection<PlFormula> getSingleAtomConclusions(Collection<PlFormula> premises){
		Collection<PlFormula> conclusions = new HashSet<>();
		for(PlFormula formula : this.model.getStructuralEquations()) {
			for(Proposition atom : formula.getAtoms()) {
				if (this.entails(premises, atom)) {
					conclusions.add(atom);
				} else {
					PlFormula negAtom = new Negation(atom);
					if(this.entails(premises, negAtom)){
						conclusions.add(negAtom);
					}
				}
			}
		}
		return conclusions;
	}
	
	@Override
	public CausalKnowledgeBase clone() {
        return new CausalKnowledgeBase(this.model, this.getAssumptions());
	}
}
