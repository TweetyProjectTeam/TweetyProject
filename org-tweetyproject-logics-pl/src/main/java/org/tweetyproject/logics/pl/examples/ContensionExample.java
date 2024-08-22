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

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.analysis.ContensionInconsistencyMeasure;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Example code illustrating the contension inconsistency measure.
 * @author Matthias Thimm
 */
public class ContensionExample {
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 * @throws InterruptedException InterruptedException
	 */
	public static void main(String[] args) throws ParserException, IOException, InterruptedException{
		// Set SAT solver
		SatSolver.setDefaultSolver(new Sat4jSolver());
		// Create some knowledge base
		PlBeliefSet kb = new PlBeliefSet();
		PlParser parser = new PlParser();
	
		kb.add((PlFormula)parser.parseFormula("a"));
		kb.add((PlFormula)parser.parseFormula("!a && b"));
		kb.add((PlFormula)parser.parseFormula("!b"));
		kb.add((PlFormula)parser.parseFormula("c || a"));
		kb.add((PlFormula)parser.parseFormula("!c || a"));
		kb.add((PlFormula)parser.parseFormula("!c || d"));
		kb.add((PlFormula)parser.parseFormula("!d"));
		kb.add((PlFormula)parser.parseFormula("d"));
		kb.add((PlFormula)parser.parseFormula("c"));
		
		// test contension inconsistency measure		
		BeliefSetInconsistencyMeasure<PlFormula> cont = new ContensionInconsistencyMeasure();
		System.out.println("Cont: " + cont.inconsistencyMeasure(kb));
	}

    /** Default Constructor */
    public ContensionExample(){}
}
