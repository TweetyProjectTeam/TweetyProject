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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.fol.parser.TPTPParser;
import org.tweetyproject.logics.fol.reasoner.EFOLReasoner;
import org.tweetyproject.logics.fol.reasoner.FolReasoner;
import org.tweetyproject.logics.fol.reasoner.SimpleFolReasoner;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * 
 * Examples for using TPTPParser.
 * 
 * @author Anna Gessler
 *
 */
public class TPTPParserExample {
	/**
	 * 
	 * @param args arguments
	 * @throws FileNotFoundException FileNotFoundException
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		TPTPParser tptp = new TPTPParser();
		
		//Parse a belief base in TPTP syntax
		FolBeliefSet tptpbs = tptp.parseBeliefBase("%---some comments \n"
				+ "%----more comments \n"
				//Include formulas named formula2 and formula3 from another file
				+ "include('src/main/resources/tptpexample.fologic',[formula2,formula3]).\n" 
				+ "fof(formula1,axiom,(p(functor(b)) & r)).\n"
				+ "fof(formula2,axiom,(~'PredicateInSingleQuotes'(a,b) & r | ~(r))).\n"
				+ "fof(formula3,axiom,(~p(a) & r | r)).\n"
				+ "fof(formula4,axiom,(r <=> ~q(a))).\n"
				+ "% random comment \n"
				+ "fof(formula5,axiom,(predicate_of_arity3(a,b,a) | r)).\n"
				+ "fof(formula6,axiom,((~p(a) => r) & (~p(b) <= r))).\n"
				+ "fof(formula7,axiom,(? [X] : (q(X)) & r => p(b))).\n"
				+ "fof(formula8,axiom,(r | ! [Y] : (p(Y)))).\n");
		System.out.println("Parsed belief base: ");
		for (FolFormula f: tptpbs)
			System.out.println("\t" + f);
		System.out.println("Parsed signature: " + tptp.getSignature());
		System.out.println();
		
		//Parse a single formula in TPTP syntax
		tptp.resetFormulaRoles();
		FolFormula tautologyOrContradiction = (FolFormula) tptp.parseFormula("fof(tautology,axiom,$true|$false).");
		System.out.println("Single formula: " + tautologyOrContradiction);
		
		//Parse a belief base in TPTP syntax but only parse axiom type formulas
		String axiomRoles = "axiom|hypothesis|definition|assumption|lemma|theorem|corollary";
		tptp.setFormulaRoles(axiomRoles);
		FolBeliefSet axioms = tptp.parseBeliefBase("fof(f1,axiom,(r=>p(a))).\n"
				+ "fof(f1,axiom,(r)).\n"
				+ "fof(f2,conjecture,($false)).");
		System.out.println("Only axioms: " + axioms);
		
		//Parse a belief base in TPTP syntax but only parse conjecture type formulas
		String conjectureRoles = "conjecture";
		tptp.setFormulaRoles(conjectureRoles);
		FolBeliefSet conjectures = tptp.parseBeliefBase("fof(f1,axiom,(r=>p(a))).\n"
				+ "fof(f2,conjecture,(p(a))).");
		System.out.println("Only conjectures: " + conjectures);
		FolFormula c1 = conjectures.iterator().next();
			
		//Prove that the conjecture follows from the axioms
		FolReasoner.setDefaultReasoner(new SimpleFolReasoner());
		FolReasoner prover = FolReasoner.getDefaultReasoner();
		System.out.println("ANSWER: " + prover.query(axioms,c1));
			
		//Parse TPTP problem COM008+2
		tptp.setSignature(new FolSignature(true));	
		tptp.setFormulaRoles(axiomRoles);
		FolBeliefSet axioms2 = tptp.parseBeliefBaseFromFile("src/main/resources/tptpexample2.fologic");
		tptp.setFormulaRoles(conjectureRoles);
		FolBeliefSet conjecture2 = tptp.parseBeliefBaseFromFile("src/main/resources/tptpexample2.fologic");
		System.out.println("TPTP problem COM008+2:");
		for (FolFormula f : axioms2)
			System.out.println("\t" + f);
		System.out.println("\t" + conjecture2.iterator().next());
		System.out.println("Parsed signature: " + tptp.getSignature());
		System.out.println();
		
		FolReasoner.setDefaultReasoner(new EFOLReasoner("/home/anna/sw/folProver/E/PROVER/eprover"));
		prover = FolReasoner.getDefaultReasoner();
		System.out.println("ANSWER: " + prover.query(axioms2,conjecture2.iterator().next()) + "\n");
		
		//Parse TPTP problem NLP080+1 
		tptp.setSignature(new FolSignature(true));	
		tptp.resetFormulaRoles();
		axioms2 = tptp.parseBeliefBaseFromFile("src/main/resources/tptpexample3.fologic");
		System.out.println("TPTP problem NLP080+1 :" + axioms2);
		System.out.println("Parsed signature: " + tptp.getSignature());
		
		//Optional: set a signature for TPTPParser before parsing
		//FolParser folparser = new FolParser();
		//FolSignature sig = folparser.parseSignature("Thing = {a, b, c}\n"
		//		+ "type(p(Thing)) \n type(q(Thing)) \n type(r)");
		//tptp.setSignature(sig); 
	}

}
