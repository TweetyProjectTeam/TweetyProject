/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.prob.examples;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;

/**
 * Example code for showing how to work with subgraph probability distributions and updates.
 * @author Matthias Thimm
 *
 */
public class SubgraphProbExample {
	public static void main(String[] args){
		// create some Dung theory
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		theory.add(a);
		theory.add(b);
		theory.add(c);		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(c,b));
		
		System.out.println(theory);
		System.out.println();
		
		SubgraphProbabilityFunction prob = new SubgraphProbabilityFunction(theory);
		
		for(DungTheory key: prob.keySet())
			System.out.println(key + "\t" + prob.probability(key));
		System.out.println();
		
		DungTheory upd = new DungTheory();
		upd.add(a);
		
		prob = prob.roughUpdate(upd);
		
		for(DungTheory key: prob.keySet())
			System.out.println(key + "\t" + prob.probability(key));
		System.out.println(prob.isNormalized());
	}
}
