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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.tweetyproject.arg.dung.equivalence.DecisionMaker;
import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.equivalence.EquivalenceCompExFinder;
import org.tweetyproject.arg.dung.equivalence.StrongEquivalence;
import org.tweetyproject.arg.dung.equivalence.strong.EquivalenceKernel;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraph;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphIso;
import org.tweetyproject.arg.dung.serialisibility.plotting.NoExampleFoundException;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
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
		
		Collection<Semantics> semanticsUsed = new HashSet<Semantics>();
		semanticsUsed.add(Semantics.ADM);
		semanticsUsed.add(Semantics.CO);
		semanticsUsed.add(Semantics.GR);
		semanticsUsed.add(Semantics.PR);
		semanticsUsed.add(Semantics.ST);
		semanticsUsed.add(Semantics.UC);
		
		semanticsUsed.parallelStream().forEach(semantics ->{
			startSeries(semantics);
		});
		
		
		
	}

	/*
	 * Starts a new series to generate diff. examples of the same semantics
	 */
	private static void startSeries(Semantics semanticsUsed) {
		int maxNumberTryFindExample = 30;
		
		var parameters = new DungTheoryGenerationParameters();
		parameters.numberOfArguments = 6;
		parameters.attackProbability = 0.2;
		parameters.avoidSelfAttacks = false;
		var fstFrameworkGen = new EnumeratingDungTheoryGenerator();
		var scndFrameworkGen = new DefaultDungTheoryGenerator(parameters);
		Equivalence<DungTheory> equivalence1 = new StrongEquivalence(EquivalenceKernel.ADMISSIBLE);
		Equivalence<DungTheory> equivalence2 = new SerialisationEquivalenceByGraph(
				new SerialisationEquivalenceByGraphIso(),
				SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
		
		String path = System.getProperty("user.home")
				+ File.separator + "Documents"
				+ File.separator + "TweetyProject"
				+ File.separator + "EquivalenceCompExFinderExample";
		createDir(path);
		ZoneId z = ZoneId.of( "Europe/Berlin" );
		ZonedDateTime now = ZonedDateTime.now( z );
		String idSeries = "" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + now.getHour() + now.getMinute();
		int indexInSeries = 0;
		
		while(true) {
			try {
				var examplePair = generateOnePair(
					maxNumberTryFindExample,
					semanticsUsed, fstFrameworkGen, scndFrameworkGen, equivalence1, equivalence2, 
					path, idSeries, indexInSeries, now);
				indexInSeries++;
				/*SerialisationAnalysisPlotter.plotAnalyses(
				new Semantics[] {semanticsUsed},
				new DungTheory[] {examplePair.keySet().toArray(new DungTheory[1])[0], examplePair.values().toArray(new DungTheory[1])[0]},
				"Example_",
				2000, 1000);
				 */
			} catch (NoExampleFoundException e) {
				System.out.println("No Examples found.");
			}
		}
	}

	/*
	 * Generates a pair of exemplary frameworks
	 */
	private static LinkedHashMap<DungTheory, DungTheory> generateOnePair(
			int maxNumberTryFindExample,
			Semantics semanticsUsed,
			Iterator<DungTheory> fstFrameworkGen, 
			Iterator<DungTheory> scndFrameworkGen,
			Equivalence<DungTheory> equivalence1, 
			Equivalence<DungTheory> equivalence2,
			String path,
			String idSeries,
			int indexInSeries, 
			ZonedDateTime now) throws NoExampleFoundException {
		var decisionMaker = new DecisionMaker() {
			@Override
			public boolean decide(boolean isEQ1, boolean isEQ2) {
				return isEQ1 != isEQ2;
			}

			@Override
			public boolean getShallCriteriaBeTrueA() {
				return true;
			}

			@Override
			public boolean getShallCriteriaBeTrueB() {
				return true;
			}
		};
		
		EquivalenceCompExFinder exampleFinder = new EquivalenceCompExFinder(
				equivalence1,
				equivalence2,
				decisionMaker);
		LinkedHashMap<DungTheory, DungTheory> output = null;
		System.out.println("Processing started: No.: " + idSeries + "_"+ indexInSeries + " " + semanticsUsed.abbreviation());
			output = exampleFinder.
					findExample(
							maxNumberTryFindExample,
							fstFrameworkGen,
							scndFrameworkGen);

			
			for (DungTheory frameworkKey : output.keySet()) {
				boolean isEQ1 = equivalence1.isEquivalent(frameworkKey, output.get(frameworkKey));
				boolean isEQ2 = equivalence2.isEquivalent(frameworkKey, output.get(frameworkKey));
				EquivalenceCompExFinderExample.writeFile(
						path, frameworkKey, idSeries, indexInSeries, 0, 
						semanticsUsed, equivalence1.getDescription(), equivalence2.getDescription(), isEQ1, isEQ2, now);
				DungTheory secondExample = output.get(frameworkKey);
				EquivalenceCompExFinderExample.writeFile(
						path, secondExample, idSeries, indexInSeries, 1, 
						semanticsUsed, equivalence1.getDescription(), equivalence2.getDescription(), isEQ1, isEQ2, now);
			}
		System.out.println("Processing finished: No.: " + idSeries + "_"+ indexInSeries + " " + semanticsUsed.abbreviation());
		
		return output;
	}
	/*
	 * Creates a new directory iff path described does not exist
	 */
	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}

	/*
	 * Appends a file with a set of comments
	 */
	private static void writeComment(File f, String[] comments) throws IOException {

		try(FileWriter fw = new FileWriter(f, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw))
		{
			out.println();
			for (String comment : comments) {
				out.println(comment);
			}
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	/*
	 * Writes a file to the disc, specifying the file name in order to identify experiment
	 */
	private static void writeFile(
			String path,
			DungTheory framework,
			String idSeries, 
			int indexInSeries, 
			int indexInPair,
			Semantics semanticsUsed,
			String equi1Name, 
			String equi2Name,
			boolean isEQ1, 
			boolean isEQ2,
			ZonedDateTime now) {
		
		var writer = new ApxWriter();
		File file = new File(path + File.separator +
				equi1Name + "_" + isEQ1 + "_" +
				equi2Name + "_" + isEQ2 + "_" +
				semanticsUsed.abbreviation() + "_" +
				idSeries + "_" + indexInSeries + "_" + indexInPair + "_" +
				".apx");
		
		var addInfo = new String[1];
		addInfo[0] = "Date/Time of creation: " +
				now.getYear() + "_" +
				now.getMonthValue() + "_" +
				now.getDayOfMonth() + "_" +
				now.getHour() + "h" +
				now.getMinute() + "m" +
				now.getSecond();

		try {
			writer.write(framework, file);
			writeComment(file, addInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
