package net.sf.tweety.arg.prob.test;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.prob.ProbabilisticRankingReasoner;
import net.sf.tweety.math.probability.Probability;

public class ProbabilisticRankingReasonerTest {
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
		ProbabilisticRankingReasoner reasoner = new ProbabilisticRankingReasoner(theory,Semantics.GROUNDED_SEMANTICS, Semantics.CREDULOUS_INFERENCE,new Probability(0.5),true);
		
		System.out.println(reasoner.getRanking());
		
	}
}
