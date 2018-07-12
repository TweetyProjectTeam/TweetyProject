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

import java.io.File;
import java.io.IOException;

import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.util.SyntacticEnumeratingIterator;

public class SyntacticEnumeratingPlBeliefSetSamplerExample {

	public static void main(String[] args) throws IOException{
		// generates all syntactic variations of propositional belief sets 
		//  each formula has maximal length 4, and 4 propositions		
		PropositionalSignature sig = new PropositionalSignature(4);
		SyntacticEnumeratingIterator s = new SyntacticEnumeratingIterator(sig,4, new File("/Users/mthimm/Desktop/plfiles/"), false);
		int i = 0;
		while(true){
			System.out.println(i++ + "\t" + s.next());			
		}
	}
}
