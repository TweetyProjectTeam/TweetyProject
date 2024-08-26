/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.logics.pcl.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pcl.parser.PclParser;
import org.tweetyproject.logics.pcl.reasoner.DefaultMeReasoner;
import org.tweetyproject.logics.pcl.semantics.ProbabilityDistribution;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.math.opt.rootFinder.GradientDescentRootFinder;
import org.tweetyproject.math.opt.solver.OctaveSqpSolver;
import org.tweetyproject.math.opt.solver.Solver;

/**
 *
 * Implementation of example Grippe (flu)
 * from lecture Commonsense Reasoning in summer term 2020, chapter 4, slide 204
 *
 * @author Jonas Schumacher
 *
 */
public class MaxEntExample {
	/**
	 * Main method that demonstrates MaxEnt reasoning on a probabilistic conditional
	 * logic belief set.
	 *
	 * @param args Command line arguments (not used)
	 * @throws ParserException If an error occurs while parsing the probabilistic
	 *                         conditionals
	 * @throws IOException     If an I/O error occurs while setting up the solver
	 */
	public static void main(String[] args) throws ParserException, IOException {
		/**
		 * @TODO: change your path to Octave
		 */
		OctaveSqpSolver
				.setPathToOctave("/Users/Jonas/Desktop/TU/Hiwi/octave-5.2.0-w64/mingw64/bin/octave-cli-5.2.0.exe");
		// Check if solver is installed:
		// System.out.println(OctaveSqpSolver.isInstalled());
		Solver.setDefaultGeneralSolver(new OctaveSqpSolver());

		PclBeliefSet kb = new PclBeliefSet();
		PclParser parser = new PclParser();
		kb.add((ProbabilisticConditional) parser.parseFormula("(k|g)[1.0]"));
		kb.add((ProbabilisticConditional) parser.parseFormula("(s|g)[0.9]"));
		kb.add((ProbabilisticConditional) parser.parseFormula("(k|s)[0.8]"));

		System.out.println("Given knowledge base: ");
		System.out.println(kb);
		System.out.println(kb.getMinimalSignature());

		DefaultMeReasoner reasoner_me_default = new DefaultMeReasoner(new GradientDescentRootFinder());

		ProbabilityDistribution prob = reasoner_me_default.getModel(kb);
		System.out.println("Resulting probability distribution of maximal entropy:");
		System.out.println(prob);

		System.out.println("Probability of selected propositions: ");
		System.out.println("P(k)" + prob.probability(new Proposition("k")));
		System.out.println("P(g)" + prob.probability(new Proposition("g")));
		System.out.println("P(s)" + prob.probability(new Proposition("s")));
	}

	/** Default Constructor */
	public MaxEntExample() {
	}
}
