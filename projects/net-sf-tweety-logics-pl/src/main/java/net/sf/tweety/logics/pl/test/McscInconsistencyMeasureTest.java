package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.McscInconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class McscInconsistencyMeasureTest {

	public static void main(String[] args) throws ParserException, IOException{
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();		
		// Example of [Ammoura 2015]
		// ¬p ∨ ¬q, ¬p ∨ ¬r, ¬q ∨ ¬r, p, q, r
		// (note that the inconsistency value is 3 and not 4 as claimed in [Ammoura 2015]) 
		beliefSet.add((PropositionalFormula)parser.parseFormula("!p || !q"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!p || !r"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!q || !r"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("p"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("q"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("r"));
		
		System.out.println(beliefSet);
		
		McscInconsistencyMeasure<PropositionalFormula> i = new McscInconsistencyMeasure<PropositionalFormula>(new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py"));
		
		System.out.println(i.inconsistencyMeasure(beliefSet));
		
	}
	
}
