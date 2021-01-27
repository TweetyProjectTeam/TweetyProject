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
import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.analysis.DHitInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.DMaxInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.DSumInconsistencyMeasure;
import org.tweetyproject.logics.pl.analysis.DalalDistance;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.semantics.PossibleWorldIterator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * Example code illustrating the distance-based inconsistency measures DSum, DMax and DHit.
 * 
 * @author Matthias Thimm
 */
public class DSumMeasureExample {

	public static void main(String[] args) throws ParserException, IOException{
		Collection<PlFormula> c = new HashSet<PlFormula>();
		PlParser parser = new PlParser();
		c.add((PlFormula) parser.parseFormula("a && b && c"));
		c.add((PlFormula) parser.parseFormula("!a && !b && !c"));
				
		PlSignature sig = new PlSignature();
		for(PlFormula f: c) sig.addAll(f.getSignature().toCollection());
		
		DSumInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula> inc1 = new DSumInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula>(new DalalDistance(),new PossibleWorldIterator(sig));
		DMaxInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula> inc2 = new DMaxInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula>(new DalalDistance(),new PossibleWorldIterator(sig));
		DHitInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula> inc3 = new DHitInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula>(new DalalDistance(),new PossibleWorldIterator(sig));
		
		System.out.println(inc1.inconsistencyMeasure(c));
		System.out.println(inc2.inconsistencyMeasure(c));
		System.out.println(inc3.inconsistencyMeasure(c));
		
	}
}
