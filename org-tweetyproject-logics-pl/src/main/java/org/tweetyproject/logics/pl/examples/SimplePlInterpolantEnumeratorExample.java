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
package org.tweetyproject.logics.pl.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.analysis.SimplePlInterpolantEnumerator;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * 
 * Illustrates the use of the SimplePlInterpolantEnumerator
 * @author Matthias Thimm
 *
 */
public class SimplePlInterpolantEnumeratorExample {
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException {
		PlParser parser = new PlParser();
		
		PlBeliefSet k1 = new PlBeliefSet();
		k1.add(parser.parseFormula("a && b"));
		k1.add(parser.parseFormula("b => c"));
		
		PlBeliefSet k2 = new PlBeliefSet();
		k2.add(parser.parseFormula("!c && b"));
		k2.add(parser.parseFormula("d => f"));
		
		SimplePlInterpolantEnumerator en = new SimplePlInterpolantEnumerator(new SimplePlReasoner());
		
		for(PlFormula f: en.getInterpolants(k1, k2))
			System.out.println(f);
		
		System.out.println();
		
		System.out.println("Strongest interpolant: " + en.getStrongestInterpolant(k1, k2));
		System.out.println("Weakest interpolant: " + en.getWeakestInterpolant(k1, k2));
	}
}
