/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.examples;

import java.io.FileNotFoundException;
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
	
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		//Add sorts, constants and predicates to a first-order logic signature
		FolSignature sig = new FolSignature();
		
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
		
		// Parse formulas with FolParser
		FolParser parser = new FolParser();
		parser.setSignature(sig); //Use the signature defined above
		FolBeliefSet bs = new FolBeliefSet();
		FolFormula f1 = (FolFormula)parser.parseFormula("!Flies(kiwi)");
		FolFormula f2 = (FolFormula)parser.parseFormula("!Flies(penguin)");
		FolFormula f3 = (FolFormula)parser.parseFormula("!Knows(penguin,kiwi)");
		bs.add(f1);
		bs.add(f2);
		bs.add(f3);
		System.out.println("Parsed BeliefBase: " + bs);
		
		// Prover
		FolTheoremProver.setDefaultProver(new NaiveProver()); //Set default prover, options are NaiveProver, EProver, Prover9
		FolTheoremProver prover = FolTheoremProver.getDefaultProver();
		System.out.println("ANSWER: " + prover.query(bs, (FolFormula)parser.parseFormula("Flies(kiwi)")));
		System.out.println("ANSWER: " + prover.query(bs, (FolFormula)parser.parseFormula("forall X: (Flies(X))")));
		
		// Parse a BeliefBase from a file
		parser = new FolParser();
		bs = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase.fologic");
		System.out.println("Parsed BeliefBase: " + bs);
	}
}
