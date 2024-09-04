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
package org.tweetyproject.arg.deductive.examples;

import java.io.IOException;

import org.tweetyproject.arg.deductive.accumulator.SimpleAccumulator;
import org.tweetyproject.arg.deductive.categorizer.ClassicalCategorizer;
import org.tweetyproject.arg.deductive.reasoner.AbstractDeductiveArgumentationReasoner;
import org.tweetyproject.arg.deductive.reasoner.SimpleDeductiveReasoner;
import org.tweetyproject.arg.deductive.syntax.DeductiveKnowledgeBase;
import org.tweetyproject.commons.*;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 *
 * Shows how to construct and query a deductive knowledge base.
 *
 * @author Matthias Thimm
 *
 */
public class DeductiveExample {
	/**
	 * Default Constructor
	 */
	public DeductiveExample() {
		// default
	}

	/**
	 * This class demonstrates the use of a deductive knowledge base in combination
	 * with a SAT solver
	 * to perform reasoning using propositional logic formulas. The program adds
	 * several logical
	 * formulas to the knowledge base, uses a deductive reasoning engine, and
	 * queries the knowledge
	 * base for a specific formula's truth value.
	 *
	 * @param args Command-line arguments (not used in this example).
	 * @throws ParserException If there is an error in parsing the logical formulas.
	 * @throws IOException     If there is an I/O error while processing.
	 */
	public static void main(String[] args) throws ParserException, IOException {
		SatSolver.setDefaultSolver(new Sat4jSolver());
		DeductiveKnowledgeBase kb = new DeductiveKnowledgeBase();

		PlParser parser = new PlParser();
		kb.add((PlFormula) parser.parseFormula("s"));
		kb.add((PlFormula) parser.parseFormula("!s || h"));
		kb.add((PlFormula) parser.parseFormula("f"));
		kb.add((PlFormula) parser.parseFormula("!f || !h"));
		kb.add((PlFormula) parser.parseFormula("v"));
		kb.add((PlFormula) parser.parseFormula("!v || !h"));

		System.out.println(kb);

		AbstractDeductiveArgumentationReasoner reasoner = new SimpleDeductiveReasoner(new ClassicalCategorizer(),
				new SimpleAccumulator());

		System.out.println(reasoner.query(kb, (PlFormula) parser.parseFormula("h")));

	}

}
