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

import net.sf.tweety.arg.dung.postulates.RankingPostulate;
import net.sf.tweety.arg.dung.reasoner.CategorizerRankingReasoner;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;
import net.sf.tweety.arg.dung.util.EnumeratingDungTheoryGenerator;
import net.sf.tweety.commons.postulates.PostulateEvaluator;


/**
 * Example code for postulates for ranking semantics.
 * 
 * @author Anna Gessler
 */
public class RaPostulateExample {
	public static void main(String[] args){
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument,DungTheory> evaluator = new PostulateEvaluator<Argument,DungTheory>(dg, new CategorizerRankingReasoner());
		evaluator.addPostulate(RankingPostulate.INDEPENDENCE);
		evaluator.addPostulate(RankingPostulate.VOIDPRECEDENCE);
		evaluator.addPostulate(RankingPostulate.SELFCONTRADICTION);
		System.out.println(evaluator.evaluate(100, false));
	}
}
