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
package net.sf.tweety.lp.asp.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.lp.asp.reasoner.ClingoSolver;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.syntax.AggregateHead;
import net.sf.tweety.lp.asp.syntax.ASPAtom;
import net.sf.tweety.lp.asp.syntax.ASPBodyElement;
import net.sf.tweety.lp.asp.syntax.ClassicalHead;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.AggregateAtom;
import net.sf.tweety.lp.asp.syntax.DefaultNegation;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.StrictNegation;

/**
 * Example code for creating ASP programs and calling the clingo solver.
 * 
 * @author Anna Gessler
 *
 */
public class ASPExample {

	private static String CLINGO_PATH = "/your/path/to/clingo";

	public static void main(String[] args) throws IOException {
		// Recreating src/main/resources/ex1.asp manually
		ASPAtom p = new ASPAtom("p");
		ASPAtom r = new ASPAtom("r");
		ASPAtom q = new ASPAtom("q");
		ASPAtom b = new ASPAtom("b");
		ASPRule r1 = new ASPRule(new ClassicalHead(p), new DefaultNegation(r));
		ASPRule r2 = new ASPRule(new ClassicalHead(r));
		r2.setBody(new StrictNegation(q), new DefaultNegation(b));
		ASPRule r3 = new ASPRule(new ClassicalHead(new StrictNegation(q)), b);
		ASPRule r4 = new ASPRule(b);
		Program p1 = new Program(r1, r2, r3, r4);
		System.out.println("Ex1: " + p1 + "\n");

		// Recreating src/main/resources/ex5.asp manually
		Predicate motive = new Predicate("motive", 1);
		Predicate guilty = new Predicate("guilty", 1);
		Predicate innocent = new Predicate("innocent", 1);
		Constant harry = new Constant("harry");
		Constant sally = new Constant("sally");
		Variable Suspect = new Variable("Suspect");
		r1 = new ASPRule(new ClassicalHead(new ASPAtom(motive, harry)));
		r2 = new ASPRule(new ClassicalHead(new ASPAtom(motive, sally)));
		r3 = new ASPRule(new ClassicalHead(new ASPAtom(guilty, harry)));
		r4 = new ASPRule(new ClassicalHead(new ASPAtom(innocent, Suspect)));
		r4.setBody(new ASPAtom(motive, Suspect), new DefaultNegation(new ASPAtom(guilty, Suspect)));
		Program p2 = new Program(r1, r2, r3, r4);
		ASPAtom query = new ASPAtom(innocent, harry);
		p2.setQuery(query);
		System.out.println("Ex5: " + p2 + "\n");

		// Calling solver (the conversion to clingo format is done automatically)
		ClingoSolver solver = new ClingoSolver(CLINGO_PATH);
		List<AnswerSet> as = solver.getModels(p2);
		System.out.println("Clingo output:\n" + as + "\n");

		// Shortcut for creating a cardinality rule
		System.out.println("Cardinality rules: ");
		List<ASPBodyElement> literals = new ArrayList<ASPBodyElement>();
		literals.add(new ASPAtom("a"));
		literals.add(new ASPAtom("b"));
		literals.add(new ASPAtom("c"));
		AggregateHead h = new AggregateHead(literals, 1, 2);
		// as head
		r1 = new ASPRule(h);
		Program p3 = new Program(r1);
		System.out.println(p3);
		solver = new ClingoSolver(CLINGO_PATH);
		as = solver.getModels(p3);
		System.out.println("\nClingo output:\n" + as);
		// same cardinality rule as body
		r2 = new ASPRule();
		r2.setBody(new AggregateAtom(literals, 1, 2));
	}

}
