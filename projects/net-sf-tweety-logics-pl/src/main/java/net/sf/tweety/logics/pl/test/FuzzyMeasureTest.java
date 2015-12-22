package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.FuzzyInconsistencyMeasure;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.math.func.fuzzy.ProductNorm;

public class FuzzyMeasureTest {
	public static void main(String[] args) throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		
		bs.add((PropositionalFormula) parser.parseFormula("a && !a"));
		bs.add((PropositionalFormula) parser.parseFormula("!(!(a && !a))"));
		bs.add((PropositionalFormula) parser.parseFormula("!(!(!(!(a && !a))))"));
		bs.add((PropositionalFormula) parser.parseFormula("!(!(!(!(!(!(a && !a))))))"));
		bs.add((PropositionalFormula) parser.parseFormula("!(!(!(!(!(!(!(!(a && !a))))))))"));
		bs.add((PropositionalFormula) parser.parseFormula("a && a"));
		
		System.out.println(bs);
		
		FuzzyInconsistencyMeasure mes = new FuzzyInconsistencyMeasure(new ProductNorm(),FuzzyInconsistencyMeasure.SUMFUZZY_MEASURE);
		
		System.out.println(mes.inconsistencyMeasure(bs));
		
		System.out.println();
		
		System.out.println(mes.getOptimalInterpretation(bs));
		
	}
}
