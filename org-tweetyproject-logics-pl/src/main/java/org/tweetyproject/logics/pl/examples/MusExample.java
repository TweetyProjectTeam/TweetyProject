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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.analysis.MusEnumerator;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.MarcoMusEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Example code illustrating the use of MUS enumerators.
 * @author Matthias Thimm
 *
 */
public class MusExample {
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException{
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();
		for(int i = 0; i < 7; i++){
			beliefSet.add((PlFormula)parser.parseFormula("a"+i));
			beliefSet.add((PlFormula)parser.parseFormula("!a"+i));
		}
		beliefSet.add((PlFormula)parser.parseFormula("!a1 && !a2"));
		beliefSet.add((PlFormula)parser.parseFormula("!a3 || !a4"));
		beliefSet.add((PlFormula)parser.parseFormula("!a3 || !a4 || a5"));
		
		
		System.out.println(beliefSet);
		
		MusEnumerator<PlFormula> enumerator = new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py");
		
		System.out.println(enumerator.minimalInconsistentSubsets(beliefSet));		
	}

    /** Default Constructor */
    public MusExample(){}
}
