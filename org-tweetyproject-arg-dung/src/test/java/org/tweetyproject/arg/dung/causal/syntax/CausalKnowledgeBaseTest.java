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
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
class CausalKnowledgeBaseTest {

	/**
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CausalKnowledgeBase#CausalKnowledgeBase(org.tweetyproject.arg.dung.causal.syntax.CausalModel, java.util.Set)}.
	 */
	@Test
	void testCausalKnowledgeBase() {
		//Arrange
		var corona = new Proposition("corona");
		var influenza = new Proposition("influenza");
		var fever = new Proposition("fever");
		var negInfluenza = new Negation(influenza);
		var causalKnowledgeBase = setup(corona, influenza, fever, negInfluenza);
		var notLiteral = new Conjunction(negInfluenza, corona);
		var notBackGroundAtom = new Proposition("this is not a background-atom");
		
				
		//Assert
		causalKnowledgeBase.Assumptions.remove(negInfluenza); //simulate missing assumption for background atom
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () 
				-> new CausalKnowledgeBase(causalKnowledgeBase.Facts, causalKnowledgeBase.Assumptions));
		causalKnowledgeBase.Assumptions.add(negInfluenza);	
		causalKnowledgeBase.Assumptions.add(notLiteral);	//simulate some assumptions being no literal
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () 
				-> new CausalKnowledgeBase(causalKnowledgeBase.Facts, causalKnowledgeBase.Assumptions));
		causalKnowledgeBase.Assumptions.remove(notLiteral);
		causalKnowledgeBase.Assumptions.add(notBackGroundAtom); //simulate assumption anything else than a background-atom
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () 
				-> new CausalKnowledgeBase(causalKnowledgeBase.Facts, causalKnowledgeBase.Assumptions));
	}

	/**
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CausalKnowledgeBase#entails(java.util.Set, org.tweetyproject.logics.pl.syntax.PlFormula)}.
	 */
	@Test
	void testEntails() {
		//Arrange
		var corona = new Proposition("corona");
		var influenza = new Proposition("influenza");
		var fever = new Proposition("fever");
		var negInfluenza = new Negation(influenza);
		var causalKnowledgeBase = setup(corona, influenza, fever, negInfluenza);

		//Act
		var premises = new HashSet<PlFormula>();
		premises.add(new Conjunction(new Negation(corona), fever));
		Assertions.assertTrue(causalKnowledgeBase.entails(premises, influenza));
	}

	/**
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CausalKnowledgeBase#getConclusions(java.util.Set)}.
	 */
	@Test
	void testGetConclusions() {
		//Arrange
		var corona = new Proposition("corona");
		var influenza = new Proposition("influenza");
		var fever = new Proposition("fever");
		var negInfluenza = new Negation(influenza);
		var causalKnowledgeBase = setup(corona, influenza, fever, negInfluenza);

		//Act
		var premises = new HashSet<PlFormula>();
		premises.add(new Conjunction(new Negation(corona), fever));
		Assertions.assertTrue(causalKnowledgeBase.getConclusions(premises).contains(influenza));
	}
	
	
	private CausalKnowledgeBase setup(Proposition corona, Proposition influenza, Proposition fever, Negation negInfluenza) {
		//causal model
		var atRisk = new Proposition("at-risk");

		var covid = new Proposition("covid");
		var flu = new Proposition("flu");
		var shortOfBreath = new Proposition("short-of-breath");
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
