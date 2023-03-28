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
import org.tweetyproject.arg.dung.serialisibility.SerialisableExtensionReasonerWithAnalysis;
import org.tweetyproject.arg.dung.serialisibility.plotter.TransitionStateNode;
import org.tweetyproject.arg.dung.serialisibility.plotter.SerialisableExtensionPlotter;
import org.tweetyproject.arg.dung.serialisibility.*;
import org.tweetyproject.graphs.*;

import java.util.HashSet;

/**
 * This class summarises examples displaying the usage of {@link org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionPlotter} 
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

	private static void plotExamplesForReasoner(Semantics semantic, DungTheory[] examples) {
		int index = 0;
		for (DungTheory example : examples) {
			Plotter groundPlotter = new Plotter();
			groundPlotter.createFrame(2000, 1000);
			DungTheoryPlotter.plotFramework(example, groundPlotter, "Example " + index);
			ContainerTransitionStateAnalysis analysis = SerialisableExtensionReasonerWithAnalysis
					.getSerialisableReasonerForSemantics(semantic)
					.getModelsWithAnalysis(example);
			SimpleGraph<TransitionStateNode> graph = analysis.getGraphResulting();
			SerialisableExtensionPlotter.plotGraph(graph, groundPlotter, "Analysis " + index);
			groundPlotter.show();
			System.out.println("================================================================================");
			System.out.println(analysis.toString());
			System.out.println("================================================================================");
			System.out.println("");
			
			index++;
		}
	}
	
	
	public static void main(String[] args) {
		//System.out.println("======================================== all Examples ========================================");
		plotExamplesForReasoner(Semantics.CO, new DungTheory[] {
				SerialisableExtensionReasonerWithAnalysisExample.buildExample1(),
				SerialisableExtensionReasonerWithAnalysisExample.buildExample2(),
				SerialisableExtensionReasonerWithAnalysisExample.buildExample3()
				});
		System.out.println("");
	}

}
