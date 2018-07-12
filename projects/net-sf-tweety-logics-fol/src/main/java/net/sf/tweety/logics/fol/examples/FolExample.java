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
package net.sf.tweety.logics.fol.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.prover.FolTheoremProver;
import net.sf.tweety.logics.fol.prover.NaiveProver;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * Some examples for using FolParser and provers.
 * 
 */
public class FolExample {
	
	public static void main(String[] args) throws ParserException, IOException{
		/*
		 * Example 1: Add sorts, constants and predicates to a first-order logic signature
		 */
		FolSignature sig = new FolSignature(true); //Create new FOLSignature with equality
		Sort s_animal = new Sort("Animal");
		sig.add(s_animal); 
		Constant c_penguin = new Constant("penguin",s_animal);
		Constant c_kiwi = new Constant("kiwi",s_animal);
		sig.add(c_penguin);
		sig.add(c_kiwi);
		List<Sort> predicate_list = new ArrayList<Sort>();
		predicate_list.add(s_animal);
		Predicate p = new Predicate("Flies",predicate_list);
		sig.add(p); //Add Predicate Flies(Animal) 
		List<Sort> predicate_list2 = new ArrayList<Sort>();
		predicate_list2.add(s_animal);
		predicate_list2.add(s_animal);
		Predicate p2 = new Predicate("Knows",predicate_list2);
		sig.add(p2); //Add Predicate Knows(Animal,Animal) 
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
		bs.add(f1);
		bs.add(f2);
		bs.add(f3);
		bs.add(f4);
		bs.add(f5);
		System.out.println("Parsed BeliefBase: " + bs);

		/*
		 * Example 3: Use one of the provers to check whether various formulas can be inferred from the knowledge base parsed in Example 2. 
		 */
		FolTheoremProver.setDefaultProver(new NaiveProver()); //Set default prover, options are NaiveProver, EProver, Prover9
		FolTheoremProver prover = FolTheoremProver.getDefaultProver();
		System.out.println("ANSWER 1: " + prover.query(bs, (FolFormula)parser.parseFormula("Flies(kiwi)")));
		System.out.println("ANSWER 2: " + prover.query(bs, (FolFormula)parser.parseFormula("forall X: (exists Y: (Flies(X) && Flies(Y) && X/==Y))")));
		System.out.println("ANSWER 3: " + prover.query(bs, (FolFormula)parser.parseFormula("kiwi == kiwi")));
		System.out.println("ANSWER 4: " + prover.query(bs, (FolFormula)parser.parseFormula("kiwi /== kiwi")));
		System.out.println("ANSWER 5: " + prover.query(bs, (FolFormula)parser.parseFormula("penguin /== kiwi")));
		
		/*
		 * Example 4: Parse another BeliefBase from a file. The signature is also parsed from the file.
		 * Then prove/disprove some queries on the knowledge bases.
		 */
		parser = new FolParser();
		parser.setSignature(new FolSignature(true));
		bs = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase2.fologic");
		System.out.println("Parsed BeliefBase: " + bs);
		FolFormula query = (FolFormula)parser.parseFormula("isTeacher(alice)");
		System.out.println("Query: " + query + "\n" + prover.query(bs,query));
		query = (FolFormula)parser.parseFormula("exists X:(teaches(bob,X))");
		System.out.println("Query: " + query + "\n" + prover.query(bs,query));
		query = (FolFormula)parser.parseFormula("isTeacher(bob)");
		System.out.println("Query: " + query + "\n" + prover.query(bs,query));
		query = (FolFormula)parser.parseFormula("exists X:(exists Y:(hasID(alice,X) && hasID(alice,Y) && X/==Y))");
		System.out.println("Query: " + query + "\n" + prover.query(bs,query));			
	}
}
