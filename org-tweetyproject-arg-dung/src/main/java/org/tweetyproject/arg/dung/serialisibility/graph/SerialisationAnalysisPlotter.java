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
package org.tweetyproject.arg.dung.serialisibility.graph;

import java.util.HashMap;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;
import org.tweetyproject.commons.Plotter;

/**
 * This class summarizes static methods used to plot serialisability analyses, 
 * consisting of plotting the underlying argumentation framework and the serialisation graphs regarding the specified semantics.
 * 
 * @see SerialisationGraphPlotter
 * @see org.tweetyproject.arg.dung.util.DungTheoryPlotter
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation.
 *      Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract
 *      Argumentation. Computational Models of Argument (2022) DOI:
 *      10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class SerialisationAnalysisPlotter {

	/**
	 * Plots specified analyses of the serializing generation of extensions, each in a separate frame.
	 * @param graphs Graphs of the serialisation process.
	 * @param title Title, common to all specified  serialisation graphs  to plot
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static void plotAnalyses(DungTheory framework, SerialisationGraph[] graphs, String title, int width, int height) {
		HashMap<DungTheory, SerialisationGraph[]> convExamples = new HashMap<>();

		for (SerialisationGraph analysis : graphs) {
			convExamples.put(framework, new SerialisationGraph[] {analysis});
		}

		SerialisationAnalysisPlotter.plotAnalyses(convExamples, title, width, height);
	}
	
	/**
	 * Plots specified frameworks and their associated serialisation graphs 
	 * for the specified semantics in one frame per framework.
	 * @param mapAFtoGraphs Frameworks mapped to the associated serialisation graphs using different semantics
	 * @param title Title, common to all specified analyses to plot
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static void plotAnalyses(HashMap<DungTheory, SerialisationGraph[]> mapAFtoGraphs, String title, int width, int height) {
		int index = 0;
		for (DungTheory exampleFramework : mapAFtoGraphs.keySet()) {
			Plotter groundPlotter = new Plotter();
			String titleToSet = title + " " + index;
			groundPlotter.createFrame(width, height);
			DungTheoryPlotter.plotFramework(exampleFramework, groundPlotter, titleToSet);
			for (SerialisationGraph graph : mapAFtoGraphs.get(exampleFramework)) {
				SerialisationGraphPlotter.plotGraph(graph, groundPlotter, titleToSet);
			}
			groundPlotter.show();
			index++;
		}
	}
	
	/**
	 * Plots specified frameworks and their associated serialisation graphs 
	 * for the specified semantics in one frame per framework.
	 * @param semantics Semantics of the extension created
	 * @param frameworks Frameworks, for which the extensions should be found.
	 * @param title Title, common to all specified  serialisation graphs to plot
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static void plotAnalyses(Semantics[] semantics, DungTheory[] frameworks, String title, int width, int height) {
		HashMap<DungTheory, SerialisationGraph[]> convExamples = new HashMap<>();

		for (DungTheory example : frameworks) {
			SerialisationGraph[] graphs = new SerialisationGraph[semantics.length];
			for (int i = 0; i < graphs.length; i++) {
				graphs[i] = SerialisableExtensionReasoner
						.getSerialisableReasonerForSemantics(semantics[i])
						.getModelsGraph(example);
			}
			convExamples.put(example, graphs);
		}

		SerialisationAnalysisPlotter.plotAnalyses(convExamples, title, width, height);
	}
}
