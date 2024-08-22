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
import org.tweetyproject.logics.commons.analysis.NaiveMusEnumerator;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.util.CnfSampler;

/**
 * Example code illustrating the use of MUS enumerators.
 * @author Matthias Thimm
 *
 */
public class MinimalInconsistentSubsetExample {
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException{
		PlSignature sig = new PlSignature();
		for(int i = 0; i < 5; i++)
			sig.add(new Proposition("a" + i));
		CnfSampler sampler = new CnfSampler(sig,0.8,30, 30);
		PlBeliefSet kb = sampler.next();
		System.out.println(kb);
		System.out.println();
		
		NaiveMusEnumerator<PlFormula> enumerator = new NaiveMusEnumerator<PlFormula>(SatSolver.getDefaultSolver());
		
		long millis = System.currentTimeMillis();		
		System.out.println(enumerator.minimalInconsistentSubsets(kb));
		System.out.println(System.currentTimeMillis()-millis);		
	}

    /** Default Constructor */
    public MinimalInconsistentSubsetExample(){}
}
