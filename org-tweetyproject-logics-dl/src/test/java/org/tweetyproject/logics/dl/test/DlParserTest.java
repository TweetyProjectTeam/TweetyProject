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
package org.tweetyproject.logics.dl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.dl.parser.DlParser;
import org.tweetyproject.logics.dl.syntax.AtomicConcept;
import org.tweetyproject.logics.dl.syntax.AtomicRole;
import org.tweetyproject.logics.dl.syntax.Complement;
import org.tweetyproject.logics.dl.syntax.ConceptAssertion;
import org.tweetyproject.logics.dl.syntax.DlAxiom;
import org.tweetyproject.logics.dl.syntax.DlBeliefSet;
import org.tweetyproject.logics.dl.syntax.DlSignature;
import org.tweetyproject.logics.dl.syntax.EquivalenceAxiom;
import org.tweetyproject.logics.dl.syntax.ExistentialRestriction;
import org.tweetyproject.logics.dl.syntax.Individual;
import org.tweetyproject.logics.dl.syntax.Intersection;
import org.tweetyproject.logics.dl.syntax.RoleAssertion;
import org.tweetyproject.logics.dl.syntax.Union;
import org.tweetyproject.logics.dl.syntax.UniversalRestriction;

/**
 * JUnit test class for the description logics parser.
 * 
 *  @author Anna Gessler
 */
public class DlParserTest {
	DlParser parser;
	
	public static final int DEFAULT_TIMEOUT = 3000;
	
	@Before
	public void initParser() {
		parser = new DlParser();
		DlSignature sig = new DlSignature();
		AtomicConcept human = new AtomicConcept("Human");
		AtomicConcept male = new AtomicConcept("Male");
		AtomicConcept female = new AtomicConcept("Female");
		AtomicConcept house = new AtomicConcept("House");
		AtomicConcept father = new AtomicConcept("Father");
		AtomicRole fatherOf = new AtomicRole("fatherOf");
		Individual bob = new Individual("Bob");
		Individual alice = new Individual("Alice");
		sig.add(human);
		sig.add(male);
		sig.add(female);
		sig.add(house);
		sig.add(father);
		sig.add(fatherOf);
		sig.add(bob);
		sig.add(alice);
		parser.setSignature(sig);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseConceptAssertionTest() throws ParserException, IOException {
		DlAxiom parsedFormula = parser.parseFormula("instance Bob Human");
		ConceptAssertion bobHuman = new ConceptAssertion(new Individual("Bob"), new AtomicConcept("Human"));
		assertEquals(parsedFormula,bobHuman);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseRoleAssertionTest() throws ParserException, IOException {
		DlAxiom parsedFormula = parser.parseFormula("related Bob Alice fatherOf");
		RoleAssertion bobFatherOfAlice = new RoleAssertion(new Individual("Bob"),new Individual("Alice"),new AtomicRole("fatherOf"));
		assertEquals(parsedFormula,bobFatherOfAlice);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseEquivalenceTest() throws ParserException, IOException {
		DlAxiom parsedFormula = parser.parseFormula("implies Female Human");
		EquivalenceAxiom femaleHuman = new EquivalenceAxiom(new AtomicConcept("Female"), new AtomicConcept("Human"));
		assertEquals(parsedFormula,femaleHuman);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseComplexEquivalenceTest1() throws ParserException, IOException {
		EquivalenceAxiom parsedFormula = (EquivalenceAxiom) parser.parseFormula("implies Human (and (not House) (exists fatherOf Human))");
		assertTrue(parsedFormula.getFormulas().getSecond() instanceof Intersection);
		Intersection i = (Intersection) (parsedFormula.getFormulas().getSecond());
		assertTrue(i.get(0) instanceof Complement);
		assertTrue(i.get(1) instanceof ExistentialRestriction);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseComplexEquivalenceTest2() throws ParserException, IOException {
		EquivalenceAxiom parsedFormula = (EquivalenceAxiom) parser.parseFormula("implies Human (or (forall fatherOf Human) (not House))");
		assertTrue(parsedFormula.getFormulas().getSecond() instanceof Union);
		Union u = (Union) (parsedFormula.getFormulas().getSecond());
		assertTrue(u.get(0) instanceof UniversalRestriction);
		assertTrue(u.get(1) instanceof Complement);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseBeliefBaseFromFileTest() throws ParserException, IOException {
		DlBeliefSet bs = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase.dlogic");
		DlSignature sig = (DlSignature) bs.getMinimalSignature();
		assertEquals(bs.size(),20);
		assertTrue(sig.containsConcept("Male"));
		assertTrue(sig.containsConcept("Female"));
		assertTrue(sig.containsRole("siblingOf"));
		assertTrue(sig.containsRole("marriedTo"));
		assertTrue(sig.containsRole("motherOf"));
		assertTrue(sig.containsRole("fatherOf"));
		assertTrue(sig.containsIndividual("philip_iv_of_spain"));
		assertTrue(sig.containsIndividual("mariana_of_austria"));
		assertTrue(sig.containsIndividual("charles_ii_of_spain"));
	}

}
