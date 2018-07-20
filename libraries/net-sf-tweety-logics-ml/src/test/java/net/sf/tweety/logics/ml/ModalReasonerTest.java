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
package net.sf.tweety.logics.ml;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.ml.parser.ModalParser;
import net.sf.tweety.logics.ml.reasoner.NaiveModalReasoner;
import net.sf.tweety.logics.ml.syntax.ModalBeliefSet;

/**
 * JUnit Test class for NaiveModalReasoner.
 * 
 *  @author Anna Gessler
 */
public class ModalReasonerTest {
	
	public static final int DEFAULT_TIMEOUT = 10000;
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleQueryTest1() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("type(p) \n !(<>(p))");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();		
		Boolean a1 = reasoner.query(b,(FolFormula) parser.parseFormula("<>(p)"));
		Boolean a2 = reasoner.query(b,(FolFormula) parser.parseFormula("!(<>(p))"));
		assertFalse(a1);
		assertTrue(a2);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleQueryTest2() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("type(p) \n p");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();		
		Boolean  a1 = reasoner.query(b,(FolFormula) parser.parseFormula("<>(p)"));
		Boolean  a2 = reasoner.query(b,(FolFormula) parser.parseFormula("[](p)"));
		assertFalse(a1);
		assertTrue(a2);
	}
	
	@Test(timeout = 20000)
	public void SimpleQueryTest3() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("Animal = {duffy,martin} \n type(Flies(Animal)) \n <>(Flies(martin))");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();
		Boolean  a1 = reasoner.query(b,(FolFormula) parser.parseFormula("Flies(duffy)"));
		Boolean  a2 = reasoner.query(b,(FolFormula) parser.parseFormula("(Flies(duffy)) || (!(Flies(duffy)))"));
		assertFalse(a1);
		assertTrue(a2);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleQueryTest4() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("Animal = {duffy,martin} \n type(Flies(Animal)) \n Flies(martin)\n Flies(duffy)");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();
		Boolean  a1 = reasoner.query(b,(FolFormula) parser.parseFormula("forall X:(Flies(X))"));
		assertTrue(a1);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ImplicationTest() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("type(p) \n type(q) \n p \n !p||q");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();		
		Boolean  a1 = reasoner.query(b,(FolFormula) parser.parseFormula("p<=>p"));
		Boolean  a2 = reasoner.query(b,(FolFormula) parser.parseFormula("!p=>!p"));
		Boolean  a3 = reasoner.query(b,(FolFormula) parser.parseFormula("!p=>p"));
		Boolean  a4 = reasoner.query(b,(FolFormula) parser.parseFormula("p=>q"));
		assertTrue(a1);
		assertTrue(a2);
		assertTrue(a3);
		assertTrue(a4);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void TautologyAsQueryTest() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("Animal = {duffy,martin} \n type(Flies(Animal)) \n <>(Flies(martin))");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();
		Boolean  a1 = reasoner.query(b,(FolFormula) parser.parseFormula("+"));
		assertEquals(a1,true);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ContradictionAsQueryTest() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("Animal = {duffy,martin} \n type(Flies(Animal)) \n <>(Flies(martin))");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();
		Boolean  a1 = reasoner.query(b,(FolFormula) parser.parseFormula("-"));
		assertEquals(a1,false);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void EmptyBeliefBaseTest() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();
		Boolean  a1 = reasoner.query(b,(FolFormula) parser.parseFormula("+"));
		assertEquals(a1,true);
	}
	
	@Test(expected=ParserException.class, timeout = DEFAULT_TIMEOUT)
	public void UnrecognizedQueryTest() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("Animal = {duffy,martin} \n type(Flies(Animal)) \n <>(Flies(martin))");
		NaiveModalReasoner reasoner = new NaiveModalReasoner();
		Boolean  a1 = reasoner.query(b,(FolFormula) parser.parseFormula("Knows(duffy,martin)"));
		assertEquals(a1,true);
	}
	
	
}
