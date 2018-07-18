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
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.analysis.ContensionInconsistencyMeasure;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Example code illustrating the contension inconsistency measure.
 * @author Matthias Thimm
 */
public class ContensionExample {
	public static void main(String[] args) throws ParserException, IOException, InterruptedException{
		// Set SAT solver
		SatSolver.setDefaultSolver(new Sat4jSolver());
		// Create some knowledge base
		PlBeliefSet kb = new PlBeliefSet();
		PlParser parser = new PlParser();
	
		kb.add((PropositionalFormula)parser.parseFormula("a"));
		kb.add((PropositionalFormula)parser.parseFormula("!a && b"));
		kb.add((PropositionalFormula)parser.parseFormula("!b"));
		kb.add((PropositionalFormula)parser.parseFormula("c || a"));
		kb.add((PropositionalFormula)parser.parseFormula("!c || a"));
		kb.add((PropositionalFormula)parser.parseFormula("!c || d"));
		kb.add((PropositionalFormula)parser.parseFormula("!d"));
		kb.add((PropositionalFormula)parser.parseFormula("d"));
		kb.add((PropositionalFormula)parser.parseFormula("c"));
		
		// test contension inconsistency measure		
		BeliefSetInconsistencyMeasure<PropositionalFormula> cont = new ContensionInconsistencyMeasure();
		System.out.println("Cont: " + cont.inconsistencyMeasure(kb));
	}
}
