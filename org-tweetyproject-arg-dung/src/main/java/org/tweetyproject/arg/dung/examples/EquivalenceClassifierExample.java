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
		
		for (Semantics singleSemantics : semanticsUsed) {
			
			var equivalence = new HashSet<Equivalence<DungTheory>>();
			//add all equivalences if no argument was given
			if(!eqcommand.isBlank()) {
				switch(eqcommand.toLowerCase()) {
				case "strong":
					equivalence.add(new StrongEquivalence(EquivalenceKernel.getKernel(singleSemantics)));
					break;
				case "standard":
					equivalence.add(new StandardEquivalence(AbstractExtensionReasoner.getSimpleReasonerForSemantics(singleSemantics)));
					break;
				case "sequencenaiv":
					equivalence.add(new SerialisationEquivalenceBySequence(
							new SerialisationEquivalenceBySequenceNaiv(), 
							SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(singleSemantics)));
					break;
				case "graphisomorph":
					equivalence.add(new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphIso(), 
							SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(singleSemantics)));
					break;
				case "graphnaiv":
					equivalence.add(new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphNaiv(), 
							SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(singleSemantics)));
				default:
					throw new IllegalArgumentException("eqCommand is not a known equivalence");
				}
			}
			else {
				equivalence.add(new StrongEquivalence(EquivalenceKernel.getKernel(singleSemantics)));
				equivalence.add(new StandardEquivalence(AbstractExtensionReasoner.getSimpleReasonerForSemantics(singleSemantics)));
				equivalence.add(new SerialisationEquivalenceBySequence(
						new SerialisationEquivalenceBySequenceNaiv(), 
						SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(singleSemantics)));
				equivalence.add(new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphIso(), 
						SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(singleSemantics)));
				equivalence.add(new SerialisationEquivalenceByGraph(new SerialisationEquivalenceByGraphNaiv(), 
						SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(singleSemantics)));
			}
			
			for (Equivalence<DungTheory> singleEQ : equivalence) {
				examineSemantics(singleSemantics, singleEQ, argNumber, path);
			}
		}
	}

	private static void examineSemantics(Semantics singleSemantics, Equivalence<DungTheory> equivalence, int argNumber, String path) {
		var generator = new EnumeratingDungTheoryGenerator();
		generator.setCurrentSize(argNumber);
		var classifier = new EquivalenceClassifier(equivalence);

		// calculate equivalence classes
		while(generator.getCurrentSize() < argNumber + 1) {
			classifier.examineNewTheory(generator.next());
		}

		// save classes
		String pathSub = path.isBlank() ? "" : path + File.separator ;
		pathSub = pathSub + "EQClasses"
				+ File.separator + equivalence.getDescription() 
				+ File.separator + "Arg_"+ argNumber 
				+ File.separator + singleSemantics.abbreviation();
		var folder = new File(pathSub);
		folder.mkdirs();
		var writer = new ApxWriter();
		var classes = classifier.getClasses();
		var addInfo = new String[2];
		addInfo[0] = "Equivalence:" + equivalence.getDescription();
		addInfo[1] = "Semantics:" + singleSemantics.abbreviation();

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
