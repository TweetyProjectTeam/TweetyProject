package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MaInconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class MaMeasureTest {

	public static void main(String[] args) throws ParserException, IOException{
		// Create some knowledge base
		PlBeliefSet kb = new PlBeliefSet();
		PlParser parser = new PlParser();
			
		kb.add((PropositionalFormula)parser.parseFormula("a"));
		kb.add((PropositionalFormula)parser.parseFormula("!a"));
		kb.add((PropositionalFormula)parser.parseFormula("!a && !b"));
		kb.add((PropositionalFormula)parser.parseFormula("b"));
		
				
		// test Ma inconsistency measure		
		BeliefSetInconsistencyMeasure<PropositionalFormula> ma = new MaInconsistencyMeasure<PropositionalFormula>(new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py"));
		System.out.println("Ma: " + ma.inconsistencyMeasure(kb));
		
	}
}
