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
package net.sf.tweety.logics.dl.examples;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.dl.syntax.DlSignature;
import net.sf.tweety.logics.dl.syntax.EquivalenceAxiom;
import net.sf.tweety.logics.dl.syntax.Individual;
import net.sf.tweety.logics.dl.syntax.Union;
import net.sf.tweety.logics.dl.syntax.AtomicConcept;
import net.sf.tweety.logics.dl.syntax.AtomicRole;
import net.sf.tweety.logics.dl.syntax.Complement;
import net.sf.tweety.logics.dl.syntax.AssertionalAxiom;
import net.sf.tweety.logics.dl.syntax.DlBeliefSet;
/**
 * 
 * Examples for using the description logic syntax classes, parser and NaiveReasoner (WIP).
 * 
 * @author Anna Gessler
 *
 */
public class DlExample {

	public static void main(String[] args) throws ParserException, IOException {
		
		//Create description logics signature
		AtomicConcept human = new AtomicConcept("Human");
		AtomicConcept male = new AtomicConcept("Male");
		AtomicConcept female = new AtomicConcept("Female");
		AtomicConcept house = new AtomicConcept("House");
		AtomicConcept father = new AtomicConcept("Father");
		AtomicRole fatherOf = new AtomicRole("fatherOf");
		AtomicRole owns = new AtomicRole("owns");
		Individual bob = new Individual("Bob");
		Individual alice = new Individual("Alice");
		Set<AtomicConcept> concepts = new HashSet<AtomicConcept>();
		Set<AtomicRole> roles = new HashSet<AtomicRole>();
		Set<Individual> individuals = new HashSet<Individual>();
		DlSignature dsig = new DlSignature(concepts,roles,individuals);

		//Create some terminological axioms
		EquivalenceAxiom femaleHuman = new EquivalenceAxiom(female,human);
		EquivalenceAxiom maleHuman = new EquivalenceAxiom(male,human);
		EquivalenceAxiom fatherEq = new EquivalenceAxiom(father, new Union(male,fatherOf));
		EquivalenceAxiom houseNotHuman = new EquivalenceAxiom(house,new Complement(house));
		
		//Create some assertional axioms
		AssertionalAxiom aliceHuman = new AssertionalAxiom(human,alice);
		AssertionalAxiom bobHuman = new AssertionalAxiom(human,bob);
		AssertionalAxiom aliceFemale = new AssertionalAxiom(female,alice);
		AssertionalAxiom bobMale = new AssertionalAxiom(male,bob);
		AssertionalAxiom bobFatherOfAlice = new AssertionalAxiom(fatherOf,bob,alice);
		
		//Add axioms to knowledge base
		DlBeliefSet dbs = new DlBeliefSet();
		dbs.add(femaleHuman);
		dbs.add(maleHuman);
		dbs.add(fatherEq);
		dbs.add(houseNotHuman);
		dbs.add(aliceHuman);
		dbs.add(bobHuman);
		dbs.add(aliceFemale);
		dbs.add(bobMale);
		dbs.add(bobFatherOfAlice);
			
		//Print knowledge base
		System.out.println(dbs);
		System.out.println("Only the ABox: " + dbs.getABox());
		System.out.println("Only the TBox: " + dbs.getTBox());	
		
		/**
		 * Parser
		 * To be completed soon
		 */
		//Parse knowledge base
//		DlParser parser = new DlParser();
//		DlBeliefSet parseddbs = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase.dlogic");
//		DlSignature parsedsig = (DlSignature) parseddbs.getSignature();
//		System.out.println("\nParsed knowledge base: " + parseddbs);
//		System.out.println("\nParsed signature: " + parsedsig.getIndividuals() + ","+ parsedsig.getConcepts() + "," + parsedsig.getRoles());
		
	}
}
