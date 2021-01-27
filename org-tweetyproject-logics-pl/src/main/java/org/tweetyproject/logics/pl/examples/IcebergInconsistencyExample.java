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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.examples;

import java.io.IOException;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.analysis.IcebergInconsistencyMeasure;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.PlMusEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;

/**
 * Example code for the "iceberg" inconsistency measures as described in [De Bona, Hunter.
 * Localising iceberg inconsistencies. AI 2017].
 * 
 * @author Anna Gessler
 */
public class IcebergInconsistencyExample {
	public static void main(String[] args) throws ParserException, IOException {
		/**
		 * Examples with different consequence operations
		 */
		PlParser parser = new PlParser();
		PlBeliefSet kb1 = parser.parseBeliefBase("o \n"
				+ "!o\n"
				+ "(!g && r)\n"
				+ "g && (r => !o)\n"
				+ "!h\n"
				+ "+");
		IcebergInconsistencyMeasure im = new IcebergInconsistencyMeasure(IcebergInconsistencyMeasure.ConsequenceOperation.IDENTITY);
		//in the trivial case of using the "identity" consequence operation, the *-conflicts are exactly the MIS 
		System.out.println("Identity\n========");
		System.out.println("*-conflicts: " + im.getStarConflicts(kb1) + ", MIS: " +  PlMusEnumerator.getDefaultEnumerator().minimalInconsistentSubsets(kb1));
		System.out.println("IM: " + im.inconsistencyMeasure(kb1));
		
		im = new IcebergInconsistencyMeasure(IcebergInconsistencyMeasure.ConsequenceOperation.CONJUNCTS);
		System.out.println("Conjuncts\n========");
		System.out.println("*-conflicts: " + im.getStarConflicts(kb1));
		System.out.println("IM: " + im.inconsistencyMeasure(kb1));
		im = new IcebergInconsistencyMeasure(IcebergInconsistencyMeasure.ConsequenceOperation.MODULAR_CLASSICAL_CONSEQUENCE);
		System.out.println("Modular classical consequence\n========");
		System.out.println("*-conflicts: " + im.getStarConflicts(kb1));
		System.out.println("IM: " + im.inconsistencyMeasure(kb1));
		
		// Sum variant of the inconsistency measure
		im = new IcebergInconsistencyMeasure(IcebergInconsistencyMeasure.ConsequenceOperation.CONJUNCTS, true);
		System.out.println("Conjuncts with the sum variant of the IM\n========");
		System.out.println("IM:" + im.inconsistencyMeasure(kb1));
		
		// Example with a non-modular consequence operation
		PlBeliefSet kb3 = parser.parseBeliefBase("x1 \n"
				+ "x1 => x2\n"
				+ "x1 && (x2 => x3)\n"
				+ "!x3");
		im = new IcebergInconsistencyMeasure(IcebergInconsistencyMeasure.ConsequenceOperation.MODUS_PONENS);
		System.out.println("Modus ponens\n========");
		System.out.println("*-conflicts: " + im.getStarConflicts(kb3));
		System.out.println("IM: " + im.inconsistencyMeasure(kb3));
	}
}
