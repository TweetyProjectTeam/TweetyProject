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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.tweetyproject.lp.asp.grounder.GringoGrounder;
import org.tweetyproject.lp.asp.parser.ASPParser;
import org.tweetyproject.lp.asp.parser.AspifParser;
import org.tweetyproject.lp.asp.parser.InstantiateVisitor;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.reasoner.ClingoSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.Program;

/*
 * Example code for using GringoGrounder and AspifParser. 
 * 
 * Tested with gringo/clingo 5.4.0
 * 
 * @author Anna Gessler
 *
 */
/**
 * 
 * @author Anna Gessler
 *
 */
public class GrounderExample {

	private static String CLINGO_PATH = "your/path/to/clingo";

	/**
	 * 
	 * @param args IOException
	 * @throws IOException IOException
	 * @throws ParseException ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		ASPParser parser = new ASPParser(new FileInputStream(new File("src/main/resources/puzzle5.dlv")));
		Program p = new InstantiateVisitor().visit(parser.Program(), null);

		// Ground program
		GringoGrounder grounder = new GringoGrounder(CLINGO_PATH);
		Program groundP = grounder.getGroundProgram(p);
		System.out.println(groundP);

		ClingoSolver clingo = new ClingoSolver(CLINGO_PATH);
		clingo.toggleOutputWhitelist(true); // hide temporary atoms added by gringo from clingo output
		// Solve grounded program
		List<AnswerSet> claspModels = clingo.getModels(groundP);
		// Ground and solve program in one step using clingo
		List<AnswerSet> clingoModels = clingo.getModels(p);

		System.out.println(claspModels);
		System.out.println(clingoModels);
		System.out.println(claspModels.equals(clingoModels) + "\n");

		// Parse a simple program in aspif format (gringo's output format)
		// The corresponding logic program to this output is:
		// {a}.
		// b :- a.
		// c :- not a.
		AspifParser aspifParser = new AspifParser();
		Program groundP2 = aspifParser.parseProgram("asp 1 0 0\n" 
				+ "1 1 1 1 0 0\n" 
				+ "1 0 1 2 0 1 1\n"
				+ "1 0 1 3 0 1 -1\n" 
				+ "4 1 a 1 1\n" 
				+ "4 1 b 1 2\n" 
				+ "4 1 c 1 3\n" + "0");
		System.out.println(groundP2 + "\n");
		
		// Parse a more complex program in aspif format. 
		// This file is the result of grounding the example from 
		// org.tweetyproject.lp.asp.examples.OptimizeExample using gringo
		Program groundP3 = aspifParser.parseProgramFile("src/main/resources/optimize.aspif");
		System.out.println(groundP3);
		System.out.println("optimum:" + clingo.getOptimum(groundP3));
	}

}
