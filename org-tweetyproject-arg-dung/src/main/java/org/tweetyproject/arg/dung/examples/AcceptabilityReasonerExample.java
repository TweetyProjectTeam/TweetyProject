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
package org.tweetyproject.arg.dung.examples;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.tweetyproject.arg.dung.parser.TgfParser;
import org.tweetyproject.arg.dung.reasoner.AbstractAcceptabilityReasoner;
import org.tweetyproject.arg.dung.reasoner.EeeAcceptabilityReasoner;
import org.tweetyproject.arg.dung.reasoner.IaqAcceptabilityReasoner;
import org.tweetyproject.arg.dung.reasoner.SatCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.SeeAcceptabilityReasoner;
import org.tweetyproject.arg.dung.reasoner.SeemAcceptabilityReasoner;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.sat.MaxSatSolver;
import org.tweetyproject.logics.pl.sat.OpenWboSolver;

/**
 * This example shows how the different acceptability reasoners work.
 * 
 * @author Matthias Thimm
 */
public class AcceptabilityReasonerExample {
	/**
	 * 
	 * @param args string
	 * @throws FileNotFoundException Exception
	 * @throws ParserException Exception
	 * @throws IOException Exception
	 */
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
