package net.sf.tweety.logics.qbf.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.qbf.parser.QCirParser;
import net.sf.tweety.logics.qbf.parser.QbfParser;
import net.sf.tweety.logics.qbf.parser.QdimacsParser;
import net.sf.tweety.logics.qbf.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.qbf.writer.QdimacsWriter;

/**
 * Some general examples for quantified boolean formulas and for parsers and writers.
 * 
 * @author Anna Gessler
 *
 */
public class QbfExample {
	public static void main(String[] args) throws ParserException, IOException {
		PlBeliefSet p = new PlBeliefSet();
		Proposition v = new Proposition("V");
		ExistsQuantifiedFormula ef = new ExistsQuantifiedFormula(v, v);
		Negation n = new Negation(ef);
		p.add(ef);
		p.add(n);
		System.out.println(p);

		// Tweety Parser
		System.out.println("\nTweety parser\n=================");
		QbfParser parser1 = new QbfParser();
		p = parser1.parseBeliefBaseFromFile("src/main/resources/tweety-example.qbf");
		System.out.println(p);

		// QDIMACS Parser
		System.out.println("\nQDIMACS parser\n=================");
		QdimacsParser parser2 = new QdimacsParser();
		PlBeliefSet p2 = parser2.parseBeliefBaseFromFile("src/main/resources/qdimacs-example1.qdimacs");
		System.out.println(p2);
		QdimacsParser.Answer answer = parser2
				.parseQDimacsOutput("c a comment \n" + "s cnf 1 2 2 0 \n" + "2 -1 0 \n" + "1 -2 0   ");
		System.out.println(answer);

		// QDIMACS Writer
		System.out.println("\nQDIMACS writer\n=================");
		QdimacsWriter writer = new QdimacsWriter();
		writer.printBase(p);

		// QCir Parser 
		System.out.println("\nQCir parser\n=================");
		QCirParser parser3 = new QCirParser();
		PlBeliefSet p3 = parser3.parseBeliefBaseFromFile("src/main/resources/qcir-example1.qcir");
		System.out.println(p3);
		System.out.println("output: "+parser3.getOutputVariable()+"\n");
		PlBeliefSet p4 = parser3.parseBeliefBaseFromFile("src/main/resources/qcir-example2-sat.qcir");
		for (PlFormula f : p4)
			System.out.println(f);
		System.out.println("output: " + parser3.getOutputVariable());
	}
}
