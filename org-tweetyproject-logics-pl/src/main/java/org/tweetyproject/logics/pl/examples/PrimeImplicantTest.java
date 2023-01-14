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
import java.util.List;
import java.util.Set;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.analysis.SimpleMinimalModelProvider;
import org.tweetyproject.logics.pl.analysis.SimplePrimeImplicantEnumerator;
import org.tweetyproject.logics.pl.analysis.PrimeImplicantBasedInconsitencyMeasure;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.SimpleModelEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * 
 * @author Sebastian Franke
 *
 */
public class PrimeImplicantTest {

	public static void main(String[] args) throws ParserException, IOException {
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();


		beliefSet.add((PlFormula)parser.parseFormula("p && r "));
		beliefSet.add((PlFormula)parser.parseFormula("!p && (q || !r) "));
		beliefSet.add((PlFormula)parser.parseFormula("!q"));
		System.out.println("beliefSet: " + beliefSet.toString());
		
		List<Set<PlFormula>> aa = new SimplePrimeImplicantEnumerator(new SimpleMinimalModelProvider(new SimpleModelEnumerator())).getPrimeImplicants(beliefSet);
		System.out.println("prime implicants: " + aa.toString());
		PrimeImplicantBasedInconsitencyMeasure incons = new PrimeImplicantBasedInconsitencyMeasure();
		System.out.println("conflicts of prime implicants: " + incons.getConflicts(beliefSet));
		System.out.println("inconsistency: " + incons.inconsistencyMeasure(beliefSet));
		
	}
}
