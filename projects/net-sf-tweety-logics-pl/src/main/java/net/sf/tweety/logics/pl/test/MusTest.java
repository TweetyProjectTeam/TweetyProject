package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.MusEnumerator;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class MusTest {
	public static void main(String[] args) throws ParserException, IOException{
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();
		for(int i = 0; i < 7; i++){
			beliefSet.add((PropositionalFormula)parser.parseFormula("a"+i));
			beliefSet.add((PropositionalFormula)parser.parseFormula("!a"+i));
		}
		beliefSet.add((PropositionalFormula)parser.parseFormula("!a1 && !a2"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!a3 || !a4"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!a3 || !a4 || a5"));
		
		
		System.out.println(beliefSet);
		
		MusEnumerator<PropositionalFormula> enumerator = new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py");
		
		System.out.println(enumerator.minimalInconsistentSubsets(beliefSet));		
	}
}
