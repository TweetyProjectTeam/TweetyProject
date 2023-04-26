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

import org.tweetyproject.arg.dung.equivalence.EquivalenceCompExFinder;
import org.tweetyproject.arg.dung.equivalence.IEquivalence;
import org.tweetyproject.arg.dung.equivalence.StrongEquivalence;
import org.tweetyproject.arg.dung.equivalence.strong.EquivalenceKernel;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraph;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphIso;
import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationAnalysisPlotter;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.ApxWriter;

/**
 * This class represents a summary of examples to show the use of {@link EquivalenceCompExFinder}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class EquivalenceCompExFinderExample {

	public static void main(String[] args) {

		Semantics semanticsUsed = Semantics.ADM;
		IEquivalence<DungTheory> equivalence1 = new StrongEquivalence(EquivalenceKernel.ADMISSIBLE);
		IEquivalence<DungTheory> equivalence2 = new SerialisationEquivalenceByGraph(
				new SerialisationEquivalenceByGraphIso(),
				SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
		boolean theoryBeEqual = false;
		boolean serialGraphBeEqual = true;
		int numberOfArguments = 6;
		int numberOfExamples = 1;
		int maxNumberTryFindExample = 10;
		double attackProbability = 0.2;
		boolean avoidSelfAttacks = false;


		ApxWriter writer = new ApxWriter();
		String path = System.getProperty("user.home")
				+ File.separator + "Documents"
				+ File.separator + "TweetyProject"
				+ File.separator + "EquivalenceCompExFinderExample";
		createDir(path);

		ZoneId z = ZoneId.of( "Europe/Berlin" );
		ZonedDateTime now = ZonedDateTime.now( z );


		EquivalenceCompExFinder exampleFinder = new EquivalenceCompExFinder(
				equivalence1,
				equivalence2,
				numberOfArguments, 
				attackProbability, 
				avoidSelfAttacks);
		LinkedHashMap<DungTheory, DungTheory> examples = exampleFinder.
				findExamples(theoryBeEqual, serialGraphBeEqual, maxNumberTryFindExample, numberOfExamples);
		int i = 0;
		for (DungTheory frameworkKey : examples.keySet()) {
			writeFile(frameworkKey, semanticsUsed, theoryBeEqual, serialGraphBeEqual, writer, path, now, i, equivalence1.getDescription(), equivalence2.getDescription(), 0);
			DungTheory secondExample = examples.get(frameworkKey);
			writeFile(secondExample, semanticsUsed, theoryBeEqual, serialGraphBeEqual, writer, path, now, i, equivalence1.getDescription(), equivalence2.getDescription(), 1);
			SerialisationAnalysisPlotter.plotAnalyses(
					new Semantics[] {semanticsUsed},
					new DungTheory[] {frameworkKey, secondExample},
					"Example_",
					2000, 1000);
			i++;
		}
		if(examples.isEmpty()) System.out.println("No Examples found.");
		System.out.println("Processing finished");
	}

	private static void writeFile(DungTheory framework, Semantics semanticsUsed, boolean theoryBeEqual, boolean serialGraphBeEqual,
			ApxWriter writer, String path, ZonedDateTime now, int indexInExampleSeries, String equi1Name, String equi2Name, int indexInPair) {
		File file = new File(path + File.separator +
				equi1Name + "_" + theoryBeEqual + "_" +
				equi2Name + "_" + serialGraphBeEqual + "_" +
				semanticsUsed.abbreviation() + "_" +
				"Example"  + "_" +
				now.getYear() + "_" +
				now.getMonthValue() + "_" +
				now.getDayOfMonth() + "_" +
				now.getHour() + "h" +
				now.getMinute() + "_" +
				"Pair" + indexInExampleSeries + "_" + indexInPair + "_" +
				".apx");
		try {
			writer.write(framework, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}
}
