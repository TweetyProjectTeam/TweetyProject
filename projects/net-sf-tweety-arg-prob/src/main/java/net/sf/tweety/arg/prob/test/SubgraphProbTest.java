package net.sf.tweety.arg.prob.test;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;

public class SubgraphProbTest {

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
