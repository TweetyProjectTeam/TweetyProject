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

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.dung.util.DefaultDungTheoryGenerator;
import net.sf.tweety.arg.dung.util.DungTheoryGenerationParameters;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;
import net.sf.tweety.arg.dung.util.EnumeratingDungTheoryGenerator;
import net.sf.tweety.arg.rankings.postulates.RankingPostulate;
import net.sf.tweety.arg.rankings.reasoner.BurdenBasedRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.CategorizerRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.DiscussionBasedRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.MTRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.SAFRankingReasoner;
import net.sf.tweety.arg.rankings.reasoner.TuplesRankingReasoner;
import net.sf.tweety.commons.postulates.PostulateEvaluator;


/**
 * Example code for postulates for ranking semantics.
 * 
 * @author Anna Gessler
 */
public class RaPostulateExample {
	public static void CategorizerExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument,DungTheory> evaluator = new PostulateEvaluator<Argument,DungTheory>(dg, new CategorizerRankingReasoner());
		evaluator.addPostulate(RankingPostulate.ABSTRACTION);
		evaluator.addPostulate(RankingPostulate.INDEPENDENCE);
		evaluator.addPostulate(RankingPostulate.VOIDPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.SELFCONTRADICTION);
		evaluator.addPostulate(RankingPostulate.CARDINALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.TOTAL);
		evaluator.addPostulate(RankingPostulate.NONATTACKEDEQUIVALENCE);
		evaluator.addPostulate(RankingPostulate.QUALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DISTDEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.COUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.STRICTCOUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.ATTACKVSFULLDEFENSE);
		evaluator.addPostulate(RankingPostulate.STRICTADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFDEFENSEBRANCH);
		System.out.println(evaluator.evaluate(2000, false));
	}
	
	public static void BurdenExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument,DungTheory> evaluator = new PostulateEvaluator<Argument,DungTheory>(dg, new BurdenBasedRankingReasoner());
		evaluator.addPostulate(RankingPostulate.ABSTRACTION);
		evaluator.addPostulate(RankingPostulate.INDEPENDENCE);
		evaluator.addPostulate(RankingPostulate.VOIDPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.SELFCONTRADICTION);
		evaluator.addPostulate(RankingPostulate.CARDINALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.TOTAL);
		evaluator.addPostulate(RankingPostulate.NONATTACKEDEQUIVALENCE);
		evaluator.addPostulate(RankingPostulate.QUALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DISTDEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.COUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.STRICTCOUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.ATTACKVSFULLDEFENSE);
		evaluator.addPostulate(RankingPostulate.STRICTADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFDEFENSEBRANCH);
		System.out.println(evaluator.evaluate(2000, false));
	}
	
	public static void DiscussionExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument,DungTheory> evaluator = new PostulateEvaluator<Argument,DungTheory>(dg, new DiscussionBasedRankingReasoner());
		evaluator.addPostulate(RankingPostulate.ABSTRACTION);
		evaluator.addPostulate(RankingPostulate.INDEPENDENCE);
		evaluator.addPostulate(RankingPostulate.VOIDPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.SELFCONTRADICTION);
		evaluator.addPostulate(RankingPostulate.CARDINALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.TOTAL);
		evaluator.addPostulate(RankingPostulate.NONATTACKEDEQUIVALENCE);
		evaluator.addPostulate(RankingPostulate.QUALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DISTDEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.COUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.STRICTCOUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.ATTACKVSFULLDEFENSE);
		evaluator.addPostulate(RankingPostulate.STRICTADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFDEFENSEBRANCH);
		System.out.println(evaluator.evaluate(2000, false));
	}
	
	public static void TuplesExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument,DungTheory> evaluator = new PostulateEvaluator<Argument,DungTheory>(dg, new TuplesRankingReasoner());
		evaluator.addPostulate(RankingPostulate.ABSTRACTION);
		evaluator.addPostulate(RankingPostulate.INDEPENDENCE);
		evaluator.addPostulate(RankingPostulate.VOIDPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.SELFCONTRADICTION);
		evaluator.addPostulate(RankingPostulate.CARDINALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.TOTAL);
		evaluator.addPostulate(RankingPostulate.NONATTACKEDEQUIVALENCE);
		evaluator.addPostulate(RankingPostulate.QUALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DISTDEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.COUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.STRICTCOUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.ATTACKVSFULLDEFENSE);
		evaluator.addPostulate(RankingPostulate.STRICTADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFDEFENSEBRANCH);
		System.out.println(evaluator.evaluate(2000, false));
	}
	
	public static void MTExample() {
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument,DungTheory> evaluator = new PostulateEvaluator<Argument,DungTheory>(dg, new MTRankingReasoner());
		evaluator.addPostulate(RankingPostulate.ABSTRACTION);
		evaluator.addPostulate(RankingPostulate.INDEPENDENCE);
		evaluator.addPostulate(RankingPostulate.VOIDPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.SELFCONTRADICTION);
		evaluator.addPostulate(RankingPostulate.CARDINALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.TOTAL);
		evaluator.addPostulate(RankingPostulate.NONATTACKEDEQUIVALENCE);
		evaluator.addPostulate(RankingPostulate.QUALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DISTDEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.COUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.STRICTCOUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.ATTACKVSFULLDEFENSE);
		evaluator.addPostulate(RankingPostulate.STRICTADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFATTACKBRANCH);	
		evaluator.addPostulate(RankingPostulate.INCREASEOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFDEFENSEBRANCH);
		System.out.println(evaluator.evaluate(10, false));
	}
	
	public static void SAFExample() {
		DungTheoryGenerator dg = new DefaultDungTheoryGenerator(new DungTheoryGenerationParameters());
		PostulateEvaluator<Argument,DungTheory> evaluator = new PostulateEvaluator<Argument,DungTheory>(dg, new SAFRankingReasoner());
		evaluator.addPostulate(RankingPostulate.ABSTRACTION);
		evaluator.addPostulate(RankingPostulate.INDEPENDENCE);
		evaluator.addPostulate(RankingPostulate.VOIDPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.SELFCONTRADICTION);
		evaluator.addPostulate(RankingPostulate.CARDINALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.TOTAL);
		evaluator.addPostulate(RankingPostulate.NONATTACKEDEQUIVALENCE);
		evaluator.addPostulate(RankingPostulate.QUALITYPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.DISTDEFENSEPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.COUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.STRICTCOUNTERTRANSITIVITY);
		evaluator.addPostulate(RankingPostulate.ATTACKVSFULLDEFENSE);
		evaluator.addPostulate(RankingPostulate.STRICTADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFDEFENSEBRANCH);
		evaluator.addPostulate(RankingPostulate.ADDITIONOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFATTACKBRANCH);
		evaluator.addPostulate(RankingPostulate.INCREASEOFDEFENSEBRANCH);
		System.out.println(evaluator.evaluate(80, false));
	}
	
	
	public static void main(String[] args){
		CategorizerExample();
		BurdenExample();
		DiscussionExample();
		TuplesExample();
		MTExample();
		SAFExample();
	}
}
