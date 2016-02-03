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
package net.sf.tweety.beliefdynamics.test;

import net.sf.tweety.arg.dung.CompleteReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.beliefdynamics.DefaultMultipleBaseExpansionOperator;
import net.sf.tweety.beliefdynamics.LeviMultipleBaseRevisionOperator;
import net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator;
import net.sf.tweety.beliefdynamics.kernels.KernelContractionOperator;
import net.sf.tweety.beliefdynamics.kernels.RandomIncisionFunction;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.logics.pl.ClassicalEntailment;
import net.sf.tweety.logics.pl.PlBeliefSet;
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
				new KernelContractionOperator<PropositionalFormula>(new RandomIncisionFunction<PropositionalFormula>(), new ClassicalEntailment()),
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
