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
package net.sf.tweety.beliefdynamics.examples;

import net.sf.tweety.arg.dung.reasoner.SatCompleteReasoner;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.beliefdynamics.DefaultMultipleBaseExpansionOperator;
import net.sf.tweety.beliefdynamics.LeviMultipleBaseRevisionOperator;
import net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator;
import net.sf.tweety.beliefdynamics.kernels.KernelContractionOperator;
import net.sf.tweety.beliefdynamics.kernels.RandomIncisionFunction;
import net.sf.tweety.logics.pl.reasoner.SimpleReasoner;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;

/**
 * Example code for applying belief dynamics on abstract argumentation frameworks.
 * 
 * @author Matthias Thimm
 *
 */
public class AbstractArgumentationExample {
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
		
		SatCompleteReasoner reasoner = new SatCompleteReasoner(SatSolver.getDefaultSolver());
		
		System.out.println(reasoner.getModels(theory));
		System.out.println();
				
		PlBeliefSet beliefSet = reasoner.getPropositionalCharacterisation(theory); 
		System.out.println(beliefSet);
		System.out.println();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds((PlSignature)beliefSet.getSignature())){
			if(w.satisfies(beliefSet))
				System.out.println(w);
		}
		
		MultipleBaseRevisionOperator<PlFormula> revise = new LeviMultipleBaseRevisionOperator<PlFormula>(
				new KernelContractionOperator<PlFormula>(new RandomIncisionFunction<PlFormula>(), new SimpleReasoner()),
				new DefaultMultipleBaseExpansionOperator<PlFormula>());
		
		PlBeliefSet beliefSet2 = new PlBeliefSet(revise.revise(beliefSet, new Proposition("in_a")));
		
		System.out.println(beliefSet2);
		System.out.println();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds((PlSignature)beliefSet2.getSignature())){
			if(w.satisfies(beliefSet2))
				System.out.println(w);
		}
	
				
	}
}
