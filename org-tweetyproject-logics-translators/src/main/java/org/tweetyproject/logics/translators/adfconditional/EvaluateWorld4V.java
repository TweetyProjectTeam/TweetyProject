package org.tweetyproject.logics.translators.adfconditional;

import java.util.Collection;

import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * This class represents an implementation of example 4, 5 and 6 from the paper
 * [Heyninck et al. 2021] Conditional Abstract Dialectical Frameworks
 * 
 * @author Jonas Schumacher
 */
public class EvaluateWorld4V {
/**
 * 
 * @param args arguments
 */
	public static void main(String[] args) {
		
		/*
		 * Create 4-valued world "abc" with v("abc") = TUI
		 */
		Proposition a = new Proposition("a");
		Proposition b = new Proposition("b");
		Proposition c = new Proposition("c");
		FourValuedWorld exampleWorld = new FourValuedWorld();
		exampleWorld.set(a, FourValuedWorld.TruthValue.TRUE);
		exampleWorld.set(b, FourValuedWorld.TruthValue.UNDECIDED);
		exampleWorld.set(c, FourValuedWorld.TruthValue.INCONSISTENT);
		System.out.println("Example World: " + exampleWorld);
		System.out.println("----------------------------------------------");
		
		/*
		 * Example 4 & 5: calculate the set of sets of 2-valued worlds from the given 4-valued world
		 */
		Collection<ThreeValuedWorld> coll3V = exampleWorld.getThreeValuedSet();
		System.out.println("Set of 3-valued worlds: " + coll3V);
		System.out.println("Set of sets of 2-valued worlds: " + exampleWorld.getTwoValuedSetOfSets(coll3V));
		System.out.println("----------------------------------------------");
		
		/*
		 * Example 6: evaluate the given formulas directly:
		 */
		PlFormula phi1 = new Conjunction(a,c);
		PlFormula phi2 = new Conjunction(b,c);
		System.out.println("Formula " + phi1 + " evaluates to: " + exampleWorld.satisfies4VL(phi1));
		System.out.println("Formula " + phi2 + " evaluates to: " + exampleWorld.satisfies4VL(phi2));
	}

    /** Default Constructor */
    public EvaluateWorld4V(){}
}