package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.PmInconsistencyMeasure;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;


public class PmMeasureTest {
	public static void main(String[] args) throws ParserException, IOException{
		PlMusEnumerator.setDefaultEnumerator(new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py"));
		// Create some knowledge base
		PlBeliefSet kb = new PlBeliefSet();
		PlParser parser = new PlParser();
			
		kb.add((PropositionalFormula)parser.parseFormula("a"));
		kb.add((PropositionalFormula)parser.parseFormula("a && d"));
		kb.add((PropositionalFormula)parser.parseFormula("!a && b"));
		kb.add((PropositionalFormula)parser.parseFormula("!a && c"));
		kb.add((PropositionalFormula)parser.parseFormula("!c"));
		
				
		// test Pm inconsistency measure		
		BeliefSetInconsistencyMeasure<PropositionalFormula> pm = new PmInconsistencyMeasure();
		System.out.println("Pm: " + pm.inconsistencyMeasure(kb));
		
	}
}
