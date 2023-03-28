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
package org.tweetyproject.arg.dung.serialisibility;

import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;

/**
 * This class represents a generator for exemplary frameworks with serialisable extensions.
 *
 * @see DefaultDungTheoryGenerator
 * @see org.tweetyproject.arg.dung.learning.ExampleFinder
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisabilityExampleFinder {

	private static final int DEFAULT_NUMBER_OF_ARGUMENTS = 5;
	private static final double DEFAULT_ATTACK_PROBABILITY = 0.5;

	private DefaultDungTheoryGenerator generator;
	private DungTheoryGenerationParameters parameters;

	/**
	 *
	 * @param avoidSelfAttacks {@link DungTheoryGenerationParameters#avoidSelfAttacks}
	 */
	public SerialisabilityExampleFinder(boolean avoidSelfAttacks) {
		this(SerialisabilityExampleFinder.DEFAULT_NUMBER_OF_ARGUMENTS, SerialisabilityExampleFinder.DEFAULT_ATTACK_PROBABILITY, avoidSelfAttacks);
	}

	/**
	 *
	 * @param numberOfArguments {@link DungTheoryGenerationParameters#numberOfArguments}
	 * @param avoidSelfAttacks {@link DungTheoryGenerationParameters#avoidSelfAttacks}
	 */
	public SerialisabilityExampleFinder(int numberOfArguments, boolean avoidSelfAttacks) {
		this(numberOfArguments, SerialisabilityExampleFinder.DEFAULT_ATTACK_PROBABILITY, avoidSelfAttacks);
	}

	/**
	 *
	 * @param numberOfArguments {@link DungTheoryGenerationParameters#numberOfArguments}
	 * @param attackProbability {@link DungTheoryGenerationParameters#attackProbability}
	 * @param avoidSelfAttacks {@link DungTheoryGenerationParameters#avoidSelfAttacks}
	 */
	public SerialisabilityExampleFinder(int numberOfArguments, double attackProbability, boolean avoidSelfAttacks) {
		this.parameters = new DungTheoryGenerationParameters();
		this.changeParameterNumberOfArguments(numberOfArguments);
		this.changeParameterAttackProbability(attackProbability);
		this.changeParameterSelfAttacks(avoidSelfAttacks);

		this.generator = new DefaultDungTheoryGenerator(this.parameters);
	}

	/**
	 * Changes the Parameter to generate exemplary serializability analyses.
	 * @param attackProbability {@link DungTheoryGenerationParameters#attackProbability}
	 */
	public void changeParameterAttackProbability(double attackProbability) {
		this.parameters.attackProbability = attackProbability;
	}

	/**
	 * Changes the Parameter to generate exemplary serializability analyses.
	 * @param numberOfArguments {@link DungTheoryGenerationParameters#numberOfArguments}
	 */
	public void changeParameterNumberOfArguments(int numberOfArguments) {
		this.parameters.numberOfArguments = numberOfArguments;
	}

	/**
	 * Changes the Parameter to generate exemplary serializability analyses.
	 * @param seed {@link org.tweetyproject.argumentation.util.DungTheoryGenerator#setSeed}
	 */
	public void changeParameterSeed(long seed) {
		this.generator.setSeed(seed);
	}

	/**
	 * Changes the Parameter to generate exemplary serializability analyses.
	 * @param avoidSelfAttacks {@link DungTheoryGenerationParameters#avoidSelfAttacks}
	 */
	public void changeParameterSelfAttacks(boolean avoidSelfAttacks) {
		this.parameters.avoidSelfAttacks = avoidSelfAttacks;
	}

	/**
	 * Creates an exemplary serializability analysis.
	 *
	 * @param semanticsForSerializing Semantics of the extensions created during the serializing process, which will be analyzed.
	 * @return Analysis result of a randomly generated exemplary problem.
	 */
	public ContainerTransitionStateAnalysis findExample(Semantics semanticsForSerializing) {
		return SerialisableExtensionReasonerWithAnalysis.getSerialisableReasonerForSemantics(semanticsForSerializing).getModelsWithAnalysis(this.generator.next());
	}

	/**
	 * Creates an exemplary serializability analysis with the specified number of arguments in the generated framework.
	 *
	 * @param semanticsForSerializing Semantics of the extensions created during the serializing process, which will be analyzed.
	 * @param numberOfArguments Number of Arguments of the framework, which will be generated as an example.
	 * @return Analysis result of a randomly generated exemplary argumentation framework.
	 */
	public ContainerTransitionStateAnalysis findExample(Semantics semanticsForSerializing, int numberOfArguments) {
		this.changeParameterNumberOfArguments(numberOfArguments);
		return this.findExample(semanticsForSerializing);
	}

	/**
	 * Creates exemplary serializability analyses.
	 *
	 * @param semanticsForSerializing Semantics of the extensions created during the serializing process, which will be analyzed.
	 * @param numberOfExamples Number of examples generated.
	 * @return Array of analysis results, analyzing each a different randomly generated exemplary argumentation framework.
	 */
	public ContainerTransitionStateAnalysis[] findExampleArray(Semantics semanticsForSerializing, int numberOfExamples) {
		ContainerTransitionStateAnalysis[] results = new ContainerTransitionStateAnalysis[numberOfExamples];
		for (int i = 0; i < results.length; i++) {
			results[i] = this.findExample(semanticsForSerializing);
		}
		return results;
	}

	/**
	 * Creates exemplary serializability analyses with a specified number of arguments in the generated frameworks
	 *
	 * @param semanticsForSerializing Semantics of the extensions created during the serializing process, which will be analyzed.
	 * @param numberOfExamples Number of examples generated.
	 * @param numberOfArguments Number of Arguments of the framework, which will be generated as an example.
	 * @return Array of analysis results, analyzing each a different randomly generated exemplary argumentation framework.
	 */
	public ContainerTransitionStateAnalysis[] findExampleArray(Semantics semanticsForSerializing, int numberOfExamples, int numberOfArguments) {
		this.changeParameterNumberOfArguments(numberOfArguments);
		return this.findExampleArray(semanticsForSerializing, numberOfExamples);
	}

	/**
	 * Creates exemplary serializability analyses, starting with the specified number of arguments,
	 * and increasing this number by the specified increment
	 * as long as the number stays lower or equal than the specified maximum number of arguments (inclusive boundary).
	 *
	 * @param semanticsForSerializing Semantics of the extensions created during the serializing process, which will be analyzed.
	 * @param numberOfArgumentsStart Number of Arguments of the first framework, which will be generated as an example.
	 * @param maxNumberOfArguments Maximum number of arguments of any framework generated during this method.
	 * @param numberOfExamplesPerIncrement Number of examples created with the same number of arguments.
	 * @param incrementForNumberOfArguments Increment by which the number of arguments is increased each time.
	 * @return Array of analysis results, analyzing each a different randomly generated exemplary argumentation framework.
	 */
	public  ContainerTransitionStateAnalysis[] findExampleArray(
			Semantics semanticsForSerializing,
			int numberOfArgumentsStart,
			int maxNumberOfArguments,
			int numberOfExamplesPerIncrement,
			int incrementForNumberOfArguments) {
		HashSet<ContainerTransitionStateAnalysis> results = new HashSet<>();
		for (int i = numberOfArgumentsStart; i <= maxNumberOfArguments; i += incrementForNumberOfArguments) {
			for (int j = 0; j < numberOfExamplesPerIncrement; j++) {
				results.add(this.findExample(semanticsForSerializing, i));
			}
		}
		return results.toArray(new ContainerTransitionStateAnalysis[0]);
	}

	/**
	 * Creates an exemplary serializability analysis, for which the number of arguments, with regards to the last analysis created by this object, is increased by the specified increment.
	 *
	 * @param semanticsForSerializing Semantics of the extensions created during the serializing process, which will be analyzed.
	 * @param incrementForNumberOfArguments Increment by which the number of arguments is increased.
	 * @return Analysis result of a randomly generated exemplary argumentation framework.
	 */
	public ContainerTransitionStateAnalysis findExampleEnumerating(Semantics semanticsForSerializing, int incrementForNumberOfArguments) {
		this.changeParameterNumberOfArguments(this.parameters.numberOfArguments + incrementForNumberOfArguments);
		return SerialisableExtensionReasonerWithAnalysis.getSerialisableReasonerForSemantics(semanticsForSerializing).getModelsWithAnalysis(this.generator.next());
	}
}
