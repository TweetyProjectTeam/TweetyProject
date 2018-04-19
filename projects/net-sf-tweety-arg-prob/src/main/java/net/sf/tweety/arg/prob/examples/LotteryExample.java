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

import java.util.Collection;

import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.GroundReasoner;
import net.sf.tweety.arg.dung.divisions.Division;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.prob.lotteries.ArgumentationLottery;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;
import net.sf.tweety.arg.prob.lotteries.UtilityFunction;


/**
 * Example code for working with lotteries in probabilistic abstract argumentation.
 * 
 * @author Matthias Thimm
 *
 */
public class LotteryExample {

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
		
		// Instantiate reasoner
		AbstractExtensionReasoner r = new GroundReasoner(theory);
		Collection<Extension> exts = r.getExtensions();
		
		// print theory
		System.out.println("AAF: " + theory);
		
		// print extensions
		System.out.println();
		System.out.println("Extensions: ");
		for(Extension e: exts)
			System.out.println(e);
		
		// print divisions
		System.out.println();
		System.out.println("Divisions: ");
		for(Division div: Division.getDivisions(exts, theory)){
			System.out.println(div);		
		}
		
		// print the uniform probability function
		SubgraphProbabilityFunction prob = new SubgraphProbabilityFunction(theory);
		System.out.println();
		System.out.println("Uniform probability function over all sub graphs: ");
		for(DungTheory key: prob.keySet())
			System.out.println("\t" + key + "\t=\t" + prob.probability(key));		
		System.out.println();
		System.out.println("Probabilities of some divisions: ");
		//---
		Extension a1 = new Extension(); Extension a2 = new Extension();
		a1.add(a); a2.add(b);
		Division d1 = new Division(a1,a2);
		System.out.println("\t" + d1 + "\t=\t" + prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS));
		//---
		a1 = new Extension(); a2 = new Extension();
		a1.add(a);
		d1 = new Division(a1,a2);
		System.out.println("\t" + d1 + "\t=\t" + prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS));
		//---
		a1 = new Extension(); a2 = new Extension();
		a1.add(a); a1.add(b);
		d1 = new Division(a1,a2);
		System.out.println("\t" + d1 + "\t=\t" + prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS));
		//---
		a1 = new Extension(); a2 = new Extension();
		a1.add(a); a1.add(c); a2.add(b); a2.add(a);
		d1 = new Division(a1,a2);
		System.out.println("\t" + d1 + "\t=\t" + prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS));
		//---
		a1 = new Extension(); a2 = new Extension();
		a1.add(a); a1.add(c); a2.add(b);
		d1 = new Division(a1,a2);
		System.out.println("\t" + d1 + "\t=\t" + prob.getAcceptanceProbability(d1, Semantics.GROUNDED_SEMANTICS));
		
		System.out.println();
		System.out.println("Probabilities of arguments: ");
		//---
		System.out.println("\t" + a + "\t=\t" + prob.getAcceptanceProbability(a, Semantics.GROUNDED_SEMANTICS));
		System.out.println("\t" + b + "\t=\t" + prob.getAcceptanceProbability(a, Semantics.GROUNDED_SEMANTICS));
		System.out.println("\t" + c + "\t=\t" + prob.getAcceptanceProbability(a, Semantics.GROUNDED_SEMANTICS));
	
		// lotteries
		Collection<Division> stDivisions = Division.getStandardDivisions(theory);
		ArgumentationLottery lottery = new ArgumentationLottery(stDivisions,prob, Semantics.GROUNDED_SEMANTICS);
		System.out.println();
		System.out.println("A lottery with standard set of divisions (see Proposition 8): ");
		System.out.println("\t" + lottery);
		
		UtilityFunction util = new UtilityFunction();
		util.put((Division)stDivisions.toArray()[2], 12d);
		util.put((Division)stDivisions.toArray()[4], 7d);
		util.put((Division)stDivisions.toArray()[6], -2d);
		System.out.println();
		System.out.println("Some utility function: ");
		System.out.println("\t" + util);
		
		System.out.println();
		System.out.println("The expected utility on the lottery: " + util.getExpectedUtility(lottery));
	}
}
