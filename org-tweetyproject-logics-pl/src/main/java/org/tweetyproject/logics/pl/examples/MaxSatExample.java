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
package org.tweetyproject.logics.pl.examples;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.MaxSatSolver;
import org.tweetyproject.logics.pl.sat.OpenWboSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Illustrates the use of MaxSAT solvers.
 * @author Matthias Thimm
 *
 */
public class MaxSatExample {
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException {
		MaxSatSolver solver = new OpenWboSolver("/Users/mthimm/Documents/software/misc_bins/open-wbo_2.1");
		
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		
		bs.add(parser.parseFormula("!a && b"));
		bs.add(parser.parseFormula("b || c"));
		bs.add(parser.parseFormula("c || d"));
		bs.add(parser.parseFormula("f || (c && g)"));
		
		Map<PlFormula, Integer> softClauses = new HashMap<>();
		softClauses.put(parser.parseFormula("a || !b"),25);
		softClauses.put(parser.parseFormula("!c"),15);
		
		Interpretation<PlBeliefSet,PlFormula> witness = solver.getWitness(bs,softClauses);
		System.out.println("Interpretation satisfying the hard constraints and minimising costs of violated soft constraints: " + witness);
		System.out.println("Cost of solution: " + MaxSatSolver.costOf(witness, bs, softClauses));
	}

    /** Default Constructor */
    public MaxSatExample(){}
}
