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
package org.tweetyproject.lp.asp.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.List;

import org.tweetyproject.lp.asp.parser.ASPCore2Parser;
import org.tweetyproject.lp.asp.parser.InstantiateVisitor;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.reasoner.ClingoSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * Examples for parsing simple ELP programs and for using Clingo to solve them.
 * Tested with clingo 5.2.2
 * 
 * @author Anna Gessler
 *
 */
public class ASPCore2ParserExample {

	public static void main(String[] args) throws ParseException, FileNotFoundException {
		ASPCore2Parser parser = new ASPCore2Parser(new StringReader(""));
		
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex5.asp"));
		parser.ReInit(fistr);
		InstantiateVisitor visitor = new InstantiateVisitor();
		Program pr1 = visitor.visit(parser.Program(), null);
		System.out.println("Parsed program:\n" + pr1);
		System.out.println("#show list:" + pr1.getOutputWhitelist());
		
		ClingoSolver solver = new ClingoSolver("/Users/mthimm/Documents/software/misc_bins/clingo-4.5.4-macos-10.9");
		List<AnswerSet> as = solver.getModels(pr1);
		System.out.println("\nClingo output:\n" + as);
		
		List<AnswerSet> as2 = solver.getModels(new File("src/main/resources/ex6.asp"));
		System.out.println("--------------\nClingo output:\n" +as2);
		
		System.out.println("-------------");
		for (ASPRule r : pr1)
			System.out.println("Sorted literals:" + r.getLiterals());
	}

}
