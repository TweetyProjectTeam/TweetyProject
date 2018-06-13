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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.examples;

import net.sf.tweety.arg.aspic.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.ModuleBasedAspicReasoner;
import net.sf.tweety.arg.aspic.NaiveAspicReasoner;
import net.sf.tweety.arg.aspic.util.RandomAspicArgumentationTheoryGenerator;
import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Exemplary code illustrating the use of the ASPIC theory generator.
 * Furthermore this code show a small performance comparison between
 * the naive ASPIC reasoner and the module based reasoner.
 * 
 * @author Matthias Thimm
 *
 */
public class AspicGeneratorExample {
	public static void main(String[] args) {		 
		int repetitions = 100;
		int numberAtoms = 20;
		int numberFormulas = 50;
		int maxLiteralsInPremises = 4;
		double percentageStrictRules = 0.2;
		
		NaiveAspicReasoner<PropositionalFormula> naiveReasoner = new NaiveAspicReasoner<PropositionalFormula>(AbstractExtensionReasoner.getReasonerForSemantics(Semantics.GR,Semantics.CREDULOUS_INFERENCE));
		ModuleBasedAspicReasoner<PropositionalFormula> moduleBasedReasoner = new ModuleBasedAspicReasoner<PropositionalFormula>(AbstractExtensionReasoner.getReasonerForSemantics(Semantics.GR,Semantics.CREDULOUS_INFERENCE));
		
		long totalNaive = 0;
		long totalModulebased = 0;
		for(int i = 0; i < repetitions; i++) {
			AspicArgumentationTheory<PropositionalFormula> theory = RandomAspicArgumentationTheoryGenerator.next(numberAtoms, numberFormulas, maxLiteralsInPremises, percentageStrictRules);
			System.out.println(i + "\t" + theory);
			PropositionalFormula query = new Proposition("A1");
			long millis = System.currentTimeMillis();
			naiveReasoner.query(theory,query).getAnswerBoolean();
			totalNaive += System.currentTimeMillis()-millis;
			millis = System.currentTimeMillis();
			moduleBasedReasoner.query(theory,query).getAnswerBoolean();
			totalModulebased += System.currentTimeMillis()-millis;
		}	
		System.out.println();
		System.out.println("Naive reasoner: " + totalNaive);
		System.out.println("Module-based reasoner: " +  totalModulebased);
	}	
}
