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

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class is responsible for the representation of an {@link Argument} that was induced from a {@link CausalKnowledgeBase}
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class InducedArgument extends Argument {

	private static String generateName(Set<PlFormula> premises, PlFormula conclusion) {
		return "(" + premises.toString() + "," + conclusion.toString() + ")";
	}

	private CausalKnowledgeBase knowledgeBase;

	private HashSet<PlFormula> premises;

	private PlFormula conclusion;

	/**
	 * Creates a causal argument
	 * @param knowledgeBase The causal knowledge base based on which this argument is created.
	 * @param premises Set of formulas which have to be added to the knowledge base to be able to come to the the specified conclusion.
	 * @param conclusion Formula that concludes from the knowledge base, if the specified premises are added to the base.
	 */
	public InducedArgument(CausalKnowledgeBase knowledgeBase, Set<PlFormula> premises, PlFormula conclusion) {
		super(InducedArgument.generateName(premises, conclusion));
		this.checkCorrectForm(knowledgeBase, premises, conclusion);

		this.knowledgeBase = knowledgeBase;
		this.premises = new HashSet<>(premises);
		this.conclusion = conclusion;
	}

    /**
     * Returns the conclusion of this induced argument.
     * 
     * @return The conclusion that can be drawn from the knowledge base given the premises.
     */
	public PlFormula getConclusion() {
		return this.conclusion;
	}

    /**
     * Returns the causal knowledge base associated with this argument.
     * 
     * @return The causal knowledge base from which this argument is derived.
     */
	public CausalKnowledgeBase getKnowledgeBase() {
		return this.knowledgeBase;
	}

    /**
     * Returns the premises of this induced argument.
     * 
     * @return A set containing all the premises required for deriving the conclusion in this argument.
     */
	public HashSet<PlFormula> getPremises() {
		return new HashSet<>(this.premises);
	}

	@Override
	public String toString() {
		return InducedArgument.generateName(this.premises, this.conclusion);
	}

	private void checkCorrectForm(CausalKnowledgeBase knowledgeBase, Set<PlFormula> premises, PlFormula conclusion) {
		for(var formula : premises) {
			if(!knowledgeBase.getAssumptions().contains(formula)) {
				throw new IllegalArgumentException("premises is not a subset of the assumptions");
			}

			var lessPremises = new HashSet<>(premises);
			lessPremises.remove(formula);
			if(knowledgeBase.entails( lessPremises, conclusion)) {
				throw new IllegalArgumentException("premises is not the minimal set of assumptions");
			}
		}

		if(!knowledgeBase.entails( premises, conclusion)) {
			throw new IllegalArgumentException("conclusion can not be infered from this knowledge base using this premises");
		}
	}

}
