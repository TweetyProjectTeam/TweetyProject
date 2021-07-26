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
import java.util.List;

import org.tweetyproject.lp.asp.parser.ASPParser;
import org.tweetyproject.lp.asp.parser.InstantiateVisitor;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.reasoner.ClingoSolver;
import org.tweetyproject.lp.asp.reasoner.DLVSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * Examples for parsing programs in Clingo and DLV syntax and using the
 * respective solvers to solve them.
 * 
 * <br>Tested with clingo 5.4.0 and DLV dec-17-2012
 * 
 * @author Anna Gessler
 *
 */
public class ASPParserExample {

	private static String CLINGO_PATH = "your/path/to/clingo";
	private static String DLV_PATH = "your/path/to/dlv";
	
	/**
	 * 
	 * @param args ParseException
	 * @throws ParseException ParseException
	 * @throws FileNotFoundException FileNotFoundException
	 */
	public static void main(String[] args) throws ParseException, FileNotFoundException {
		/**
		 * The same parser is used to parse programs in ASP-Core-2, Clingo and DLV syntax
		 * But some special predicates will only work with the corresponding solver (see ASPParser for details)
		 */
		ASPParser parser = new ASPParser(new FileInputStream(new File("src/main/resources/ex5.asp")));
		Program pr1 =  new InstantiateVisitor().visit(parser.Program(), null);
		System.out.println("Parsed ex5.asp:\n" + pr1);
		System.out.println("#show list:" + pr1.getOutputWhitelist()); //the #show option can be used by ClingoSolver
		System.out.println("-------------\n");
		
		parser = new ASPParser(new FileInputStream(new File("src/main/resources/puzzle5.dlv")));
		Program pr2 =  new InstantiateVisitor().visit(parser.Program(), null);
		System.out.println("Parsed puzzle5.dlv:\n" + pr2);
		System.out.println("options:" + pr2.getAdditionalOptions()); //the #maxint option will be automatically used by DLVSolver
		System.out.println("-------------\n");
		
		parser = new ASPParser(new FileInputStream(new File("src/main/resources/latin_square_problem.dlv")));
		Program pr3 =  new InstantiateVisitor().visit(parser.Program(), null);
		System.out.println("Parsed latin_square_problem:\n" + pr3);
		System.out.println("-------------\n");
		
		ClingoSolver clingoSolver = new ClingoSolver(CLINGO_PATH);
		List<AnswerSet> as = clingoSolver.getModels(pr1);
		System.out.println("ex5.asp Clingo output:\n" + as);
		clingoSolver.toggleOutputWhitelist(true); //enable usage of #show
		as = clingoSolver.getModels(pr1);
		System.out.println("ex5.asp Clingo output with #show:\n" + as);
		System.out.println("-------------\n");
		
		as = clingoSolver.getModels(pr2);
		System.out.println("puzzle5.dlv Clingo output:\n" + as);
		System.out.println("-------------\n");
		
		DLVSolver dlvSolver = new DLVSolver(DLV_PATH);
		as = dlvSolver.getModels(pr1);
		System.out.println("ex5.asp DLV output:\n" + as);
		System.out.println("-------------\n");
		
		as = dlvSolver.getModels(pr3);
		System.out.println("latin_square_problem.dlv DLV output:\n" + as);
		System.out.println("-------------\n");
		
		for (ASPRule r : pr1)
			System.out.println("Sorted literals:" + r.getLiterals());
	}

}
