/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.ContensionInconsistencyMeasure;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class ContensionTest {
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
