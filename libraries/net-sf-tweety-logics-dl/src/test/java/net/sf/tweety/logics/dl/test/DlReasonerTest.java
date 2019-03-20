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
package net.sf.tweety.logics.dl.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.dl.parser.DlParser;
import net.sf.tweety.logics.dl.reasoner.NaiveDlReasoner;
import net.sf.tweety.logics.dl.syntax.DlAxiom;
import net.sf.tweety.logics.dl.syntax.DlBeliefSet;

/**
 * JUnit Test class for NaiveDLReasoner.
 * 
 *  @author Anna Gessler
 */
public class DlReasonerTest {
	DlParser parser;
	NaiveDlReasoner reasoner;
	
	public static final int DEFAULT_TIMEOUT = 10000;
	
	@Before
	public void initParser() {
		parser = new DlParser();
		reasoner = new NaiveDlReasoner();
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleQueryTest() throws ParserException, IOException {
		DlBeliefSet kb = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase2.dlogic");
		DlAxiom f1 = parser.parseFormula("implies Mammal (not Bird)");
		DlAxiom f2 = parser.parseFormula("implies Cat Mammal");
		DlAxiom f3 = parser.parseFormula("instance sylvester Bird");
		DlAxiom f4 = parser.parseFormula("related sylvester tweety enemyOf");
		assertTrue(reasoner.query(kb, f1));
		assertTrue(reasoner.query(kb, f2));
		assertFalse(reasoner.query(kb, f3));
		assertFalse(reasoner.query(kb, f4));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComplexQueryTest() throws ParserException, IOException {
		DlBeliefSet kb = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase2.dlogic");
		DlAxiom f1 = parser.parseFormula("implies Bird (or Bird Mammal)");
		DlAxiom f2 = parser.parseFormula("implies Mammal (and Mammal Mammal)");
		DlAxiom f3 = parser.parseFormula("instance tweety (not (and Bird Mammal))");
		DlAxiom f4 = parser.parseFormula("instance sylvester (and Mammal (exists enemyOf Bird))");
		DlAxiom f5 = parser.parseFormula("instance tweety (and Bird (exists enemyOf Mammal))");
		DlAxiom f6 = parser.parseFormula("instance tweety (forall enemyOf (or Mammal Bird))");
		assertTrue(reasoner.query(kb, f1));
		assertTrue(reasoner.query(kb, f2));
		assertTrue(reasoner.query(kb, f3));
		assertFalse(reasoner.query(kb, f4));
		assertTrue(reasoner.query(kb, f5));
		assertFalse(reasoner.query(kb, f6));
	}
	
	

}
