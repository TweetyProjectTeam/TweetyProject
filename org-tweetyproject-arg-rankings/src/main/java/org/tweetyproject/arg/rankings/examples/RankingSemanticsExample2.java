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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.CountingRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.PropagationRankingReasoner;
import org.tweetyproject.arg.rankings.util.RankingTools;

/**
 * Example code for even more ranking semantics:
 * <br> - Counting Semantics [Pu, Zhang, G.Luo, J.Luo. Attacker and Defender Counting Approach 
 * for Abstract Argumentation. CoRR 2015].
 * <br> - The three variations of the Propagation Semantics 
 * [Delobelle. Ranking-based Semantics for Abstract Argumentation. Thesis, 2017])
 * 
 * @author Anna Gessler
 */
public class RankingSemanticsExample2 {
	public static void main(String[] args) {	
		// Example 1, taken from [Bonzon, Delobelle, Konieczny, Maudet. A Comparative
		// Study of Ranking-Based Semantics for Abstract Argumentation. AAAI 2016]
		DungTheory example1 = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		example1.add(a,b,c,d,e);
		example1.add(new Attack(a, e));
		example1.add(new Attack(d, a));
		example1.add(new Attack(e, d));
		example1.add(new Attack(c, e));
		example1.add(new Attack(b, c), new Attack(b, a));
		
		// Example 2, taken from Figure 1.a in [Pu, Zhang, G.Luo, J.Luo.
		// Attacker and Defender Counting Approach for Abstract Argumentation. CoRR 2015]
		DungTheory example2 = new DungTheory();
		Argument x1 = new Argument("x1");
		Argument x2 = new Argument("x2");
		Argument x3 = new Argument("x3");
		Argument x4 = new Argument("x4");
		example2.add(x1, x2, x3, x4);
		example2.add(new Attack(x2, x3), new Attack(x2, x1));
		example2.add(new Attack(x3, x2), new Attack(x3, x3));
		example2.add(new Attack(x4, x2));
		
		// Example 3, taken from Figure 2.4 in 
		// [Delobelle, Jerome. Ranking-based Semantics for Abstract Argumentation. 2017]
		DungTheory example3 = new DungTheory();
		Argument f = new Argument("f");
		Argument g = new Argument("g");
		Argument h = new Argument("h");
		Argument i = new Argument("i");
		Argument j = new Argument("j");
		example3.add(a,b,c,d,e,f,g,h,i,j);
		example3.add(new Attack(a,b));
		example3.add(new Attack(b,c), new Attack(b,f));
		example3.add(new Attack(d,g), new Attack(d,f));
		example3.add(new Attack(e,h), new Attack(e,d), new Attack(e,i));
		example3.add(new Attack(h,g));
		example3.add(new Attack(j,i));
		
		// Counting semantics
		CountingRankingReasoner reasoner = new CountingRankingReasoner(0.98, 0.001);
		System.out.println(reasoner.getClass().getSimpleName());
		System.out.println(RankingTools.roundRanking(reasoner.getModel(example1), 2));
		System.out.println(RankingTools.roundRanking(reasoner.getModel(example2), 2));
		reasoner = new CountingRankingReasoner(0.9, 0.001);
		System.out.println(RankingTools.roundRanking(reasoner.getModel(example3), 3));
		
		// Propagation semantics (examples from chapter 3.3 in 
		// [Delobelle. Ranking-based Semantics for Abstract Argumentation. Thesis, 2017])
		System.out.println("Propagation semantics epsilon:");
		PropagationRankingReasoner propagation_reasoner_1 = new PropagationRankingReasoner(0.75, false, PropagationRankingReasoner.PropagationSemantics.PROPAGATION1);
		System.out.println("S,0.75:" + propagation_reasoner_1.getModel(example3));
		propagation_reasoner_1 = new PropagationRankingReasoner(0.3, false, PropagationRankingReasoner.PropagationSemantics.PROPAGATION1);
		System.out.println("S,0.3:" + propagation_reasoner_1.getModel(example3));
		propagation_reasoner_1 = new PropagationRankingReasoner(0.75, true, PropagationRankingReasoner.PropagationSemantics.PROPAGATION1);
		// Example that uses the multiset variant
		System.out.println("M:" + propagation_reasoner_1.getModel(example3));
		
		System.out.println("Propagation semantics 1+epsilon:");
		PropagationRankingReasoner propagation_reasoner_2 = new PropagationRankingReasoner(0.75, false, PropagationRankingReasoner.PropagationSemantics.PROPAGATION2);
		System.out.println("S:" + propagation_reasoner_2.getModel(example3));
		propagation_reasoner_2 = new PropagationRankingReasoner(0.75, true, PropagationRankingReasoner.PropagationSemantics.PROPAGATION2);
		System.out.println("M:" + propagation_reasoner_2.getModel(example3));
		
		System.out.println("Propagation semantics 1->epsilon:");
		propagation_reasoner_2 = new PropagationRankingReasoner(0.75, false, PropagationRankingReasoner.PropagationSemantics.PROPAGATION3);
		System.out.println("S:" + propagation_reasoner_2.getModel(example3));
		propagation_reasoner_2 = new PropagationRankingReasoner(0.75, true, PropagationRankingReasoner.PropagationSemantics.PROPAGATION3);
		System.out.println("M:" + propagation_reasoner_2.getModel(example3));
	}

}
