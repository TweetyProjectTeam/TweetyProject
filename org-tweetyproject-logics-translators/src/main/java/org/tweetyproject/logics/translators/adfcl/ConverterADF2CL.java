package org.tweetyproject.logics.translators.adfcl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tweetyproject.logics.cl.syntax.ClBeliefSet;
import org.tweetyproject.logics.cl.syntax.Conditional;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;
import org.tweetyproject.logics.cl.semantics.RankingFunction;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.NegationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;

/**
 * This class bridges the gap between Abstract Dialectical Frameworks (ADFs) and Conditional Logic (CL)
 * @author Jonas Schumacher
 *
 */
public class ConverterADF2CL {
	
	/**
	 * Translate "Acceptance Condition" into "PlFormula" 
	 * Recursive function: Case distinction by class name
	 * @param ac 			= Acceptance Condition
	 * @return PlFormula 	= translated propositional logic formula
	 */
	public PlFormula getFormulaFromAcc(AcceptanceCondition ac) {
		
		PlFormula result = null;
		String class_type = ac.getClass().getName();
		
		// Base (= terminating) cases (A)-(C)
		// (A) Proposition (self-referencing)
		if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.Argument")) {
			//System.out.println(ac + " is a Proposition");
			Argument arg = (Argument) ac;
			result = new Proposition(arg.getName());
		}
		// (B) Tautology
		else if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.acc.TautologyAcceptanceCondition")) {
			//System.out.println(ac + " is a Tautology");
			result = new Tautology();
		}		
		// (C) Contradiction 
		else if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.acc.ContradictionAcceptanceCondition")) {
			//System.out.println(ac + " is a Contradiction");
			result = new Contradiction();
		}		
		// Recursive cases (1)-(6)
		// (1) Negation
		else if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.acc.NegationAcceptanceCondition")) {
			//System.out.println(ac + " is a Negation");
			NegationAcceptanceCondition ac_neg = (NegationAcceptanceCondition)ac;
			result = new Negation(getFormulaFromAcc(ac_neg.getChild()));
		}
		// (2) Conjunction
		else if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition")) {
			//System.out.println(ac + " is a Conjunction");
			ConjunctionAcceptanceCondition ac_con = (ConjunctionAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			Iterator<AcceptanceCondition> acc_it = ac_con.getChildren().iterator();
			while(acc_it.hasNext()) {
				f_temp = getFormulaFromAcc(acc_it.next());
				if (counter == 0) {
					f = f_temp;
				}
				else {
					f = new Conjunction(f,f_temp);
				}
				result = f;
				counter++;
			}
		}
		// (3) Disjunction
		else if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition")) {
			//System.out.println(ac + " is a Disjunction");
			DisjunctionAcceptanceCondition ac_dis = (DisjunctionAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			Iterator<AcceptanceCondition> acc_it = ac_dis.getChildren().iterator();
			while(acc_it.hasNext()) {
				f_temp = getFormulaFromAcc(acc_it.next());
				if (counter == 0) {
					f = f_temp;
				}
				else {
					f = new Disjunction(f,f_temp);
				}
				result = f;
				counter++;
			}
		}
		// (4) Exclusive Disjunction
		else if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition")) {
			//System.out.println(ac + " is an exclusive Disjunction");
			ExclusiveDisjunctionAcceptanceCondition ac_exc = (ExclusiveDisjunctionAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			Iterator<AcceptanceCondition> acc_it = ac_exc.getChildren().iterator();
			while(acc_it.hasNext()) {
				f_temp = getFormulaFromAcc(acc_it.next());
				if (counter == 0) {
					f = f_temp;
				}
				else {
					f = new ExclusiveDisjunction(f,f_temp);
				}
				result = f;
				counter++;
			}
		}
		// (5) Implication
		else if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.acc.ImplicationAcceptanceCondition")) {
			//System.out.println(ac + " is an Implication");
			ImplicationAcceptanceCondition ac_imp = (ImplicationAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			Iterator<AcceptanceCondition> acc_it = ac_imp.getChildren().iterator();
			while(acc_it.hasNext()) {
				f_temp = getFormulaFromAcc(acc_it.next());
				if (counter == 0) {
					f = f_temp;
				}
				else {
					f = new Implication(f,f_temp);
				}
				result = f;
				counter++;
			}
		}
		// (6) Equivalence
		else if (class_type.contentEquals("org.tweetyproject.arg.adf.syntax.acc.EquivalenceAcceptanceCondition")) {
			//System.out.println(ac + " is an Equivalence");
			EquivalenceAcceptanceCondition ac_equ = (EquivalenceAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			Iterator<AcceptanceCondition> acc_it = ac_equ.getChildren().iterator();
			while(acc_it.hasNext()) {
				f_temp = getFormulaFromAcc(acc_it.next());
				if (counter == 0) {
					f = f_temp;
				}
				else {
					f = new Equivalence(f,f_temp);
				}
				result = f;
				counter++;
			}
		}
		return result;
	}
	
	/**
	 * Creates a conditional belief base using the provided ADF "adf" and Translation Function "theta"
	 * @param adf 			= Abstract Dialectical Framework
	 * @param theta 		= Index of Translation Function
	 * @return ClBeliefSet 	= Belief base based on input ADF
	 */
	public ClBeliefSet getBeliefSetFromADF(AbstractDialecticalFramework adf, int theta) {
		
		Set<Argument> arguments = adf.getArguments();
		Iterator<Argument> argument_iterator = arguments.iterator();
		Argument ar; 
		AcceptanceCondition ac;
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
					premise = getFormulaFromAcc(ac);
					conclusion = new Proposition(ar.getName());
					bs.add(new Conditional(premise,conclusion));
					break;
				// Theta 2
				case 2:
					premise = new Proposition(ar.getName());
					conclusion = getFormulaFromAcc(ac);
					bs.add(new Conditional(premise,conclusion));
					break;
				// Theta 3
				case 3:
					premise = getFormulaFromAcc(ac);
					conclusion = new Proposition(ar.getName());
					bs.add(new Conditional(premise,conclusion));
					bs.add(new Conditional(conclusion,premise));
					break;
				// Theta 4
				case 4:
					premise = getFormulaFromAcc(ac);
					conclusion = new Proposition(ar.getName());
					bs.add(new Conditional(premise,conclusion));
					bs.add(new Conditional(new Negation(premise),new Negation(conclusion)));
					break;
				// Theta 5
				case 5:
					premise = new Tautology();
					conclusion = new Equivalence(getFormulaFromAcc(ac),new Proposition(ar.getName()));
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
	 * @param adf
	 * @param reasoner_adf
	 * @param kappa
	 */
	public void compareInference(AbstractDialecticalFramework adf, AbstractDialecticalFrameworkReasoner reasoner_adf, RankingFunction kappa) {
		
		// Calculate ADF extensions
		Collection<Interpretation> extensions = reasoner_adf.getModels(adf);
		System.out.println(extensions);
		
		// Iterate over all arguments
		Set<Argument> arguments = adf.getArguments();
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
			
			bool_adf = reasoner_adf.skepticalQuery(adf, ar);
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
}
