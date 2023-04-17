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
package org.tweetyproject.arg.dung.serialisibility.equivalence;

import java.util.LinkedHashMap;

import org.tweetyproject.arg.dung.equivalence.ITheoryComparator;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.NoExampleFoundException;
import org.tweetyproject.arg.dung.serialisibility.SerialisableExtensionReasonerWithAnalysis;
import org.tweetyproject.arg.dung.serialisibility.graph.SerialisationGraph;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;

/**
 * This class represents an analysis, whether strong equivalence correlates with the equivalence of serializing graphs. 
 *
 * @see Matthias Thimm. Revisiting initial sets in abstract argumentation.
 *      Argument & Computation 13 (2022) 325–360 DOI 10.3233/AAC-210018
 * @see Lars Bengel and Matthias Thimm. Serialisable Semantics for Abstract
 *      Argumentation. Computational Models of Argument (2022) DOI:
 *      10.3233/FAIA220143
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class TheoryEqToSerialEqExFinder {

	private ITheoryComparator frameworkComparator;
	private ISerializingComparator analysisComparator;
	private DefaultDungTheoryGenerator generator;
	private DungTheoryGenerationParameters parameters;
	private int maxNumberTryFindExample;
	
	/**
	 * 
	 * @param frameworkComparator Compares if two generated frameworks are equivalent to one another
	 * @param analysisComparator Compare if two serializing analysis are equivalent to one another
	 * @param numberOfArguments {@link DungTheoryGenerationParameters#numberOfArguments}
	 * @param attackProbability {@link DungTheoryGenerationParameters#attackProbability}
	 * @param avoidSelfAttacks {@link DungTheoryGenerationParameters#avoidSelfAttacks}
	 * @param maxNumberTryFindExample Maximal number of iterations, the generator does in order to find a suitable example
	 */
	public TheoryEqToSerialEqExFinder(
			ITheoryComparator frameworkComparator,
			ISerializingComparator analysisComparator,			
			int numberOfArguments, 
			double attackProbability, 
			boolean avoidSelfAttacks,
			int maxNumberTryFindExample) {
		this.frameworkComparator = frameworkComparator;
		this.analysisComparator = analysisComparator;
		this.maxNumberTryFindExample = maxNumberTryFindExample;
		
		this.parameters = new DungTheoryGenerationParameters();
		this.parameters.numberOfArguments = numberOfArguments;
		this.parameters.attackProbability = attackProbability;
		this.parameters.avoidSelfAttacks = avoidSelfAttacks;
		
		this.generator = new DefaultDungTheoryGenerator(this.parameters);
	}

	/**
	 * This method tries to generate 2 frameworks, which are equivalent or not to another as specified by the parameter. 
	 * Moreover the serializing analyses of there frameworks wrt to the specified semantics have to be equivalent or not 
	 * to another, as specified with the parameter.
	 * 
	 * @param semanticsUsed Semantics used to generate an extension, by serializing sets of arguments.
	 * @param theoryBeEqual If TRUE, frameworks returned are equivalent wrt the comparator of the object.
	 * @param serialGraphBeEqual If TRUE, serializing analyses are equivalent wrt the comparator of the object.
	 * @return Map of the generated frameworks and their associated analyses.
	 * @throws NoExampleFoundException Thrown if the object couldn't create examples compliant to the conditions, within the specified number of maximum iterations.
	 */
	public LinkedHashMap<DungTheory, SerialisationGraph[]> findExamples(
			Semantics semanticsUsed,
			boolean theoryBeEqual, 
			boolean serialGraphBeEqual) throws NoExampleFoundException {

		for (int i = 0; i < this.maxNumberTryFindExample; i++) {
			var generatedFramework1 = this.generator.next();
			var generatedFramework2 = this.generator.next();

			if(frameworkComparator.isEquivalent(generatedFramework1, generatedFramework2) == theoryBeEqual) {
				SerialisationGraph analysis1 = SerialisableExtensionReasonerWithAnalysis
						.getSerialisableReasonerForSemantics(semanticsUsed).getModelsGraph(generatedFramework1);
				SerialisationGraph analysis2 = SerialisableExtensionReasonerWithAnalysis
						.getSerialisableReasonerForSemantics(semanticsUsed).getModelsGraph(generatedFramework2);

				if(analysisComparator.isEquivalent(analysis1, analysis2) == serialGraphBeEqual) {
					var output = new LinkedHashMap<DungTheory, SerialisationGraph[]>();
					output.put(generatedFramework1, new SerialisationGraph[] {analysis1});
					output.put(generatedFramework2, new SerialisationGraph[] {analysis2});
					return output;
				}
			}
		}

		throw new NoExampleFoundException();
	}
}
