/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.lp.asp.examples;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.NumberTerm;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.reasoner.ClingoSolver;
import net.sf.tweety.lp.asp.reasoner.SolverException;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.syntax.ASPAtom;
import net.sf.tweety.lp.asp.syntax.ASPBodyElement;
import net.sf.tweety.lp.asp.syntax.ASPOperator.ArithmeticOperator;
import net.sf.tweety.lp.asp.syntax.ASPOperator.OptimizeFunction;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.AggregateAtom;
import net.sf.tweety.lp.asp.syntax.AggregateHead;
import net.sf.tweety.lp.asp.syntax.ArithmeticTerm;
import net.sf.tweety.lp.asp.syntax.OptimizationStatement;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * An example for using optimization statements, taken from the clingo guide, chapter
 * 3.1.13 {@link (https://github.com/potassco/guide)}.
 * 
 * @author Anna Gessler
 */
public class OptimizeExample {
	private static String CLINGO_PATH =  "/your/path/to/clingo";

	public static void main(String[] args) throws IOException, ParseException, SolverException {
		Program hotelsExample = new Program();

		// Represent that we have 5 hotels
		Predicate hotel = new Predicate("hotel", 1);
		List<ASPBodyElement> hotels = new ArrayList<ASPBodyElement>();
		Constant h1 = new Constant("h1");
		Constant h2 = new Constant("h2");
		Constant h3 = new Constant("h3");
		Constant h4 = new Constant("h4");
		Constant h5 = new Constant("h5");
		hotels.add(new ASPAtom(hotel, h1));
		hotels.add(new ASPAtom(hotel, h2));
		hotels.add(new ASPAtom(hotel, h3));
		hotels.add(new ASPAtom(hotel, h4));
		hotels.add(new ASPAtom(hotel, h5));

		// Represent that we want to pick exactly one hotel
		AggregateAtom count = new AggregateAtom(hotels, 1, 1);
		ASPRule countRule = new ASPRule(new AggregateHead(count));
		hotelsExample.add(countRule);

		// Add information about cost, stars and noisiness levels of hotels
		Predicate star = new Predicate("star", 2);
		Predicate cost = new Predicate("cost", 2);
		Predicate noisy = new Predicate("noisy");
		Predicate main_street = new Predicate("main_street", 1);
		hotelsExample.add(new ASPRule(new ASPAtom(star, h1, new NumberTerm(5))));
		hotelsExample.add(new ASPRule(new ASPAtom(star, h2, new NumberTerm(4))));
		hotelsExample.add(new ASPRule(new ASPAtom(star, h3, new NumberTerm(3))));
		hotelsExample.add(new ASPRule(new ASPAtom(star, h4, new NumberTerm(3))));
		hotelsExample.add(new ASPRule(new ASPAtom(star, h5, new NumberTerm(2))));
		hotelsExample.add(new ASPRule(new ASPAtom(cost, h1, new NumberTerm(170))));
		hotelsExample.add(new ASPRule(new ASPAtom(cost, h2, new NumberTerm(140))));
		hotelsExample.add(new ASPRule(new ASPAtom(cost, h3, new NumberTerm(90))));
		hotelsExample.add(new ASPRule(new ASPAtom(cost, h4, new NumberTerm(75))));
		hotelsExample.add(new ASPRule(new ASPAtom(cost, h5, new NumberTerm(60))));
		hotelsExample.add(new ASPRule(new ASPAtom(main_street, h4)));
		ASPRule noisyRule = new ASPRule();
		Variable X = new Variable("X");
		Variable Y = new Variable("Y");
		Variable Z = new Variable("Z");
		noisyRule.setHead(new ASPAtom(noisy));
		noisyRule.addBody(new ASPAtom(hotel, X));
		noisyRule.addBody(new ASPAtom(main_street, X));
		hotelsExample.add(noisyRule);

		// Optimization statements that represent priorities about how much we want cost
		// and stars to influence the choice of hotel
		List<Term<?>> termTuple = new ArrayList<Term<?>>();
		termTuple.add(X);
		List<ASPBodyElement> literalsTuple = new ArrayList<ASPBodyElement>();
		literalsTuple.add(new ASPAtom(hotel, X));
		literalsTuple.add(new ASPAtom(star, X, Y));
		OptimizationStatement optMax = new OptimizationStatement(OptimizeFunction.MAXIMIZE, Y, 1, termTuple, literalsTuple);
		hotelsExample.add(new ASPRule(optMax));

		termTuple = new ArrayList<Term<?>>();
		termTuple.add(X);
		literalsTuple = new ArrayList<ASPBodyElement>();
		literalsTuple.add(new ASPAtom(hotel, X));
		literalsTuple.add(new ASPAtom(cost, X, Y));
		literalsTuple.add(new ASPAtom(star, X, Z));
		OptimizationStatement optMin = new OptimizationStatement(OptimizeFunction.MINIMIZE, new ArithmeticTerm(ArithmeticOperator.DIV, Y, Z), 2, termTuple, literalsTuple);
		hotelsExample.add(new ASPRule(optMin));

		// Add weak constraint stating that avoiding noise is the main priority
		ASPRule weakConstraint = new ASPRule();
		weakConstraint.setBody(new ASPAtom(noisy));
		weakConstraint.setWeight(new NumberTerm(1));
		weakConstraint.setLevel(new NumberTerm(3));
		hotelsExample.add(weakConstraint);

		System.out.println("Hotels example:\n" + hotelsExample);
		ClingoSolver solver = new ClingoSolver(CLINGO_PATH);
		List<AnswerSet> as = solver.getModels(hotelsExample);
		// When using optimization statements with clingo, the optimal answer set is the
		// first entry of the returned answer set
		System.out.println("\nOptimal model:" + as.get(0));
		System.out.println("Optimization:" + solver.getOptimumString());
		System.out.println("All models:" + as);

		// Using the parser to create optimization statements
		ASPCore2Parser parser = new ASPCore2Parser(new StringReader(""));
		StringReader s = new StringReader("#minimize { Y@1, X: hotel(X), star(X,Y) } .\n");
		parser.ReInit(s);
		InstantiateVisitor visitor = new InstantiateVisitor();
		Program pr1 = visitor.visit(parser.Program(), null);
		System.out.println("\nParsed optimization statement:\n" + pr1);
	}
}
