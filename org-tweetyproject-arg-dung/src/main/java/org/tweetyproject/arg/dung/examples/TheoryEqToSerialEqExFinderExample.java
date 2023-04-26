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

import org.tweetyproject.arg.dung.equivalence.AdmissibleKernel;
import org.tweetyproject.arg.dung.equivalence.IEquivalence;
import org.tweetyproject.arg.dung.equivalence.StrongEquivalence;
import org.tweetyproject.arg.dung.equivalence.EquivalenceKernel;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.NoExampleFoundException;
import org.tweetyproject.arg.dung.serialisibility.equivalence.ISerializingComparator;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphIso;
import org.tweetyproject.arg.dung.serialisibility.equivalence.TheoryEqToSerialEqExFinder;
import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationAnalysisPlotter;
import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationGraph;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.ApxWriter;

/**
 * This class represents a summary of examples to show the use of {@link TheoryEqToSerialEqExFinder}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class TheoryEqToSerialEqExFinderExample {
	
	
	
	
	public static void main(String[] args) {

		IEquivalence frameworkComparator = new StrongEquivalence(EquivalenceKernel.ADMISSIBLE);
		ISerializingComparator analysisComparator = new SerialisationEquivalenceByGraphIso();
		boolean theoryBeEqual = true;
		boolean serialGraphBeEqual = true;
		int numberOfArguments = 10;
		int numberOfExamples = 1;
		int maxNumberTryFindExample = 1000;
		double attackProbability = 0.2;
		boolean avoidSelfAttacks = false;
		Semantics semanticsUsed = Semantics.ADM;

		ApxWriter writer = new ApxWriter();
		String path = System.getProperty("user.home")
				+ File.separator + "Documents"
				+ File.separator + "TweetyProject"
				+ File.separator + "TheoryEqToSerialEqExFinderExample";
		createDir(path);

		ZoneId z = ZoneId.of( "Europe/Berlin" );
		ZonedDateTime now = ZonedDateTime.now( z );

		try {
			for (int i = 0; i < numberOfExamples; i++) {
				TheoryEqToSerialEqExFinder exampleFinder = new TheoryEqToSerialEqExFinder(
						frameworkComparator,
						analysisComparator,
						numberOfArguments, 
						attackProbability, 
						avoidSelfAttacks, 
						maxNumberTryFindExample);
				LinkedHashMap<DungTheory, SerialisationGraph[]> examples;

				examples = exampleFinder.
						findExamples(semanticsUsed, theoryBeEqual, serialGraphBeEqual);


				int index = 0;
				for (DungTheory framework : examples.keySet()) {
					File file = new File(path + File.separator +
							"frameworkEQ_" + theoryBeEqual + "_" +
							"graphEQ_" + serialGraphBeEqual + "_" +
							semanticsUsed.abbreviation() + "_" +
							"Example_Pair" + i + "_" + index + "_" +
							now.getYear() + "_" +
							now.getMonthValue() + "_" +
							now.getDayOfMonth() + "_" +
							now.getHour() + "h" +
							now.getMinute() +
							".apx");
					try {
						writer.write(framework, file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					index++;
				}

				SerialisationAnalysisPlotter.plotAnalyses( examples, "Example_", 2000, 1000);
			}
		} catch (NoExampleFoundException e1) {
			System.out.println("No Example found.");
			e1.printStackTrace();
		}
	}

	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}
}
