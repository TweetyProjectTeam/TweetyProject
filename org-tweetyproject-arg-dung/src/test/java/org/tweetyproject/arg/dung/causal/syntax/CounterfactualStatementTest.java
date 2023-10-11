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

import java.util.HashMap;
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
 * @author User
 *
 */
class CounterfactualStatementTest {

	/**
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CounterfactualStatement#holds(org.tweetyproject.arg.dung.causal.syntax.CausalKnowledgeBase)}.
	 */
	@Test
	void testHolds() {
		//Arrange
				var covid = new Proposition("covid");
				var corona = new Proposition("corona");
				var shortOfBreath = new Proposition("short-of-breath");
				var atRisk = new Proposition("at-risk");
				var influenza = new Proposition("influenza");
				var fever = new Proposition("fever");
				var negInfluenza = new Negation(influenza);
				var causalModel = setupModel(covid, corona, shortOfBreath, atRisk, influenza, fever);
				var causalKnowledgeBase = setup(corona, atRisk, negInfluenza, causalModel);

				//Act
				var premises = new HashSet<PlFormula>();
				premises.add(fever);
				var interventions = new HashMap<Proposition, Boolean>();
				interventions.put(covid, false);
				var feverCopy = new Proposition("fever*");
				var conclusions1 = new HashSet<PlFormula>();
				conclusions1.add(feverCopy);
				var counterfactualStatement1 = new CounterfactualStatement(conclusions1, interventions, premises);
				var conclusions2 = new HashSet<PlFormula>();
				conclusions2.add( new Negation(feverCopy));
				var counterfactualStatement2 = new CounterfactualStatement(conclusions2, interventions, premises);
				
				//Assert
				Assertions.assertFalse(counterfactualStatement1.holds(causalKnowledgeBase));
				Assertions.assertFalse(counterfactualStatement2.holds(causalKnowledgeBase));
				
				//Act
				var premises2 = new HashSet<PlFormula>();
				premises2.add(new Conjunction(fever, shortOfBreath));
				var counterfactualStatement3 = new CounterfactualStatement(conclusions1, interventions, premises2);
				var counterfactualStatement4 = new CounterfactualStatement(conclusions2, interventions, premises2);
				Assertions.assertFalse(counterfactualStatement3.holds(causalKnowledgeBase));
				Assertions.assertFalse(counterfactualStatement4.holds(causalKnowledgeBase));
	}
	
	private CausalModel setupModel(Proposition covid, Proposition corona, Proposition shortOfBreath, Proposition atRisk, Proposition influenza, Proposition fever) {
		
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
		return new CausalModel(eqs);
	}

	private CausalKnowledgeBase setup(Proposition corona, Proposition atRisk, Negation negInfluenza, CausalModel causalModel) {
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
