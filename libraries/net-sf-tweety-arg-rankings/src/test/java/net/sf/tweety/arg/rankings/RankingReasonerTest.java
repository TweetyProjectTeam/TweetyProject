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
import net.sf.tweety.arg.rankings.reasoner.CategorizerRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.MTRankingReasoner;
import net.sf.tweety.arg.rankings.semantics.LatticeArgumentRanking;
import net.sf.tweety.arg.rankings.semantics.NumericalArgumentRanking;

/**
 * Test class for checking counterexamples for some postulates.
 * 
 * @author Anna Gessler
 */
public class RankingReasonerTest {

	public static final int DEFAULT_TIMEOUT = 50000;

	@Test(timeout = DEFAULT_TIMEOUT)
	//Counterexample for QP (and DP) for the Tuples* reasoner
	public void DPandQPCounterexampleTest() throws Exception {
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
		
		TuplesRankingReasoner reasoner_tuples = new TuplesRankingReasoner();
		LatticeArgumentRanking ranking = reasoner_tuples.getModel(dt);
		assertFalse(ranking.isStrictlyMoreAcceptableThan(b, a));
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	//Counterexample for DDP for the Categorizer, Tuples, Discussion and SAF reasoner
	public void DDPCounterexampleTest() throws Exception {
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
		
		TuplesRankingReasoner reasoner_tuples = new TuplesRankingReasoner();
		SAFRankingReasoner reasoner_saf = new SAFRankingReasoner();
		BurdenBasedRankingReasoner reasoner_burden = new BurdenBasedRankingReasoner();
		DiscussionBasedRankingReasoner reasoner_dis = new DiscussionBasedRankingReasoner();
		CategorizerRankingReasoner reasoner_cat = new CategorizerRankingReasoner();
		MTRankingReasoner reasoner_mt = new MTRankingReasoner();
		LatticeArgumentRanking ranking = reasoner_tuples.getModel(dt);
		NumericalArgumentRanking ranking2 = reasoner_saf.getModel(dt);
		LatticeArgumentRanking ranking3 = reasoner_burden.getModel(dt);
		LatticeArgumentRanking ranking4 = reasoner_dis.getModel(dt);
		NumericalArgumentRanking ranking5 = reasoner_cat.getModel(dt);
//		NumericalArgumentRanking ranking6 = reasoner_mt.getModel(dt); //causes timeout
		assertFalse(ranking.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking2.isStrictlyMoreAcceptableThan(a, b));
		assertTrue(ranking3.isStrictlyMoreAcceptableThan(a, b)); //Bbs satisfies DDP
		assertFalse(ranking4.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking5.isStrictlyMoreAcceptableThan(a, b));
//		assertFalse(ranking6.isStrictlyMoreAcceptableThan(a, b));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	//Counterexample for AvsFD for the SAF reasoner
	public void AvsFDCounterexampleTest() throws Exception {
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
		
		SAFRankingReasoner reasoner_saf = new SAFRankingReasoner();
		NumericalArgumentRanking ranking = reasoner_saf.getModel(dt);
		assertFalse(ranking.isStrictlyMoreAcceptableThan(a, b));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	//Test that shows a difference between Bbs and Dbs
	public void DiscussionBurdenExample() throws Exception {
		BurdenBasedRankingReasoner reasoner_burden = new BurdenBasedRankingReasoner();
		DiscussionBasedRankingReasoner reasoner_dis = new DiscussionBasedRankingReasoner();
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument g = new Argument("g");
		Argument h = new Argument("h");
		Argument i = new Argument("i");
		Argument j = new Argument("j");
		Argument k = new Argument("k");
		Argument l = new Argument("l");
		dt.add(a);
		dt.add(b);
		dt.add(c);
		dt.add(d);
		dt.add(e);
		dt.add(g);
		dt.add(h);
		dt.add(i);
		dt.add(j);
		dt.add(k);
		dt.add(l);
		dt.add(new Attack(h,c));
		dt.add(new Attack(c,a));
		dt.add(new Attack(g,d));
		dt.add(new Attack(d,a));
		dt.add(new Attack(l,h));
		dt.add(new Attack(e,j));
		dt.add(new Attack(k,j));
		dt.add(new Attack(j,b));
		dt.add(new Attack(i,b));
	
		LatticeArgumentRanking ranking_burden = reasoner_burden.getModel(dt);
		LatticeArgumentRanking ranking_discussion = reasoner_dis.getModel(dt);
		assertTrue(ranking_burden.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking_discussion.isStrictlyMoreAcceptableThan(a,b));
	}
	
}
