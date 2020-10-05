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
package net.sf.tweety.logics.bpm.examples;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;

import net.sf.tweety.logics.bpm.analysis.IndeterminateInconsistencyMeasure;
import net.sf.tweety.logics.bpm.analysis.DeadEndInconsistencyMeasure;
import net.sf.tweety.logics.bpm.parser.RootParser;
import net.sf.tweety.logics.bpm.plotting.BpmnModelPlotter;
import net.sf.tweety.logics.bpm.syntax.BpmnModel;

/**
 * Draw BPMN Models and display their inconsistency value according to some measures.
 * @author Benedikt Knopp
 */
public class InconsistencyMeasuresExamples {
	
	/**
	 * @return the measures to apply to the selected BPMN models
	 */
	private static Set<InconsistencyMeasure<BpmnModel>> getMeasures() {
		Set<InconsistencyMeasure<BpmnModel>> measures = new HashSet<>();
		measures.add(new DeadEndInconsistencyMeasure());
		measures.add(new IndeterminateInconsistencyMeasure());
		return measures;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String modelPath = System.getProperty("user.dir") + "/../net-sf-tweety-logics-bpm/src/main/resources/";
		File unproblematic_browsing = new File(modelPath + "unproblematic_browsing.bpmn");
		File unproblematic_dinner = new File(modelPath + "unproblematic_dinner.bpmn");
		File problematic_hit = new File(modelPath + "problematic_hit.bpmn");
		File problematic_basketball = new File(modelPath + "problematic_basketball.bpmn");

		runExample(unproblematic_browsing);
		runExample(unproblematic_dinner);
		runExample(problematic_hit);
		runExample(problematic_basketball);

	} 
	
	/**
	 * @param modelFile a BPMN file according to the BPMN XML standard
	 */
	private static void runExample(File modelFile) {
		RootParser rootParser = new RootParser(modelFile);
		try {
			rootParser.parse();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BpmnModel model = rootParser.getBpmnModel();

		BpmnModelPlotter plotter = new BpmnModelPlotter(model);
		plotter.addLabel(modelFile.getName());
		int frameWidth = 420;
		int frameHeight = 920;
		double nodeWidth = 100;
		double nodeHeight = 100;
		int fontSize = 14;
		plotter.createGraph(frameWidth, frameHeight, nodeWidth, nodeHeight, fontSize);
		plotter.addLabel("-------------- Inconsistency values --------------");
		for( InconsistencyMeasure<BpmnModel> measure : getMeasures() ) {
			Double inconsistencyValue = measure.inconsistencyMeasure(model);
			String[] measureNamePathTokens = measure.getClass().getName().split("\\.");
			String measureName = measureNamePathTokens[ measureNamePathTokens.length - 1];
			plotter.addLabel(measureName + ": " + inconsistencyValue);
		}
		plotter.show();
	}

}
