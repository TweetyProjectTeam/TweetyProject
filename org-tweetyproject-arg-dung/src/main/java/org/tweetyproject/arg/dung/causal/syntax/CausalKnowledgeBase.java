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
import org.tweetyproject.logics.pl.syntax.*;

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
public class CausalKnowledgeBase {
	
	/**
	 * causal model 
	 */
	public CausalModel Facts;
	/**
	 * set of background assumptions <br/> - <br/>
	 * There is at least one background assumption for each background atom (in K).
	 * A background assumption for an atom u is a literal l \in {u, \neg{u}}.
	 */
	public HashSet<PlFormula> Assumptions;
	
	public CausalKnowledgeBase(CausalModel facts, Set<PlFormula> assumptions) {
		for(var u : facts.BackGroundAtoms) {
			if(!assumptions.contains(u) && !assumptions.contains(new Negation(u))) {
				throw new IllegalArgumentException("There is at least one background atom without any assumption.");
			}
		}
		
		for(var assumption : assumptions) {
			if(!assumption.isLiteral()) {
				throw new IllegalArgumentException("There is at least one background assumption, that is not a literal.");
			}
			
			for(var atom : assumption.getAtoms()) {
				if(!facts.BackGroundAtoms.contains(atom)) {
					throw new IllegalArgumentException("There is at least one assumption, that contains an atom which is not a background atom.");
				}
			}
		}
		
		Facts = facts;
		Assumptions = new HashSet<PlFormula>(assumptions);
	}
	
	public boolean entails(Set<PlFormula> premises, PlFormula conclusion) {
		var beliefs = new HashSet<PlFormula>(this.Facts.getBeliefs());
		beliefs.addAll(premises);
		var beliefSet = new PlBeliefSet(beliefs);
		return new SimplePlReasoner().query(beliefSet, conclusion);
	}
	
	public HashSet<PlFormula> getConclusions(Set<PlFormula> premises){
		var conclusions = new HashSet<PlFormula>();
		for(var fact : Facts.BackGroundAtoms) {
			if(entails(premises, fact)) {
				conclusions.add(fact);
			}
		}
		for(var fact : Facts.ExplainableAtoms) {
			if(entails(premises, fact)) {
				conclusions.add(fact);
			}
		}
		
		return conclusions;
	}
}
