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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.rankings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.reasoner.SAFRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.TuplesRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.BurdenBasedRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import net.sf.tweety.arg.rankings.semantics.LatticeArgumentRanking;
import net.sf.tweety.arg.rankings.semantics.NumericalArgumentRanking;

/**
 * Test class for checking some counterexamples for the Tuples*
 * ranking reasoner and the SAF ranking reasoner.
 * 
 * @author Anna Gessler
 */
public class RankingReasonerTest {

	public static final int DEFAULT_TIMEOUT = 20000;

	@Test(timeout = DEFAULT_TIMEOUT)
	public void DPandQPCounterexampleTest() throws Exception {
		TuplesRankingReasoner reasoner_tuples = new TuplesRankingReasoner();
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument a1 = new Argument("a1");
		Argument a2 = new Argument("a2");
		Argument b = new Argument("b");
		Argument b1 = new Argument("b1");
		Argument b2 = new Argument("b2");
		Argument b3 = new Argument("b3");
		Argument b4 = new Argument("b4");
		Argument b5 = new Argument("b5");
		Argument b6 = new Argument("b6");
		Argument b7 = new Argument("b7");
		dt.add(a);
		dt.add(b);
		dt.add(a1);
		dt.add(a2);
		dt.add(b1);
		dt.add(b2);
		dt.add(b3);
		dt.add(b4);
		dt.add(b5);
		dt.add(b6);
		dt.add(b7);
		dt.add(new Attack(a1,a));
		dt.add(new Attack(a2,a));
		dt.add(new Attack(b2,b1));
		dt.add(new Attack(b1,b));
		dt.add(new Attack(b6,b4));
		dt.add(new Attack(b5,b4));
		dt.add(new Attack(b7,b4));
		dt.add(new Attack(b4,b3));
		dt.add(new Attack(b3,b)); 
		
		LatticeArgumentRanking ranking = reasoner_tuples.getModel(dt);
		assertFalse(ranking.isStrictlyMoreAcceptableThan(b, a));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void DDPCounterexampleTest() throws Exception {
		TuplesRankingReasoner reasoner_tuples = new TuplesRankingReasoner();
		SAFRankingReasoner reasoner_saf = new SAFRankingReasoner();
		BurdenBasedRankingReasoner reasoner_burden = new BurdenBasedRankingReasoner();
		DiscussionBasedRankingReasoner reasoner_dis = new DiscussionBasedRankingReasoner();
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument a1 = new Argument("a1");
		Argument a2 = new Argument("a2");
		Argument a3 = new Argument("a3");
		Argument a4 = new Argument("a4");
		Argument a5 = new Argument("a5");
		Argument a6 = new Argument("a6");
		Argument b = new Argument("b");
		Argument b1 = new Argument("b1");
		Argument b2 = new Argument("b2");
		Argument b3 = new Argument("b3");
		Argument b4 = new Argument("b4");
		dt.add(a);
		dt.add(b);
		dt.add(a1);
		dt.add(a2);
		dt.add(a3);
		dt.add(a4);
		dt.add(a5);
		dt.add(a6);
		dt.add(b1);
		dt.add(b2);
		dt.add(b3);
		dt.add(b4);
		dt.add(new Attack(a6, a5));
		dt.add(new Attack(a5, a4));
		dt.add(new Attack(a4, a));
		dt.add(new Attack(a3, a2));
		dt.add(new Attack(a2, a1));
		dt.add(new Attack(a1, a));
		dt.add(new Attack(b3, b2));
		dt.add(new Attack(b2, b));
		dt.add(new Attack(b4, b2));
		dt.add(new Attack(b1, b));
		
		LatticeArgumentRanking ranking = reasoner_tuples.getModel(dt);
		NumericalArgumentRanking ranking2 = reasoner_saf.getModel(dt);
		NumericalArgumentRanking ranking3 = reasoner_burden.getModel(dt);
		LatticeArgumentRanking ranking4 = reasoner_dis.getModel(dt);
		assertFalse(ranking.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking2.isStrictlyMoreAcceptableThan(a, b));
//		assertTrue(ranking3.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking4.isStrictlyMoreAcceptableThan(a, b));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void AvsFDCounterexampleTest() throws Exception {
		SAFRankingReasoner reasoner_saf = new SAFRankingReasoner();
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument a1 = new Argument("a1");
		Argument a2 = new Argument("a2");
		Argument a3 = new Argument("a3");
		Argument a4 = new Argument("a4");
		Argument a5 = new Argument("a5");
		Argument a6 = new Argument("a6");
		Argument b = new Argument("b");
		Argument b1 = new Argument("b1");
		dt.add(a);
		dt.add(b);
		dt.add(a1);
		dt.add(a2);
		dt.add(a3);
		dt.add(a4);
		dt.add(a5);
		dt.add(a6);
		dt.add(b1);
		
		dt.add(new Attack(a4,a1));
		dt.add(new Attack(a1,a));
		dt.add(new Attack(a5,a2));
		dt.add(new Attack(a2,a));
		dt.add(new Attack(a6,a3));
		dt.add(new Attack(a3,a));
		dt.add(new Attack(b1,b));
		
		NumericalArgumentRanking ranking = reasoner_saf.getModel(dt);
		assertFalse(ranking.isStrictlyMoreAcceptableThan(a, b));
	}
	
}
