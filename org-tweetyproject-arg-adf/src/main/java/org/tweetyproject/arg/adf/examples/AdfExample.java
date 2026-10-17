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
package org.tweetyproject.arg.adf.examples;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatTwoValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * This example shows how to build an ADF in code and how to compute its
 * models under several semantics with the SAT-based reasoners.
 *
 * @author Lars Bengel
 */
public class AdfExample {

	/**
	 * Default constructor
	 */
	public AdfExample() {
		super();
	}

	/**
	 * Builds the ADF a[!b], b[!a], c[+], d[!b &amp;&amp; !c] and prints its models,
	 * an acceptance query and a verification query.
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		// acceptance conditions are propositional formulas over the arguments
		Argument a = new Argument("a"), b = new Argument("b"), c = new Argument("c"), d = new Argument("d");
		Proposition pa = new Proposition("a"), pb = new Proposition("b"), pc = new Proposition("c");
		Map<Argument, PlFormula> conditions = new LinkedHashMap<>();
		conditions.put(a, new Negation(pb));
		conditions.put(b, new Negation(pa));
		conditions.put(c, new Tautology());
		conditions.put(d, new Conjunction(new Negation(pb), new Negation(pc)));
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework(conditions);
		System.out.println(adf);

		// models under several semantics
		for (Semantics semantics : new Semantics[] { Semantics.ADM, Semantics.CO, Semantics.GR, Semantics.PR, Semantics.ST }) {
			AbstractAdfReasoner reasoner = AbstractAdfReasoner.getSatReasonerForSemantics(semantics);
			System.out.println(semantics.abbreviation() + ": " + reasoner.getModels(adf));
		}
		System.out.println("MOD: " + new SatTwoValuedModelAdfReasoner().getModels(adf));

		// acceptance and verification
		AbstractAdfReasoner preferred = AbstractAdfReasoner.getSatReasonerForSemantics(Semantics.PR);
		System.out.println("c is skeptically accepted under PR: " + preferred.query(adf, c, InferenceMode.SKEPTICAL));
		System.out.println("a is credulously accepted under PR: " + preferred.query(adf, a, InferenceMode.CREDULOUS));
		ThreeValuedInterpretation v = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		System.out.println(v + " is preferred: " + preferred.isModel(adf, v));
	}

}
