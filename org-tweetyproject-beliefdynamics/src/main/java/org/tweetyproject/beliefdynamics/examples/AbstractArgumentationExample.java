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
package org.tweetyproject.beliefdynamics.examples;

import org.tweetyproject.arg.dung.reasoner.SatCompleteReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.beliefdynamics.DefaultMultipleBaseExpansionOperator;
import org.tweetyproject.beliefdynamics.LeviMultipleBaseRevisionOperator;
import org.tweetyproject.beliefdynamics.MultipleBaseRevisionOperator;
import org.tweetyproject.beliefdynamics.kernels.KernelContractionOperator;
import org.tweetyproject.beliefdynamics.kernels.RandomIncisionFunction;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

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
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds((PlSignature)beliefSet.getMinimalSignature())){
			if(w.satisfies(beliefSet))
				System.out.println(w);
		}
		
		MultipleBaseRevisionOperator<PlFormula> revise = new LeviMultipleBaseRevisionOperator<PlFormula>(
				new KernelContractionOperator<PlFormula>(new RandomIncisionFunction<PlFormula>(), new SimplePlReasoner()),
				new DefaultMultipleBaseExpansionOperator<PlFormula>());
		
		PlBeliefSet beliefSet2 = new PlBeliefSet(revise.revise(beliefSet, new Proposition("in_a")));
		
		System.out.println(beliefSet2);
		System.out.println();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds((PlSignature)beliefSet2.getMinimalSignature())){
			if(w.satisfies(beliefSet2))
				System.out.println(w);
		}
	
				
	}
}
