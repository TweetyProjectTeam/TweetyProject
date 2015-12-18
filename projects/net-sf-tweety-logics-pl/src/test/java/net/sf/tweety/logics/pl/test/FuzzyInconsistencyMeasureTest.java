package net.sf.tweety.logics.pl.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.FuzzyInconsistencyMeasure;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.math.func.fuzzy.MinimumNorm;
import net.sf.tweety.math.func.fuzzy.ProductNorm;

public class FuzzyInconsistencyMeasureTest {

	@Test
	public void test() throws ParserException, IOException {
		double accuracy = 0.001;				
				
		PlParser parser = new PlParser();
		PlBeliefSet bs = new PlBeliefSet();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		
		FuzzyInconsistencyMeasure mes_min = new FuzzyInconsistencyMeasure(new MinimumNorm());
		FuzzyInconsistencyMeasure mes_prod = new FuzzyInconsistencyMeasure(new ProductNorm());
		
		assertEquals(mes_min.inconsistencyMeasure(bs), 0.5, accuracy);
		assertEquals(mes_prod.inconsistencyMeasure(bs), 0.75, accuracy);
	}

}
