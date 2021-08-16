package org.tweetyproject.logics.translators.adfconditional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;


/**
 * This class implements the cADF semantics by applying the Gamma Operator
 * and checking for admissible, complete, grounded, preferred and 2-valued interpretations plus the grounded state
 * refer to [Heyninck et al. 2021] for further information
 * 
 * @author Jonas Schumacher
 *
 */
public class CADFSemantics {
	
	public static Collection<FourValuedWorld> allPossibleWorlds;
	
	/**
	 * Implementation of the gamma operator: calculate a new set of (output) interpretations given a cADF and an (input) interpretation
	 * @param statementFormulas
	 * @param acceptanceFormulas
	 * @param sig
	 * @param worldInput
	 * @return
	 */
	public static Collection<FourValuedWorld> gammaOperator(ArrayList<PlFormula> statementFormulas, ArrayList<PlFormula> acceptanceFormulas, PlSignature sig, FourValuedWorld worldInput) {
		FourValuedWorldIterator iteratorInner = new FourValuedWorldIterator(sig);
		Collection<FourValuedWorld> moreInformativeWorlds = new HashSet<FourValuedWorld>();		
		Collection<FourValuedWorld> moreInformativeWorldsIntersection = new HashSet<FourValuedWorld>(allPossibleWorlds);
		FourValuedWorld possibleOutputWorld = new FourValuedWorld();
		
		// Iterate over the statement-acceptance pairs of the given ADF
		for (int i = 0; i < statementFormulas.size(); i++) {
			//System.out.println("Pair No" + i + ": \t" + statementFormulas.get(i) + " \t " + acceptanceFormulas.get(i));
			FourValuedWorld.TruthValue truthOfAcceptanceFormula = worldInput.satisfies4VL(acceptanceFormulas.get(i));
			//System.out.println("4-valued truth value of " + acceptanceFormulas.get(i) + ": \t " + truthOfAcceptanceFormula);
			
			//System.out.println("Interpretations of " + statementFormulas.get(i) + " which are at least as informative as " + truthOfAcceptanceFormula + ":");
			iteratorInner = new FourValuedWorldIterator(sig);
			moreInformativeWorlds.clear();
			while (iteratorInner.hasNext()) {
				possibleOutputWorld = iteratorInner.next();
				FourValuedWorld.TruthValue truthOfStatementFormula = possibleOutputWorld.satisfies4VL(statementFormulas.get(i));
				if (FourValuedWorld.informationOrder(truthOfStatementFormula, truthOfAcceptanceFormula)) {
					moreInformativeWorlds.add(possibleOutputWorld);
				}
			}
			//System.out.println(moreInformativeWorlds);
			moreInformativeWorldsIntersection.retainAll(moreInformativeWorlds);
			// System.out.println("Running intersection: " + moreInformativeWorldsIntersection);
			//System.out.println("------------------------------------------------------------------------");
		}
		//System.out.println("Intersection: " + moreInformativeWorldsIntersection);
		Collection<FourValuedWorld> outputWorlds = FourValuedWorld.reduceToLeastInformativeWorlds(moreInformativeWorldsIntersection, false);
		return outputWorlds;
	}
	
	/**
	 * Implementation of the Gamma Prime Operator: 
	 * Calculate the union of the Gamma Operator of all input worlds and then reduce those worlds to the least informative ones
	 * @param inputCollection
	 * @param statementFormulas
	 * @param acceptanceFormulas
	 * @param sig
	 * @return
	 */
	public static Collection<FourValuedWorld> gammaPrimeOperator(Collection<FourValuedWorld> inputCollection, ArrayList<PlFormula> statementFormulas, ArrayList<PlFormula> acceptanceFormulas, PlSignature sig) {
		Collection<FourValuedWorld> bigUnion = new HashSet<FourValuedWorld>();
		for (FourValuedWorld inputWorld : inputCollection) {
			bigUnion.addAll(gammaOperator(statementFormulas, acceptanceFormulas, sig, inputWorld));
		}
		Collection<FourValuedWorld> outputWorlds = FourValuedWorld.reduceToLeastInformativeWorlds(bigUnion, false);
		return outputWorlds;
	}
	
	/**
	 * Check whether a given world constitutes an admissible interpretation
	 * @param inputWorld
	 * @param outputWorlds
	 * @return
	 */
	public static boolean isAdmissible(FourValuedWorld inputWorld, Collection<FourValuedWorld> outputWorlds) {
		for (FourValuedWorld outputWorld : outputWorlds) {
			if (outputWorld.isAtLeastAsInformativeAs(inputWorld)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check whether a given world constitutes a complete interpretation
	 * @param inputWorld
	 * @param outputWorlds
	 * @param statementFormulas
	 * @param acceptanceFormulas
	 * @return
	 */
	public static boolean isComplete(FourValuedWorld inputWorld, Collection<FourValuedWorld> outputWorlds, ArrayList<PlFormula> statementFormulas, ArrayList<PlFormula> acceptanceFormulas) {
		// Iterate over all output worlds
		for (FourValuedWorld outputWorld : outputWorlds) {
			if (outputWorld.equals(inputWorld)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Input an ADF, calculate all possible 4-valued interpretations, apply the gamma operator
	 * Check whether the interpretations are admissible, complete, grounded, preferred or 2-valued  
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		/**
		 * Set the given cADF:
		 */
		
		//Example 8
//		Proposition p = new Proposition("p");
//		Proposition s = new Proposition("s");
//		
//		ArrayList<PlFormula> statementFormulas = new ArrayList<PlFormula>();
//		ArrayList<PlFormula> acceptanceFormulas = new ArrayList<PlFormula>();	
//		statementFormulas.add(new Disjunction(p,s));
//		acceptanceFormulas.add(new Tautology());
//		statementFormulas.add(new Negation(s));
//		acceptanceFormulas.add(p);

		// Example 17
		Proposition p = new Proposition("p");
		Proposition q = new Proposition("q");
		Proposition s = new Proposition("s");

		ArrayList<PlFormula> statementFormulas = new ArrayList<PlFormula>();
		ArrayList<PlFormula> acceptanceFormulas = new ArrayList<PlFormula>();
		statementFormulas.add(new Disjunction(p,q));
		acceptanceFormulas.add(new Tautology());
		statementFormulas.add(s);
		acceptanceFormulas.add(p);
		statementFormulas.add(s);
		acceptanceFormulas.add(q);
		
		int adfSize = statementFormulas.size();
		
		//System.out.println(statementFormulas);
		//System.out.println(acceptanceFormulas);
		
		/*
		 * Calculate signature of the given ADF
		 */
		Collection<PlFormula> coll = new HashSet<PlFormula>();
		coll.addAll(statementFormulas);
		coll.addAll(acceptanceFormulas);
		PlSignature sig = PlSignature.getSignature(coll);
		// System.out.println(sig);
		
		System.out.println("Given ADF: ");
		for (int i = 0; i < adfSize; i++) {
			System.out.println("Statement-Acceptance-Pair #" + i + ": \t" + statementFormulas.get(i) + " \t " + acceptanceFormulas.get(i));
		}
		
		/*
		 * Keep one collection of all possible worlds
		 */
		FourValuedWorldIterator iterator4V = new FourValuedWorldIterator(sig);
		allPossibleWorlds = new HashSet<FourValuedWorld>();		
		while (iterator4V.hasNext()) {
			allPossibleWorlds.add(iterator4V.next());
		}
                		
		/*
		 * Loop over all possible input interpretations = worlds
		 * and call the gamma operator
		 */
		Collection<FourValuedWorld> admissibleWorlds = new HashSet<FourValuedWorld>();
		Collection<FourValuedWorld> completeWorlds = new HashSet<FourValuedWorld>();
		Collection<FourValuedWorld> twoValuedWorlds = new HashSet<FourValuedWorld>();
		Collection<FourValuedWorld> preferredWorlds;
		Collection<FourValuedWorld> groundedWorlds;
		iterator4V = new FourValuedWorldIterator(sig);
		while (iterator4V.hasNext()) {
			System.out.println("------------------------------------------------------------------------");	
			FourValuedWorld inputWorld = iterator4V.next();
			System.out.println("Input Interpretation: " + inputWorld.printValues());
			Collection<FourValuedWorld> outputWorlds = gammaOperator(statementFormulas, acceptanceFormulas, sig, inputWorld);
			String outputWorldPrint = FourValuedWorld.printCollection(outputWorlds);
			System.out.println("Output interpretations: " + outputWorldPrint);
			if (isAdmissible(inputWorld, outputWorlds)) {
				admissibleWorlds.add(inputWorld);
				System.out.println("Admissible");
			}
			if (isComplete(inputWorld, outputWorlds, statementFormulas, acceptanceFormulas)) {
				completeWorlds.add(inputWorld);
				System.out.println("Complete");
				if (inputWorld.isTwoValued()) {
					twoValuedWorlds.add(inputWorld);
					System.out.println("2-valued");
				}
			}
		}
		groundedWorlds = FourValuedWorld.reduceToLeastInformativeWorlds(completeWorlds, false);
		preferredWorlds = FourValuedWorld.reduceToLeastInformativeWorlds(completeWorlds, true);
		
		/*
		 * Calculate the grounded state according to definition 9
		 * 1) Start with a collection containing only one world consisting of UNDECIDED only
		 * 2) Repeatedly call GammaPrimeOperator, until input == output
		 */
		FourValuedWorld undecidedWorld = new FourValuedWorld();
		
		for (Proposition prop : sig) {
			undecidedWorld.set(prop, FourValuedWorld.TruthValue.UNDECIDED);
		}
		Collection<FourValuedWorld> inputCollection = new HashSet<FourValuedWorld>();
		Collection<FourValuedWorld> outputCollection;
		inputCollection.add(undecidedWorld);
		System.out.println("------------------------------------------------------------------------");	
		System.out.println("Search for grounded state:");
		System.out.println("Start with world : " + FourValuedWorld.printCollection(inputCollection));
		
		boolean reachedFixedPoint = false;
		while (!reachedFixedPoint) {
			outputCollection = gammaPrimeOperator(inputCollection, statementFormulas, acceptanceFormulas, sig);
			System.out.println("\u0393'(" + FourValuedWorld.printCollection(inputCollection) + ") = " + FourValuedWorld.printCollection(outputCollection));
			if (inputCollection.equals(outputCollection)) {
				reachedFixedPoint = true;
				System.out.println("Reached fixed point = grounded state = " + FourValuedWorld.printCollection(inputCollection));
			}
			inputCollection = outputCollection;
		}
		
		/*
		 * Summarize results
		 */
		System.out.println("------------------------------------------------------------------------");	
		System.out.println("Admissible interpretations: " + FourValuedWorld.printCollection(admissibleWorlds));
		System.out.println("Complete interpretations: " + FourValuedWorld.printCollection(completeWorlds));
		System.out.println("2-valued interpretations: " + FourValuedWorld.printCollection(twoValuedWorlds));
		System.out.println("Grounded interpretations: " + FourValuedWorld.printCollection(groundedWorlds));
		System.out.println("Preferred interpretations: " + FourValuedWorld.printCollection(preferredWorlds));
		System.out.println("Grounded State: " + FourValuedWorld.printCollection(inputCollection));
	}
}

