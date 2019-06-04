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
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;
import net.sf.tweety.arg.dung.util.EnumeratingDungTheoryGenerator;
import net.sf.tweety.commons.postulates.PostulateEvaluator;


/**
 * Example code for ranking semantics and postulates.
 * 
 * @author Anna Gessler
 */
public class RaPostulateExample {
	public static void main(String[] args){
		//Categorizer ranking semantics Example
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
	
		theory.add(new Attack(a,e));
		theory.add(new Attack(d,a));
		theory.add(new Attack(e,d));
		theory.add(new Attack(c,e));
		theory.add(new Attack(b,c));
		theory.add(new Attack(b,a));
		
		CategorizerRankingReasoner reasoner = new CategorizerRankingReasoner();
		System.out.println(reasoner.getModels(theory));
		
		//Ranking postulate example (in progress)
		DungTheoryGenerator dg = new EnumeratingDungTheoryGenerator();
		PostulateEvaluator<Argument,DungTheory> evaluator = new PostulateEvaluator<Argument,DungTheory>(dg, new CategorizerRankingReasoner());
		evaluator.addPostulate(RankingPostulate.INDEPENDENCE);
		evaluator.addPostulate(RankingPostulate.VOIDPRECEDENCE);
		System.out.println(evaluator.evaluate(100, false));
	}
}
