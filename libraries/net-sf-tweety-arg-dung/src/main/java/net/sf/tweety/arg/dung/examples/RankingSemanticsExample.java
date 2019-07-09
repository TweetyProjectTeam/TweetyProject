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
package net.sf.tweety.arg.dung.examples;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.tweety.arg.dung.reasoner.BurdenBasedRankingReasoner;
import net.sf.tweety.arg.dung.reasoner.CategorizerRankingReasoner;
import net.sf.tweety.arg.dung.reasoner.DiscussionBasedRankingReasoner;
import net.sf.tweety.arg.dung.reasoner.GrossiModgilRankingReasoner;
import net.sf.tweety.arg.dung.reasoner.TuplesRankingReasoner;
import net.sf.tweety.arg.dung.semantics.ArgumentRanking;
import net.sf.tweety.arg.dung.semantics.NumericalArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 * Example code for ranking semantics.
 * 
 * @author Anna Gessler
 */
public class RankingSemanticsExample {
	public static void main(String[] args) {
		// Categorizer ranking semantic
		CategorizerRankingReasoner reasoner = new CategorizerRankingReasoner();
		System.out.println(reasoner);

		// Example 1, taken from [Bonzon, Delobelle, Konieczny, Maudet. A Comparative
		// Study
		// of Ranking-Based Semantics for Abstract Argumentation]
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

		// Discussion-based ranking semantic
		DiscussionBasedRankingReasoner reasoner3 = new DiscussionBasedRankingReasoner();
		System.out.println(reasoner3);
		System.out.println(roundRanking(reasoner3.getModel(example1), 3));
		System.out.println(roundRanking(reasoner3.getModel(example2), 3));

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

		// Categorizer ranking semantic
		System.out.println(roundRanking(reasoner.getModel(example1), 2));
		System.out.println(roundRanking(reasoner.getModel(example2), 3));

		// Burden-based ranking semantic
		BurdenBasedRankingReasoner reasoner2 = new BurdenBasedRankingReasoner();
		System.out.println(reasoner2);
		System.out.println(roundRanking(reasoner2.getModel(example1), 3));
		System.out.println(roundRanking(reasoner2.getModel(example2), 3));

		// Tuples* ranking semantic
		TuplesRankingReasoner reasoner4 = new TuplesRankingReasoner();
		System.out.println(reasoner4);
		System.out.println(reasoner4.getModel(example3));

		// Matt & Toni ranking semantic
//		MTRankingReasoner reasoner5 = new MTRankingReasoner();
//		System.out.println(reasoner5);
//		System.out.println(reasoner5.getModel(theory1));

		// Social Abstract Argumentation framework with simple product semantic
//		SocialAbstractArgumentationFramework saf_example1 = new SocialAbstractArgumentationFramework();
//		saf_example1.add(example1);
//		saf_example1.voteUp(a,1);
//		saf_example1.voteUp(b,1);
//		saf_example1.voteUp(c,1);
//		saf_example1.voteUp(d,1);
//		saf_example1.voteUp(e,1);
//		IssReasoner reasoner = new IssReasoner(new SimpleProductSemantics(0.1),0.001);
//		System.out.println(roundRanking(reasoner.getModel(saf_example1), 2));
		
		// Grossi & Modgil ranking semantic
		GrossiModgilRankingReasoner reasoner7 = new GrossiModgilRankingReasoner();
		System.out.println(reasoner7);
		System.out.println(reasoner7.getModel(example1));
		
		// Probabilistic graded ranking semantic [Thimm, Cerutti, Rienstra; 2018]
//		ProbabilisticRankingReasoner reasoner8 = new ProbabilisticRankingReasoner();
	}

	/**
	 * Rounds a double value to n decimals.
	 * 
	 * @param value
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
