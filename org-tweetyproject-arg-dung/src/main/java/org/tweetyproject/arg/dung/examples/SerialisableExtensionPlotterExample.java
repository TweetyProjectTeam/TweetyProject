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

public class SerialisableExtensionPlotterExample {

	private static void plotExamplesForReasoner(Semantics semantic, DungTheory[] examples) {
		/*
		HashSet<Graph<SerialisableExtensionAnalysisNode>> graphs = new HashSet<Graph<SerialisableExtensionAnalysisNode>>();
		for (DungTheory example : examples) {
			graphs.add(
					SerialisableExtensionReasonerWithAnalysis
						.getSerialisableReasonerForSemantics(semantic)
						.getModelsWithAnalysis(example).getGraph()
					);
		}
		SerialisableExtensionPlotter.plotGraph(graphs, 2000, 1000);
		*/
		
		for (DungTheory example : examples) {
			Plotter groundPlotter = new Plotter();
			groundPlotter.createFrame(2000, 1000);
			DungTheoryPlotter.plotFramework(example, groundPlotter);
			TransitionStateAnalysis analysis = SerialisableExtensionReasonerWithAnalysis
					.getSerialisableReasonerForSemantics(semantic)
					.getModelsWithAnalysis(example);
			SimpleGraph<TransitionStateNode> graph = analysis.getGraph();
			SerialisableExtensionPlotter.plotGraph(graph, groundPlotter);
			groundPlotter.show();
			System.out.println("================================================================================");
			System.out.println(analysis.toString());
			System.out.println("================================================================================");
			System.out.println("");
			
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
		/*
		System.out.println("======================================== Example 2 ========================================");
		DungTheoryPlotter.plotFramework(example2(), 2000, 1000);
		System.out.println("");
		
		System.out.println("======================================== Example 3 ========================================");
		DungTheoryPlotter.plotFramework(example3(), 2000, 1000);
		System.out.println("");
		*/
	}

}
