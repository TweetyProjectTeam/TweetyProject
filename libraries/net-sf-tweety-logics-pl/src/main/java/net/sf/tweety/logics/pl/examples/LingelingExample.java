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
package net.sf.tweety.logics.pl.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.LingelingSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Example code illustrating the use of the SAT solver Lingeling.
 * @author Matthias Thimm
 */
public class LingelingExample {
	public static void main(String[] args) throws ParserException, IOException {

		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();
		beliefSet.add((PlFormula) parser.parseFormula("a || b || c"));
		beliefSet.add((PlFormula) parser.parseFormula("!a || b && d"));
		beliefSet.add((PlFormula) parser.parseFormula("a"));
		beliefSet.add((PlFormula) parser.parseFormula("!c"));

		System.out.println(beliefSet);

		SatSolver solver = new LingelingSolver("/Users/mthimm/Projects/misc_bins/lingeling");

		System.out.println(solver.getWitness(beliefSet));
	}
}
