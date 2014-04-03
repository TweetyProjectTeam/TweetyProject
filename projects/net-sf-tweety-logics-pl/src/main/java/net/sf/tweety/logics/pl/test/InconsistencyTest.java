package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.EtaInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.HsInconsistencyMeasure;
import net.sf.tweety.logics.pl.DefaultConsistencyTester;
import net.sf.tweety.logics.pl.LingelingEntailment;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.ContensionInconsistencyMeasure;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.semantics.PossibleWorldIterator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.math.opt.solver.LpSolve;

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
		BeliefSetInconsistencyMeasure<PropositionalFormula> hs = new HsInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator((PropositionalSignature)kb.getSignature()));
		System.out.println("HS: " + hs.inconsistencyMeasure(kb));
		
		// test eta inconsistency measure		
		LpSolve.binary = "/opt/local/bin/lp_solve";
		BeliefSetInconsistencyMeasure<PropositionalFormula> eta = new EtaInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator((PropositionalSignature)kb.getSignature()));
		System.out.println("Eta: " + eta.inconsistencyMeasure(kb));
		
		// test contension inconsistency measure		
		BeliefSetInconsistencyMeasure<PropositionalFormula> cont = new ContensionInconsistencyMeasure(new DefaultConsistencyTester(new LingelingEntailment("/Users/mthimm/Projects/misc_bins/lingeling")));
		System.out.println("Cont: " + cont.inconsistencyMeasure(kb));
	}
}
