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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.examples;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.Plotter;
import org.tweetyproject.logics.bpm.analysis.BpmnInconsistencyMeasure;
import org.tweetyproject.logics.bpm.analysis.DeadEndMeasure;
import org.tweetyproject.logics.bpm.analysis.DeadTransitionMeasure;
import org.tweetyproject.logics.bpm.analysis.UnfairnessEntropyMeasure;
import org.tweetyproject.logics.bpm.analysis.UnfairnessMeasure;
import org.tweetyproject.logics.bpm.analysis.UnlivenessMeasure;
import org.tweetyproject.logics.bpm.parser.bpmn_to_petri.PetriNetParser;
import org.tweetyproject.logics.bpm.parser.xml_to_bpmn.RootParser;
import org.tweetyproject.logics.bpm.plotting.BpmnModelPlotter;
import org.tweetyproject.logics.bpm.syntax.BpmnModel;
import org.tweetyproject.logics.petri.plotting.PetriNetPlotter;
import org.tweetyproject.logics.petri.plotting.ReachabilityGraphPlotter;
import org.tweetyproject.logics.petri.syntax.PetriNet;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraph;
import org.tweetyproject.logics.petri.syntax.reachability_graph.ReachabilityGraphParser;

/**
 * Visualize inconsistency analysis for some BPMN models 
 * @author Benedikt Knopp
 */
public class Example {

	/**
	 * Specify BPMN models for which the functionalities of inconsistency analysis
	 * are to be visualized
	 * @param args args
	 * @throws ParserException ParserException
	 * @throws IOException IOExeption
	 */
	public static void main(String[] args) throws ParserException, IOException {

		DeadEndMeasure deadEndMeasure = new DeadEndMeasure();
		DeadTransitionMeasure deadTransitionMeasure = new DeadTransitionMeasure();
		UnlivenessMeasure unlivenessMeasure = new UnlivenessMeasure();
		UnfairnessMeasure unfairnessMeasure = new UnfairnessMeasure();
		UnfairnessEntropyMeasure unfairnessEntropyMeasure = new UnfairnessEntropyMeasure();
		
		
		String modelPath = System.getProperty("user.dir") + "/../org-tweetyproject-logics-bpm/src/main/resources/";
		File unproblematic_browsing = new File(modelPath + "unproblematic_browsing.bpmn");
		File unproblematic_dinner = new File(modelPath + "unproblematic_dinner.bpmn");
		File problematic_hit = new File(modelPath + "problematic_hit.bpmn");
		File problematic_with_inclusive_gateways = new File(modelPath + "problematic_with_inclusive_gateways.bpmn");
		
		runExample(unproblematic_browsing, false, ProbabilityFunctionType.RANDOM, deadEndMeasure);
		runExample(unproblematic_dinner, false, ProbabilityFunctionType.RANDOM, deadEndMeasure);
		runExample(unproblematic_browsing, true, ProbabilityFunctionType.RANDOM, deadTransitionMeasure);
		runExample(problematic_with_inclusive_gateways, true, ProbabilityFunctionType.IRREGULAR, deadTransitionMeasure);
		runExample(problematic_hit, true, ProbabilityFunctionType.RANDOM, unlivenessMeasure);
		runExample(problematic_with_inclusive_gateways, true, ProbabilityFunctionType.IRREGULAR, unlivenessMeasure);
		runExample(problematic_hit, true, ProbabilityFunctionType.RANDOM, unfairnessMeasure);
		runExample(problematic_with_inclusive_gateways, true, ProbabilityFunctionType.IRREGULAR, unfairnessEntropyMeasure);		

	} 
	
	private enum ProbabilityFunctionType {
		/**
		 * An equal distribution
		 */
		DEFAULT, 
		/**
		 * A Probability Function with random behaviour
		 */
		RANDOM,
		/**
		 * A deterministic function (each probability is either 0 or 1)
		 */
		IRREGULAR
	}
	
	/**
	 * Run the visualization for a particular BPMN model
	 * @param modelFile the file of the BPMN model
	 * @throws ParserException
	 * @throws IOException
	 */
	private static void runExample(File modelFile, boolean useShortCircuit, ProbabilityFunctionType pftype, BpmnInconsistencyMeasure measure) throws ParserException, IOException {
		BpmnModel bpmnModel;
		PetriNet petriNet;
		ReachabilityGraph reachabilityGraph;
		
		RootParser rootParser = new RootParser();
		FileReader reader = new FileReader(modelFile);
		bpmnModel  =  (BpmnModel) rootParser.parseBeliefBase(reader);
		
		PetriNetParser petriNetParser = new PetriNetParser(bpmnModel);
		petriNetParser.setProvideInitialTokensAtStartEvents(true);
		petriNetParser.construct();
		petriNet = petriNetParser.get();
		if(useShortCircuit) {
			petriNet.transformToShortCircuit();
		}
		
		ReachabilityGraphParser rg_parser = new ReachabilityGraphParser(petriNet);
		rg_parser.construct();
		reachabilityGraph = rg_parser.get();
		if(pftype == ProbabilityFunctionType.RANDOM ) {
			reachabilityGraph.initializeRandomProbabilityFunction();
		} else if (pftype == ProbabilityFunctionType.DEFAULT) {
			reachabilityGraph.initializeDefaultProbabilityFunction();			
		} else {
			reachabilityGraph.initializeIrregularProbabilityFunction();			
		}

		Plotter groundPlotter = new Plotter();
		groundPlotter.createFrame(2000, 1000);
		
		BpmnModelPlotter bpmn_plotter = new BpmnModelPlotter(groundPlotter, bpmnModel);
		bpmn_plotter.createGraph();
		
		PetriNetPlotter petri_plotter = new PetriNetPlotter(groundPlotter, petriNet);
		petri_plotter.createGraph();
		
		ReachabilityGraphPlotter rg_plotter = 
		new ReachabilityGraphPlotter(groundPlotter, reachabilityGraph);
		rg_plotter.createGraph();
		
		measure.inconsistencyMeasure(reachabilityGraph);
		
		List<String> labels = new ArrayList<>();
		labels.add(modelFile.getName());
		labels.addAll(measure.getInfoStrings());

		groundPlotter.addLabels(labels);
		groundPlotter.show();
	}
	
	

    /** Default Constructor */
    public Example(){}
}
