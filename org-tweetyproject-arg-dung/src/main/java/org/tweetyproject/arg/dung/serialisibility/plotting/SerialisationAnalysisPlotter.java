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
package org.tweetyproject.arg.dung.serialisibility.plotting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.syntax.SerialisationGraph;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryPlotter;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.commons.PlotterMultiFrame;

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
	 * Plots specified analyses of the serialising generation of extensions, each in a separate frame.
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
	 * for the specified semantics. Creates one frame for every single framework and its graphs.
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
	 * for the specified semantics. Creates one frame for every single framework and its graphs.
	 * @param semantics Semantics of the extension created
	 * @param frameworks Frameworks, for which the extensions should be found.
	 * @param title Title, common to all specified  serialisation graphs to plot
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static void plotAnalyses(Semantics[] semantics, DungTheory[] frameworks, String title, int width, int height) {
		SerialisationAnalysisPlotter.plotAnalyses(generateGraphs(semantics, frameworks), title, width, height);
	}
	
	/**
	 * Plots specified frameworks and their associated serialisation graphs 
	 * for the specified semantics. Creates one single frame for all frameworks and graphs.
	 * @param mapAFtoGraphs Frameworks mapped to the associated serialisation graphs using different semantics
	 * @param titles Titles of the frameworks
	 * @param plotter The plotter, in which a new frame will be created.
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static PlotterMultiFrame plotAnalysesOneFrame(
			HashMap<DungTheory, SerialisationGraph[]> mapAFtoGraphs, String[] titles, int width, int height) {
		var groundPlotter = new PlotterMultiFrame();
		groundPlotter.createFrame(width, height);
		int i = 0;
		for (DungTheory exampleFramework : mapAFtoGraphs.keySet()) {
			DungTheoryPlotter.plotFramework(exampleFramework, groundPlotter, titles[i]);
			for (SerialisationGraph graph : mapAFtoGraphs.get(exampleFramework)) {
				if(graph == null) {
					var labels = new LinkedList<String>();
					labels.add("No Sequence found.");
					groundPlotter.addLabels(labels);
				}
				else {
					SerialisationGraphPlotter.plotGraph(graph, groundPlotter, titles[i]);
				}
			}
			i++;
		}
		groundPlotter.show();
		return groundPlotter;
	}

	/**
	 * Plots specified frameworks and their associated serialisation graphs 
	 * for the specified semantics. Creates one single frame for all frameworks and graphs.
	 * @param semantics Semantics of the extension created
	 * @param frameworks Frameworks, for which the extensions should be found.
	 * @param titles Titles of the frameworks
	 * @param plotter The plotter, in which a new frame will be created.
	 * @param width Width of the new frames created.
	 * @param height Height of the new frames created.
	 */
	public static PlotterMultiFrame plotAnalysesOneFrame(Semantics[] semantics, DungTheory[] frameworks, String[] titles, int width, int height) {
		return SerialisationAnalysisPlotter.plotAnalysesOneFrame(generateGraphs(semantics, frameworks), titles, width, height);
	}
	
	private static HashMap<DungTheory, SerialisationGraph[]> generateGraphs(Semantics[] semantics,
			DungTheory[] frameworks) {
		var convExamples = new HashMap<DungTheory, SerialisationGraph[]>();

		for (DungTheory example : frameworks) {
			SerialisationGraph[] graphs = new SerialisationGraph[semantics.length];
			for (int i = 0; i < graphs.length; i++) {
				try {
					graphs[i] = SerialisableExtensionReasoner
							.getSerialisableReasonerForSemantics(semantics[i])
							.getModelsGraph(example);
				}
				catch(NoSuchElementException e) {
					graphs[i] = null;
				}
			}
			convExamples.put(example, graphs);
		}
		return convExamples;
	}
}
