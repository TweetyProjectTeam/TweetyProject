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
package net.sf.tweety.arg.dung.examples;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.arg.dung.parser.TgfParser;
import net.sf.tweety.arg.dung.reasoner.AbstractAcceptabilityReasoner;
import net.sf.tweety.arg.dung.reasoner.EeeAcceptabilityReasoner;
import net.sf.tweety.arg.dung.reasoner.IaqAcceptabilityReasoner;
import net.sf.tweety.arg.dung.reasoner.SatCompleteReasoner;
import net.sf.tweety.arg.dung.reasoner.SeeAcceptabilityReasoner;
import net.sf.tweety.arg.dung.reasoner.SeemAcceptabilityReasoner;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.sat.MaxSatSolver;
import net.sf.tweety.logics.pl.sat.OpenWboSolver;

/**
 * This example shows how the different acceptability reasoner work.
 * 
 * @author Matthias Thimm
 */
public class AcceptabilityReasonerExample {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException {
		// load some example AF
		TgfParser parser = new TgfParser();
		DungTheory af = parser.parseBeliefBaseFromFile("src/main/resources/ex3.tgf");
		
		// instantiate (Max)SAT solver (provide path to open-wbo_2.1)
		MaxSatSolver solver = new OpenWboSolver("/Users/mthimm/Projects/misc_bins/open-wbo_2.1");
		
		// instantiate acceptability reasoner for credulous complete reasoning
		AbstractAcceptabilityReasoner iaq  = new IaqAcceptabilityReasoner(new SatCompleteReasoner(solver), InferenceMode.CREDULOUS);
		AbstractAcceptabilityReasoner eee  = new EeeAcceptabilityReasoner(new SatCompleteReasoner(solver), InferenceMode.CREDULOUS);
		AbstractAcceptabilityReasoner see  = new SeeAcceptabilityReasoner(solver);
		AbstractAcceptabilityReasoner seem = new SeemAcceptabilityReasoner(solver);
		
		// apply reasoner to af
		System.out.println("Acceptable arguments (according to IAQ): " + iaq.getAcceptableArguments(af));
		System.out.println("Acceptable arguments (according to EEE): " + eee.getAcceptableArguments(af));
		System.out.println("Acceptable arguments (according to SEE): " + see.getAcceptableArguments(af));
		System.out.println("Acceptable arguments (according to SEEM): " + seem.getAcceptableArguments(af));
	}
}
