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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.NumberTerm;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.reasoner.ClingoSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.ASPAtom;
import org.tweetyproject.lp.asp.syntax.ASPBodyElement;
import org.tweetyproject.lp.asp.syntax.ASPOperator;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.AggregateAtom;
import org.tweetyproject.lp.asp.syntax.AggregateElement;
import org.tweetyproject.lp.asp.syntax.AggregateHead;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * Some more examples on how aggregates work. Note that you can
 * also parse aggregates using ASPParser, which is
 * easier than manually creating them for bigger examples.
 * 
 * <br> Tested with clingo 5.4.0
 * 
 * @author Anna Gessler
 *
 */
public class AggregateExample {

	private static String CLINGO_PATH = "your/path/to/clingo";

	public static void main(String[] args) throws IOException, ParseException {
		/**
		 * Course selection example
		 * (partially taken from clingo guide)
		 */
		Program coursesExample = new Program();
		Predicate course = new Predicate("course", 1);
		ASPAtom courseAi = new ASPAtom(course,new Constant("ai"));
		ASPAtom courseDb = new ASPAtom(course,new Constant("databases"));
		ASPAtom courseNw = new ASPAtom(course,new Constant("networks"));
		ASPAtom courseProject = new ASPAtom(course,new Constant("project"));
		ASPAtom courseXml = new ASPAtom(course,new Constant("xml"));
		coursesExample.add(new ASPRule(courseDb));
		
		//representing "you need to take at least one and at most two out of the courses databases, ai and networks"
		List<ASPBodyElement> courses = new ArrayList<ASPBodyElement>();
		courses.add(courseAi);
		courses.add(courseDb);
		courses.add(courseNw);
		//cardinality rules (#count aggregates) don't need weights, TweetyProject has a simple constructor for this case
		AggregateAtom count = new AggregateAtom(courses, 1, 2);
		ASPRule countRule = new ASPRule(new AggregateHead(count));
		coursesExample.add(countRule);
		
		//representing "the sum of a semester's course credits must be at least 18"
		//the credits are represented by the weights (the term of the aggregate elements)
		AggregateElement c1 = new AggregateElement(new NumberTerm(6), courseAi);
		AggregateElement c2 = new AggregateElement(new NumberTerm(6), courseDb);
		AggregateElement c3 = new AggregateElement(new NumberTerm(4), courseNw);
		AggregateElement c4 = new AggregateElement(new NumberTerm(12), courseProject);
		AggregateElement c5 = new AggregateElement(new NumberTerm(3), courseXml);
		List<AggregateElement> aggregateElements = new ArrayList<AggregateElement>();
		aggregateElements.add(c1);
		aggregateElements.add(c2);
		aggregateElements.add(c3);
		aggregateElements.add(c4);
		aggregateElements.add(c5);
		AggregateAtom sum = new AggregateAtom(ASPOperator.AggregateFunction.SUM, aggregateElements);
		sum.setLeft(new NumberTerm(18), ASPOperator.BinaryOperator.LEQ);
		ASPRule sumRule = new ASPRule(new AggregateHead(sum));
		coursesExample.add(sumRule);
		
		//representing "you don't want to take any courses with less than 4 credits"
		AggregateAtom min = new AggregateAtom(ASPOperator.AggregateFunction.MIN, aggregateElements);
		min.setLeft(new NumberTerm(4), ASPOperator.BinaryOperator.LEQ);
		ASPRule minRule = new ASPRule(new AggregateHead(min));
		coursesExample.add(minRule);
		
		System.out.println("Full example:\n" + coursesExample);
		ClingoSolver solver = new ClingoSolver(CLINGO_PATH);
		List<AnswerSet> as = solver.getModels(coursesExample);
		System.out.println("Clingo output:\n" + as + "\n");
	}

}
