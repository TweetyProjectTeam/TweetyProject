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
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.streams.DefaultFormulaStream;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.streams.DefaultInconsistencyListener;
import net.sf.tweety.logics.commons.analysis.streams.DefaultStreamBasedInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.streams.StreamBasedInconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.ContensionInconsistencyMeasure;
import net.sf.tweety.logics.pl.analysis.ContensionInconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class ContensionTest {
	public static void main(String[] args) throws ParserException, IOException, InterruptedException{
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
		
		Thread.sleep(1000);
		
		// test stream-based variant
		Map<String,Object>config = new HashMap<String,Object>();
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, SatSolver.getDefaultSolver());
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 10);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, kb.getSignature());
		StreamBasedInconsistencyMeasure<PropositionalFormula> cont2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(ContensionInconsistencyMeasurementProcess.class,config);
		cont2.addInconsistencyListener(new DefaultInconsistencyListener());
		cont2.getInconsistencyMeasureProcess(new DefaultFormulaStream<PropositionalFormula>(kb,true)).start();
	}
}
