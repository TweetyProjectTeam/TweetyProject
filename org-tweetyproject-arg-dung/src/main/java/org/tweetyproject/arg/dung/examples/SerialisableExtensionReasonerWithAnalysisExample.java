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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedPreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedStableReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisedUnchallengedReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.SerialisableExtensionReasonerWithAnalysis;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * {@inheritDoc}
 * 
 * This specialization of the reasoner gives access to all interim results of
 * the computation.
 * 
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation.
 *      Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract
 *      Argumentation. Computational Models of Argument (2022) DOI:
 *      10.3233/FAIA220143
 * 
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisableExtensionReasonerWithAnalysisExample extends SerialisableExtensionReasonerExample {

	
	protected static void executeExamplesInUniformLayout(SerialisableExtensionReasonerWithAnalysis reasoner, String description, DungTheory[] examples) {
		System.out.println(description + ":");
		for (int i = 0; i < examples.length-1; i++) {
			examineFrameworkWithReasonerInUniformLayout(examples[i], reasoner);
			System.out.println("");
		}
		examineFrameworkWithReasonerInUniformLayout(examples[examples.length-1], reasoner);
		System.out.println("======================================================================================================");
		System.out.println("");
	}
	
	
	protected static void examineFrameworkWithReasonerInUniformLayout(DungTheory frameWork, SerialisableExtensionReasonerWithAnalysis reasoner) {
		System.out.println(reasoner.getModelsWithAnalysis(frameWork));
	}
	
	
	/**
	 * @param args No input required.
	 */
	public static void main(String[] args) {
		DungTheory[] examples = new DungTheory[] {buildExample1(), buildExample2(), buildExample3()};
		
		SerialisedAdmissibleReasoner admReasoner = new SerialisedAdmissibleReasoner();
		executeExamplesInUniformLayout(admReasoner, "Admissible Semantics", examples);
		
		SerialisedCompleteReasoner coReasoner = new SerialisedCompleteReasoner();
		executeExamplesInUniformLayout(coReasoner, "Complete Semantics", examples);
		
		SerialisedGroundedReasoner grReasoner = new SerialisedGroundedReasoner();
		executeExamplesInUniformLayout(grReasoner, "Grounded Semantics", examples);
		
		SerialisedPreferredReasoner prReasoner = new SerialisedPreferredReasoner();
		executeExamplesInUniformLayout(prReasoner, "Preferred Semantics", examples);
		
		SerialisedStableReasoner stReasoner = new SerialisedStableReasoner();
		executeExamplesInUniformLayout(stReasoner, "Stable Semantics", examples);
		
		SerialisedUnchallengedReasoner ucReasoner = new SerialisedUnchallengedReasoner();
		executeExamplesInUniformLayout(ucReasoner, "Unchallenged Semantics", examples);

	}
}
