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
import org.tweetyproject.logics.commons.analysis.McscInconsistencyMeasure;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.MarcoMusEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Example code illustrating the MCSC inconsistency measure.
 * 
 * @author Matthias Thimm
 */
public class McscInconsistencyMeasureExample {
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException{
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();		
		// Example of [Ammoura 2015]
		// ¬p ∨ ¬q, ¬p ∨ ¬r, ¬q ∨ ¬r, p, q, r
		// (note that the inconsistency value is 3 and not 4 as claimed in [Ammoura 2015]) 
		beliefSet.add((PlFormula)parser.parseFormula("!p || !q"));
		beliefSet.add((PlFormula)parser.parseFormula("!p || !r"));
		beliefSet.add((PlFormula)parser.parseFormula("!q || !r"));
		beliefSet.add((PlFormula)parser.parseFormula("p"));
		beliefSet.add((PlFormula)parser.parseFormula("q"));
		beliefSet.add((PlFormula)parser.parseFormula("r"));
		
		System.out.println(beliefSet);
		
		McscInconsistencyMeasure<PlFormula> i = new McscInconsistencyMeasure<PlFormula>(new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py"));
		
		System.out.println(i.inconsistencyMeasure(beliefSet));
		
	}
	

    /** Default Constructor */
    public McscInconsistencyMeasureExample(){}
}
