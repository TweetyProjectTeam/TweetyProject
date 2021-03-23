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

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.rankings.postulates.RankingPostulate;
import org.tweetyproject.arg.rankings.reasoner.BurdenBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.CategorizerRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.CountingRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.PropagationRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.SAFRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.StrategyBasedRankingReasoner;
import org.tweetyproject.arg.rankings.reasoner.TuplesRankingReasoner;
import org.tweetyproject.commons.postulates.PostulateEvaluator;

/**
 * Example code for evaluating ranking semantics in regard
 * to postulates. Each postulate represents a single property
 * that characterizes how the semantics ranks arguments.
 * 
 * @author Anna Gessler
 */
public class RankingPostulatesExample {
	private static Collection<RankingPostulate> all_postulates;

	public static void main(String[] args) {
		all_postulates = new HashSet<RankingPostulate>();
		all_postulates.add(RankingPostulate.ABSTRACTION);
		all_postulates.add(RankingPostulate.ADDITIONOFATTACKBRANCH);
		all_postulates.add(RankingPostulate.ADDITIONOFDEFENSEBRANCH);
		all_postulates.add(RankingPostulate.ATTACKVSFULLDEFENSE);
		all_postulates.add(RankingPostulate.CARDINALITYPRECEDENCE);
		all_postulates.add(RankingPostulate.COUNTERTRANSITIVITY);
		all_postulates.add(RankingPostulate.DEFENSEPRECEDENCE);
		all_postulates.add(RankingPostulate.DISTDEFENSEPRECEDENCE);
		all_postulates.add(RankingPostulate.INCREASEOFATTACKBRANCH);
		all_postulates.add(RankingPostulate.INCREASEOFDEFENSEBRANCH);
		all_postulates.add(RankingPostulate.INDEPENDENCE);
		all_postulates.add(RankingPostulate.NONATTACKEDEQUIVALENCE);
		all_postulates.add(RankingPostulate.QUALITYPRECEDENCE);
		all_postulates.add(RankingPostulate.SELFCONTRADICTION);
		all_postulates.add(RankingPostulate.STRICTADDITIONOFDEFENSEBRANCH);
		all_postulates.add(RankingPostulate.STRICTCOUNTERTRANSITIVITY);
		all_postulates.add(RankingPostulate.TOTAL);
		all_postulates.add(RankingPostulate.VOIDPRECEDENCE);

		CategorizerExample();
		BurdenExample();
		DiscussionExample();
		TuplesExample();
		StrategyBasedExample();
		SAFExample();
		CountingExample();
		PropagationExample();
	}

	public static void CategorizerExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new CategorizerRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, false).prettyPrint());
	}

	public static void BurdenExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new BurdenBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(100, false).prettyPrint());
	}

	public static void DiscussionExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new DiscussionBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(2000, false).prettyPrint());
	}

	public static void TuplesExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new TuplesRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(4000, false).prettyPrint());
	}

	public static void StrategyBasedExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new StrategyBasedRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(10, false).prettyPrint());
	}

	public static void SAFExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new SAFRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(2000, false).prettyPrint());
	}

	public static void CountingExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				new CountingRankingReasoner());
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(2000, false).prettyPrint());
	}
	
	public static void PropagationExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PropagationRankingReasoner propagation_reasoner = new PropagationRankingReasoner(0.75, false, PropagationRankingReasoner.PropagationSemantics.PROPAGATION1);
		PostulateEvaluator<Argument, DungTheory> evaluator = new PostulateEvaluator<Argument, DungTheory>(dg,
				propagation_reasoner);
		evaluator.addAllPostulates(all_postulates);
		System.out.println(evaluator.evaluate(2000, false).prettyPrint());
	}

}
