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
package org.tweetyproject.logics.qbf.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.qbf.parser.QbfParser;
import org.tweetyproject.logics.qbf.reasoner.CadetSolver;
import org.tweetyproject.logics.qbf.reasoner.CaqeSolver;
import org.tweetyproject.logics.qbf.reasoner.GhostQSolver;
import org.tweetyproject.logics.qbf.reasoner.NaiveQbfReasoner;
import org.tweetyproject.logics.qbf.reasoner.QuteSolver;

/**
 * Examples for using QBF solvers.
 *
 * @author Anna Gessler
 *
 */
public class QbfReasonersExample {

	/** Default */
	public QbfReasonersExample() {

	}

	/**
	 * Demonstrates the use of various Quantified Boolean Formula (QBF) reasoners
	 * and solvers with the TweetyProject library.
	 * The method parses belief sets and formulas from files or strings, and
	 * evaluates their satisfiability or consistency
	 * using different solvers such as NaiveQbfReasoner, CAQE, GhostQ, Qute, and
	 * Cadet.
	 *
	 * The demonstration includes:
	 * 1. Parsing belief sets and formulas using the QbfParser.
	 * 2. Performing naive classical inference using the NaiveQbfReasoner.
	 * 3. Utilizing external QBF solvers (CAQE, GhostQ, Qute, and Cadet) to check
	 * satisfiability and consistency of belief sets and formulas.
	 *
	 * Each solver is evaluated on the same set of inputs to compare their
	 * performance and behavior.
	 *
	 * @param args Command-line arguments (not used in this example).
	 * @throws ParserException If an error occurs during parsing.
	 * @throws IOException     If an error occurs during file I/O operations.
	 */
	public static void main(String[] args) throws ParserException, IOException {
		QbfParser parser = new QbfParser();
		PlBeliefSet p = parser.parseBeliefBaseFromFile("src/main/resources/tweety-example.qbf");
		System.out.println(p);

		// Naive classical inference
		System.out.println("\nNaiveQbfReasoner\n=================");
		NaiveQbfReasoner reasoner = new NaiveQbfReasoner();
		PlBeliefSet p2 = parser.parseBeliefBase("forall a: (a || !a) \n"
				+ "!b");
		System.out.println(p2);
		PlFormula query = parser.parseFormula("a || !a");
		System.out.println(reasoner.query(p2, query));
		PlFormula query2 = parser.parseFormula("forall a: (a || !b)");
		System.out.println(reasoner.query(p2, query2));
		PlFormula query3 = parser.parseFormula("exists b: (b && !b)");
		System.out.println(reasoner.query(p2, query3));

		PlFormula f1 = parser
				.parseFormula("forall x1: (exists y1: (forall x2: (exists y2: ((x1 || y1) && (!x2 || y2)))))");
		PlFormula f2 = parser.parseFormula("forall x1: (x1||!x1)");
		PlFormula f3 = parser.parseFormula("exists x2: (forall x1: (x1||!x2||x2 && (!x1||x2)))");
		PlBeliefSet p3 = parser.parseBeliefBase("forall A: (forall B:( exists C:( (C) <=> (A && B))))");
		PlBeliefSet p4 = parser.parseBeliefBase("exists A: ( forall C:( exists B:((!A && C) && (D || B && !C))))");

		// CAQE
		System.out.println("\nCAQE\n=================");
		CaqeSolver reasoner3 = new CaqeSolver("/home/anna/snap/qbf/caqe/");
		System.out.println(reasoner3.isSatisfiable(p3));
		System.out.println(reasoner3.isSatisfiable(p4));
		System.out.println(reasoner3.isConsistent(f1));
		System.out.println(reasoner3.isConsistent(f2));
		System.out.println(reasoner3.isConsistent(f3));

		// GhostQ
		System.out.println("\nGhostQ\n=================");
		GhostQSolver reasoner5 = new GhostQSolver("/home/anna/snap/qbf/ghostq/bin/");
		System.out.println(reasoner5.isSatisfiable(p3));
		System.out.println(reasoner5.isSatisfiable(p4));
		System.out.println(reasoner5.isConsistent(f1));
		System.out.println(reasoner5.isConsistent(f2));
		System.out.println(reasoner5.isConsistent(f3));

		// Qute
		System.out.println("\nQute\n=================");
		QuteSolver reasoner4 = new QuteSolver("/home/anna/snap/qbf/qute");
		System.out.println(reasoner4.isSatisfiable(p3)); // TODO: Why does this evaluate to false (in contrast to the
															// other solvers)?
		System.out.println(reasoner4.isSatisfiable(p4));
		System.out.println(reasoner4.isConsistent(f1));
		System.out.println(reasoner4.isConsistent(f2));

		// Cadet
		// Note: Only takes input in 2QBF
		System.out.println("\nCadet\n=================");
		CadetSolver reasoner2 = new CadetSolver("/home/anna/snap/qbf/cadet/");
		System.out.println(reasoner2.isSatisfiable(p3));
	}
}
