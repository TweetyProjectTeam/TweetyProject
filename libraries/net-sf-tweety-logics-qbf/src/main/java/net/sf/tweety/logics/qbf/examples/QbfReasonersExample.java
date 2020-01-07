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
		PlBeliefSet p2 = parser1.parseBeliefBase("forall a: (a || !a) \n"
				+ "!b");
		System.out.println(p2);
		PlFormula query = parser1.parseFormula("a || !a");
		System.out.println(reasoner.query(p2, query));
		PlFormula query2 = parser1.parseFormula("forall a: (a || !b)");
		System.out.println(reasoner.query(p2, query2));
		PlFormula query3 = parser1.parseFormula("exists b: (b && !b)");
		System.out.println(reasoner.query(p2, query3));


		// Cadet
		System.out.println("\nCadet\n=================");
		PlBeliefSet p3 = parser1.parseBeliefBase("forall A: (forall B:( exists C:( (C) <=> (A && B))))");
		CadetSolver reasoner2 = new CadetSolver("/home/anna/snap/qbf/cadet/");
		System.out.println(reasoner2.isSatisfiable(p3));

		// CAQE
		System.out.println("\nCAQE\n=================");
		CaqeSolver reasoner3 = new CaqeSolver("/home/anna/snap/qbf/caqe/");
		PlBeliefSet p4 = parser1.parseBeliefBase("exists A: ( forall C:( exists B:((!A && C) && (D || B && !C))))");
		System.out.println(reasoner3.isSatisfiable(p4));

		// Qute
		System.out.println("\nQute\n=================");
		QuteSolver reasoner4 = new QuteSolver("/home/anna/snap/qbf/qute");
		System.out.println(reasoner4.isSatisfiable(p3)+"\n"); //TODO: Why does this evaluate to false (in contrast to the other solvers)?
		System.out.println(reasoner4.isSatisfiable(p4));

		// GhostQ
		System.out.println("\nGhostQ\n=================");
		GhostQSolver reasoner5 = new GhostQSolver("/home/anna/snap/qbf/ghostq/bin/");
		System.out.println(reasoner5.isSatisfiable(p3) + "\n");
		System.out.println(reasoner5.isSatisfiable(p4));
	}
}
