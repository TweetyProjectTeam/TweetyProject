package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.pl.ClassicalInference;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.Sat4jEntailment;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class SatTest {
	public static void main(String[] args) throws ParserException, IOException{
		
		
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();
		beliefSet.add((PropositionalFormula)parser.parseFormula("a || b || c"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!a || b"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!b || c"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!c || (!a && !b && !c && !d)"));
		
		System.out.println(beliefSet);
		
		Sat4jEntailment entail = new Sat4jEntailment();
		
		ClassicalInference reasoner = new ClassicalInference(beliefSet, entail);
		
		System.out.println(reasoner.query(parser.parseFormula("a")));
	}
}
