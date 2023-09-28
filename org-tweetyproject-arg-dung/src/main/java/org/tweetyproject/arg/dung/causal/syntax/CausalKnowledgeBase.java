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

import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class describes a causal knowledge base.
 *
 * @see "Argumentation-based Causal and Counterfactual Reasoning" by
 * Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, published at 1st International Workshop on Argumentation
 * for eXplainable AI (ArgXAI, co-located with COMMA ’22), September 12, 2022
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class CausalKnowledgeBase extends PlBeliefSet {

	/**
	 * set of background assumptions <br/> - <br/>
	 * There is at least one background assumption for each background atom (in K).
	 * A background assumption for an atom u is a literal l \in {u, \neg{u}}.
	 */
	private HashSet<PlFormula> assumptions;

	/**
	 * Creates a new causal knowledge base
	 * @param facts Causal model representing the causal origin of this knowledge base.
	 * @param assumptions Set of assumptions about the background atoms of the causal model.
	 */
	public CausalKnowledgeBase(CausalModel facts, Set<PlFormula> assumptions) {
		super(facts.getStructuralEquations());
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

		this.assumptions = new HashSet<>(assumptions);
	}

	public boolean addAssumption(PlFormula assumption) {
		return this.assumptions.add(assumption);
	}

	/**
	 * Computes if a specified conclusion could be drawn from adding the specified premises to this instance.
	 * @param premises Set of formulas, which will be added to this knowledge base.
	 * @param conclusion Formula, which is checked to be a conclusion of the combination of this instance and the specified premises or not.
	 * @return TRUE iff the specified formula is a conclusion of this knowledge base and the specified set of premises.
	 */
	public boolean entails(Set<PlFormula> premises, PlFormula conclusion) {
		var beliefs = new HashSet<>(this.formulas);
		beliefs.addAll(premises);
		var beliefSet = new PlBeliefSet(beliefs);
		return new SimplePlReasoner().query(beliefSet, conclusion);
	}

	public HashSet<PlFormula> getAssumptions() {
		return new HashSet<>(this.assumptions);
	}

	/**
	 * Returns all 1-atom-conclusions of this instance if the specified set of formulas is
	 * used as premises.
	 * @param premises Set of formulas which are added to this knowledge base to get to the returned conlusions.
	 * @return Set of formulas, that can be concluded from this knowledge base, if the specified formulas are added.
	 */
	public HashSet<PlFormula> getSingelAtomConclusions(Set<PlFormula> premises){
		var conclusions = new HashSet<PlFormula>();
		for(var formula : this.formulas) {
			for(var atom : formula.getAtoms()) {
				if(this.entails(premises, atom)) {
					conclusions.add(atom);
				}
			}
		}

		return conclusions;
	}
	
	public boolean removeAssumption(PlFormula assumption) {
		return this.assumptions.remove(assumption);
	}
}
