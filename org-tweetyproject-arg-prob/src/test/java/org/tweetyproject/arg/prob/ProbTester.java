/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.prob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.divisions.Division;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.prob.lotteries.ArgumentationLottery;
import org.tweetyproject.arg.prob.lotteries.SubgraphProbabilityFunction;
import org.tweetyproject.arg.prob.lotteries.UtilityFunction;

/**
 * 
 * @author Sebastian Franke
 *
 */

public class ProbTester {
	@Test
	public void inconsistencyTest(){
		// create some Dung theory
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		theory.add(a);
		theory.add(b);
		theory.add(c);		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(c,b));
		
		
		SubgraphProbabilityFunction prob = new SubgraphProbabilityFunction(theory);
		
		
		DungTheory upd = new DungTheory();
		upd.add(a);
		
		prob = prob.roughUpdate(upd);

		assertTrue(prob.isNormalized());
	}
	@Test
	public void subGraphTest(){
		// create some Dung theory
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		theory.add(a);
		theory.add(b);
		theory.add(c);		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(c,b));
		
		
		
		// print the uniform probability function
		SubgraphProbabilityFunction prob = new SubgraphProbabilityFunction(theory);
	

		//---
		Extension<DungTheory> a1 = new Extension<DungTheory>(); Extension<DungTheory> a2 = new Extension<DungTheory>();
		a1.add(a); a2.add(b);
		Division d1 = new Division(a1,a2);
		assertTrue(prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS).toString().equals("0.42105263157894735"));
		//---
		a1 = new Extension<DungTheory>(); a2 = new Extension<DungTheory>();
		a1.add(a);
		d1 = new Division(a1,a2);
		assertTrue(prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS).toString().equals("0.5263157894736842"));
		//---
		a1 = new Extension<DungTheory>(); a2 = new Extension<DungTheory>();
		a1.add(a); a1.add(b);
		d1 = new Division(a1,a2);
		assertTrue(prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS).toString().equals("0.10526315789473684"));
		//---
		a1 = new Extension<DungTheory>(); a2 = new Extension<DungTheory>();
		a1.add(a); a1.add(c); a2.add(b); a2.add(a);
		d1 = new Division(a1,a2);
		assertTrue(prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS).toString().equals("0.0"));
		//---
		a1 = new Extension<DungTheory>(); a2 = new Extension<DungTheory>();
		a1.add(a); a1.add(c); a2.add(b);
		d1 = new Division(a1,a2);
		assertTrue(prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS).toString().equals("0.3157894736842105"));
		

		//---
		assertTrue(prob.getAcceptanceProbability(a, Semantics.GROUNDED_SEMANTICS).toString().equals("0.5263157894736842"));

	
		// lotteries
		Collection<Division> stDivisions = Division.getStandardDivisions(theory);
		ArgumentationLottery lottery = new ArgumentationLottery(stDivisions,prob, Semantics.GROUNDED_SEMANTICS);
		assertTrue(lottery.toString().equals("[ 0.05263157894736842,({a,b}, {c}) ; 0.15789473684210525,({c}, {a,b}) ; 0.10526315789473684,({b}, {a,c}) ; 0.10526315789473684,({a}, {b,c}) ; 0.10526315789473684,({}, {a,b,c}) ; 0.05263157894736842,({a,b,c}, {}) ; 0.10526315789473684,({b,c}, {a}) ; 0.3157894736842105,({a,c}, {b}) ]"));
		
		UtilityFunction util = new UtilityFunction();
		util.put((Division)stDivisions.toArray()[2], 12d);
		util.put((Division)stDivisions.toArray()[4], 7d);
		util.put((Division)stDivisions.toArray()[6], -2d);

		assertTrue(util.toString().equals("{({b}, {a,c})=12.0, ({}, {a,b,c})=7.0, ({b,c}, {a})=-2.0}"));
		assertEquals(util.getExpectedUtility(lottery), 1.7894736842105263, 0.01);
	}

}
