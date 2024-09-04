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
import java.util.List;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * Some general examples for using basic propositional logic classes like PlParser and SimplePlReasoner.
 * 
 * @author Anna Gessler
 *
 */
public class PlExample {
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException {
		//Manually create formulas and belief base
		PlBeliefSet beliefSet = new PlBeliefSet();
		Proposition f1 = new Proposition("a");
		Negation f2 = new Negation(f1);
		Conjunction c = new Conjunction();
		c.add(f1, f2, new Proposition("b"));
		Implication i = new Implication(f2, new Proposition("c"));
		beliefSet.add(f1,f2,c,i);
		System.out.println(beliefSet + "\n");
		
		//Parse belief base from string
		PlParser parser = new PlParser();	
		beliefSet = parser.parseBeliefBase("a || b || c \n !a || b \n !b || c \n !c || (!a && !b && !c && !d)");
		System.out.println(beliefSet);
		
		//Parse belief base from file
		beliefSet = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase.proplogic");
		System.out.println(beliefSet);
		
		//Parse list of belief bases from file
		List<PlBeliefSet> beliefSets = parser.parseListOfBeliefBasesFromFile("src/main/resources/examplebeliefbase_multiple.proplogic");
		System.out.println(beliefSets);
		
		//Parse list of belief bases using a custom delimiter
		beliefSets = parser.parseListOfBeliefBases("a || b \n a && !a ##### c => d", "#####");
		System.out.println(beliefSets);
		
		//Note that belief bases can have signatures larger than their formulas' signature
		PlSignature sig = beliefSet.getSignature();
		sig.add(new Proposition("f"));
		beliefSet.setSignature(sig);
		System.out.println(beliefSet);
		System.out.println("Minimal signature: " + beliefSet.getMinimalSignature());
		//...but not smaller (commented out line throws exception)
		sig.remove(new Proposition("a"));
		//beliefSet2.setSignature(sig);
		
		//Use simple inference reasoner
		SimplePlReasoner reasoner = new SimplePlReasoner();
		PlFormula query = new Negation(new Proposition("a"));
		Boolean answer1 = reasoner.query(beliefSet, query);
		System.out.println(answer1);
		Boolean answer2 = reasoner.query(beliefSet, new Contradiction());
		System.out.println(answer2);
		
		//Examples for using XOR
		System.out.println();
		PlFormula xor = parser.parseFormula("a ^^ b ^^ c");
		System.out.println("parsed formula: " + xor);
		System.out.println("dnf: " +  xor.toDnf());
		System.out.println("cnf: " + xor.toCnf());
		System.out.println("nnf: " +  xor.toNnf());
		System.out.println("models :" + xor.getModels());
		xor = parser.parseFormula("a ^^ b ^^ c ^^ d ^^ e ^^ f");
		System.out.println("parsed formula: " + xor);
		System.out.println("models: " + xor.getModels());
		System.out.println();
		beliefSet = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase_xor.proplogic");
		System.out.println(beliefSet);
	}

    /** Default Constructor */
    public PlExample(){}
}
