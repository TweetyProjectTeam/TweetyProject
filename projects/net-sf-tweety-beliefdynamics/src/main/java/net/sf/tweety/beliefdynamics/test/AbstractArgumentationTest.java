package net.sf.tweety.beliefdynamics.test;

import net.sf.tweety.BeliefBase;
import net.sf.tweety.arg.dung.CompleteReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.beliefdynamics.DefaultMultipleBaseExpansionOperator;
import net.sf.tweety.beliefdynamics.LeviMultipleBaseRevisionOperator;
import net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator;
import net.sf.tweety.beliefdynamics.kernels.KernelContractionOperator;
import net.sf.tweety.beliefdynamics.kernels.RandomIncisionFunction;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.Sat4jEntailment;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

public class AbstractArgumentationTest {
	public static void main(String[] args){
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");		
		theory.add(a);
		theory.add(b);
		theory.add(c);		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,c));
		theory.add(new Attack(c,b));
		theory.add(new Attack(c,a));
		
		CompleteReasoner reasoner = new CompleteReasoner(theory);
		
		System.out.println(reasoner.getExtensions());
		System.out.println();
				
		PlBeliefSet beliefSet = reasoner.getPropositionalCharacterisation(); 
		System.out.println(beliefSet);
		System.out.println();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds((PropositionalSignature)beliefSet.getSignature())){
			if(w.satisfies((BeliefBase)beliefSet))
				System.out.println(w);
		}
		
		MultipleBaseRevisionOperator<PropositionalFormula> revise = new LeviMultipleBaseRevisionOperator<PropositionalFormula>(
				new KernelContractionOperator<PropositionalFormula>(new RandomIncisionFunction<PropositionalFormula>(), new Sat4jEntailment()),
				new DefaultMultipleBaseExpansionOperator<PropositionalFormula>());
		
		PlBeliefSet beliefSet2 = new PlBeliefSet(revise.revise(beliefSet, new Proposition("in_a")));
		
		System.out.println(beliefSet2);
		System.out.println();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds((PropositionalSignature)beliefSet2.getSignature())){
			if(w.satisfies((BeliefBase)beliefSet2))
				System.out.println(w);
		}
	
				
	}
}
