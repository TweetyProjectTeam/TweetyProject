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
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.function.Function;

import org.tweetyproject.arg.dung.equivalence.DecisionMaker;
import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.equivalence.EquivalenceCompExFinder;
import org.tweetyproject.arg.dung.equivalence.StrongEquivalence;
import org.tweetyproject.arg.dung.equivalence.strong.EquivalenceKernel;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceBySequence;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceBySequenceNaiv;
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

		Collection<Semantics> semanticsUsed = new HashSet<>();
		semanticsUsed.add(Semantics.ADM);
		semanticsUsed.add(Semantics.CO);
		semanticsUsed.add(Semantics.GR);
		semanticsUsed.add(Semantics.PR);
		semanticsUsed.add(Semantics.ST);
		//semanticsUsed.add(Semantics.UC);

		/*
		semanticsUsed.parallelStream().forEach(semantics ->{
			startSeries(semantics);
		});
		 */

		for (Semantics semantics : semanticsUsed) {
			Thread thread = new Thread(semantics.abbreviation()) {
				@Override
				public void run(){
					EquivalenceCompExFinderExample.startSeries(semantics);
				}
			};
			thread.start();
		}
	}
	
	/*
	 * Starts a new series to generate diff. examples of the same semantics
	 */
	private static void startSeries(Semantics semanticsUsed) {
		//======================= STEPS to configure experiment ===============================================   <= Configuration starts here
		//[STEP] 1/5: set number for tries to compute 2nd framework.
		//High numbers seem to be preferable, since they increase the chance of getting a compliant framework.
		int maxNumberTryFindExample = 30;
		// creates only pairs with less arguments than maxNumArguments. If maxNumArguments is 0, then no limit
		int maxNumArguments = 4;

		//[STEP] 2/5: set the sort of equivalences, which you would like to investigate
		Equivalence<DungTheory> equivalence1 = new StrongEquivalence(EquivalenceKernel.getKernel(semanticsUsed));
		Equivalence<DungTheory> equivalence2 = new SerialisationEquivalenceBySequence(
				new SerialisationEquivalenceBySequenceNaiv(), 
				SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
		
		// [STEP] 3/5: define how the two equivalence should be compared to one another
		var decisionMaker = new DecisionMaker() {
			@Override
			public boolean decide(boolean isEQ1, boolean isEQ2) {
				return isEQ1 != isEQ2;
			}

			// This method is used to decide, if the 2nd framework shall be generated as an equivalent framework 
			//to the 1st framework in the definition of equivalence1
			@Override
			public boolean getShallCriteriaBeTrueA() {
				return true;
			}

			// This method is used to decide, if the 2nd framework shall be generated as an equivalent framework 
			//to the 1st framework in the definition of equivalence2
			@Override
			public boolean getShallCriteriaBeTrueB() {
				return true;
			}
		};

		// [STEP] 4/5: set the generators, which will be used to generate the frameworks
		var fstFrameworkGen = new EnumeratingDungTheoryGenerator();

		var parameters = new DungTheoryGenerationParameters();
		parameters.attackProbability = 0.2;
		parameters.avoidSelfAttacks = false;
		int factorNumArgsGen1ToGen2 = 1;
		var scndFrameworkGen = new DefaultDungTheoryGenerator(parameters);

		//[STEP] 5/5: set the destination, where the files of the frameworks shall be saved to
		String path = System.getProperty("user.home")
				+ File.separator + "experiments"
				+ File.separator + "tweetyProject"
				+ File.separator + "eQExperiment_Strong"
				+ File.separator + semanticsUsed.abbreviation();

		// ================================== optional configuration ==========================================

		//[STEP] 6: you can choose to create a new generator, for each new pair of frameworks
		// methods below are called once, before trying to generate such a pair
		Function<String, Iterator<DungTheory>> getGen1 = new Function<>() {

			@Override
			public Iterator<DungTheory> apply(String t) {
				return fstFrameworkGen;
			}
		};
		Function<String, Iterator<DungTheory>> getGen2 = new Function<>() {

			@Override
			public Iterator<DungTheory> apply(String t) {
				parameters.numberOfArguments = fstFrameworkGen.getCurrentSize() * factorNumArgsGen1ToGen2;
				return scndFrameworkGen;
			}
		};

		// ================================== configuration completed =======================================================
		EquivalenceCompExFinderExample.createDir(path);
		ZoneId z = ZoneId.of( "Europe/Berlin" );
		ZonedDateTime now = ZonedDateTime.now( z );
		String idSeries = "" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + now.getHour() + now.getMinute();
		int indexInSeries = 0;
		LinkedHashMap<DungTheory, DungTheory> examplePair = null;
		
		do{
			try {
				examplePair = EquivalenceCompExFinderExample.generateOnePair(
						maxNumberTryFindExample,
						semanticsUsed, getGen1, getGen2, equivalence1, equivalence2, decisionMaker,
						path, idSeries, indexInSeries, z);
				indexInSeries++;
				/*SerialisationAnalysisPlotter.plotAnalyses(
				new Semantics[] {semanticsUsed},
				new DungTheory[] {examplePair.keySet().toArray(new DungTheory[1])[0], examplePair.values().toArray(new DungTheory[1])[0]},
				"Example_",
				2000, 1000);
				 */
			} catch (NoExampleFoundException e) {
				//System.out.println("No Examples found.");
			}
		}while( maxNumArguments == 0 || examplePair.keySet().toArray(new DungTheory[1])[0].getNodes().size() < maxNumArguments);
		
		System.out.println("Finished processing for semantics: " + semanticsUsed.abbreviation());
	}

	/*
	 * Creates a new directory iff path described does not exist
	 */
	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}

	/*
	 * Generates a pair of exemplary frameworks
	 */
	private static LinkedHashMap<DungTheory, DungTheory> generateOnePair(
			int maxNumberTryFindExample,
			Semantics semanticsUsed,
			Function<String, Iterator<DungTheory>> getGen1,
			Function<String, Iterator<DungTheory>> getGen2,
			Equivalence<DungTheory> equivalence1,
			Equivalence<DungTheory> equivalence2,
			DecisionMaker decisionMaker,
			String path,
			String idSeries,
			int indexInSeries,
			ZoneId currentZone) throws NoExampleFoundException {
		

		EquivalenceCompExFinder exampleFinder = new EquivalenceCompExFinder(
				equivalence1,
				equivalence2,
				decisionMaker);
		LinkedHashMap<DungTheory, DungTheory> output = null;
		//System.out.println("Processing started: No.: " + idSeries + "_"+ indexInSeries + " " + semanticsUsed.abbreviation());

		ZonedDateTime timeStampProcessStart = ZonedDateTime.now( currentZone );
		output = exampleFinder.
				findExample(
						maxNumberTryFindExample,
						getGen1.apply(""),
						getGen2.apply(""));
		ZonedDateTime timeStampProcessFinished = ZonedDateTime.now( currentZone );

		for (DungTheory frameworkKey : output.keySet()) {
			boolean isEQ1 = equivalence1.isEquivalent(frameworkKey, output.get(frameworkKey));
			boolean isEQ2 = equivalence2.isEquivalent(frameworkKey, output.get(frameworkKey));
			DungTheory secondExample = output.get(frameworkKey);
			EquivalenceCompExFinderExample.writeFile(
					path, frameworkKey, idSeries, indexInSeries, 0,
					semanticsUsed, equivalence1.getDescription(), equivalence2.getDescription(), isEQ1, isEQ2,
					frameworkKey.getNumberOfNodes(), secondExample.getNumberOfNodes(), timeStampProcessStart, timeStampProcessFinished);

			EquivalenceCompExFinderExample.writeFile(
					path, secondExample, idSeries, indexInSeries, 1,
					semanticsUsed, equivalence1.getDescription(), equivalence2.getDescription(), isEQ1, isEQ2,
					frameworkKey.getNumberOfNodes(), secondExample.getNumberOfNodes(), timeStampProcessStart, timeStampProcessFinished);
		}
		//System.out.println("Processing finished: No.: " + idSeries + "_"+ indexInSeries + " " + semanticsUsed.abbreviation());

		return output;
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
				out.println("// " + comment);
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
			int numArgumentFramework1,
			int numArgumentFramework2,
			ZonedDateTime dateTimeProcessStart,
			ZonedDateTime dateTimeProcessFinished) {

		var writer = new ApxWriter();
		File file = new File(path + File.separator +
				equi1Name + "_" + isEQ1 + "_" +
				equi2Name + "_" + isEQ2 + "_" +
				semanticsUsed.abbreviation() + "_" +
				numArgumentFramework1 + "_" + numArgumentFramework2 + "_" +
				idSeries + "_" + indexInSeries + "_" + indexInPair + "_" +
				".apx");

		var addInfo = new String[8];
		addInfo[0] = "Date of creation:";
		addInfo[1] = dateTimeProcessStart.getYear() + "_" +
				dateTimeProcessStart.getMonthValue() + "_" +
				dateTimeProcessStart.getDayOfMonth();
		addInfo[2] = "Time-Stamp Starting Generation-Process:";
		addInfo[3] = dateTimeProcessStart.getYear() + "_" +
				dateTimeProcessStart.getMonthValue() + "_" +
				dateTimeProcessStart.getDayOfMonth() + "_" +
				dateTimeProcessStart.getHour() + "h" +
				dateTimeProcessStart.getMinute() + "m" +
				dateTimeProcessStart.getSecond() + "s" +
				dateTimeProcessStart.getNano();
		addInfo[4] = "Time-Stamp Finishing Generation-Process:";
		addInfo[5] = dateTimeProcessFinished.getYear() + "_" +
				dateTimeProcessFinished.getMonthValue() + "_" +
				dateTimeProcessFinished.getDayOfMonth() + "_" +
				dateTimeProcessFinished.getHour() + "h" +
				dateTimeProcessFinished.getMinute() + "m" +
				dateTimeProcessFinished.getSecond() + "s" +
				dateTimeProcessFinished.getNano();
		addInfo[6] = "Processing Time:";
		Duration procTime = Duration.between(dateTimeProcessStart, dateTimeProcessFinished).abs();
		addInfo[7] = procTime.toString();	

		try {
			writer.write(framework, file);
			EquivalenceCompExFinderExample.writeComment(file, addInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
