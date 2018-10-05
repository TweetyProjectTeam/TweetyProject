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
package net.sf.tweety.lp.asp.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Set;

import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.reasoner.ClingoSolver;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * Examples for parsing simple ELP programs and for using Clingo to solve them.
 * 
 * @author Anna Gessler
 *
 */
public class ASPCore2ParserExample {

	public static void main(String[] args) throws ParseException, FileNotFoundException {
		ASPCore2Parser parser = new ASPCore2Parser(new StringReader(""));;
		
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex5.asp"));
		parser.ReInit(fistr);
		InstantiateVisitor visitor = new InstantiateVisitor();
		Program pr1 = visitor.visit(parser.Program(), null);
		System.out.println("Parsed program:\n" + pr1);
		System.out.println("#show list:" + pr1.getOutputWhitelist());
		
		ClingoSolver solver = new ClingoSolver("/home/anna/sw/asp/clingo");
		AnswerSetList as = solver.getModels(pr1);
		System.out.println("\nClingo output:\n" + as);
		
		AnswerSetList as2 = solver.getModels(new File("src/main/resources/ex6.asp"));
		System.out.println("--------------\nClingo output:\n" +as2);
		
		System.out.println("-------------");
		Set<ASPRule> rules = pr1.getRules();
		for (ASPRule r : rules)
			System.out.println("Sorted literals:" + r.getLiterals());
	}

}
