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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * @author User
 *
 */
class CausalTheoryTest {

	/**
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CausalTheory#addAttack(org.tweetyproject.arg.dung.syntax.Argument, org.tweetyproject.arg.dung.syntax.Argument)}.
	 */
	@Test
	void testAddAttack() {
		//Arrange
		var corona = new Proposition("corona");
		var influenza = new Proposition("influenza");
		var fever = new Proposition("fever");
		var shortOfBreath = new Proposition("short-of-breath");
		var negInfluenza = new Negation(influenza);
		var causalKnowledgeBase = setup(corona, influenza, fever, shortOfBreath, negInfluenza);
		var framework = new CausalTheory(causalKnowledgeBase);
		var args = framework.getArguments();
		var attacker = args.iterator().next();
		CausalArgument victim = null;
		
		for(var arg : args) {
			if(!arg.Premises.contains(attacker.Conclusion.complement())) {
				victim = arg;
				break;
			}
		}
		
		//Act
		//Assert
		CausalArgument victim2 = victim;
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> framework.addAttack(attacker, victim2));
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> framework.addAttack((Argument)attacker, (Argument) victim2));
	}

	/**
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CausalTheory#add(org.tweetyproject.arg.dung.syntax.Argument)}.
	 */
	@Test
	void testAddArgument() {
		//Arrange
		var corona = new Proposition("corona");
		var influenza = new Proposition("influenza");
		var fever = new Proposition("fever");
		var shortOfBreath = new Proposition("short-of-breath");
		var negInfluenza = new Negation(influenza);
		var causalKnowledgeBase = setup(corona, influenza, fever, shortOfBreath, negInfluenza);
		var framework = new CausalTheory(causalKnowledgeBase);
		var a0 = new Argument("a0");

		//Act
		//Assert
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> framework.add(a0));
	}

	/**
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CausalTheory#CausalTheory(org.tweetyproject.arg.dung.causal.syntax.CausalKnowledgeBase)}.
	 */
	@Test
	void testCausalTheory() {
		//Arrange
		var corona = new Proposition("corona");
		var influenza = new Proposition("influenza");
		var fever = new Proposition("fever");
		var shortOfBreath = new Proposition("short-of-breath");
		var negInfluenza = new Negation(influenza);
		var causalKnowledgeBase = setup(corona, influenza, fever, shortOfBreath, negInfluenza);
		
		//Test entailment of causal knowledge base
		var premises = new HashSet<PlFormula>();
		premises.add(fever);
		Assertions.assertFalse(causalKnowledgeBase.entails(premises, shortOfBreath));
		var negShortOfBreath = new Negation(shortOfBreath);
		Assertions.assertFalse(causalKnowledgeBase.entails(premises, negShortOfBreath));
		
		//Act
		causalKnowledgeBase.Facts.StructuralEquations.add(new Equivalence(fever, new Tautology()));
		var framework = new CausalTheory(causalKnowledgeBase);
		var reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.ST);
		var extensions = reasoner.getModels(framework);
		
		boolean allExtConcludeShortOfBreath = true;
		boolean allExtConcludeNotShortOfBreath = true;
		for(var ext : extensions) {
			boolean hasConclusionShortOfBreath = false;
			boolean hasConclusionNotShortOfBreath = false;
			for(var argument : ext) {
				if(((CausalArgument) argument).Conclusion.equals(shortOfBreath)) {
					hasConclusionShortOfBreath = true;
				}
				if(((CausalArgument) argument).Conclusion.equals(negShortOfBreath)) {
					hasConclusionNotShortOfBreath = true;
				}
			}
			if(!hasConclusionShortOfBreath) {
				allExtConcludeShortOfBreath = false;
			}
			if(!hasConclusionNotShortOfBreath) {
				allExtConcludeNotShortOfBreath = false;
			}
		}
		
		//Assert
		Assertions.assertFalse(allExtConcludeShortOfBreath);
		Assertions.assertFalse(allExtConcludeNotShortOfBreath);
		
		//Test entailment of causal knowledge base
		var causalKnowledgeBase2 = setup(corona, influenza, fever, shortOfBreath, negInfluenza);
		var premises2 = new HashSet<PlFormula>();
		var formula = new Conjunction(new Negation(corona), fever);
		premises2.add(formula);
		Assertions.assertTrue(causalKnowledgeBase2.getConclusions(premises2).contains(influenza));
		
		//Act
		causalKnowledgeBase2.Facts.StructuralEquations.add(new Equivalence(formula, new Tautology()));
		var framework2 = new CausalTheory(causalKnowledgeBase2);
		var extensions2 = reasoner.getModels(framework2);
		
		boolean allExtConcludeInfluenza = true;
		for(var ext : extensions2) {
			boolean hasConclusionInfluenza = false;
			for(var argument : ext) {
				if(((CausalArgument) argument).Conclusion.equals(influenza)) {
					hasConclusionInfluenza = true;
				}
			}
			if(!hasConclusionInfluenza) {
				allExtConcludeInfluenza = false;
			}
		}
		Assertions.assertTrue(allExtConcludeInfluenza);
	}

	private CausalKnowledgeBase setup(Proposition corona, Proposition influenza, Proposition fever, Proposition shortOfBreath, Negation negInfluenza) {
		//causal model
		var atRisk = new Proposition("at-risk");
		
		var covid = new Proposition("covid");
		var flu = new Proposition("flu");
		var chills = new Proposition("chills");

		var eq1 = new Equivalence(covid, corona);
		var eq2 = new Equivalence(flu, influenza);
		var eq3 = new Equivalence(fever, new Disjunction(covid, flu));
		var eq4 = new Equivalence(chills, fever);
		var eq5 = new Equivalence(shortOfBreath, new Conjunction(covid, atRisk));
		var eqs = new HashSet<Equivalence>();
		eqs.add(eq1);
		eqs.add(eq2);
		eqs.add(eq3);
		eqs.add(eq4);
		eqs.add(eq5);
		var causalModel = new CausalModel(eqs);

		// Assumptions
		var negAtRisk = new Negation(atRisk);
		var negCorona = new Negation(corona);
		var assumptions = new HashSet<PlFormula>();
		assumptions.add(atRisk);
		assumptions.add(negAtRisk);
		assumptions.add(negCorona);
		assumptions.add(negInfluenza);

		return new CausalKnowledgeBase(causalModel, assumptions);
	}
}
