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
import java.util.HashMap;
import java.util.HashSet;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.equivalence.StandardEquivalence;
import org.tweetyproject.arg.dung.equivalence.StrongEquivalence;
import org.tweetyproject.arg.dung.equivalence.classes.EquivalenceClassifier;
import org.tweetyproject.arg.dung.equivalence.strong.EquivalenceKernel;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraph;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphIso;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceByGraphNaiv;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceBySequence;
import org.tweetyproject.arg.dung.serialisibility.equivalence.SerialisationEquivalenceBySequenceNaiv;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.dung.writer.ApxWriter;

/**
 * This class summarizes example showing how to use the {@link EquivalenceClassifier}
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class EquivalenceClassifierExample {

	public static void main(String[] args) {
		int argNumber = Integer.parseInt(args[0]);
		Semantics[] semanticsUsed;
		if(args.length > 1 && !args[1].isBlank()) {
			semanticsUsed = new Semantics[] {Semantics.getSemantics(args[1])};
		}
		else {
			semanticsUsed = new Semantics[] {Semantics.ADM, Semantics.CO,  Semantics.PR,  Semantics.ST,  Semantics.GR};
		}
	
		var eqcommand = args.length > 2 ? args[2] : "";
		String path = args.length > 3 ? args[3] : "";
		
		var mapSemEQ = new HashMap<Semantics, HashSet<Equivalence<DungTheory>>>();
		
		for (Semantics semantics : semanticsUsed) {
			var tempEQs = new HashSet<Equivalence<DungTheory>>();
			//add all equivalences if no argument was given
			if(!eqcommand.isBlank()) {
				switch(eqcommand.toLowerCase()) {
				case "strong":
					tempEQs.add(new StrongEquivalence(EquivalenceKernel.getKernel(semantics)));
					break;
				case "standard":
					tempEQs.add(new StandardEquivalence(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics)));
					break;
				case "sequencenaiv":
					tempEQs.add(new SerialisationEquivalenceBySequence(
							new SerialisationEquivalenceBySequenceNaiv(), 
							SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics)));
					break;
				case "graphisomorph":
					tempEQs.add(new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphIso(), 
							SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics)));
					break;
				case "graphnaiv":
					tempEQs.add(new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphNaiv(), 
							SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics)));
				default:
					throw new IllegalArgumentException("eqCommand is not a known equivalence");
				}
			}
			else {
				tempEQs.add(new StrongEquivalence(EquivalenceKernel.getKernel(semantics)));
				tempEQs.add(new StandardEquivalence(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics)));
				tempEQs.add(new SerialisationEquivalenceBySequence(
						new SerialisationEquivalenceBySequenceNaiv(), 
						SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics)));
				tempEQs.add(new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphIso(), 
						SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics)));
				tempEQs.add(new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphNaiv(), 
						SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics)));
			}
			mapSemEQ.put(semantics, tempEQs);
		}
		
		saveClasses(mapSemEQ, argNumber, path);
	}

	private static void saveClasses(HashMap<Semantics, HashSet<Equivalence<DungTheory>>> mapSemEQ, int argNumber, String path) {
		var generator = new EnumeratingDungTheoryGenerator();
		generator.setCurrentSize(argNumber);
		var mapSemClassifiers = new HashMap<Semantics, HashSet<EquivalenceClassifier>>();
		
		for (var semantic : mapSemEQ.keySet()) {
			var tempClassifiers = new HashSet<EquivalenceClassifier>();
			for (Equivalence<DungTheory> eqElem : mapSemEQ.get(semantic)) {
				tempClassifiers.add(new EquivalenceClassifier(eqElem));
			}
			mapSemClassifiers.put(semantic, tempClassifiers);
		}
		
		// calculate equivalence classes
		while(generator.getCurrentSize() < argNumber + 1) {
			var tempFramework = generator.next();
			for (var semantics : mapSemClassifiers.keySet()) {
				var tempClassifiers = mapSemClassifiers.get(semantics);
				for (EquivalenceClassifier classifier : tempClassifiers) {
					classifier.examineNewTheory(tempFramework);
				}
			}
		}

		// save classes
		for (var semantics : mapSemClassifiers.keySet()) {
			var tempClassifiers = mapSemClassifiers.get(semantics);
			for (EquivalenceClassifier classifier : tempClassifiers) {
				String pathSub = path.isBlank() ? "" : path + File.separator ;
				pathSub = pathSub + "EQClasses"
						+ File.separator + classifier.getEquivalence().getDescription() 
						+ File.separator + "Arg_"+ argNumber 
						+ File.separator + semantics.abbreviation();
				var folder = new File(pathSub);
				folder.mkdirs();
				var writer = new ApxWriter();
				var classes = classifier.getClasses();
				var addInfo = new String[2];
				addInfo[0] = "Equivalence:" + classifier.getEquivalence().getDescription();
				addInfo[1] = "Semantics:" + semantics.abbreviation();

				for (int i = 0; i < classes.length; i++) {
					var file = new File(pathSub + File.separator + "Class_" + i + ".apx");
					try {
						writer.write(classes[i], file);
						EquivalenceCompExFinderExample.writeComment(file, addInfo);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
