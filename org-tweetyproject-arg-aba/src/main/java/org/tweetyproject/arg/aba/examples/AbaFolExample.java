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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.aba.examples;

import java.util.List;

import org.tweetyproject.arg.aba.reasoner.PreferredReasoner;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.aba.syntax.InferenceRule;
import org.tweetyproject.arg.aba.syntax.Negation;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;

/**
 * Shows how to build a first-order ABA theory in code. Such a theory has to be
 * grounded with {@link AbaTheory#ground()} before reasoning; the parser does
 * this automatically.
 *
 * @author Lars Bengel
 */
public class AbaFolExample {

	/** Default */
	public AbaFolExample() {
	}

	/**
	 * Builds and evaluates the theory
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		Sort person = new Sort("Person");
		Constant a = new Constant("a", person);
		Constant b = new Constant("b", person);
		Variable x = new Variable("X", person);
		Variable y = new Variable("Y", person);
		Predicate innocent = new Predicate("Innocent", List.of(person));
		Predicate guilty = new Predicate("Guilty", List.of(person));
		Predicate blames = new Predicate("Blames", List.of(person, person));

		AbaTheory<FolFormula> abat = new AbaTheory<>();
		// everyone is assumed innocent; Guilty(X) is the contrary of Innocent(X)
		abat.add(new Assumption<FolFormula>(new FolAtom(innocent, x)));
		abat.add(new Negation<FolFormula>(new FolAtom(innocent, x), new FolAtom(guilty, x)));
		// someone blamed by an innocent person is guilty
		InferenceRule<FolFormula> rule = new InferenceRule<>();
		rule.setConclusion(new FolAtom(guilty, x));
		rule.addPremise(new FolAtom(blames, y, x));
		rule.addPremise(new FolAtom(innocent, y));
		abat.add(rule);
		// a and b blame each other
		InferenceRule<FolFormula> fact1 = new InferenceRule<>();
		fact1.setConclusion(new FolAtom(blames, a, b));
		abat.add(fact1);
		InferenceRule<FolFormula> fact2 = new InferenceRule<>();
		fact2.setConclusion(new FolAtom(blames, b, a));
		abat.add(fact2);

		// ground over the constants a and b, the reasoners expect a ground theory
		abat = abat.ground();
		System.out.println("Ground theory: " + abat);

		PreferredReasoner<FolFormula> reasoner = new PreferredReasoner<>();
		System.out.println("Preferred extensions: " + reasoner.getModels(abat));
		Assumption<FolFormula> query = new Assumption<>(new FolAtom(innocent, a));
		System.out.println("Credulously accept " + query + ": "
				+ reasoner.query(abat, query, InferenceMode.CREDULOUS));
		System.out.println("Skeptically accept " + query + ": " + reasoner.query(abat, query));
	}
}
