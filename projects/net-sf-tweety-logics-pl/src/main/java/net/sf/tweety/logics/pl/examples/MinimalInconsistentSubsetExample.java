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
package net.sf.tweety.logics.pl.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.NaiveMusEnumerator;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.util.CnfSampler;

public class MinimalInconsistentSubsetExample {

	public static void main(String[] args) throws ParserException, IOException{
		PropositionalSignature sig = new PropositionalSignature();
		for(int i = 0; i < 5; i++)
			sig.add(new Proposition("a" + i));
		CnfSampler sampler = new CnfSampler(sig,0.8,30, 30);
		PlBeliefSet kb = sampler.next();
		System.out.println(kb);
		System.out.println();
		
		NaiveMusEnumerator<PropositionalFormula> enumerator = new NaiveMusEnumerator<PropositionalFormula>(SatSolver.getDefaultSolver());
		
		long millis = System.currentTimeMillis();		
		System.out.println(enumerator.minimalInconsistentSubsets(kb));
		System.out.println(System.currentTimeMillis()-millis);		
	}
}
