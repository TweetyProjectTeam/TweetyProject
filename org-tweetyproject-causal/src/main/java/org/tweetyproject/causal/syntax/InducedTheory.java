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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class describes an {@link DungTheory argumentation framework} that was induced by a {@link CausalKnowledgeBase}
 *
 * @see "Lars Bengel, Lydia Blümel, Tjitze Rienstra and Matthias Thimm, 'Argumentation-based Causal and Counterfactual Reasoning', 1st International Workshop on Argumentation for eXplainable AI (ArgXAI), 2022"
 *
 * @see DungTheory
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class InducedTheory extends DungTheory {

	private CausalKnowledgeBase knowledgeBase;

	/**
	 * Induces an argumentation framework from a causal knowledge base
	 * @param knowledgeBase some causal knowledge base
	 */
	public InducedTheory(CausalKnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;

		// TODO fix argument construction (minimality, relevance?)
		for(Set<PlFormula> subSetAssumptions : new SetTools<PlFormula>().subsets(knowledgeBase.getAssumptions())) {
			for(PlFormula conclusion : knowledgeBase.getSingleAtomConclusions(subSetAssumptions)) {
				this.add(new InducedArgument(subSetAssumptions, conclusion));
			}
		}

		for(Argument arg1 : this.getNodes()) {
			InducedArgument cArg1 = (InducedArgument) arg1;
			for(Argument arg2 : this.getNodes()) {
				InducedArgument cArg2 = (InducedArgument) arg2;
				if(this.checkUndercut(cArg1, cArg2)) {
					this.addAttack(cArg1, cArg2);
				}
			}
		}
	}

	@Override
	public boolean add(Argument argument) {
		if(argument instanceof InducedArgument) {
			if (!this.knowledgeBase.getAssumptions().containsAll(((InducedArgument) argument).getPremises())) {
				throw new IllegalArgumentException("Argument contains invalid assumptions");
			} else if (!this.knowledgeBase.entails(((InducedArgument) argument).getPremises(), ((InducedArgument) argument).getConclusion())) {
				throw new IllegalArgumentException("Argument has invalid conclusion");
			} // TODO check minimality

			return super.add(argument);
		} else {
			throw new IllegalArgumentException("Argument has wrong type");
		}
	}

	@Override
	public boolean addAttack(Argument attacker, Argument attacked) {
		if(!((attacker instanceof InducedArgument) && (attacked instanceof InducedArgument))) {
			throw new IllegalArgumentException("Argument has wrong type");
		}

		return this.addAttack((InducedArgument) attacker, (InducedArgument) attacked);
	}

	/**
	 * Adds an attack from the first argument to the second to this theory.
	 *
	 * @param attacker Argument which undercuts the second argument.
	 * @param attacked Argument which is undercut by the first argument.
	 * @return TRUE iff the set of attacks was changed
	 */
	public boolean addAttack(InducedArgument attacker, InducedArgument attacked) {
		if(!this.checkUndercut(attacker, attacked)) {
			throw new IllegalArgumentException("The attacking argument does not undercut the attacked argument");
		}

		return super.addAttack(attacker, attacked);
	}
	
	/**
	 * This method checks if a specified formula can be concluded from this instance,
	 * by checking whether all stable extensions contain at least one argument with the given conclusion.
	 *
	 * @param conclusion some formula, which is in question to be a conclusion of this instance.
	 * @return TRUE iff the conclusion can be drawn from this instance. FALSE if not.
	 */
	public boolean entails(PlFormula conclusion) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.ST);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(this);
		
		boolean allExtContainConclusion = true;
		for(Extension<DungTheory> ext : extensions) {
			boolean hasConclusion = false;
			for(Argument argument : ext) {
				if(((InducedArgument) argument).getConclusion().equals(conclusion)) {
					hasConclusion = true;
					break;
				}
			}
			if(!hasConclusion) {
				allExtContainConclusion = false;
				break;
			}
		}
		return allExtContainConclusion;
	}

    /**
     * Retrieves all InducedArguments from the theory.
     * 
     * @return A set of all InducedArguments within this theory.
     */
	public Collection<InducedArgument> getArguments(){
		var output = new HashSet<InducedArgument>();
		for(var argument : this.getNodes()) {
			output.add((InducedArgument) argument);
		}
		return output;
	}

    /**
     * Retrieves the causal knowledge base associated with this theory.
     * 
     * @return The causal knowledge base from which this theory is induced.
     */
	public CausalKnowledgeBase getKnowledgeBase() {
		return this.knowledgeBase;
	}

    /**
     * Checks if one argument undercuts another based on the conclusions and premises.
     * 
     * @param attacker 	The argument that potentially undercuts.
     * @param victim 	The argument that is potentially undercut.
     * @return true iff the attacker undercuts the victim, otherwise false.
     */
	private boolean checkUndercut(InducedArgument attacker, InducedArgument victim) {
		for(PlFormula premise : victim.getPremises()) {
			if(attacker.getConclusion().complement().equals(premise)) {
				return true;
			}
		}

		return false;
	}
}
