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

import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.reasoner.ProbabilisticRankingReasoner;
import net.sf.tweety.math.probability.Probability;

/**
 * Example code for using graded semantics reasoner based on the ideas from [Thimm, Cerutti, Rienstra; 2018].
 * @author Matthias Thimm
 *
 */
public class ProbabilisticRankingReasonerExample {
	public static void main(String[] args){
		//Construct AAF
		DungTheory theory = new DungTheory();
		Argument a1 = new Argument("a1");
		Argument a2 = new Argument("a2");
		Argument a3 = new Argument("a3");
		Argument a4 = new Argument("a4");
		
		theory.add(a1);
		theory.add(a2);
		theory.add(a3);
		theory.add(a4);
		
		theory.add(new Attack(a1,a2));
		theory.add(new Attack(a2,a3));
		theory.add(new Attack(a3,a4));
		
		System.out.println(theory);
		
		// Compute probabilistic ranking wrt. grounded semantics, credoulous reasoning, and p=0.5
		ProbabilisticRankingReasoner reasoner = new ProbabilisticRankingReasoner(Semantics.GROUNDED_SEMANTICS,new Probability(0.5),true);
		
		System.out.println(reasoner.getModel(theory));		
	}
}
