package net.sf.tweety.logics.pl.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.reasoner.SimplePlReasoner;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Some general examples for using basic propositional logic classes like PlParser and SimplePlReasoner.
 * 
 * @author Anna Gessler
 *
 */
public class PlExample {
	public static void main(String[] args) throws ParserException, IOException {
		PlParser parser = new PlParser();	
		//Parse belief base from string
		PlBeliefSet beliefSet = parser.parseBeliefBase("a || b || c \n !a || b \n !b || c \n !c || (!a && !b && !c && !d)");
		System.out.println(beliefSet);
		
		//Parse belief base from file
		beliefSet = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase.proplogic");
		System.out.println(beliefSet);
		
		//Note that belief bases can have signatures larger than their formulas' signature
		PlSignature sig = beliefSet.getSignature();
		sig.add(new Proposition("f"));
		beliefSet.setSignature(sig);
		System.out.println(beliefSet);
		System.out.println("Minimal signature: " + beliefSet.getMinimalSignature());
		//...but not smaller (commented out line throws exception)
		sig.remove(new Proposition("a"));
		//beliefSet2.setSignature(sig);
		
		//Use simple inference reasoner
		SimplePlReasoner reasoner = new SimplePlReasoner();
		PlFormula query = new Negation(new Proposition("a"));
		Boolean answer1 = reasoner.query(beliefSet, query);
		System.out.println(answer1);
		Boolean answer2 = reasoner.query(beliefSet, new Contradiction());
		System.out.println(answer2);
		
		System.out.println();
		PlFormula xor = parser.parseFormula("a ^^ b ^^ c");
		System.out.println("parsed formula: " + xor);
		System.out.println("dnf: " +  xor.toDnf());
		System.out.println("cnf: " + xor.toCnf());
		System.out.println("nnf: " +  xor.toNnf());
		System.out.println("models :" + xor.getModels());
		xor = parser.parseFormula("a ^^ b ^^ c ^^ d ^^ e ^^ f");
		System.out.println("parsed formula: " + xor);
		System.out.println("models: " + xor.getModels());
		

		System.out.println();
		beliefSet = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase_xor.proplogic");
		System.out.println(beliefSet);
	}
}
