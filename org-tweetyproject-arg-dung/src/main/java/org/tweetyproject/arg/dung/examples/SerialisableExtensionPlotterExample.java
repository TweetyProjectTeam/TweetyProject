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

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.plotter.TransitionStateNode;
import org.tweetyproject.arg.dung.serialisibility.plotter.SerialisableExtensionPlotter;
import org.tweetyproject.arg.dung.serialisibility.*;
import org.tweetyproject.graphs.*;

import java.util.HashMap;

/**
 * This class summarises examples displaying the usage of {@link SerialisableExtensionPlotter} 
 * for a chosen type of serialisable semantics.
 * <br>
 * <br> See
 * <br>
 * <br> Matthias Thimm. Revisiting initial sets in abstract argumentation.
 * <br> Argument & Computation 13 (2022) 325–360 
 * <br> DOI 10.3233/AAC-210018
 * <br>
 * <br> and
 * <br>
 * <br> Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract Argumentation.
 * <br> Computational Models of Argument (2022)
 * <br> DOI: 10.3233/FAIA220143
 * 
 * @author Julian Sander
 *
 */
public class SerialisableExtensionPlotterExample {

	/**
	 * Plots specified frameworks and their associated analyses of the serializing generation of extensions in one frame
	 * @param examples Frameworks mapped to the associated analyses using different semantics
	 */
	public static void plotExamplesForReasoner(HashMap<DungTheory, ContainerTransitionStateAnalysis[]> examples) {
		int index = 0;
		for (DungTheory exampleFramework : examples.keySet()) {
			Plotter groundPlotter = new Plotter();
			groundPlotter.createFrame(2000, 1000);
			DungTheoryPlotter.plotFramework(exampleFramework, groundPlotter, "Example " + index);
			for (ContainerTransitionStateAnalysis analysis : examples.get(exampleFramework)) {
				analysis.setTitle(analysis.getTitle() + " Example " + index);
				SimpleGraph<TransitionStateNode> graph = analysis.getGraphResulting();
				SerialisableExtensionPlotter.plotGraph(analysis, groundPlotter);
			}
			groundPlotter.show();
			index++;
		}
	}
	
	
	/**
	 * Plots specified frameworks and their associated analysis of the serializing generation of extensions
	 * @param examples Analyses, which framework and graph should be plotted
	 */
	public static void plotExamplesForReasoner(ContainerTransitionStateAnalysis[] examples) {
		HashMap<DungTheory, ContainerTransitionStateAnalysis[]> convExamples = new HashMap<DungTheory, ContainerTransitionStateAnalysis[]>();
		
		for (ContainerTransitionStateAnalysis analysis : examples) {
			convExamples.put(analysis.getStateExamined().getTheory(), new ContainerTransitionStateAnalysis[] {analysis});
		}
		
		plotExamplesForReasoner(convExamples);
	}
	
	/**
	 * Plots specified frameworks and their associated analysis of the serializing generation of extensions
	 * @param semantics Semantics of the extension created
	 * @param examples Frameworks, for which the extensions should be found.
	 */
	public static void plotExamplesForReasoner(Semantics[] semantics, DungTheory[] examples) {
		HashMap<DungTheory, ContainerTransitionStateAnalysis[]> convExamples = new HashMap<DungTheory, ContainerTransitionStateAnalysis[]>();
		
		for (DungTheory example : examples) {
			ContainerTransitionStateAnalysis[] analyses = new ContainerTransitionStateAnalysis[semantics.length];
			for (int i = 0; i < analyses.length; i++) {
				analyses[i] = SerialisableExtensionReasonerWithAnalysis
						.getSerialisableReasonerForSemantics(semantics[i])
						.getModelsWithAnalysis(example);
			}
			convExamples.put(example, analyses);
		}
				
		plotExamplesForReasoner(convExamples);
	}
	
	
	public static void main(String[] args) {
		Semantics[] semanticsUsed = new Semantics[] {Semantics.CO, Semantics.UC};
		DungTheory[] exampleFrameworks = new DungTheory[] {
				SerialisableExtensionReasonerWithAnalysisExample.buildExample1(),
				SerialisableExtensionReasonerWithAnalysisExample.buildExample2(),
				SerialisableExtensionReasonerWithAnalysisExample.buildExample3()
				};
		
		
		//System.out.println("======================================== all Examples ========================================");
		plotExamplesForReasoner(semanticsUsed, exampleFrameworks);
		System.out.println("");
	}

}
