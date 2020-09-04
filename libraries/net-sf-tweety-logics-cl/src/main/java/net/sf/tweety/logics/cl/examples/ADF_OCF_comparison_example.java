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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.cl.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import net.sf.tweety.commons.ParserException;

import net.sf.tweety.logics.cl.syntax.ClBeliefSet;
import net.sf.tweety.logics.cl.syntax.Conditional;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Equivalence;
import net.sf.tweety.logics.pl.syntax.ExclusiveDisjunction;
import net.sf.tweety.logics.pl.syntax.Implication;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.Tautology;
import net.sf.tweety.logics.cl.semantics.RankingFunction;
import net.sf.tweety.logics.cl.reasoner.ZReasoner;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.NegationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.semantics.link.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.link.SatLinkStrategy;
import net.sf.tweety.arg.adf.io.KppADFFormatParser;
import net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import net.sf.tweety.arg.adf.reasoner.GroundReasoner;
import net.sf.tweety.arg.adf.reasoner.ModelReasoner;
import net.sf.tweety.arg.adf.reasoner.PreferredReasoner;
import net.sf.tweety.arg.adf.reasoner.StableReasoner;
import net.sf.tweety.arg.adf.sat.NativeMinisatSolver;

/**
 *  Example code illustrating the translation from Abstract Dialectical Frameworks (ADFs) to Conditional logics 
 *  
 * @author Jonas Schumacher
 *
 */
public class ADF_OCF_comparison_example {
	
	/**
	 * Translate "Acceptance Condition" into "PlFormula" 
	 * Recursive function: Case distinction by class name
	 * @param ac 			= Acceptance Condition
	 * @return PlFormula 	= translated propositional logic formula
	 */
	public static PlFormula getFormulaFromAcc(AcceptanceCondition ac) {
		
		PlFormula result = null;
		String class_type = ac.getClass().getName();
		
		// Base (= terminating) cases (A)-(C)
		// (A) Proposition (self-referencing)
		if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.Argument")) {
			//System.out.println(ac + " is a Proposition");
			Argument arg = (Argument) ac;
			result = new Proposition(arg.getName());
		}
		// (B) Tautology
		else if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.acc.TautologyAcceptanceCondition")) {
			//System.out.println(ac + " is a Tautology");
			result = new Tautology();
		}		
		// (C) Contradiction 
		else if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.acc.ContradictionAcceptanceCondition")) {
			//System.out.println(ac + " is a Contradiction");
			result = new Contradiction();
		}		
		// Recursive cases (1)-(6)
		// (1) Negation
		else if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.acc.NegationAcceptanceCondition")) {
			//System.out.println(ac + " is a Negation");
			NegationAcceptanceCondition ac_neg = (NegationAcceptanceCondition)ac;
			result = new Negation(getFormulaFromAcc(ac_neg.getChild()));
		}
		// (2) Conjunction
		else if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.acc.ConjunctionAcceptanceCondition")) {
			//System.out.println(ac + " is a Conjunction");
			ConjunctionAcceptanceCondition ac_con = (ConjunctionAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			for (AcceptanceCondition ac_sub : ac_con.getChildren()) {
				f_temp = getFormulaFromAcc(ac_sub);
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
		else if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.acc.DisjunctionAcceptanceCondition")) {
			//System.out.println(ac + " is a Disjunction");
			DisjunctionAcceptanceCondition ac_dis = (DisjunctionAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			for (AcceptanceCondition ac_sub : ac_dis.getChildren()) {
				f_temp = getFormulaFromAcc(ac_sub);
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
		else if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition")) {
			//System.out.println(ac + " is an exclusive Disjunction");
			ExclusiveDisjunctionAcceptanceCondition ac_exc = (ExclusiveDisjunctionAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			for (AcceptanceCondition ac_sub : ac_exc.getChildren()) {
				f_temp = getFormulaFromAcc(ac_sub);
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
		else if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.acc.ImplicationAcceptanceCondition")) {
			//System.out.println(ac + " is an Implication");
			ImplicationAcceptanceCondition ac_imp = (ImplicationAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			for (AcceptanceCondition ac_sub : ac_imp.getChildren()) {
				f_temp = getFormulaFromAcc(ac_sub);
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
		else if (class_type.contentEquals("net.sf.tweety.arg.adf.syntax.acc.EquivalenceAcceptanceCondition")) {
			//System.out.println(ac + " is an Equivalence");
			EquivalenceAcceptanceCondition ac_equ = (EquivalenceAcceptanceCondition)ac;
			
			PlFormula f_temp = null;
			PlFormula f = null;
			int counter = 0;
			for (AcceptanceCondition ac_sub : ac_equ.getChildren()) {
				f_temp = getFormulaFromAcc(ac_sub);
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
	public static ClBeliefSet getBeliefSetFromADF(AbstractDialecticalFramework adf, int theta) {
		
		Set<Argument> arguments = adf.getArguments();
		AcceptanceCondition ac;
		PlFormula premise;
		PlFormula conclusion;
		ClBeliefSet bs = new ClBeliefSet();
		System.out.println("----------------------------------------");
		
		// Iterate over all Arguments and corresponding Acceptance Conditions:
		for (Argument ar : arguments) {
			
			// reset premise and conclusion
			premise = null;
			conclusion = null;
					
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
				// Theta 6
				case 6:
					premise = new Proposition(ar.getName());
					conclusion = getFormulaFromAcc(ac);
					bs.add(new Conditional(premise,conclusion));
					bs.add(new Conditional(new Negation(premise),new Negation(conclusion)));
					break;
				// Theta 7
				case 7:
					premise = new Negation(getFormulaFromAcc(ac));
					conclusion = new Negation(new Proposition(ar.getName()));
					bs.add(new Conditional(premise,conclusion));
					bs.add(new Conditional(conclusion,premise));
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
	public static void compareInference(AbstractDialecticalFramework adf, AbstractDialecticalFrameworkReasoner reasoner_adf, RankingFunction kappa) {
		
		// Calculate ADF extensions
		Collection<Interpretation> extensions = reasoner_adf.getModels(adf);
		System.out.println(extensions);
		
		// Iterate over all arguments
		Set<Argument> arguments = adf.getArguments();
		//System.out.println("Arguments contained in ADF: " + arguments);
		Proposition prop = null;
		boolean bool_adf;
		boolean bool_ocf;
		boolean translation_successful = true;
		
		for(Argument ar : arguments) {
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
	
	/**
	 * Compares the inference behavior of an ADF with that of an OCF induced by translation function Theta
	 * @param args
	 * @throws FileNotFoundException
	 * @throws ParserException
	 * @throws IOException
	 */
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException {
		
		// Set path to ADF and chose translation function Theta
		String path_to_file = "src/main/resources/adf_to_ocf_example.txt";
		int theta = 7;
		
		/*
		 * Step 1: Create ADF from text file
		 */
		NativeMinisatSolver solver = new NativeMinisatSolver();
		LinkStrategy strat = new SatLinkStrategy(solver);
		KppADFFormatParser parser = new KppADFFormatParser(strat, true);
		AbstractDialecticalFramework adf = parser.parse(new File(path_to_file));
		
		/*
		 * Step 2: Create conditional belief base from ADF
		 */
		System.out.println("Apply translation Theta " + theta);
		ClBeliefSet bs = getBeliefSetFromADF(adf,theta);
		System.out.println("Resulting belief base:");
		System.out.println(bs);
		
		/*
		 * Step 3: Compare inference behavior of ADF and OCF
		 */
		System.out.println("----------------------------------------");
		System.out.println("Compare inference behavior:");
		
		// OCF System Z semantics
		System.out.println("Ranking function based on System Z:");
		ZReasoner reasoner_ocf = new ZReasoner();
		RankingFunction kappa = reasoner_ocf.getModel(bs);
		if (kappa == null) {
			System.out.println("The translation of this ADF is inconsistent.");
		}
		else {
			System.out.println(kappa);
			
			// ADF semantics
			// grounded
			AbstractDialecticalFrameworkReasoner reasoner_adf = new GroundReasoner(solver);
			System.out.println("----------------------------------------");
			System.out.println("ADF grounded semantics:");		
			compareInference(adf,reasoner_adf,kappa);
			// preferred
			reasoner_adf = new PreferredReasoner(solver);
			System.out.println("----------------------------------------");
			System.out.println("ADF preferred semantics:");
			compareInference(adf,reasoner_adf,kappa);
			// 2-valued
			reasoner_adf = new ModelReasoner(solver);
			System.out.println("----------------------------------------");
			System.out.println("ADF 2-valued semantics:");
			compareInference(adf,reasoner_adf,kappa);
			// stable
			reasoner_adf = new StableReasoner(solver);
			System.out.println("----------------------------------------");
			System.out.println("ADF stable semantics:");
			compareInference(adf,reasoner_adf,kappa);
		}
	}
}