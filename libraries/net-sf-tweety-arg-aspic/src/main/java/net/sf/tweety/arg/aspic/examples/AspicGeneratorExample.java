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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.examples;

import net.sf.tweety.arg.aspic.reasoner.ModuleBasedAspicReasoner;
import net.sf.tweety.arg.aspic.reasoner.SimpleAspicReasoner;
import net.sf.tweety.arg.aspic.reasoner.RandomAspicReasoner;
import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.util.RandomAspicArgumentationTheoryGenerator;
import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Exemplary code illustrating the use of the ASPIC theory generator.
 * Furthermore this code show a small performance comparison between
 * the naive ASPIC reasoner, the module based reasoner, and the random reasoner.
 * 
 * @author Matthias Thimm
 *
 */
public class AspicGeneratorExample {
	public static void main(String[] args) {		 
		int repetitions = 50;
		int numberAtoms = 20;
		int numberFormulas = 70;
		int maxLiteralsInPremises = 2;
		double percentageStrictRules = 0.2;
		
		SimpleAspicReasoner<PropositionalFormula> naiveReasoner = new SimpleAspicReasoner<PropositionalFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.GR));
		ModuleBasedAspicReasoner<PropositionalFormula> moduleBasedReasoner = new ModuleBasedAspicReasoner<PropositionalFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.GR));
		RandomAspicReasoner<PropositionalFormula> randomReasoner = new RandomAspicReasoner<PropositionalFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.GR),600,100);
		
		long totalNaive = 0;
		long totalModulebased = 0;
		long totalRandom = 0;
		long correctRandom = 0;
		for(int i = 0; i < repetitions; i++) {
			AspicArgumentationTheory<PropositionalFormula> theory = RandomAspicArgumentationTheoryGenerator.next(numberAtoms, numberFormulas, maxLiteralsInPremises, percentageStrictRules);
			System.out.println(i + "\t" + theory);
			PropositionalFormula query = new Proposition("A1");
			long millis = System.currentTimeMillis();
			boolean answer = naiveReasoner.query(theory,query,InferenceMode.CREDULOUS);
			totalNaive += System.currentTimeMillis()-millis;
			millis = System.currentTimeMillis();
			moduleBasedReasoner.query(theory,query,InferenceMode.CREDULOUS);
			totalModulebased += System.currentTimeMillis()-millis;
			millis = System.currentTimeMillis();
			if(randomReasoner.query(theory,query,InferenceMode.CREDULOUS) == answer)
				correctRandom++;
			totalRandom += System.currentTimeMillis()-millis;
		}	
		System.out.println();
		System.out.println("Runtime naive reasoner: " + totalNaive + "ms");
		System.out.println("Runtime module-based reasoner: " +  totalModulebased+ "ms");
		System.out.println("Runtime random reasoner: " +  totalRandom + "ms");
		System.out.println("Accuracy random reasoner: " +(new Double(correctRandom)/(repetitions+1)));
	}	
}
