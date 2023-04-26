package org.tweetyproject.arg.dung.equivalence;

import java.util.HashMap;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;

public class EquivalenceCompExFinder {

	private IEquivalence<DungTheory> equivalence1;
	private IEquivalence<DungTheory> equivalence2;
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
			IEquivalence<DungTheory> equivalence1,
			IEquivalence<DungTheory> equivalence2,			
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
	public HashMap<DungTheory,DungTheory> findExamples(
			boolean equivalence1IsTRUE, 
			boolean equivalence2IsTRUE,
			int numberOfMaxRandomGenerationTries,
			int numberOfMaxIterations){

		var output = new HashMap<DungTheory,DungTheory>();
		
		for (int i = 0; i < numberOfMaxIterations; i++) {
			var generatedFramework1 = this.generator.next();
			DungTheory generatedFramework2 = null;
			for (int j = 0; j < numberOfMaxRandomGenerationTries; j++) {
				generatedFramework2 = this.generator.next();
				if(
						equivalence1.isEquivalent(generatedFramework1, generatedFramework2) == equivalence1IsTRUE &&
						equivalence2.isEquivalent(generatedFramework1, generatedFramework2) == equivalence2IsTRUE
						) {
					break; // stops generation of a framework for generatedFramework2
				}
			}
			if(generatedFramework2 == null) {
				if(equivalence1IsTRUE)
					generatedFramework2 = equivalence1.getEquivalentTheories(generatedFramework1).iterator().next();
				else if(equivalence2IsTRUE) 
					generatedFramework2 = equivalence2.getEquivalentTheories(generatedFramework1).iterator().next();
				else
					continue; // skips this iteration since no pair could be generated
			}
			
			output.put(generatedFramework1, generatedFramework2);
		}
		
		return output;
	}
}
