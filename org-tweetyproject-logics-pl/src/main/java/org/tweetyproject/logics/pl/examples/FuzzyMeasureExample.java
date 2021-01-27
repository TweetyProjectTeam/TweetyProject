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
import org.tweetyproject.logics.pl.analysis.FuzzyInconsistencyMeasure;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.math.func.fuzzy.ProductNorm;

/**
 * Example code illustrating fuzzy logic-based inconsistency measures.
 * 
 * @author Matthias Thimm
 */
public class FuzzyMeasureExample {
	public static void main(String[] args) throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		
		bs.add((PlFormula) parser.parseFormula("a && !a"));
		bs.add((PlFormula) parser.parseFormula("!(!(a && !a))"));
		bs.add((PlFormula) parser.parseFormula("!(!(!(!(a && !a))))"));
		bs.add((PlFormula) parser.parseFormula("!(!(!(!(!(!(a && !a))))))"));
		bs.add((PlFormula) parser.parseFormula("!(!(!(!(!(!(!(!(a && !a))))))))"));
		bs.add((PlFormula) parser.parseFormula("a && a"));
		
		System.out.println(bs);
		
		FuzzyInconsistencyMeasure mes = new FuzzyInconsistencyMeasure(new ProductNorm(),FuzzyInconsistencyMeasure.SUMFUZZY_MEASURE);
		
		System.out.println(mes.inconsistencyMeasure(bs));
		
		System.out.println();
		
		System.out.println(mes.getOptimalInterpretation(bs));
		
	}
}
