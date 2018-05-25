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
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.parser.TPTPParser;
import net.sf.tweety.logics.fol.prover.FolTheoremProver;
import net.sf.tweety.logics.fol.prover.NaiveProver;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * 
 * Examples for using TPTPParser.
 * 
 * @author Anna Gessler
 *
 */
public class TPTPParserExample {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		//Set signature for the TPTPParser
		FolParser folparser = new FolParser();
		FolSignature sig = folparser.parseSignature("Thing = {a, b}\n"
				+ "type(p(Thing)) \n type(q(Thing)) \n type(r)");
		
		TPTPParser tptp = new TPTPParser();
		tptp.setSignature(sig);
		System.out.println("Using signature: " + sig);
		
		//Parse belief base in TPTP syntax
		FolBeliefSet tptpbs = tptp.parseBeliefBase("%---some comments \n"
				+ "%----more comments \n"
				//Include some formulas from another file
				+ "include('src/main/resources/tptpexample.fologic',[formula2,formula3]).\n" 	
				+ "fof(myname,axiom,(p(b) & r)).\n"
				+ "fof(myname,axiom,(~q(a) & r | ~(r))).\n"
				+ "fof(myname,axiom,(~p(a) & r | r)).\n"
				+ "fof(myname,axiom,(r <=> ~q(a))).\n"
				+ "% random comment \n"
				+ "fof(myname,axiom,((~p(a) => r) & (~p(b) <= r))).\n"
				+ "fof(myname,axiom,(? [X] : (q(X)) & r => p(b))).\n"
				+ "fof(myname,axiom,(r | ! [Y] : (p(Y)))).\n");
		System.out.println("Parsed belief base: " + tptpbs);
		
		//Parse belief base in TPTP syntax but only parse axiom type formulas
		String axiomRoles = "axiom|hypothesis|definition|assumption|lemma|theorem|corollary";
		tptp.setFormulaRoles(axiomRoles);
		FolBeliefSet axioms = tptp.parseBeliefBase("fof(f1,axiom,(r=>p(a))).\n"
				+ "fof(f1,axiom,(r)).\n"
				+ "fof(f2,conjecture,($false)).");
		System.out.println("Only axioms: " + axioms);
		
		//Parse belief base in TPTP syntax but only parse conjecture type formulas
		String conjectureRoles = "conjecture";
		tptp.setFormulaRoles(conjectureRoles);
		FolBeliefSet conjectures = tptp.parseBeliefBase("fof(f1,axiom,(r=>p(a))).\n"
				+ "fof(f2,conjecture,(p(a))).");
		System.out.println("Only conjectures: " + conjectures);
		FolFormula c1 = conjectures.iterator().next();
			
		//Prove that the conjecture follows from the axioms
		FolTheoremProver.setDefaultProver(new NaiveProver());
		FolTheoremProver prover = FolTheoremProver.getDefaultProver();
		System.out.println("ANSWER: " + prover.query(axioms,c1));
		
		//Parse a single formula in TPTP syntax
		tptp.resetFormulaRoles();
		FolFormula tautology = (FolFormula) tptp.parseFormula("fof(tautology,axiom,$true).");
		System.out.println("Tautology: " + tautology);
	}

}
