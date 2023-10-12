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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.tweetyproject.arg.dung.serialisibility.plotting.NoExampleFoundException;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
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
	 * Generates all possible examples of pairs of frameworks, containing the specified framework. 
	 * These pairs show an behavior wrt. equivalence as specified in the decisionMaker.
	 * @param numberOfMaxRandomGenerationTries Maximum Number of iterations trying to generate randomly 
	 * 2 frameworks compliant to the specified conditions. After surpassing this threshold, the second 
	 * framework will be generated on the basis of the first one (if a an equivalent framework was demanded)
	 * @param framework1  Framework, for which a second partner shall be generated.
	 * @param generatorFramework2 Iterator, which generates the second framework (Note that the framework will be generated as an 
	 * equivalent theory if wished, in case this generator fails. 
	 * @param askIf1stFrameInteresting Function which checks if the generated 1st framework is interesting enough to try to generate a partner framework
	 * @param askIfInterestingPair Function which checks, if the generated pair is interesting enough to examine its equivalence
	 * @param askContinueGenerate2nd Function which checks, if the generation of 2nd frameworks in order to form a valide pair, 
	 * should be continued or not, based on the properties of the last generated pair
	 * @return Pair of frameworks, which are equivalent to each other as specified in the method call
	 * @throws NoExampleFoundException Thrown if no pair of frameworks (compliant to the equivalent-conditions) could be generated
	 */
	public LinkedHashMap<DungTheory,HashSet<DungTheory>> findAllExamples(
			int numberOfMaxRandomGenerationTries,
			DungTheory framework1,
			EnumeratingDungTheoryGenerator generatorFramework2,
			Function<DungTheory, Boolean> askIf1stFrameInteresting,
			Function<DungTheory[], Boolean> askIfInterestingPair,
			Function<EnumeratingDungTheoryGenerator, Boolean> askGen2Finished) 
					throws NoExampleFoundException{
		
		var output = new LinkedHashMap<DungTheory,HashSet<DungTheory>>();
		
		DungTheory tempKey = null;
		DungTheory tempValue = null;
		try {
			do{
				var example = findExample(numberOfMaxRandomGenerationTries, 
						framework1, generatorFramework2, 
						askIf1stFrameInteresting, askIfInterestingPair, 
						t -> askGen2Finished.apply(generatorFramework2) );
				tempKey = example.keySet().iterator().next();
				tempValue = example.get(tempKey);
				
				if(!output.containsKey(tempKey)) {
					output.put(tempKey, new HashSet<DungTheory>());
				}
				output.get(tempKey).add(tempValue);
				
			}while(askGen2Finished.apply(generatorFramework2));
		} catch(NoExampleFoundException e) {
			if(output.size() == 0) {
				// only throw exception if no prior examples have been found
				throw new NoExampleFoundException();
			}
		}
		
		return output;
	}

	/**
	 * Generates a second argumentation framework, so that the pair has the specified behavior.
	 * @param numberOfMaxRandomGenerationTries Maximum Number of iterations trying to generate randomly 
	 * 2 frameworks compliant to the specified conditions. After surpassing this threshold, the second 
	 * framework will be generated on the basis of the first one (if a an equivalent framework was demanded)
	 * @param framework1 Framework, for which a second partner shall be generated.
	 * @param generatorFramework2 Iterator, which generates the second framework (Note that the framework will be generated as an 
	 * equivalent theory if wished, in case this generator fails. 
	 * @param askIf1stFrameInteresting Function which checks if the generated 1st framework is interesting enough to try to generate a partner framework
	 * @param askIfInterestingPair Function which checks, if the generated pair is interesting enough to examine its equivalence
	 * @param askContinueGenerate2nd Function which checks, if the generation of 2nd frameworks in order to form a valide pair, 
	 * should be continued or not, based on the properties of the last generated pair
	 * @return Pair of frameworks, which are equivalent to each other as specified in the method call
	 * @throws NoExampleFoundException Thrown if no pair of frameworks (compliant to the equivalent-conditions) could be generated
	 */
	@SuppressWarnings("unchecked")
	public LinkedHashMap<DungTheory,DungTheory> findExample(
			int numberOfMaxRandomGenerationTries,
			DungTheory framework1,
			Iterator<DungTheory> generatorFramework2,
			Function<DungTheory, Boolean> askIf1stFrameInteresting,
			Function<DungTheory[], Boolean> askIfInterestingPair,
			Function<String, Boolean> askContinueGenerate2nd) 
					throws NoExampleFoundException{

		var output = new LinkedHashMap<DungTheory,DungTheory>();

		//System.out.println("Generated:    1st AF       " + generatedFramework1.getNumberOfNodes() + " Arguments / " + generatedFramework1.getEdges().size() + " Attacks");
		
		if(!askIf1stFrameInteresting.apply(framework1)) {
			throw new NoExampleFoundException();
		}
		
		DungTheory generatedFramework2 = generateCompliantFramework(
				numberOfMaxRandomGenerationTries, 
				framework1, 
				generatorFramework2,
				askIfInterestingPair,
				t -> askContinueGenerate2nd.apply(""));

		var alwaysTrue = new Function<String, Boolean>(){

			@Override
			public Boolean apply(String t) {
				return true;
			}};
		
		if(decisionMaker.getShallCriteriaBeTrueA() && generatedFramework2 == null && (equivalence1 instanceof EquivalentTheories<?>)) {
			generatedFramework2 = generateCompliantFramework(
					numberOfMaxRandomGenerationTries, 
					framework1, 
					((EquivalentTheories<DungTheory>) equivalence1).getEquivalentTheories(framework1).iterator(),
					askIfInterestingPair,
					alwaysTrue);
		}
		// single IF-statements, since framework could still be null, after method call
		if(decisionMaker.getShallCriteriaBeTrueB() && generatedFramework2 == null && (equivalence2 instanceof EquivalentTheories<?>)) {
			generatedFramework2 = generateCompliantFramework(
					numberOfMaxRandomGenerationTries, 
					framework1, 
					((EquivalentTheories<DungTheory>) equivalence2).getEquivalentTheories(framework1).iterator(),
					askIfInterestingPair,
					alwaysTrue);
		}					

		if(generatedFramework2 == null) {
			// equivalent theory could not be generated
			//System.out.println("No 2nd AF found");
			throw new NoExampleFoundException();
		}

		output.put(framework1, generatedFramework2);


		return output;
	}

	private DungTheory generateCompliantFramework(
			int numberOfMaxRandomGenerationTries,
			DungTheory framework, 
			Iterator<DungTheory> generator,
			Function<DungTheory[], Boolean> askIfInterestingPair,
			Function<String, Boolean> askContinueGenerate2nd) {
		DungTheory output = null;
		try {
			for (int j = 0; j < numberOfMaxRandomGenerationTries; j++) {
				DungTheory temp = generator.next();

				if(askIfInterestingPair.apply(new DungTheory[] {framework, temp})) {
					if( decisionMaker.decide(equivalence1.isEquivalent(framework, temp), equivalence2.isEquivalent(framework, temp))) {
						output = temp;
						//System.out.println("Generated:    2nd AF       " + output.getNumberOfNodes() + " Arguments / " + output.getEdges().size() + " Attacks");
						break; // stops generation of a framework
					}
				}

				// ask if it should be continued to try to generate a 2nd framework
				if(askContinueGenerate2nd.apply("")) 
				{
					continue;
				}else{					
					break;
				}
			}
			if(generator.hasNext()) {
				//System.out.println("DEBUG: could be more tries");
			}
		}catch(NoSuchElementException e) {
			// nothing needed to do, iterator has just nothing to create
		}

		return output;
	}
}
