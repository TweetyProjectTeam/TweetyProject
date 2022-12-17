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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.examples;

import java.io.IOException;
import java.util.Set;

import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.analysis.SimpleMinimalModelProvider;
import org.tweetyproject.logics.pl.analysis.SimplePrimeImplicantEnumerator;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.SimpleModelEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * 
 * @author Sebastian Franke
 *
 */
public class PrimeImplicantTest {

	public static void main(String[] args) throws ParserException, IOException {
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();

		beliefSet.add((PlFormula)parser.parseFormula("!a1 && !a2 "));
		beliefSet.add((PlFormula)parser.parseFormula("!a3 || !a4"));
		beliefSet.add((PlFormula)parser.parseFormula("(!a3 || !a4) && a5"));
		System.out.println("beliefSet: " + beliefSet.toString());
		
		Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> Models = (Set<InterpretationSet<Proposition, PlBeliefSet, PlFormula>>) new SimpleModelEnumerator().getModels(beliefSet);
		System.out.println("all models: " + Models.toString());
//		for(InterpretationSet<Proposition,PlBeliefSet,PlFormula> m : Models)
//			System.out.println(m.toString() + " " + m.satisfies(beliefSet));
		
		System.out.println("minimal models: " + new SimpleMinimalModelProvider(new SimpleModelEnumerator()).getMinModels(beliefSet).toString());
		Set<Set<PlFormula>> aa = new SimplePrimeImplicantEnumerator(new SimpleMinimalModelProvider(new SimpleModelEnumerator())).getPrimeImplicants(beliefSet);
		System.out.println("prime implicants: " + aa.toString());
		
	}
}
