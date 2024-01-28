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
import org.tweetyproject.arg.dung.serialisability.equivalence.SerialisationEquivalenceByGraph;
import org.tweetyproject.arg.dung.serialisability.equivalence.SerialisationEquivalenceByGraphIso;
import org.tweetyproject.arg.dung.serialisability.equivalence.SerialisationEquivalenceByGraphNaiv;
import org.tweetyproject.arg.dung.serialisability.equivalence.SerialisationEquivalenceByTransitionStateSequence;
import org.tweetyproject.arg.dung.serialisability.equivalence.SerialisationEquivalenceByTransitionStateSequenceNaiv;
import org.tweetyproject.arg.dung.serialisability.equivalence.SerialisationEquivalenceBySequence;
import org.tweetyproject.arg.dung.serialisability.equivalence.SerialisationEquivalenceBySequenceNaiv;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;

/**
 * This class summarizes example showing how to use the {@link EquivalenceClassifier}
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class EquivalenceClassifierExample {

	/**
	 * *description missing*
	 * @param args *description missing*
	 */
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
		String path = args.length > 3 ? args[3] : System.getProperty("user.dir");

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
					break;
				case "reductsequencenaiv":
					tempEQs.add(new SerialisationEquivalenceByTransitionStateSequence(new SerialisationEquivalenceByTransitionStateSequenceNaiv(), 
							SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics)));
					break;
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
				tempEQs.add(new SerialisationEquivalenceByTransitionStateSequence(new SerialisationEquivalenceByTransitionStateSequenceNaiv(), 
						SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics)));
			}
			mapSemEQ.put(semantics, tempEQs);
		}

		var generator = new EnumeratingDungTheoryGenerator();
		generator.setCurrentSize(argNumber);
		var mapSemClassifiers = new HashMap<Semantics, HashSet<EquivalenceClassifier>>();

		// create classifier
		for (var semantic : mapSemEQ.keySet()) {
			var tempClassifiers = new HashSet<EquivalenceClassifier>();
			for (Equivalence<DungTheory> eqElem : mapSemEQ.get(semantic)) {
				String pathSub = path 
						+ File.separator + "EQClasses" 
						+ File.separator + eqElem.getDescription()
						+ File.separator + argNumber + "_Arguments"
						+ File.separator + semantic.abbreviation();
				tempClassifiers.add(new EquivalenceClassifier(eqElem, pathSub));
			}
			mapSemClassifiers.put(semantic, tempClassifiers);
		}

		// generate one framework and calculate equivalence classes
		while(generator.getCurrentSize() < argNumber + 1) {
			var tempFramework = generator.next();
			for (var semantics : mapSemClassifiers.keySet()) {
				var tempClassifiers = mapSemClassifiers.get(semantics);
				for (EquivalenceClassifier classifier : tempClassifiers) {
					if(classifier.examineNewTheory(tempFramework)) {

					}
				}
			}
		}
	}
}
