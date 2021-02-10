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
package org.tweetyproject.arg.adf.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.reasoner.AdmissibleReasoner;
import org.tweetyproject.arg.adf.reasoner.CompleteReasoner;
import org.tweetyproject.arg.adf.reasoner.GroundReasoner;
import org.tweetyproject.arg.adf.reasoner.ModelReasoner;
import org.tweetyproject.arg.adf.reasoner.PreferredReasoner;
import org.tweetyproject.arg.adf.reasoner.StableReasoner;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 *  Example code illustrating Abstract Dialectical Frameworks (ADFs) with different semantics.
 *  
 * @author Jonas Schumacher
 *
 */

public class ADFExample {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		// Parse ADF from text file
		NativeMinisatSolver solver = new NativeMinisatSolver();
		LinkStrategy strat = new SatLinkStrategy(solver);
		KppADFFormatParser parser = new KppADFFormatParser(strat, true);
		AbstractDialecticalFramework adf = parser.parse(new File("src/main/resources/adf_example.txt"));

		Set<Argument> arguments = adf.getArguments();
		System.out.println("Arguments contained in ADF: " + arguments);
		System.out.println("------------------------------------------");
		
		// Print all arguments and corresponding acceptance conditions 
		Iterator<Argument> arg_iterator = arguments.iterator();
		Argument ar;
		AcceptanceCondition ac;
		while(arg_iterator.hasNext()) {
			ar = arg_iterator.next();
			System.out.println("Argument: \t\t\t " + ar);
			ac = adf.getAcceptanceCondition(ar);
			System.out.println("Acceptance Condition (" + ar + ") \t" + ac);
		}
		System.out.println("------------------------------------------");
		
		// Calculate and print different types of semantics
		
		// Admissible interpretations
		AdmissibleReasoner adm_reasoner = new AdmissibleReasoner(solver);
		System.out.println("Admissible interpretations: ");
		Collection<Interpretation> extensions = adm_reasoner.getModels(adf);
		System.out.println(extensions);
		
		// Complete interpretations
		CompleteReasoner comp_reasoner = new CompleteReasoner(solver);
		System.out.println("Complete interpretations: ");
		extensions = comp_reasoner.getModels(adf);
		System.out.println(extensions);
		
		// Grounded interpretations
		GroundReasoner ground_reasoner = new GroundReasoner(solver);
		System.out.println("Grounded interpretation: ");
		extensions = ground_reasoner.getModels(adf);
		System.out.println(extensions);	
		
		// Preferred interpretations
		PreferredReasoner pref_reasoner = new PreferredReasoner(solver);
		System.out.println("Preferred interpretations: ");
		extensions = pref_reasoner.getModels(adf);
		System.out.println(extensions);	
		
		// 2-valued interpretations
		ModelReasoner model_reasoner = new ModelReasoner(solver);
		System.out.println("2-valued interpretations: ");
		Collection<Interpretation> extensions_2val = model_reasoner.getModels(adf);
		System.out.println(extensions_2val);
		
		// Stable interpretation
		StableReasoner stab_reasoner = new StableReasoner(solver);
		System.out.println("Stable interpretations: ");
		extensions = stab_reasoner.getModels(adf);
		System.out.println(extensions);
	}
}