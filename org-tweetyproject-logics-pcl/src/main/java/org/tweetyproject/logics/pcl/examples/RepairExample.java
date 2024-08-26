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
package org.tweetyproject.logics.pcl.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pcl.analysis.GeneralizedMeMachineShop;
import org.tweetyproject.logics.pcl.parser.PclParser;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;

/**
 * Example code illustrating the use of repairing approaches.
 */
public class RepairExample {

    /**
     * Main method demonstrating the repair of an inconsistent probabilistic conditional logic belief set.
     *
     * @param args Command line arguments (not used)
     * @throws ParserException If an error occurs while parsing the probabilistic conditionals
     * @throws IOException If an I/O error occurs during the repair process
     */
	public static void main(String[] args) throws ParserException, IOException{
		// TODO set solver
		// Solver.setDefaultGeneralSolver(...);

		// some inconsistent belief base
		PclBeliefSet kb = new PclBeliefSet();
		PclParser parser = new PclParser();
		//kb.add((ProbabilisticConditional)parser.parseFormula("(A)[0]"));
		//kb.add((ProbabilisticConditional)parser.parseFormula("(A)[1]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sp)[0.25]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sp|ss)[0.8]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sp|sc)[0.6]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sc|sp)[0.7]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(ss|sp)[0.5]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(sc| !sp )[0.05]"));
		kb.add((ProbabilisticConditional)parser.parseFormula("(ss| !sp )[0.01]"));
		System.out.println(kb);

		// repair
		GeneralizedMeMachineShop gm = new GeneralizedMeMachineShop(2);
		System.out.println(gm.repair(kb));
	}

    /** Default Constructor */
    public RepairExample(){}
}
