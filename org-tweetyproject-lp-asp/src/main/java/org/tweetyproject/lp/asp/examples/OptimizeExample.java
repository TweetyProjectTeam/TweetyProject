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
package org.tweetyproject.lp.asp.examples;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.NumberTerm;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.lp.asp.parser.ASPParser;
import org.tweetyproject.lp.asp.parser.InstantiateVisitor;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.reasoner.ClingoSolver;
import org.tweetyproject.lp.asp.reasoner.SolverException;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.ASPAtom;
import org.tweetyproject.lp.asp.syntax.ASPBodyElement;
import org.tweetyproject.lp.asp.syntax.ASPOperator.ArithmeticOperator;
import org.tweetyproject.lp.asp.syntax.ASPOperator.OptimizeFunction;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.AggregateAtom;
import org.tweetyproject.lp.asp.syntax.AggregateHead;
import org.tweetyproject.lp.asp.syntax.ArithmeticTerm;
import org.tweetyproject.lp.asp.syntax.OptimizationStatement;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * An example for using optimization statements, taken from the clingo guide, chapter
 * 3.1.13 <a href="https://github.com/potassco/guide">https://github.com/potassco/guide</a>.
 * 
 * <br> Tested with clingo 5.4.0
 * 
 * @author Anna Gessler
 */
public class OptimizeExample {
	
	private static String CLINGO_PATH = "your/path/to/clingo";

	/**
	 * 
	 * @param args IOException
	 * @throws IOException IOException
	 * @throws ParseException ParseException
	 * @throws SolverException SolverException
	 */
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
		Predicate mainStreet = new Predicate("main_street", 1);
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
		hotelsExample.add(new ASPRule(new ASPAtom(mainStreet, h4)));
		ASPRule noisyRule = new ASPRule();
		Variable X = new Variable("X");
		Variable Y = new Variable("Y");
		Variable Z = new Variable("Z");
		noisyRule.setHead(new ASPAtom(noisy));
		noisyRule.addBody(new ASPAtom(hotel, X));
		noisyRule.addBody(new ASPAtom(mainStreet, X));
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
		ASPParser parser = new ASPParser(new StringReader(""));
		parser.ReInit(new StringReader("#minimize { Y@1, X: hotel(X), star(X,Y) }.\n"));
		Program pr1 = new InstantiateVisitor().visit(parser.Program(), null);
		System.out.println("\nParsed optimization statement:\n" + pr1);
	}
}
