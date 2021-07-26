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
package org.tweetyproject.logics.fol.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.reasoner.FolReasoner;
import org.tweetyproject.logics.fol.reasoner.SimpleFolReasoner;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * Some examples for using FolParser and provers.
 * 
 */
public class FolExample {
	/**
	 * 
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException{
		/*
		 * Example 1: Add sorts, constants and predicates to a first-order logic signature
		 */
		//Create new FOLSignature with equality
		FolSignature sig = new FolSignature(true); 
		
		//Add sort
		Sort sortAnimal = new Sort("Animal");
		sig.add(sortAnimal); 
		
		//Add constants
		Constant constantPenguin = new Constant("penguin",sortAnimal);
		Constant constantKiwi = new Constant("kiwi",sortAnimal);
		sig.add(constantPenguin, constantKiwi);

		//Add predicates
		List<Sort> predicateList = new ArrayList<Sort>();
		predicateList.add(sortAnimal);
		Predicate p = new Predicate("Flies",predicateList);
		List<Sort> predicateList2 = new ArrayList<Sort>();
		predicateList2.add(sortAnimal);
		predicateList2.add(sortAnimal);
		Predicate p2 = new Predicate("Knows",predicateList2); //Add Predicate Knows(Animal,Animal) 
		sig.add(p, p2); 
		System.out.println("Signature: " + sig);
		
		/*
		 * Example 2: Parse formulas with FolParser using the signature defined above
		 */
		FolParser parser = new FolParser();
		parser.setSignature(sig); //Use the signature defined above
		FolBeliefSet bs = new FolBeliefSet();
		FolFormula f1 = (FolFormula)parser.parseFormula("!Flies(kiwi)");
		FolFormula f2 = (FolFormula)parser.parseFormula("Flies(penguin)");
		FolFormula f3 = (FolFormula)parser.parseFormula("!Knows(penguin,kiwi)");
		FolFormula f4 = (FolFormula)parser.parseFormula("/==(penguin,kiwi)");
		FolFormula f5 = (FolFormula)parser.parseFormula("kiwi == kiwi");
		bs.add(f1, f2, f3, f4, f5);
		System.out.println("\nParsed BeliefBase: " + bs);
		
		//Note that belief bases can have signatures larger (but not smaller) than their formulas' signature
		FolSignature sigLarger = bs.getSignature();
		sigLarger.add(new Constant("archaeopteryx",sortAnimal));
		bs.setSignature(sigLarger);
		System.out.println(bs);
		System.out.println("Minimal signature: " + bs.getMinimalSignature());

		/*
		 * Example 3: Use one of the provers to check whether various formulas can be inferred from the knowledge base parsed in Example 2. 
		 */
		FolReasoner.setDefaultReasoner(new SimpleFolReasoner()); //Set default prover, options are NaiveProver, EProver, Prover9
		FolReasoner prover = FolReasoner.getDefaultReasoner();
		System.out.println("ANSWER 1: " + prover.query(bs, (FolFormula)parser.parseFormula("Flies(kiwi)")));
		System.out.println("ANSWER 2: " + prover.query(bs, (FolFormula)parser.parseFormula("forall X: (exists Y: (Flies(X) && Flies(Y) && X/==Y))")));
		System.out.println("ANSWER 3: " + prover.query(bs, (FolFormula)parser.parseFormula("kiwi == kiwi")));
		System.out.println("ANSWER 4: " + prover.query(bs, (FolFormula)parser.parseFormula("kiwi /== kiwi")));
		System.out.println("ANSWER 5: " + prover.query(bs, (FolFormula)parser.parseFormula("penguin /== kiwi")));
		
		/*
		 * Example 4: Parse another BeliefBase from a file. The signature is also parsed from the file.
		 * Then prove/disprove some queries on the knowledge bases.
		 * Note: This may take a long time to compute.
		 */
		parser = new FolParser();
		parser.setSignature(new FolSignature(true));
		bs = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase2.fologic");
		System.out.println("\nParsed BeliefBase: " + bs);
		FolFormula query = (FolFormula)parser.parseFormula("exists X:(teaches(alice,X))");
		System.out.println("Query: " + query + "\nANSWER 1: " + prover.query(bs,query));
		query = (FolFormula)parser.parseFormula("exists X:(exists Y:(hasID(alice,X) && hasID(alice,Y) && X/==Y))");
		System.out.println("Query: " + query + "\nANSWER 2: " + prover.query(bs,query));
	}
}
