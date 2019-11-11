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

import java.io.IOException;

import net.sf.tweety.arg.aspic.parser.AspicParser;
import net.sf.tweety.arg.aspic.reasoner.SimpleAspicReasoner;
import net.sf.tweety.arg.aspic.ruleformulagenerator.FolFormulaGenerator;
import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * ASPIC example code for fol formulas.
 * 
 * @author Anna Gessler
 *
 */
public class AspicExampleFol {
	public static void main(String[] args) throws ParserException, IOException {
		// FOL Example
		FolParser folparser = new FolParser();
		FolSignature sig = folparser.parseSignature("Person = {alice,bob}\n" + 
				"type(snores(Person))\n" + 
				"type(professor(Person))\n" + 
				"type(accessDenied(Person))\n" + 
				"type(accessAllowed(Person))\n" + 
				"type(misbehaves(Person))");
		folparser.setSignature(sig);
		AspicParser<FolFormula> parser2 = new AspicParser<FolFormula>(folparser, new FolFormulaGenerator());
		parser2.setSymbolComma(";");
		
		AspicArgumentationTheory<FolFormula> at = parser2.parseBeliefBaseFromFile(AspicExampleFol.class.getResource("/ex5_fol.aspic").getFile());		
		SimpleAspicReasoner<FolFormula> ar = new SimpleAspicReasoner<FolFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.CONFLICTFREE_SEMANTICS));
		FolFormula pf = (FolFormula)folparser.parseFormula("accessDenied(bob)");	
		
		System.out.println(at.asDungTheory());
		System.out.println(pf + "\t" + ar.query(at,pf,InferenceMode.CREDULOUS));
	}
}
