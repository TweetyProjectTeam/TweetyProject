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

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationAnalysisPlotter;
import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisibility.SerialisationExampleFinder;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.ApxWriter;

/**
 * This class summarises examples displaying the usage of {@link SerialisationExampleFinder}
 * for a chosen type of serialisable semantics.
 *
 * @see source Matthias Thimm. Revisiting initial sets in abstract argumentation.
 *      Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see source Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract
 *      Argumentation. Computational Models of Argument (2022) DOI:
 *      10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisabilityExampleFinderExample {

	public static void main(String[] args) {

		try {
			int numberOfArguments = 6;
			int numberOfArgumentsAtStart = 3;
			int incrementOfArguments = 1;
			int numberOfExamples = 4;
			int maxNumberTryGenerateFramework = 10;
			double attackProbability = 0.2;
			boolean avoidSelfAttack = false;
			Semantics[] semanticsUsed = new Semantics[] {Semantics.CO, Semantics.GR, Semantics.UC};

			ApxWriter writer = new ApxWriter();
			String path = System.getProperty("user.home")
					+ File.separator + "Documents"
					+ File.separator + "TweetyProject"
					+ File.separator + "SerialisabilityExampleFinderExample";
			SerialisabilityExampleFinderExample.createDir(path);

			ZoneId z = ZoneId.of( "Europe/Berlin" );
			ZonedDateTime now = ZonedDateTime.now( z );

			SerialisationExampleFinder exampleFinder = new SerialisationExampleFinder(numberOfArguments, attackProbability, avoidSelfAttack, maxNumberTryGenerateFramework);
			LinkedHashMap<DungTheory, SerialisationGraph[]> examples = exampleFinder.findExampleForDifferentSemantics(semanticsUsed, numberOfExamples, false, false); //exampleFinder.findExampleArrayForDifferentSemantics(
			/*							semanticsUsed,
									numberOfArgumentsAtStart,
									numberOfArguments,
									numberOfExamples,
									incrementOfArguments,
									false, 
									false),
			 */

			int index = 0;
			for (DungTheory framework : examples.keySet()) {
				File file = new File(path + File.separator +
						now.getYear() + "_" +
						now.getMonthValue() + "_" +
						now.getDayOfMonth() + "_" +
						now.getHour() + "h" +
						now.getMinute() + "_" +
						"Example_" + index + ".apx");
				try {
					writer.write(framework, file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				index++;
			}

			SerialisationAnalysisPlotter.plotAnalyses( examples, "Example_", 2000, 1000);
		}catch(Exception e) {

		}
	}

	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}
}
