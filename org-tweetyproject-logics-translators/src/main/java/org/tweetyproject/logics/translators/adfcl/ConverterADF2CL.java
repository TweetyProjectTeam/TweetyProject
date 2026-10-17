package org.tweetyproject.logics.translators.adfcl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tweetyproject.logics.cl.syntax.ClBeliefSet;
import org.tweetyproject.logics.cl.syntax.Conditional;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;
import org.tweetyproject.logics.cl.semantics.RankingFunction;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;

/**
 * This class bridges the gap between Abstract Dialectical Frameworks (ADFs) and Conditional Logic (CL)
 * @author Jonas Schumacher
 *
 */
public class ConverterADF2CL {

	/**
	 * Creates a conditional belief base using the provided ADF "adf" and Translation Function "theta"
	 * @param adf 			= Abstract Dialectical Framework
	 * @param theta 		= Index of Translation Function
	 * @return ClBeliefSet 	= Belief base based on input ADF
	 */
	public ClBeliefSet getBeliefSetFromADF(AbstractDialecticalFramework adf, int theta) {

		Set<Argument> arguments = adf.getNodes();
		Iterator<Argument> argument_iterator = arguments.iterator();
		Argument ar;
		PlFormula ac;
		PlFormula premise;
		PlFormula conclusion;
		ClBeliefSet bs = new ClBeliefSet();
		System.out.println("----------------------------------------");

		// Iterate over all Arguments and corresponding Acceptance Conditions:
		while(argument_iterator.hasNext()) {

			// reset premise and conclusion
			premise = null;
			conclusion = null;

			ar = argument_iterator.next();
			System.out.println("Argument: \t\t " + ar);
			ac = adf.getAcceptanceCondition(ar);
			System.out.println("Acceptance Condition \t" + ac);

			// Calculate conditionals based on input function Theta and add them to knowledge base "bs"
			switch (theta) {
				// Theta 1
				case 1:
					premise = ac;
					conclusion = new Proposition(ar.getName());
					bs.add(new Conditional(premise,conclusion));
					break;
				// Theta 2
				case 2:
					premise = new Proposition(ar.getName());
					conclusion = ac;
					bs.add(new Conditional(premise,conclusion));
					break;
				// Theta 3
				case 3:
					premise = ac;
					conclusion = new Proposition(ar.getName());
					bs.add(new Conditional(premise,conclusion));
					bs.add(new Conditional(conclusion,premise));
					break;
				// Theta 4
				case 4:
					premise = ac;
					conclusion = new Proposition(ar.getName());
					bs.add(new Conditional(premise,conclusion));
					bs.add(new Conditional(new Negation(premise),new Negation(conclusion)));
					break;
				// Theta 5
				case 5:
					premise = new Tautology();
					conclusion = new Equivalence(ac,new Proposition(ar.getName()));
					bs.add(new Conditional(premise,conclusion));
					break;
			default:
					throw new IllegalArgumentException("Unknown Theta Function specified!");
			}

			System.out.println("----------------------------------------");
		}
		return bs;
	}

	/**
	 * Compare the inference behavior of ADF "adf" and Ranking function "kappa"
	 * @param adf adf
	 * @param reasoner_adf reasoner_adf
	 * @param kappa kappa
	 */
	public void compareInference(AbstractDialecticalFramework adf, AbstractAdfReasoner reasoner_adf, RankingFunction kappa) {

		// Calculate ADF extensions
		Collection<ThreeValuedInterpretation> extensions = reasoner_adf.getModels(adf);
		System.out.println(extensions);

		// Iterate over all arguments
		Set<Argument> arguments = adf.getNodes();
		//System.out.println("Arguments contained in ADF: " + arguments);
		Iterator<Argument> argument_iterator = arguments.iterator();
		Argument ar;
		Proposition prop = null;
		boolean bool_adf;
		boolean bool_ocf;
		boolean translation_successful = true;

		while(argument_iterator.hasNext()) {
			ar = argument_iterator.next();
			prop = new Proposition(ar.getName());
			System.out.print("Argument: \t" + ar + "\t");

			bool_adf = reasoner_adf.query(adf, ar, InferenceMode.SKEPTICAL);
			bool_ocf = kappa.satisfies(new Conditional(new Tautology(),prop));

			if (bool_adf == bool_ocf) {
				System.out.println("Inferences coincide (" + bool_adf + ")");
			}
			else {
				System.out.println("Inferences differ between " + "ADF (" + bool_adf + ") and OCF (" + bool_ocf + ")");
				translation_successful = false;
			}
		}
		if (translation_successful) {
			System.out.println("Translation successful!");
		}
		else {
			System.out.println("Translation failed!");
		}
	}

    /** Default Constructor */
    public ConverterADF2CL(){}
}
