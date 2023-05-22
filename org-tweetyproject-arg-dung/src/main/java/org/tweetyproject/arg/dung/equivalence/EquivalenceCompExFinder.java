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

import org.tweetyproject.arg.dung.serialisibility.plotting.NoExampleFoundException;
import org.tweetyproject.arg.dung.syntax.DungTheory;
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
	private DecisionMaker decisionMaker;

	/**
	 * 
	 * @param equivalence1 Compares if two generated frameworks are equivalent to one another, 
	 * the definition of the equivalence should be different than the one used for equivalence2
	 * @param equivalence2 Compares if two generated frameworks are equivalent to one another, 
	 * the definition of the equivalence should be different than the one used for equivalence1
	 * @param decisionMaker Object, which decides if the resulting equivalences are acceptable
	 */
	public EquivalenceCompExFinder(
			Equivalence<DungTheory> equivalence1,
			Equivalence<DungTheory> equivalence2,
			DecisionMaker decisionMaker) {
		this.equivalence1 = equivalence1;
		this.equivalence2 = equivalence2;
		this.decisionMaker = decisionMaker;
	}

	/**
	 * Generates argumentation frameworks, with the behaviors wrt equivalence as specified.
	 * @param numberOfMaxRandomGenerationTries Maximum Number of iterations trying to generate randomly 
	 * 2 frameworks compliant to the specified conditions. After surpassing this threshold, the second 
	 * framework will be generated on the basis of the first one (if a an equivalent framework was demanded)
	 * @param onlySameNumberOfArguments If TRUE the two generated frameworks will have the same number of arguments. 
	 * If FALSE, the number of arguments may differ from one framework to another.
	 * @param generatorFramework1 Iterator, which generates the first framework
	 * @param generatorFramework2 Iterator, which generates the second framework (Note that the framework will be generated as an 
	 * equivalent theory if wished, in case this generator fails. 
	 * @return Pair of frameworks, which are equivalent to each other as specified in the method call
	 * @throws NoExampleFoundException Thrown if no pair of frameworks (compliant to the equivalent-conditions) could be generated
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<DungTheory,DungTheory> findExample(
			int numberOfMaxRandomGenerationTries,
			boolean onlySameNumberOfArguments,
			Iterator<DungTheory> generatorFramework1,
			Iterator<DungTheory> generatorFramework2) 
					throws NoExampleFoundException{

		var output = new LinkedHashMap<DungTheory,DungTheory>();

		
			var generatedFramework1 = generatorFramework1.next();
			DungTheory generatedFramework2 = generateCompliantFramework(
					numberOfMaxRandomGenerationTries, 
					onlySameNumberOfArguments,
					generatedFramework1, 
					generatorFramework2);

			if(decisionMaker.getShallCriteriaBeTrueA() && generatedFramework2 == null && (equivalence1 instanceof EquivalentTheories<?>)) {
				generatedFramework2 = generateCompliantFramework(
						numberOfMaxRandomGenerationTries, 
						onlySameNumberOfArguments,
						generatedFramework1, 
						((EquivalentTheories<DungTheory>) equivalence1).getEquivalentTheories(generatedFramework1).iterator());
			}
			// single IF-statements, since framework could still be null, after method call
			if(decisionMaker.getShallCriteriaBeTrueB() && generatedFramework2 == null && (equivalence2 instanceof EquivalentTheories<?>)) {
				generatedFramework2 = generateCompliantFramework(
						numberOfMaxRandomGenerationTries, 
						onlySameNumberOfArguments,
						generatedFramework1, 
						((EquivalentTheories<DungTheory>) equivalence2).getEquivalentTheories(generatedFramework1).iterator());
			}					

			if(generatedFramework2 == null)
				// equivalent theory could not be generated
				throw new NoExampleFoundException();

			output.put(generatedFramework1, generatedFramework2);
		

		return output;
	}

	private DungTheory generateCompliantFramework(int numberOfMaxRandomGenerationTries, boolean onlySameNumArgs, DungTheory framework, Iterator<DungTheory> generator) {
		DungTheory output = null;
		for (int j = 0; j < numberOfMaxRandomGenerationTries; j++) {
			try {
				DungTheory temp = generator.next();
				if(onlySameNumArgs && temp.getNumberOfNodes() != framework.getNumberOfNodes()) {
					continue; //skips this try
				}
				if( decisionMaker.decide(equivalence1.isEquivalent(framework, temp), equivalence2.isEquivalent(framework, temp))) {
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
