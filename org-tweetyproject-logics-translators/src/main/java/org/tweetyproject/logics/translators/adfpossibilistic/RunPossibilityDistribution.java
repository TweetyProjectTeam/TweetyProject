package org.tweetyproject.logics.translators.adfpossibilistic;

import java.io.IOException;
import java.util.ArrayList;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * 
 * This class implements methods to test the class "PossibilityDistribution"
 * @author Jonas Schumacher
 *
 */
public class RunPossibilityDistribution {
/**
 * 
 * @param args arguments
 * @throws ParserException ParserException
 * @throws IOException IOException
 */
	public static void main(String[] args) throws ParserException, IOException {
		
		PlParser parser = new PlParser();
		
		// Step 1: Create possibility distribution
		
		double[] startValues = {0.5,0.7,0.5,0.5};			// example 7 of the previous paper
		
		PlSignature sig = new PlSignature();
		sig.add(new Proposition("a"));
		sig.add(new Proposition("b"));
		PossibilityDistribution possDist = new PossibilityDistribution(sig);
		System.out.println("Initial possibility distribution (with maximum entropy):");
		System.out.println(possDist);
		
		ArrayList<Proposition> setOfProps = new ArrayList<Proposition>();
		setOfProps.add(new Proposition("a"));
		setOfProps.add(new Proposition("b"));
		possDist.setPossibility(new PossibleWorld(setOfProps), startValues[0]);			// a,b
		setOfProps = new ArrayList<Proposition>();
		setOfProps.add(new Proposition("a"));
		possDist.setPossibility(new PossibleWorld(setOfProps), startValues[1]);			// a,-b
		setOfProps = new ArrayList<Proposition>();
		setOfProps.add(new Proposition("b"));
		possDist.setPossibility(new PossibleWorld(setOfProps), startValues[2]);			// -a,b
		setOfProps = new ArrayList<Proposition>();
		possDist.setPossibility(new PossibleWorld(setOfProps), startValues[3]);			// -a,-b
		
		System.out.println("Final possibility distribution with possibility values set manually:");		
		System.out.println(possDist);
		
		// Step 2: Create formula A
		//PlFormula testFormula = parser.parseFormula("a && b");
		//PlFormula testFormula = parser.parseFormula("a && !b");
		//PlFormula testFormula = parser.parseFormula("!a && b");
		PlFormula testFormula = parser.parseFormula("!a && !b");
		
		// Step 3: Calculate Possibility Pos(A)
		System.out.println("Possibility of formula " + testFormula + ": " + possDist.getPossibility(testFormula));
		
		// Step 4: Calculate Necessity Nec(A)
		System.out.println("Necessity of formula " + testFormula + ": " + possDist.getNecessity(testFormula));
		
		/*
		 *  Optional features we might be interested in in the future:
		 */
		System.out.println("--------------------------------------------");
		System.out.println("Optional features");
		
		// check if PD is normalized
		System.out.println("Is the PD normalized? " + possDist.isNormalized());
		// get all the worlds with possibility of 1.0:
		System.out.println("Most plausible worlds: " + possDist.getPlausibleWorlds());
		
		// compare Specificity
		PossibilityDistribution possDistAlternative = new PossibilityDistribution(sig);
		System.out.println("Possibility distribution to be compared with:");
		System.out.println(possDistAlternative);
		if (possDist.atLeastAsSpecificAs(possDistAlternative)) {
			if (possDistAlternative.atLeastAsSpecificAs(possDist)) {
				System.out.println("Distribution A and B have the same specifity.");
			}
			else {
				System.out.println("Distribution A is strictly more specific than B");
			}
		}
		else {
			if (possDistAlternative.atLeastAsSpecificAs(possDist)) {
				System.out.println("Distribution B is strictly more specific than A");
			}
			else {
				System.out.println("Distribution A and B are incomparable");
			}
		}
	}


    /** Default Constructor */
    public RunPossibilityDistribution(){}
}
