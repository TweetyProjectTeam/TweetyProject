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

import java.io.File;
import java.io.IOException;

import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.util.SyntacticEnumeratingIterator;

/**
 * Illustrates the use of the belief base enumerators.
 * @author Matthias Thimm
 */
public class SyntacticEnumeratingPlBeliefSetSamplerExample {
	/**
	 * main
	 * @param args arguments
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws IOException{
		// generates all syntactic variations of propositional belief sets 
		//  each formula has maximal length 4, and 4 propositions		
		PlSignature sig = new PlSignature(4);
		SyntacticEnumeratingIterator s = new SyntacticEnumeratingIterator(sig,4, new File("/Users/mthimm/Desktop/plfiles/"), false);
		int i = 0;
		while(true){
			System.out.println(i++ + "\t" + s.next());			
		}
	}

    /** Default Constructor */
    public SyntacticEnumeratingPlBeliefSetSamplerExample(){}
}
