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
package net.sf.tweety.arg.aspic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.tweety.arg.aspic.ruleformulagenerator.PlFormulaGenerator;
import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.syntax.DefeasibleInferenceRule;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.arg.aspic.syntax.StrictInferenceRule;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Generates random ASPIC argumentation theories.
 * 
 * @author Matthias Thimm
 *
 */
public class RandomAspicArgumentationTheoryGenerator{ 

	/**
	 * Generates a random ASPIC argumentation theory with <code>numPropositions</code>
	 * and <code>numFormulas</code> formulas (inference rules).
	 * @param numPropositions the number of propositions
	 * @param numFormulas the number of formulas
	 * @param maxBodyLiterals the maximal number of body literals in each rule.
	 * @param probStrict the probability of each rule being strict.
	 * @return an ASPIC argumentation theory
	 */
	public static AspicArgumentationTheory<PropositionalFormula> next(int numPropositions, int numFormulas, int maxBodyLiterals, double probStrict){
		Random rand = new Random();
		PropositionalSignature sig = new PropositionalSignature(numPropositions);
		List<Proposition> atoms = new ArrayList<Proposition>(sig);
		AspicArgumentationTheory<PropositionalFormula> theory = new AspicArgumentationTheory<PropositionalFormula>(new PlFormulaGenerator());
		for(int i = 0; i < numFormulas; i++) {
			InferenceRule<PropositionalFormula> rule;
			if(rand.nextFloat() < probStrict) 
				rule = new StrictInferenceRule<PropositionalFormula>();
			else
				rule = new DefeasibleInferenceRule<PropositionalFormula>();
			if(rand.nextBoolean())
				rule.setConclusion(atoms.get(rand.nextInt(atoms.size())));
			else
				rule.setConclusion(new Negation(atoms.get(rand.nextInt(atoms.size()))));
			int numBodyLiterals = rand.nextInt(maxBodyLiterals);
			for(int j = 0; j < numBodyLiterals; j++)
				if(rand.nextBoolean())
					rule.addPremise(atoms.get(rand.nextInt(atoms.size())));
				else
					rule.addPremise(new Negation(atoms.get(rand.nextInt(atoms.size()))));
			theory.addRule(rule);
		}		
		return theory;
	}
}
