package net.sf.tweety.logics.qbf.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.qbf.parser.QbfParser;
import net.sf.tweety.logics.qbf.reasoner.CadetSolver;
import net.sf.tweety.logics.qbf.reasoner.CaqeSolver;
import net.sf.tweety.logics.qbf.reasoner.GhostQSolver;
import net.sf.tweety.logics.qbf.reasoner.NaiveQbfReasoner;
import net.sf.tweety.logics.qbf.reasoner.QuteSolver;

/**
 * Examples for various QBF solvers.
 * 
 * @author Anna Gessler
 *
 */
public class QbfReasonersExample {
	public static void main(String[] args) throws ParserException, IOException {
		QbfParser parser1 = new QbfParser();
		PlBeliefSet p = parser1.parseBeliefBaseFromFile("src/main/resources/tweety-example.qbf");
		System.out.println(p);

		// Naive classical inference
		System.out.println("\nNaiveQbfReasoner\n=================");
		NaiveQbfReasoner reasoner = new NaiveQbfReasoner();
		PlBeliefSet p2 = parser1.parseBeliefBase("forall a: (a && b)");
		System.out.println(p2);
		PlFormula query = parser1.parseFormula("b");
		System.out.println(reasoner.query(p2, query));

		// Cadet
		System.out.println("\nCadet\n=================");
		PlBeliefSet p3 = parser1.parseBeliefBase("forall A: (forall B:( exists C:( (C) <=> (A && B))))");
		CadetSolver reasoner2 = new CadetSolver("/home/anna/snap/qbf/cadet/");
		System.out.println(reasoner2.isSatisfiable(p3));

		// CAQE
		System.out.println("\nCAQE\n=================");
		CaqeSolver reasoner3 = new CaqeSolver("/home/anna/snap/qbf/caqe/");
		System.out.println(reasoner3.isSatisfiable(p3));

		// Qute
		System.out.println("\nQute\n=================");
		QuteSolver reasoner4 = new QuteSolver("/home/anna/snap/qbf/qute");
		System.out.println(reasoner4.isSatisfiable(p3));

		// GhostQ
		System.out.println("\nGhostQ\n=================");
		GhostQSolver reasoner5 = new GhostQSolver("/home/anna/snap/qbf/ghostq");
//		System.out.println(reasoner5.isSatisfiable(p3));
	}
}
