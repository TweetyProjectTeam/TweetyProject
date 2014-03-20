package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.commons.analysis.HsInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.semantics.PossibleWorldIterator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

public class InconsistencyTest {

	public static void main(String[] args) throws ParserException, IOException{
		// Create some knowledge base
		PlBeliefSet kb = new PlBeliefSet();
		PlParser parser = new PlParser();
		
		kb.add((PropositionalFormula)parser.parseFormula("a"));
		kb.add((PropositionalFormula)parser.parseFormula("!a"));
		kb.add((PropositionalFormula)parser.parseFormula("!a && c"));
		kb.add((PropositionalFormula)parser.parseFormula("!a && !c"));
		kb.add((PropositionalFormula)parser.parseFormula("b"));
		kb.add((PropositionalFormula)parser.parseFormula("c"));
		kb.add((PropositionalFormula)parser.parseFormula("!b && !c"));
				
		// test hs inconsistency measure
		InconsistencyMeasure<PlBeliefSet> hs = new HsInconsistencyMeasure<PropositionalFormula,PlBeliefSet>(new PossibleWorldIterator((PropositionalSignature)kb.getSignature()));
		System.out.println(hs.inconsistencyMeasure(kb));
		
	}
}
