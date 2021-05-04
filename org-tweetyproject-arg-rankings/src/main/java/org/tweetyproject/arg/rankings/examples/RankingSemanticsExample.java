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

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.BurdenBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.IteratedGradedDefenseReasoner;
import org.tweetyproject.arg.rankings.reasoner.ProbabilisticRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.SAFRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.StrategyBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.TuplesRankingReasoner;
import org.tweetyproject.arg.rankings.util.RankingTools;
import org.tweetyproject.math.probability.Probability;

/**
 * Example code for the following ranking semantics:
 * <br> - Categorizer [Besnard, Hunter. A logic-based theory of deductive arguments. 2001]
 * <br> - Burden-Based [Amgoud, Ben-Naim. Ranking-based semantics for argumentation
 * frameworks. 2013]
 * <br> - Discussion-Based [Amgoud, Ben-Naim. Ranking-based semantics for argumentation
 * frameworks. 2013]
 * <br> - Tuples [Cayrol, Lagasquie-Schiex. Graduality in argumentation. 2005]
 * <br> - Strategy-Based [Matt, Toni. A
 * game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
 * <br> - Social Abstract Argumentation with simple product semantics [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of Ranking-Based 
 * Semantics for Abstract Argumentation. AAAI 2016]
 * <br> - Iterated Graded Defense [Grossi, Modgil. On the Graded Acceptability of Arguments. IJCAI 2015]
 * <br> - Probabilistic Graded Semantics [Thimm, Cerutti, Rienstra. Probabilistic Graded Semantics. COMMA 2018].
 * 
 * @author Anna Gessler
 */
public class RankingSemanticsExample {
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

		// Example 2, taken from
		// [Baumeister, Neugebauer, Rothe. Argumentation Meets Computational Social
		// Choice. Tutorial. 2018]
		DungTheory example2 = new DungTheory();
		Argument f = new Argument("f");
		example2.add(a,b,c,d,e,f);
		example2.add(new Attack(a, b));
		example2.add(new Attack(b, c));
		example2.add(new Attack(d, e));
		example2.add(new Attack(c, f));
		example2.add(new Attack(e, d),new Attack(e, c));

		// Example 3, taken from
		// [Cayrol, Lagasquie-Schiex. Graduality in argumentation. 2005]
		DungTheory example3 = new DungTheory();
		Argument a1 = new Argument("A");
		Argument b1 = new Argument("B1");
		Argument b2 = new Argument("B2");
		Argument b3 = new Argument("B3");
		Argument b4 = new Argument("B4");
		Argument c1 = new Argument("C1");
		Argument c2 = new Argument("C2");
		Argument c3 = new Argument("C3");
		Argument c4 = new Argument("C4");
		Argument d1 = new Argument("D1");
		Argument d2 = new Argument("D2");
		Argument d3 = new Argument("D3");
		Argument e1 = new Argument("E1");
		example3.add(a1,b1,b2,b3,b4);
		example3.add(c1,c2,c3,c4);
		example3.add(d1,d2,d3,e1);
		example3.add(new Attack(b1, a1));
		example3.add(new Attack(b2, a1));
		example3.add(new Attack(b3, a1));
		example3.add(new Attack(b4, a1));
		example3.add(new Attack(c1, b1));
		example3.add(new Attack(c2, b1));
		example3.add(new Attack(c3, b2));
		example3.add(new Attack(c4, b3));
		example3.add(new Attack(d1, c1));
		example3.add(new Attack(d2, c2));
		example3.add(new Attack(d3, c3));
		example3.add(new Attack(e1, d1));
		
		// Example 4a, taken from Figure 2 in
		// [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
		DungTheory example4a = new DungTheory();
		Argument g = new Argument("g");
		example4a.add(a,b,c,d,e,f,g);
		example4a.add(new Attack(b, a));	
		example4a.add(new Attack(c, a));	
		example4a.add(new Attack(d, a));	
		example4a.add(new Attack(f, a));	
		example4a.add(new Attack(e, d));	
		example4a.add(new Attack(g, f));	
		
		// Example 4b, taken from Figure 4 in
		// [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
		DungTheory example4b = new DungTheory();
		example4b.add(a,b,c,d,e,f);
		example4b.add(new Attack(a, b));	
		example4b.add(new Attack(c, b));	
		example4b.add(new Attack(d, e));
		example4b.add(new Attack(e, f),new Attack(e, b));	
		example4b.add(new Attack(f, e));		
		
		// Example 4c, taken from Figure 4 in
		// [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
		DungTheory example4c = new DungTheory();
		example4c.add(a,b,c,d,e,f);
		example4c.add(new Attack(a, b), new Attack(a, e));
		example4c.add(new Attack(c, b));
		example4c.add(new Attack(d, e));
		example4c.add(new Attack(e, f), new Attack(e, e), new Attack(e, b));
		example4c.add(new Attack(f, e), new Attack(f, b));
		
		// Example 5, taken from taken from Figure 2.4 in 
		// [Delobelle, Jerome. Ranking-based Semantics for Abstract Argumentation. 2017]
		DungTheory example5 = new DungTheory();
		Argument h = new Argument("h");
		Argument i = new Argument("i");
		Argument j = new Argument("j");
		example5.add(a,b,c,d,e,f,g,h,i,j);
		example5.add(new Attack(a,b));
		example5.add(new Attack(b,c), new Attack(b,f));
		example5.add(new Attack(d,g), new Attack(d,f));
		example5.add(new Attack(e,h), new Attack(e,d), new Attack(e,i));
		example5.add(new Attack(h,g));
		example5.add(new Attack(j,i));
		
		// Categorizer ranking semantics
		CategorizerRankingReasoner reasoner = new CategorizerRankingReasoner();
		System.out.println(reasoner.getClass().getSimpleName());
		System.out.println(RankingTools.roundRanking(reasoner.getModel(example1), 2));
		System.out.println(RankingTools.roundRanking(reasoner.getModel(example2), 3));
		
		// Burden-based ranking semantics
		BurdenBasedRankingReasoner reasoner2 = new BurdenBasedRankingReasoner();
		System.out.println(reasoner2.getClass().getSimpleName());
		System.out.println(reasoner2.getModel(example1));
		System.out.println(reasoner2.getModel(example2));
		System.out.println(reasoner2.getModel(example5));

		// Discussion-based ranking semantics
		DiscussionBasedRankingReasoner reasoner3 = new DiscussionBasedRankingReasoner();
		System.out.println(reasoner3.getClass().getSimpleName());
		System.out.println(reasoner3.getModel(example1));
		System.out.println(reasoner3.getModel(example2));

		// Tuples* ranking semantics
		TuplesRankingReasoner reasoner4 = new TuplesRankingReasoner();
		System.out.println(reasoner4.getClass().getSimpleName());
		System.out.println(reasoner4.getModel(example5));
		System.out.println(reasoner4.prettyPrintTupledValues());
		System.out.println(reasoner4.getModel(example3));
		System.out.println(reasoner4.prettyPrintTupledValues());
		
		// Strategy based ranking semantics (Matt & Toni)
		StrategyBasedRankingReasoner reasoner5 = new StrategyBasedRankingReasoner();
		System.out.println(reasoner5.getClass().getSimpleName());
		System.out.println(RankingTools.roundRanking(reasoner5.getModel(example1),2));
		System.out.println(RankingTools.roundRanking(reasoner5.getModel(example4a),3));
		System.out.println(RankingTools.roundRanking(reasoner5.getModel(example4b),3));
		System.out.println(RankingTools.roundRanking(reasoner5.getModel(example4c),3));

		// Social Abstract Argumentation framework with simple product semantics
		SAFRankingReasoner reasoner6 = new SAFRankingReasoner();
		System.out.println(reasoner6.getClass().getSimpleName());
		System.out.println(RankingTools.roundRanking(reasoner6.getModel(example1), 2));

		// Iterated graded defense (Grossi & Modgil) ranking semantics
		IteratedGradedDefenseReasoner reasoner7 = new IteratedGradedDefenseReasoner();
		System.out.println(reasoner7.getClass().getSimpleName());
		System.out.println(reasoner7.getModel(example1));

		// Probabilistic graded ranking semantic [Thimm, Cerutti, Rienstra; 2018]
		ProbabilisticRankingReasoner reasoner8 = new ProbabilisticRankingReasoner(Semantics.GROUNDED_SEMANTICS,	new Probability(0.5), true);
		System.out.println(reasoner8.getClass().getSimpleName());
		System.out.println(reasoner8.getModel(example2));
	}


}
