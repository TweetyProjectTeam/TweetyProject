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
package net.sf.tweety.arg.rankings.examples;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.tweety.arg.dung.semantics.ArgumentRanking;
import net.sf.tweety.arg.dung.semantics.NumericalArgumentRanking;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.reasoner.BurdenBasedRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.CategorizerRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.GrossiModgilRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.MTRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.ProbabilisticRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.SAFRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.TuplesRankingReasoner;
import net.sf.tweety.math.probability.Probability;

/**
 * Example code for ranking semantics.
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
		example1.add(a);
		example1.add(b);
		example1.add(c);
		example1.add(d);
		example1.add(e);
		example1.add(new Attack(a, e));
		example1.add(new Attack(d, a));
		example1.add(new Attack(e, d));
		example1.add(new Attack(c, e));
		example1.add(new Attack(b, c));
		example1.add(new Attack(b, a));

		// Example 2, taken from
		// [Baumeister, Neugebauer, Rothe. Argumentation Meets Computational Social
		// Choice. Tutorial. 2018]
		DungTheory example2 = new DungTheory();
		Argument f = new Argument("f");
		example2.add(a);
		example2.add(b);
		example2.add(c);
		example2.add(d);
		example2.add(e);
		example2.add(f);
		example2.add(new Attack(a, b));
		example2.add(new Attack(b, c));
		example2.add(new Attack(d, e));
		example2.add(new Attack(e, d));
		example2.add(new Attack(c, f));
		example2.add(new Attack(e, c));

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
		example3.add(a1);
		example3.add(b1);
		example3.add(b2);
		example3.add(b3);
		example3.add(b4);
		example3.add(c1);
		example3.add(c2);
		example3.add(c3);
		example3.add(c4);
		example3.add(d1);
		example3.add(d2);
		example3.add(d3);
		example3.add(e1);
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
		example4a.add(a);
		example4a.add(b);
		example4a.add(c);
		example4a.add(d);
		example4a.add(e);
		example4a.add(f);
		example4a.add(g);
		example4a.add(new Attack(b, a));	
		example4a.add(new Attack(c, a));	
		example4a.add(new Attack(d, a));	
		example4a.add(new Attack(f, a));	
		example4a.add(new Attack(e, d));	
		example4a.add(new Attack(g, f));	
		
		// Example 4b, taken from Figure 4 in
		// [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
		DungTheory example4b = new DungTheory();
		example4b.add(a);
		example4b.add(b);
		example4b.add(c);
		example4b.add(d);
		example4b.add(e);
		example4b.add(f);
		example4b.add(new Attack(e, f));	
		example4b.add(new Attack(f, e));		
		example4b.add(new Attack(d, e));
		example4b.add(new Attack(e, b));	
		example4b.add(new Attack(a, b));	
		example4b.add(new Attack(c, b));	
		
		// Example 4c, taken from Figure 4 in
		// [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
		DungTheory example4c = new DungTheory();
		example4c.add(a);
		example4c.add(b);
		example4c.add(c);
		example4c.add(d);
		example4c.add(e);
		example4c.add(f);
		example4c.add(new Attack(a, b));
		example4c.add(new Attack(a, e));
		example4c.add(new Attack(c, b));
		example4c.add(new Attack(d, e));
		example4c.add(new Attack(e, f));
		example4c.add(new Attack(e, e));
		example4c.add(new Attack(e, b));
		example4c.add(new Attack(f, e));
		example4c.add(new Attack(f, b));
		
		// Categorizer ranking semantics
		CategorizerRankingReasoner reasoner = new CategorizerRankingReasoner();
		System.out.println(reasoner.getClass().getSimpleName());
		System.out.println(roundRanking(reasoner.getModel(example1), 2));
		System.out.println(roundRanking(reasoner.getModel(example2), 3));

		// Burden-based ranking semantics
		BurdenBasedRankingReasoner reasoner2 = new BurdenBasedRankingReasoner();
		System.out.println(reasoner2.getClass().getSimpleName());
		System.out.println(roundRanking(reasoner2.getModel(example1), 3));
		System.out.println(roundRanking(reasoner2.getModel(example2), 3));

		// Discussion-based ranking semantics
		DiscussionBasedRankingReasoner reasoner3 = new DiscussionBasedRankingReasoner();
		System.out.println(reasoner3.getClass().getSimpleName());
		System.out.println(reasoner3.getModel(example1));
		System.out.println(reasoner3.getModel(example2));

		// Tuples* ranking semantics
		TuplesRankingReasoner reasoner4 = new TuplesRankingReasoner();
		System.out.println(reasoner4.getClass().getSimpleName());
		System.out.println(reasoner4.getModel(example3));

		// Matt & Toni ranking semantics
		MTRankingReasoner reasoner5 = new MTRankingReasoner();
		System.out.println(reasoner5.getClass().getSimpleName());
		System.out.println(roundRanking(reasoner5.getModel(example1),2));
		System.out.println(roundRanking(reasoner5.getModel(example4a),3));
		System.out.println(roundRanking(reasoner5.getModel(example4b),3));
		System.out.println(roundRanking(reasoner5.getModel(example4c),3));

		// Social Abstract Argumentation framework with simple product semantics
		SAFRankingReasoner reasoner6 = new SAFRankingReasoner();
		System.out.println(reasoner6.getClass().getSimpleName());
		System.out.println(roundRanking(reasoner6.getModel(example1), 2));

		// Grossi & Modgil ranking semantics
		GrossiModgilRankingReasoner reasoner7 = new GrossiModgilRankingReasoner();
		System.out.println(reasoner7.getClass().getSimpleName());
		System.out.println(reasoner7.getModel(example1));

		// Probabilistic graded ranking semantic [Thimm, Cerutti, Rienstra; 2018]
		ProbabilisticRankingReasoner reasoner8 = new ProbabilisticRankingReasoner(Semantics.GROUNDED_SEMANTICS,	new Probability(0.5), true);
		System.out.println(reasoner8.getClass().getSimpleName());
		System.out.println(reasoner8.getModel(example2));
		
	}

	/**
	 * Rounds a double value to n decimals.
	 * 
	 * @param value a double value
	 * @param n     number of decimals
	 * @return value rounded to n decimals
	 */
	private static double round(double value, int n) {
		if (n < 0)
			throw new IllegalArgumentException();
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(n, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * Rounds values in the given numerical argument ranking to n decimals.
	 * 
	 * @param ranking a NumericalArgumentRanking
	 * @param n       decimals
	 * @return rounded NumericalArgumentRanking
	 */
	private static ArgumentRanking roundRanking(NumericalArgumentRanking ranking, int n) {
		Iterator<Entry<Argument, Double>> it = ranking.entrySet().iterator();
		NumericalArgumentRanking reval = new NumericalArgumentRanking();
		while (it.hasNext()) {
			Map.Entry<Argument, Double> pair = (Map.Entry<Argument, Double>) it.next();
			Argument a = pair.getKey();
			reval.put(a, round(pair.getValue(), n));
			it.remove();
		}
		return reval;
	}
}
