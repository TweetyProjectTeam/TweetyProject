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

import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.util.SyntacticRandomSampler;
import org.tweetyproject.math.probability.Probability;

/**
 * Illustrates the use of the belief base enumerators.
 * @author Matthias Thimm
 */
public class SyntacticRandomPlBeliefSetSamplerExample {
	/**
	 * main
	 * @param args arguments
	 */
	public static void main(String[] args){
		PlSignature sig = new PlSignature(4);
		SyntacticRandomSampler sampler = new SyntacticRandomSampler(sig,new Probability(0.2),new Probability(0.35),new Probability(0.35),0.5,1, 1);
		for(int i = 0; i< 10; i++)
			System.out.println(sampler.next());
		
	}
}
