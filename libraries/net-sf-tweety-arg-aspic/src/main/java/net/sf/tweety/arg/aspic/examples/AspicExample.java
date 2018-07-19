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

import net.sf.tweety.arg.aspic.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.ruleformulagenerator.PlFormulaGenerator;
import net.sf.tweety.arg.aspic.syntax.DefeasibleInferenceRule;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * ASPIC example code.
 * 
 * @author Matthias Thimm
 *
 */
public class AspicExample {
	public static void main(String[] args){
		Proposition a = new Proposition("a");
		Proposition b = new Proposition("b");
		Proposition c = new Proposition("c");
		Proposition d = new Proposition("d");
		
		AspicArgumentationTheory<PropositionalFormula> t = new AspicArgumentationTheory<>(new PlFormulaGenerator());
		t.setRuleFormulaGenerator(new PlFormulaGenerator());
		
		DefeasibleInferenceRule<PropositionalFormula> r1 = new DefeasibleInferenceRule<>();
		r1.setConclusion(a);
		r1.addPremise(b);
		r1.addPremise(c);
		t.addRule(r1);
		
		r1 = new DefeasibleInferenceRule<>();
		r1.setConclusion(d);
		r1.addPremise(b);
		t.addRule(r1);
		
		r1 = new DefeasibleInferenceRule<>();
		r1.setConclusion(new Negation(d));
		r1.addPremise(a);
		t.addRule(r1);
		
		t.addAxiom(b);
		t.addAxiom(c);
		
		
		System.out.println(t);
		System.out.println();
		
		DungTheory aaf = t.asDungTheory();
		
		for(Argument arg: aaf)
			System.out.println(arg);
		
		System.out.println();
		
		for(Attack att: aaf.getAttacks())
			System.out.println(att);	
		
	}
}
