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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.examples;

import java.io.IOException;
import java.util.List;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.DimacsParser;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.CmdLineSatSolver;
import org.tweetyproject.logics.pl.sat.DimacsSatSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;

/**
 * Example code illustrating the use of external SAT solvers such as Lingeling
 * and CaDicaL, and related utilities. Most other modern SAT solvers that use the
 * Dimacs format can be used the same way as the solvers in this example by 
 * providing the path to the solver's binary.
 * 
 * <br>
 * Note: You need to download the respective solvers and replace the paths to
 * their binaries in the example, otherwise it won't run. See
 * {@link org.tweetyproject.logics.pl.sat.CmdLineSatSolver} for download links.
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 */
public class SatSolverExample {

	// Insert the paths to your solver binaries here
	private static String lingeling_path = "/home/anna/snap/sat/lingeling/lingeling";
	private static String cadical_path = "/home/anna/snap/sat/cadical/build/cadical";
	private static String kissat_path = "/home/anna/snap/sat/kissat/build/kissat";
	private static String slime_path = "/home/anna/snap/sat/slime/slime/bin/slime_cli";
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException {
		// Creating a belief base manually
		PlBeliefSet kb1 = new PlBeliefSet();
		PlParser tweetyParser = new PlParser();
		kb1.add(tweetyParser.parseFormula("a || b || c"));
		kb1.add(tweetyParser.parseFormula("!a || b && d"));
		kb1.add(tweetyParser.parseFormula("a"));
		kb1.add(tweetyParser.parseFormula("!c"));
		kb1.add(tweetyParser.parseFormula("a || -"));
		System.out.println(kb1);

		// The conversion into dimacs format is done automatically by CmdLineSatSolver,
		// but the method can also be called manually:
		List<String> re = DimacsSatSolver.convertToDimacs(kb1);
		for(String s: re)
			System.out.println(s);

		// Parsing a belief base in dimacs format
		DimacsParser dimacsParser = new DimacsParser();
		PlBeliefSet kb2 = dimacsParser.parseBeliefBaseFromFile("src/main/resources/dimacs_ex4.cnf");
		System.out.println(kb2);

		// Using the SAT solver Lingeling
		CmdLineSatSolver lingelingSolver = new CmdLineSatSolver(lingeling_path);
		// add a cmd line parameter
		lingelingSolver.addOption("--reduce");
		System.out.println("\n" + lingelingSolver.isSatisfiable(kb1));
		System.out.println("Witness: " + lingelingSolver.getWitness(kb1));
		System.out.println(lingelingSolver.isSatisfiable(kb2));

		// Using the SAT solver CaDiCaL
		CmdLineSatSolver cadicalSolver = new CmdLineSatSolver(cadical_path);
		System.out.println("\n" + cadicalSolver.isSatisfiable(kb1));
		System.out.println("Witness: " + cadicalSolver.getWitness(kb1));
		System.out.println(cadicalSolver.isSatisfiable(kb2));

		// Using the SAT solver Kissat
		CmdLineSatSolver kissatSolver = new CmdLineSatSolver(kissat_path);
		// add a cmd line parameter
		kissatSolver.addOption("--unsat");
		System.out.println("\n" + kissatSolver.isSatisfiable(kb1));
		System.out.println("Witness: " + kissatSolver.getWitness(kb1));
		System.out.println(kissatSolver.isSatisfiable(kb2));

		// Using the SAT solver Slime
		CmdLineSatSolver slimeSolver = new CmdLineSatSolver(slime_path);
		System.out.println("\n" + slimeSolver.isSatisfiable(kb1));
		System.out.println("Witness: " + slimeSolver.getWitness(kb1));
		System.out.println(slimeSolver.isSatisfiable(kb2));

		// For easier switching of solvers and when using classes that use
		// a sat solver internally, you can set the default solver
		// for your whole program once and call getDefaultSolver in all other places of
		// usage
		SatSolver.setDefaultSolver(kissatSolver);
		SatSolver defaultSolver = SatSolver.getDefaultSolver();
		System.out.println("\n" + defaultSolver.isSatisfiable(kb1));
	}
}
