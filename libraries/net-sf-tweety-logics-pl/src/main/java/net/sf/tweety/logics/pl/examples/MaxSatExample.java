package net.sf.tweety.logics.pl.examples;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.MaxSatSolver;
import net.sf.tweety.logics.pl.sat.OpenWboSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Illustrates the use of MaxSAT solvers.
 * @author Matthias Thimm
 *
 */
public class MaxSatExample {
	public static void main(String[] args) throws ParserException, IOException {
		MaxSatSolver solver = new OpenWboSolver("/Users/mthimm/Projects/misc_bins/open-wbo_2.1");
		
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		
		bs.add(parser.parseFormula("!a && b"));
		bs.add(parser.parseFormula("b || c"));
		bs.add(parser.parseFormula("c || d"));
		bs.add(parser.parseFormula("f || (c && g)"));
		
		Map<PlFormula, Integer> softClauses = new HashMap<>();
		softClauses.put(parser.parseFormula("a || !b"),25);
		softClauses.put(parser.parseFormula("!c"),15);
		
		Interpretation<PlBeliefSet,PlFormula> witness = solver.getWitness(bs,softClauses);
		System.out.println("Interpretation satisfying the hard constraints and minimising costs of violated soft constraints: " + witness);
		System.out.println("Cost of solution: " + MaxSatSolver.costOf(witness, bs, softClauses));
	}
}
