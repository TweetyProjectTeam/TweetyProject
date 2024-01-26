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

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class describes a causal knowledge base.
 *
 * Reference: "Argumentation-based Causal and Counterfactual Reasoning" by
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, published at 1st International Workshop on Argumentation
 * for eXplainable AI (ArgXAI, co-located with COMMA ’22), September 12, 2022
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class CausalKnowledgeBase extends KnowledgeBase {
	
	/**
	 * A causal model, which is the basis of this knowledge base.
	 */
	private CausalModel causalModel;

	/**
	 * Creates a new causal knowledge base
	 * @param facts Causal model representing the causal origin of this knowledge base.
	 * @param assumptions Set of assumptions about the background atoms of the causal model.
	 */
	public CausalKnowledgeBase(CausalModel facts, Set<PlFormula> assumptions) {
		super(assumptions);
		this.causalModel = facts;
		for(var assumption : assumptions) {
			if(!assumption.isLiteral()) {
				throw new IllegalArgumentException("There is at least one background assumption, that is not a literal.");
			}

			for(var atom : assumption.getAtoms()) {
				if(!facts.getBackGroundAtoms().contains(atom)) {
					throw new IllegalArgumentException("There is at least one assumption, that contains an atom which is not a background atom.");
				}
			}
		}

		for(var u : facts.getBackGroundAtoms()) {
			if(!assumptions.contains(u) && !assumptions.contains(new Negation(u))) {
				throw new IllegalArgumentException("There is at least one background atom without any assumption.");
			}
		}
	}
	
	/**
	 * @return The underlying causal model of this instance.
	 */
	public CausalModel getCausalModel() {
		return this.causalModel;
	}
	
	/**
	 * @return Returns all propositional formulas of this knowledge base, this includes the structural equations of the underlying causal model.
	 */
	@Override
	public HashSet<PlFormula> getBeliefs(){
		var output = new HashSet<PlFormula>(this.formulas);
		output.addAll(this.causalModel.getStructuralEquations());
		return output;
	}
	
	/**
	 * *description missing*
	 * @return *description missing*
	 */
	public HashSet<PlFormula> getBeliefsWithoutStructuralEquations(){
		return new HashSet<PlFormula>(this.formulas);
	}

	/**
	 * Returns all 1-atom-conclusions of this instance if the specified set of formulas is
	 * used as premises.
	 * @param premises Set of formulas which are added to this knowledge base to get to the returned conlusions.
	 * @return Set of formulas, that can be concluded from this knowledge base, if the specified formulas are added.
	 */
	public HashSet<PlFormula> getSingelAtomConclusions(Set<PlFormula> premises){
		var conclusions = new HashSet<PlFormula>();
		for(var formula : this.causalModel.getStructuralEquations()) {
			for(var atom : formula.getAtoms()) {
				if(this.entails(premises, atom)) {
					conclusions.add(atom);
				}else{
					var negAtom = new Negation(atom);
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
		var output = new CausalKnowledgeBase(this.causalModel, this.getAssumptions());
		output.addAll(this.formulas);
		return output;
	}
}
