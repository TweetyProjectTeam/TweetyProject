package net.sf.tweety.logics.pl.test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.DHitInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.DMaxInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.DSumInconsistencyMeasure;
import net.sf.tweety.logics.pl.analysis.DalalDistance;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.semantics.PossibleWorldIterator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

public class DSumMeasureTest {

	public static void main(String[] args) throws ParserException, IOException{
		Collection<PropositionalFormula> c = new HashSet<PropositionalFormula>();
		PlParser parser = new PlParser();
		c.add((PropositionalFormula) parser.parseFormula("a && b && c"));
		c.add((PropositionalFormula) parser.parseFormula("!a && !b && !c"));
				
		PropositionalSignature sig = new PropositionalSignature();
		for(PropositionalFormula f: c) sig.addAll(f.getSignature());
		
		DSumInconsistencyMeasure<PossibleWorld,PropositionalFormula> inc1 = new DSumInconsistencyMeasure<PossibleWorld,PropositionalFormula>(new DalalDistance(),new PossibleWorldIterator(sig));
		DMaxInconsistencyMeasure<PossibleWorld,PropositionalFormula> inc2 = new DMaxInconsistencyMeasure<PossibleWorld,PropositionalFormula>(new DalalDistance(),new PossibleWorldIterator(sig));
		DHitInconsistencyMeasure<PossibleWorld,PropositionalFormula> inc3 = new DHitInconsistencyMeasure<PossibleWorld,PropositionalFormula>(new DalalDistance(),new PossibleWorldIterator(sig));
		
		System.out.println(inc1.inconsistencyMeasure(c));
		System.out.println(inc2.inconsistencyMeasure(c));
		System.out.println(inc3.inconsistencyMeasure(c));
		
	}
}
