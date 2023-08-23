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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.function.Function;

import org.tweetyproject.arg.dung.equivalence.DecisionMaker;
import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.equivalence.EquivalenceCompExFinder;
import org.tweetyproject.arg.dung.equivalence.IdentityEquivalence;
import org.tweetyproject.arg.dung.equivalence.StandardEquivalence;
import org.tweetyproject.arg.dung.equivalence.StrongEquivalence;
import org.tweetyproject.arg.dung.equivalence.strong.EquivalenceKernel;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraph;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphIso;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphNaiv;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByTransitionStateSequence;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByTransitionStateSequenceNaiv;
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
	
	private static final String VERSION = "18";

	public static void main(String[] args) {

		int numArgugments = Integer.parseInt(args[0]);
		String eq1Command = args[1];
		String eq2Command = args[2];
		String experimentName = args[3];
		int argNumTries = 0;
		if(args.length > 4) {
			try {
				argNumTries = Integer.parseInt(args[4]);
			}catch(NumberFormatException e) {
				//do nothing
			}
		}
		int numTries = argNumTries > 0 ? argNumTries : 1000;
		
		Semantics[] semanticsUsed1 = args.length > 5 ? parseSemantics(args[5]) : null;
		
		Semantics[] semanticsUsed2;
		if(args.length > 6) {
			semanticsUsed2 = parseSemantics(args[6]);
		}
		else {
			semanticsUsed2 = new Semantics[7];
			semanticsUsed2[0] = Semantics.ADM;
			semanticsUsed2[1] = Semantics.CO;
			semanticsUsed2[2] = Semantics.GR;
			semanticsUsed2[3] = Semantics.PR;
			semanticsUsed2[4] = Semantics.ST;
			semanticsUsed2[5] = Semantics.UC;
			semanticsUsed2[6] = Semantics.SA;
		}
		
		String pathToFolder = args.length > 7 ? args[7] : System.getProperty("user.dir");
		
		for (Semantics semantics : semanticsUsed2) {
			Thread thread = new Thread(semantics.abbreviation()) {
				@Override
				public void run(){
					if(semanticsUsed1 != null) {
						for(Semantics semantics1 : semanticsUsed1) {
							Thread thread2 = new Thread(semantics.abbreviation()) {
								@Override
								public void run(){
									EquivalenceCompExFinderExample.startSeries(
											semantics1, semantics, numTries, numArgugments, eq1Command, eq2Command, pathToFolder, experimentName);
								}
							};
							thread2.start();
						}
					}
					else {
						EquivalenceCompExFinderExample.startSeries(
								semantics, semantics, numTries, numArgugments, eq1Command, eq2Command, pathToFolder, experimentName);
					}
				}
			};
			thread.start();
		}
	}
	
	/*
	 * Starts a new series to generate diff. examples of the same semantics
	 */
	private static void startSeries(
			Semantics semanticsUsed1, 
			Semantics semanticsUsed2, 
			int numTries, 
			int numArguments, 
			String eq1Command, 
			String eq2Command, 
			String pathToFolder,
			String expName) {
		//======================= STEPS to configure experiment ===============================================   <= Configuration starts here
		//[STEP] 1/5: set number for tries to compute 2nd framework.
		//High numbers seem to be preferable, since they increase the chance of getting a compliant framework.
		int maxNumberTryFindExample = numTries;
		// creates only pairs with less arguments than maxNumArguments. If maxNumArguments is 0, then no limit
		int maxNumArguments = numArguments;
		// creates only pairs with this minimal number of arguments
		int minNumArguments = numArguments;
		// if TRUE, then the generated frameworks will both have the same number of arguments
		boolean onlySameNumberOfArguments = true;

		//[STEP] 2/5: set the sort of equivalences, which you would like to investigate
		var equivalence1 = getEquivalence(semanticsUsed1, eq1Command);
		var equivalence2 = getEquivalence(semanticsUsed2, eq2Command);
		
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
				return false;
			}
		};
		var askIf1stFrameworkInteresting = new Function<DungTheory, Boolean>(){
			@Override
			public Boolean apply(DungTheory generatedFramework) {
				return true;
			}
		};
		
		var askIfInterestingPair = new Function<DungTheory[], Boolean>(){
			@Override
			public Boolean apply(DungTheory[] generatedFrameworks) {
				if(generatedFrameworks[0].getNumberOfNodes() == generatedFrameworks[1].getNumberOfNodes()) {
					return true;
				}
				else {
					return false;
				}
			}
		};

		// [STEP] 4/5: set the generators, which will be used to generate the frameworks
		var fstFrameworkGen = new EnumeratingDungTheoryGenerator();
		int factorNumArgsGen1ToGen2 = 1;
		fstFrameworkGen.setCurrentSize(minNumArguments);
//		var parameters = new DungTheoryGenerationParameters();
//		parameters.attackProbability = 0.2;
//		parameters.avoidSelfAttacks = false;
//		var scndFrameworkGen = new DefaultDungTheoryGenerator(parameters);
		
		/*You can choose to create a new generator, for each new pair of frameworks. 
		The methods below are called once, before trying to generate such a pair*/
		var getGen2 = new Function<String, EnumeratingDungTheoryGenerator>() {

			@Override
			public EnumeratingDungTheoryGenerator apply(String t) {
//				parameters.numberOfArguments = fstFrameworkGen.getCurrentSize() * factorNumArgsGen1ToGen2;
//				return scndFrameworkGen;
				var scndGen = new EnumeratingDungTheoryGenerator();
				scndGen.setCurrentSize(fstFrameworkGen.getCurrentSize() * factorNumArgsGen1ToGen2);
				scndGen.setAttacks(fstFrameworkGen.getAttacks());
				return scndGen;
			}
		};
		
		var askGen2Finished = new Function<EnumeratingDungTheoryGenerator, Boolean>() {
			@Override
			public Boolean apply(EnumeratingDungTheoryGenerator gen2) {
				// abort generation if the number of arguments is different 
				//(and hence 2nd enumerationg gen has generated all frameworks with the same number of arguments)
				if(gen2.getCurrentSize() > fstFrameworkGen.getCurrentSize()) {
					return !onlySameNumberOfArguments;
				}else if(gen2.getCurrentSize() < fstFrameworkGen.getCurrentSize()) {
					return !onlySameNumberOfArguments;
				}else {
					return true;
				}
			}
		};

		//[STEP] 5/5: set the destination, where the files of the frameworks shall be saved to
		String path = "";
		path = pathToFolder 
				+ File.separator
				+ expName;
		String semanticsDesc = semanticsUsed1.equals(semanticsUsed2) ?
				semanticsUsed1.abbreviation():
				semanticsUsed1.abbreviation() + "_" + semanticsUsed2.abbreviation();
		path = path + File.separator + semanticsDesc;
		
		// ================================== configuration completed =======================================================
		
		EquivalenceCompExFinderExample.createDir(path);
		var z = ZoneId.of( "Europe/Berlin" );
		var now = ZonedDateTime.now( z );
		String idSeries = "" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + now.getHour() + now.getMinute();
		int indexInSeries = 0;
		//LinkedHashMap<DungTheory, DungTheory> examplePair = null;
		
		int numFstFramesGenerated = 0;
		do{
			try {				
				var framework1 = fstFrameworkGen.next();
				var exampleFinder = new EquivalenceCompExFinder(
						equivalence1,
						equivalence2,
						decisionMaker);
				//System.out.println("Processing started: No.: " + idSeries + "_"+ indexInSeries + " " + semanticsUsed.abbreviation());
				var timeStampProcessStart = ZonedDateTime.now( z );
				var examples = exampleFinder.
						findAllExamples(
								maxNumberTryFindExample,
								framework1,
								getGen2.apply(""),
								askIf1stFrameworkInteresting,
								askIfInterestingPair,
								askGen2Finished);
				var timeStampProcessFinished = ZonedDateTime.now( z );

				indexInSeries = saveExamples(semanticsUsed1, semanticsUsed2, equivalence1, equivalence2, path, idSeries,
						indexInSeries, numFstFramesGenerated, timeStampProcessStart, examples,
						timeStampProcessFinished);
				//System.out.println("Processing finished: No.: " + idSeries + "_"+ indexInSeries + " " + semanticsUsed.abbreviation());
			} catch (NoExampleFoundException e) {
				System.out.println("No Examples found for " + semanticsUsed1.abbreviation() + "/" + semanticsUsed2.abbreviation() + " " + fstFrameworkGen.getCurrentSize() + " Arguments");
			}
			numFstFramesGenerated++;
			PrintLoadingBarConsole(numFstFramesGenerated, numArguments, semanticsUsed1, semanticsUsed2);
		}while( maxNumArguments == 0 || fstFrameworkGen.getCurrentSize() < maxNumArguments + 1);
		
		System.out.println("Finished processing for semantics: " + semanticsUsed1.abbreviation() + "/" + semanticsUsed2.abbreviation());
	}

	public static int saveExamples(
			Semantics semanticsUsed1, 
			Semantics semanticsUsed2,
			Equivalence<DungTheory> equivalence1, 
			Equivalence<DungTheory> equivalence2, 
			String path, 
			String idSeries,
			int indexInSeries, 
			int numFstFramesGenerated, 
			ZonedDateTime timeStampProcessStart,
			LinkedHashMap<DungTheory, HashSet<DungTheory>> examples, 
			ZonedDateTime timeStampProcessFinished) {
		for (DungTheory frameworkKey : examples.keySet()) {
			for(var frameworkValue : examples.get(frameworkKey)) {
				boolean isEQ1 = equivalence1.isEquivalent(frameworkKey, frameworkValue);
				boolean isEQ2 = equivalence2.isEquivalent(frameworkKey, frameworkValue);
				EquivalenceCompExFinderExample.writeFile(
						path, frameworkKey, idSeries, indexInSeries, 0,
						semanticsUsed1, semanticsUsed2, equivalence1.getDescription(), equivalence2.getDescription(), isEQ1, isEQ2,
						frameworkKey.getNumberOfNodes(), frameworkValue.getNumberOfNodes(), timeStampProcessStart, timeStampProcessFinished, numFstFramesGenerated);

				EquivalenceCompExFinderExample.writeFile(
						path, frameworkValue, idSeries, indexInSeries, 1,
						semanticsUsed1, semanticsUsed2, equivalence1.getDescription(), equivalence2.getDescription(), isEQ1, isEQ2,
						frameworkKey.getNumberOfNodes(), frameworkValue.getNumberOfNodes(), timeStampProcessStart, timeStampProcessFinished, numFstFramesGenerated);
				indexInSeries++;
			}
		}
		return indexInSeries;
	}

	private static Equivalence<DungTheory> getEquivalence(Semantics semanticsUsed, String eqCommand) {
		switch(eqCommand.toLowerCase()) {
		case "strong":
			return new StrongEquivalence(EquivalenceKernel.getKernel(semanticsUsed));
		case "standard":
			if(semanticsUsed.equals(Semantics.UC) || semanticsUsed.equals(Semantics.SA)) {
				return new StandardEquivalence(SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
			}
			else {
				return new StandardEquivalence(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semanticsUsed));
			}
		case "sequencenaiv":
			return new SerialisationEquivalenceBySequence(
					new SerialisationEquivalenceBySequenceNaiv(), 
					SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
		case "graphisomorph":
			return new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphIso(), 
					SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
		case "graphnaiv":
			return new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphNaiv(), 
					SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
		case "transeqnaiv":
			return new SerialisationEquivalenceByTransitionStateSequence(new SerialisationEquivalenceByTransitionStateSequenceNaiv(), 
					SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semanticsUsed));
		case "identity":
			return new IdentityEquivalence();
		default:
			throw new IllegalArgumentException("eq1Command is not a known equivalence");
		}
	}

	/*
	 * Creates a new directory iff path described does not exist
	 */
	private static void createDir(String path) {
		var customDir = new File(path);
		customDir.mkdirs();
	}

	/*
	 * Appends a file with a set of comments
	 */
	public static void writeComment(File f, String[] comments) throws IOException {

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
			Semantics semanticsUsed1,
			Semantics semanticsUsed2,
			String equi1Name,
			String equi2Name,
			boolean isEQ1,
			boolean isEQ2,
			int numArgumentFramework1,
			int numArgumentFramework2,
			ZonedDateTime dateTimeProcessStart,
			ZonedDateTime dateTimeProcessFinished,
			int numberFstAFCreated) {

		var writer = new ApxWriter();
		String pathFile = path + File.separator +
				equi1Name + "_" + semanticsUsed1.abbreviation() + "_" + isEQ1 + "_" +
				equi2Name + "_" + semanticsUsed2.abbreviation() + "_" + isEQ2 + "_" +
				numArgumentFramework1 + "_" + numArgumentFramework2 + "_" +
				idSeries + "_" + indexInSeries + "_" + indexInPair + 
				".apx";
		var file = new File(pathFile);

		var addInfo = new String[9];
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
		Duration procTime = Duration.between(dateTimeProcessStart, dateTimeProcessFinished).abs();
		addInfo[6] = "Processing Time: " + procTime.toString();
		addInfo[7] = "Generating Tool: EQExperiment_V" + VERSION;
		double allFrameToCreate = java.lang.Math.pow(java.lang.Math.pow(2,numArgumentFramework1),numArgumentFramework1);
		addInfo[8] = "Generation-Progress: (" 
				+ numberFstAFCreated + "/" 
				+ java.lang.Math.round(allFrameToCreate) + ")";

		try {
			writer.write(framework, file);
			EquivalenceCompExFinderExample.writeComment(file, addInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void PrintLoadingBarConsole(int numberDone, int numArguments, Semantics semanticsUsed1, Semantics semanticsUsed2) {
		double allFrames = java.lang.Math.pow(java.lang.Math.pow(2,numArguments),numArguments);
		//double procentDone = (numberDone * 100) / allFrames;
		System.out.println("(" + numberDone + "/" + allFrames + ") examined " + semanticsUsed1.abbreviation() + "/" + semanticsUsed2.abbreviation());
		/*
		 * System.out.print("["); for (int i = 0; i < 100; i++) { if(procentDone > i) {
		 * System.out.print("█"); }else { System.out.print("|"); } }
		 * 
		 * System.out.println("]");
		 */
	}
	
	private static Semantics[] parseSemantics(String command) {
		int idxComma = command.indexOf(',');
		if(idxComma == -1){
			return new Semantics[] {Semantics.getSemantics(command)};
		}
		else {
			String temp = command;
			var output = new HashSet<Semantics>();
			while(temp.length() > 0) {
				idxComma = temp.indexOf(',');
				if(idxComma == -1) {
					output.add(Semantics.getSemantics(temp));
					break;
				}
				
				output.add(Semantics.getSemantics(temp.substring(0, idxComma)));
				temp = temp.substring(idxComma + 1);
			}
				
			return output.toArray(new Semantics[0]);
		}
	}
	
}
