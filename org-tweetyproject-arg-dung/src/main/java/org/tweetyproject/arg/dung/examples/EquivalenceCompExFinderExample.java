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
import org.tweetyproject.arg.dung.equivalence.StandardEquivalence;
import org.tweetyproject.arg.dung.equivalence.StrongEquivalence;
import org.tweetyproject.arg.dung.equivalence.strong.EquivalenceKernel;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraph;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphIso;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphNaiv;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceBySequence;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceBySequenceNaiv;
import org.tweetyproject.arg.dung.serialisibility.plotting.NoExampleFoundException;
import org.tweetyproject.arg.dung.syntax.DungTheory;
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
		int numArgugments = Integer.parseInt(args[0]);
		String eq1Command = args[1];
		String eq2Command = args[2];
		for (Semantics semantics : semanticsUsed) {
			Thread thread = new Thread(semantics.abbreviation()) {
				@Override
				public void run(){
					EquivalenceCompExFinderExample.startSeries(semantics, numArgugments, eq1Command, eq2Command);
				}
			};
			thread.start();
		}
	}
	
	/*
	 * Starts a new series to generate diff. examples of the same semantics
	 */
	private static void startSeries(Semantics semanticsUsed, int numArguments, String eq1Command, String eq2Command) {
		//======================= STEPS to configure experiment ===============================================   <= Configuration starts here
		//[STEP] 1/5: set number for tries to compute 2nd framework.
		//High numbers seem to be preferable, since they increase the chance of getting a compliant framework.
		int maxNumberTryFindExample = 30;
		// creates only pairs with less arguments than maxNumArguments. If maxNumArguments is 0, then no limit
		int maxNumArguments = numArguments;
		// creates only pairs with this minimal number of arguments
		int minNumArgument = numArguments;
		// if TRUE, then the generated frameworks will both have the same number of arguments
		boolean onlySameNumberOfArguments = true;

		//[STEP] 2/5: set the sort of equivalences, which you would like to investigate
		Equivalence<DungTheory> equivalence1;
		Equivalence<DungTheory> equivalence2;
		
		switch(eq1Command.toLowerCase()) {
		case "strong":
			equivalence1 = new StrongEquivalence(EquivalenceKernel.getKernel(semanticsUsed));
			break;
		case "standard":
			equivalence1 = new StandardEquivalence(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semanticsUsed));
			break;
		default:
			throw new IllegalArgumentException("eq1Command(2. argument) is not a known command");
		}
		
		switch(eq2Command.toLowerCase()) {
		case "sequencenaiv":
			equivalence2 = new SerialisationEquivalenceBySequence(
					new SerialisationEquivalenceBySequenceNaiv(), 
					SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
			break;
		case "graphisomorph":
			equivalence2 = new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphIso(), 
					SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
			break;
		case "graphnaiv":
			equivalence2 = new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphNaiv(), 
					SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
		default:
			throw new IllegalArgumentException("eq2Command(3. argument) is not a known command");
		}
		
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
		int factorNumArgsGen1ToGen2 = 1;
//		var parameters = new DungTheoryGenerationParameters();
//		parameters.attackProbability = 0.2;
//		parameters.avoidSelfAttacks = false;
//		var scndFrameworkGen = new DefaultDungTheoryGenerator(parameters);
		/*You can choose to create a new generator, for each new pair of frameworks. 
		The methods below are called once, before trying to generate such a pair*/
		Function<String, Iterator<DungTheory>> getGen1 = new Function<>() {

			@Override
			public Iterator<DungTheory> apply(String t) {
				return fstFrameworkGen;
			}
		};
		Function<String, Iterator<DungTheory>> getGen2 = new Function<>() {

			@Override
			public Iterator<DungTheory> apply(String t) {
//				parameters.numberOfArguments = fstFrameworkGen.getCurrentSize() * factorNumArgsGen1ToGen2;
//				return scndFrameworkGen;
				var scndGen = new EnumeratingDungTheoryGenerator();
				while(scndGen.getCurrentSize() < fstFrameworkGen.getCurrentSize() * factorNumArgsGen1ToGen2) {
					scndGen.next();
				}
				return scndGen;
			}
		};

		//[STEP] 5/5: set the destination, where the files of the frameworks shall be saved to
		String path = System.getProperty("user.home")
				+ File.separator + "experiments"
				+ File.separator + "tweetyProject"
				+ File.separator + "eQExperiment"
				+ File.separator + equivalence1.getDescription() + "_" + equivalence2.getDescription()
				+ File.separator + semanticsUsed.abbreviation();
		// ================================== configuration completed =======================================================
		EquivalenceCompExFinderExample.createDir(path);
		var z = ZoneId.of( "Europe/Berlin" );
		var now = ZonedDateTime.now( z );
		String idSeries = "" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + now.getHour() + now.getMinute();
		int indexInSeries = 0;
		LinkedHashMap<DungTheory, DungTheory> examplePair = null;
		
		while(fstFrameworkGen.getCurrentSize() < minNumArgument) {
			fstFrameworkGen.next();
		}
		
		do{
			try {
				examplePair = EquivalenceCompExFinderExample.generateOnePair(
						maxNumberTryFindExample,
						onlySameNumberOfArguments,
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
		}while( maxNumArguments == 0 || examplePair.keySet().toArray(new DungTheory[1])[0].getNodes().size() < maxNumArguments + 1);
		
		System.out.println("Finished processing for semantics: " + semanticsUsed.abbreviation());
	}

	/*
	 * Creates a new directory iff path described does not exist
	 */
	private static void createDir(String path) {
		var customDir = new File(path);
		customDir.mkdirs();
	}

	/*
	 * Generates a pair of exemplary frameworks
	 */
	private static LinkedHashMap<DungTheory, DungTheory> generateOnePair(
			int maxNumberTryFindExample,
			boolean onlySameNumberOfArguments,
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
		

		var exampleFinder = new EquivalenceCompExFinder(
				equivalence1,
				equivalence2,
				decisionMaker);
		//System.out.println("Processing started: No.: " + idSeries + "_"+ indexInSeries + " " + semanticsUsed.abbreviation());
		var timeStampProcessStart = ZonedDateTime.now( currentZone );
		var output = exampleFinder.
				findExample(
						maxNumberTryFindExample,
						onlySameNumberOfArguments,
						getGen1.apply(""),
						getGen2.apply(""));
		var timeStampProcessFinished = ZonedDateTime.now( currentZone );

		for (DungTheory frameworkKey : output.keySet()) {
			boolean isEQ1 = equivalence1.isEquivalent(frameworkKey, output.get(frameworkKey));
			boolean isEQ2 = equivalence2.isEquivalent(frameworkKey, output.get(frameworkKey));
			var secondExample = output.get(frameworkKey);
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
		var file = new File(path + File.separator +
				equi1Name + "_" + isEQ1 + "_" +
				equi2Name + "_" + isEQ2 + "_" +
				semanticsUsed.abbreviation() + "_" +
				numArgumentFramework1 + "_" + numArgumentFramework2 + "_" +
				idSeries + "_" + indexInSeries + "_" + indexInPair + 
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
