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
package org.tweetyproject.arg.dung.equivalence;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;

/**
 * This class represents an example generator. Objects of this class generate argumentation frameworks, which comply
 * conditions regarding their state of equivalence to each other. The conditions are expressed by different 
 * definitions of equivalence between the two frameworks and whether the frameworks have to be equivalent or must not.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class EquivalenceCompExFinder {

	private Equivalence<DungTheory> equivalence1;
	private Equivalence<DungTheory> equivalence2;
	private DefaultDungTheoryGenerator generator;
	private DungTheoryGenerationParameters parameters;

	/**
	 * 
	 * @param equivalence1 Compares if two generated frameworks are equivalent to one another, 
	 * the definition of the equivalence should be different than the one used for equivalence2
	 * @param equivalence2 Compares if two generated frameworks are equivalent to one another, 
	 * the definition of the equivalence should be different than the one used for equivalence1
	 * @param numberOfArguments {@link DungTheoryGenerationParameters#numberOfArguments}
	 * @param attackProbability {@link DungTheoryGenerationParameters#attackProbability}
	 * @param avoidSelfAttacks {@link DungTheoryGenerationParameters#avoidSelfAttacks}
	 * @param maxNumberTryFindExample Maximal number of iterations, the generator does in order to find a suitable example
	 */
	public EquivalenceCompExFinder(
			Equivalence<DungTheory> equivalence1,
			Equivalence<DungTheory> equivalence2,			
			int numberOfArguments, 
			double attackProbability, 
			boolean avoidSelfAttacks) {
		this.equivalence1 = equivalence1;
		this.equivalence2 = equivalence2;

		this.parameters = new DungTheoryGenerationParameters();
		this.parameters.numberOfArguments = numberOfArguments;
		this.parameters.attackProbability = attackProbability;
		this.parameters.avoidSelfAttacks = avoidSelfAttacks;

		this.generator = new DefaultDungTheoryGenerator(this.parameters);
	}

	/**
	 * Generates argumentation frameworks, with the behaviors wrt equivalence as specified.
	 * @param equivalence1IsTRUE If TRUE, then the two generated argumentation frameworks are equivalent 
	 * wrt the definition of equivalence1. If FALSE both framewokrs are not equivalent wrt this definition.
	 * @param equivalence2IsTRUE If TRUE, then the two generated argumentation frameworks are equivalent 
	 * wrt the definition of equivalence2. If FALSE both framewokrs are not equivalent wrt this definition.
	 * @param numberOfMaxRandomGenerationTries Maximum Number of iterations trying to generate randomly 
	 * 2 frameworks compliant to the specified conditions. After surpassing this threshold, the second 
	 * framework will be generated on the basis of the first one (if a an equivalent framework was demanded)
	 * @param numberOfMaxIterations Maximum number of iterations and thus possible pairs of examples. In each 
	 * iteration it will be tried to generate a pair of frameworks. Thus if every iteration is successful, 
	 * this parameter defines the maximum number of pairs returned by this method.
	 * @return Pair of frameworks, which are equivalent to each other as specified in the method call
	 */
	public LinkedHashMap<DungTheory,DungTheory> findExamples(
			boolean equivalence1IsTRUE, 
			boolean equivalence2IsTRUE,
			int numberOfMaxRandomGenerationTries,
			int numberOfMaxIterations){

		var output = new LinkedHashMap<DungTheory,DungTheory>();

		for (int i = 0; i < numberOfMaxIterations; i++) {
			var generatedFramework1 = this.generator.next();
			DungTheory generatedFramework2 = generateCompliantFramework(
					equivalence1IsTRUE, 
					equivalence2IsTRUE,
					numberOfMaxRandomGenerationTries, 
					generatedFramework1, 
					this.generator);

			if(equivalence1IsTRUE && generatedFramework2 == null && equivalence1.getEquivalentTheories(generatedFramework1) != null) {
				generatedFramework2 = generateCompliantFramework(
						equivalence1IsTRUE, 
						equivalence2IsTRUE,
						numberOfMaxRandomGenerationTries, 
						generatedFramework1, 
						equivalence1.getEquivalentTheories(generatedFramework1).iterator());
			}
			// single IF-statements, since framework could still be null, after method call
			if(equivalence2IsTRUE && generatedFramework2 == null && equivalence2.getEquivalentTheories(generatedFramework1) != null) {
				generatedFramework2 = generateCompliantFramework(
						equivalence1IsTRUE, 
						equivalence2IsTRUE,
						numberOfMaxRandomGenerationTries, 
						generatedFramework1, 
						equivalence2.getEquivalentTheories(generatedFramework1).iterator());
			}					

			if(generatedFramework2 == null)
				// equivalent theory could not be generated
				continue; // skips this iteration since no pair could be generated

			output.put(generatedFramework1, generatedFramework2);
		}

		return output;
	}

	private DungTheory generateCompliantFramework(boolean equivalence1IsTRUE, boolean equivalence2IsTRUE,
			int numberOfMaxRandomGenerationTries, DungTheory framework, Iterator<DungTheory> generator) {
		DungTheory output = null;
		for (int j = 0; j < numberOfMaxRandomGenerationTries; j++) {
			try {
				DungTheory temp = generator.next();
				if(
						equivalence1.isEquivalent(framework, temp) == equivalence1IsTRUE &&
						equivalence2.isEquivalent(framework, temp) == equivalence2IsTRUE
						) {
					output = temp;
					break; // stops generation of a framework
				}
			}catch(NoSuchElementException e) {
				// nothing needed to do, iterator has just nothing to create
			}
		}

		return output;
	}
}
