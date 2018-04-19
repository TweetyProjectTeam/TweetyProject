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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.arg.aspic.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.AspicReasoner;
import net.sf.tweety.arg.aspic.parser.AspicParser;
import net.sf.tweety.arg.aspic.ruleformulagenerator.PlFormulaGenerator;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * ASPIC example code.
 * 
 * @author Matthias Thimm
 *
 */
public class AspicExample2 {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		PlParser plparser = new PlParser();
		AspicParser<PropositionalFormula> parser = new AspicParser<>(plparser, new PlFormulaGenerator());
		AspicArgumentationTheory<PropositionalFormula> at = parser.parseBeliefBaseFromFile(new File(".").getAbsolutePath() + "/src/main/java/net/sf/tweety/arg/aspic/examples/ex1.aspic");		
		AspicReasoner ar = new AspicReasoner(at, Semantics.CONFLICTFREE_SEMANTICS, Semantics.CREDULOUS_INFERENCE);
		Argument query = null;
		PropositionalFormula pf = (PropositionalFormula)plparser.parseFormula("p");
		for (AspicArgument<PropositionalFormula> arg : at.getArguments()) {
			if (arg.getConclusion().equals(pf)) {
				query = arg;
				break;
			}
		}
		System.out.println(at);
		System.out.println(query + "\t" + ar.query(query));		
	}
}
