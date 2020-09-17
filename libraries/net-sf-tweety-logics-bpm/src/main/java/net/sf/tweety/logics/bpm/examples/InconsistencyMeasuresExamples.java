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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.sf.tweety.logics.bpm.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.bpm.analysis.IndeterminateInconsistencyMeasure;
import net.sf.tweety.logics.bpm.analysis.DeadEndInconsistencyMeasure;
import net.sf.tweety.logics.bpm.parser.RootParser;
import net.sf.tweety.logics.bpm.syntax.BpmnModel;

/**
 * @author Benedikt Knopp
 */
public class InconsistencyMeasuresExamples {
	
	public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir") + "/../PPM_Plugin/src/main/resources");

		String modelPath = System.getProperty("user.dir") + "/../net-sf-tweety-logics-bpm/src/main/resources/";
		File unproblematic_browsing = new File(modelPath + "unproblematic_browsing.bpmn");
		File unproblematic_dinner = new File(modelPath + "unproblematic_dinner.bpmn");
		File problematic_hit = new File(modelPath + "problematic_hit.bpmn");
		File problematic_basketball = new File(modelPath + "problematic_basketball.bpmn");

		
		DeadEndInconsistencyMeasure notTerminateMeasure = new DeadEndInconsistencyMeasure();
		IndeterminateInconsistencyMeasure indeterminateMeasure = new IndeterminateInconsistencyMeasure();
		
		runExample(notTerminateMeasure, unproblematic_browsing);
		runExample(notTerminateMeasure, unproblematic_dinner);
		runExample(notTerminateMeasure, problematic_hit);
		runExample(notTerminateMeasure, problematic_basketball);
		runExample(indeterminateMeasure, unproblematic_browsing);
		runExample(indeterminateMeasure, unproblematic_dinner);
		runExample(indeterminateMeasure, problematic_hit);
		runExample(indeterminateMeasure, problematic_basketball);
		
	} 
	
	public static void runExample(InconsistencyMeasure<BpmnModel> measure, File modelFile) {
		RootParser rootParser = new RootParser(modelFile);
		try {
			rootParser.parse();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BpmnModel model = rootParser.getBpmnModel();
		Double inconsistencyValue = measure.inconsistencyMeasure(model);
		String[] measureNamePathTokens = measure.getClass().getName().split("\\.");
		String measureName = measureNamePathTokens[ measureNamePathTokens.length - 1];
		
		System.out.println("value of '"+modelFile.getName()+"' with respect to '"
				+ measureName  + "': \n\t" +inconsistencyValue + "\n");
	}

}
