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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.CounterTransitivityReasoner;
import org.tweetyproject.arg.rankings.semantics.LatticeArgumentRanking;

public class CounterTransitivityReasonerExample {

	public static void main(String[] args) {

		//Construct AAF
		DungTheory theory = new DungTheory();
		Argument a1 = new Argument("a");
		Argument a2 = new Argument("b");
		Argument a3 = new Argument("c");
		Argument a4 = new Argument("d");
		Argument a5 = new Argument("e");
		Argument a6 = new Argument("f");
		
		theory.add(a1);
		theory.add(a2);
		theory.add(a3);
		theory.add(a4);
		theory.add(a5);
		theory.add(a6);
		
		theory.add(new Attack(a1,a2));
		theory.add(new Attack(a2,a1));
		theory.add(new Attack(a1,a5));
		theory.add(new Attack(a2,a3));
		theory.add(new Attack(a5,a4));
		theory.add(new Attack(a4,a1));
		theory.add(new Attack(a3,a5));
		theory.add(new Attack(a6,a3));
		
		LatticeArgumentRanking lat = new LatticeArgumentRanking(theory);
		
		lat.setStrictlyLessOrEquallyAcceptableThan(a2, a5);
		lat.setStrictlyLessOrEquallyAcceptableThan(a5, a2);
		lat.setStrictlyLessOrEquallyAcceptableThan(a2, a6);
		lat.setStrictlyLessOrEquallyAcceptableThan(a6, a2);
		lat.setStrictlyLessOrEquallyAcceptableThan(a1, a3);
		lat.setStrictlyLessOrEquallyAcceptableThan(a3, a1);
		lat.setStrictlyLessOrEquallyAcceptableThan(a1, a4);
		lat.setStrictlyLessOrEquallyAcceptableThan(a4, a1);
		lat.setStrictlyLessOrEquallyAcceptableThan(a1, a2);
		
		CounterTransitivityReasoner r = new CounterTransitivityReasoner(CounterTransitivityReasoner.solver.quality, lat);
		
		
		System.out.println("cardinality: " +r.cardinality(theory).toString());
		System.out.println("quality: " +  r.quality(theory, lat).toString());
		System.out.println("quality first: " +  r.qualityFirst(theory, lat).toString());
		System.out.println("cardinality first: " +r.cardinalityFirst(theory, lat).toString());
		System.out.println("gfp: " +r.gfpCardinality(theory).toString());
		System.out.println("dominance: " +r.simpleDominance(theory, lat).toString());
		System.out.println("call by getModel (quality): " +r.getModel(theory).toString());
	}
}
