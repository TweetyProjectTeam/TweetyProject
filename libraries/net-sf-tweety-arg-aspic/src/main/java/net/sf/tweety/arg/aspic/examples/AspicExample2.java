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

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.arg.aspic.parser.AspicParser;
import net.sf.tweety.arg.aspic.reasoner.SimpleAspicReasoner;
import net.sf.tweety.arg.aspic.ruleformulagenerator.PlFormulaGenerator;
import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * ASPIC example code that shows how to parse an ASPIC file and ask queries.
 * 
 * @author Matthias Thimm
 *
 */
public class AspicExample2 {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		PlParser plparser = new PlParser();
		AspicParser<PlFormula> parser = new AspicParser<>(plparser, new PlFormulaGenerator());
		AspicArgumentationTheory<PlFormula> at = parser.parseBeliefBaseFromFile(AspicExample2.class.getResource("/ex1.aspic").getFile());		
		SimpleAspicReasoner<PlFormula> ar = new SimpleAspicReasoner<PlFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.CONFLICTFREE_SEMANTICS));
		PlFormula pf = (PlFormula)plparser.parseFormula("p");		
		System.out.println(at);
		System.out.println(pf + "\t" + ar.query(at,pf,InferenceMode.CREDULOUS));		
	}
}
