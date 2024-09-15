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
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.qbf.parser.QCirParser;
import org.tweetyproject.logics.qbf.parser.QbfParser;
import org.tweetyproject.logics.qbf.parser.QdimacsParser;
import org.tweetyproject.logics.qbf.syntax.ExistsQuantifiedFormula;
import org.tweetyproject.logics.qbf.writer.QdimacsWriter;

/**
 * Some general examples for quantified boolean formulas and for parsers and
 * writers.
 *
 * @author Anna Gessler
 *
 */
public class QbfExample {

	/** Default */
	public QbfExample() {

	}

	/**
	 * Demonstrates the use of the TweetyProject library for working with various
	 * types of belief sets
	 * and parsers, including propositional logic, QBF (Quantified Boolean
	 * Formulas), QDIMACS, and QCir formats.
	 * The method performs the following tasks:
	 *
	 * 1. Creates a propositional logic belief set and adds formulas to it.
	 * 2. Uses the TweetyProject QBF parser to read a belief set from a file.
	 * 3. Uses the QDIMACS parser to read and parse QDIMACS-formatted belief sets
	 * and outputs.
	 * 4. Uses the QDIMACS writer to print a belief set in QDIMACS format.
	 * 5. Uses the QCir parser to read QCir-formatted belief sets and print them.
	 *
	 * @param args Command-line arguments (not used in this example).
	 * @throws ParserException If an error occurs during parsing.
	 * @throws IOException     If an error occurs during file I/O operations.
	 */
	public static void main(String[] args) throws ParserException, IOException {
		PlBeliefSet p = new PlBeliefSet();
		Proposition v = new Proposition("V");
		ExistsQuantifiedFormula ef = new ExistsQuantifiedFormula(v, v);
		Negation n = new Negation(ef);
		p.add(ef);
		p.add(n);
		System.out.println(p);

		// TweetyProject Parser
		System.out.println("\nTweetyProject parser\n=================");
		QbfParser parser1 = new QbfParser();
		p = parser1.parseBeliefBaseFromFile("src/main/resources/tweety-example.qbf");
		System.out.println(p);

		// QDIMACS Parser
		System.out.println("\nQDIMACS parser\n=================");
		QdimacsParser parser2 = new QdimacsParser();
		PlBeliefSet p2 = parser2.parseBeliefBaseFromFile("src/main/resources/qdimacs-example1.qdimacs");
		System.out.println(p2);
		QdimacsParser.Answer answer = parser2
				.parseQDimacsOutput("c a comment \n" + "s cnf 1 2 2 0 \n" + "2 -1 0 \n" + "1 -2 0   ");
		System.out.println(answer);

		// QDIMACS Writer
		System.out.println("\nQDIMACS writer\n=================");
		QdimacsWriter writer = new QdimacsWriter();
		System.out.println(writer.printBase(p));

		// QCir Parser
		System.out.println("\nQCir parser\n=================");
		QCirParser parser3 = new QCirParser();
		PlBeliefSet p3 = parser3.parseBeliefBaseFromFile("src/main/resources/qcir-example1.qcir");
		System.out.println(p3);
		System.out.println("output: " + parser3.getOutputVariable() + "\n");
		PlBeliefSet p4 = parser3.parseBeliefBaseFromFile("src/main/resources/qcir-example2-sat.qcir");
		for (PlFormula f : p4)
			System.out.println(f);
		System.out.println("output: " + parser3.getOutputVariable());
	}
}
