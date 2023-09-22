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

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
class CausalModelTest {

	/**
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CausalModel#CausalModel(java.util.Set, java.util.Set, java.util.Set)}.
	 * Test method for {@link org.tweetyproject.arg.dung.causal.syntax.CausalModel#CausalModel(java.util.Set)}.
	 */
	@Test
	void testCausalModel() {
		
		//Arrange
		var corona = new Proposition("corona");
		var influenza = new Proposition("influenza");
		var atRisk = new Proposition("at-risk");
		
		var covid = new Proposition("covid");
		var flu = new Proposition("flu");
		var shortOfBreath = new Proposition("short-of-breath");
		var fever = new Proposition("fever");
//		var chills = new Proposition("chills");
		
		var eq1 = new Equivalence(covid, corona);
		var eq2 = new Equivalence(flu, influenza);
		var eq3 = new Equivalence(fever, new Disjunction(covid, flu));
		//var eq4 = new Equivalence(chills, fever);
		var eq5 = new Equivalence(shortOfBreath, new Conjunction(covid, atRisk));
		var wrongEq3 = new Equivalence(fever, new Disjunction(covid, fever));
		var missingFluEq2 = new Equivalence(fever, influenza);
		var doubleFeverEq4 = new Equivalence(new Conjunction(covid, atRisk), fever);
		
		//Act
		var model1 = BuildFirstOption(corona, influenza, atRisk, covid, flu, shortOfBreath, fever,  eq1, eq2, eq3, eq5);
		var model2 = BuildSecondOption(eq1, eq2, eq3, eq5);
		
		//Assure
		Assert.assertEquals(model1.hashCode(), model2.hashCode());
//		Assert.assertEquals(model1, model2);
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () 
				-> BuildFirstOption(corona, influenza, atRisk, covid, flu, shortOfBreath, fever,  eq1, eq2, wrongEq3, eq5));
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () 
				-> BuildFirstOption(corona, influenza, atRisk, covid, flu, shortOfBreath, fever,  eq1, missingFluEq2, eq3, eq5));
		Assertions.assertThrowsExactly(IllegalArgumentException.class, () 
				-> BuildFirstOption(corona, influenza, atRisk, covid, flu, shortOfBreath, fever,  eq1, eq2, eq3, doubleFeverEq4));
//		Assertions.assertEquals(model1, model2);
	}
	
	private CausalModel BuildFirstOption(Proposition corona, Proposition influenza, Proposition atRisk, Proposition covid, 
			Proposition flu, Proposition shortOfBreath, Proposition fever, 
			Equivalence eq1, Equivalence eq2, Equivalence eq3, Equivalence eq4 ) {
		
		var u = new HashSet<Proposition>();
		u.add(corona);
		u.add(influenza);
		u.add(atRisk);

		var v = new HashSet<Proposition>();
		v.add(covid);
		v.add(flu);
		v.add(shortOfBreath);
		v.add(fever);
//		v.add(chills);

		var eqs = new HashSet<Equivalence>();
		eqs.add(eq1);
		eqs.add(eq2);
		eqs.add(eq3);
		eqs.add(eq4);
//		eqs.add(eq5);
		
		return new CausalModel(u, v, eqs);
		
	}
	
	private CausalModel BuildSecondOption(Equivalence eq1, Equivalence eq2, Equivalence eq3, Equivalence eq4) {
		var eqs = new HashSet<Equivalence>();
		eqs.add(eq1);
		eqs.add(eq2);
		eqs.add(eq3);
		eqs.add(eq4);
//		eqs.add(eq5);
		
		return new CausalModel(eqs);
	}

}
