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
 package net.sf.tweety.logics.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.el.parser.ModalParser;
import net.sf.tweety.logics.el.syntax.ModalFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * JUnit Test class for ModalParser.
 * 
 *  @author Anna Gessler
 */
public class ModalParserTest {
	
	ModalParser parser;
	public static final int DEFAULT_TIMEOUT = 5000;
	
	@Before
	public void initParser() {
		parser = new ModalParser();
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
		sig.add(p); 
		List<Sort> predicate_list2 = new ArrayList<Sort>();
		predicate_list2.add(s_animal);
		predicate_list2.add(s_animal);
		Predicate p2 = new Predicate("Knows",predicate_list2);
		sig.add(p2); 
		parser.setSignature(sig);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseModalOperatorTest() throws ParserException, IOException {
		ModalFormula f1 = (ModalFormula)parser.parseFormula("[](Flies(penguin))");
		ModalFormula f2 = (ModalFormula)parser.parseFormula("<>(Flies(penguin))");
		assertTrue(f1.containsModalityOperator());
		assertTrue(f2.containsModalityOperator());
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void NestedModalFormulaTest() throws ParserException, IOException {
		RelationalFormula f1 = (RelationalFormula) parser.parseFormula("forall X: (<>(Flies(X)))");
		RelationalFormula f2 = (RelationalFormula) parser.parseFormula("[](forall X: ([](Flies(X))))");
		RelationalFormula f3 = (RelationalFormula) parser.parseFormula("[](<>((!Flies(kiwi)) || (Knows(penguin,kiwi))))");
		FolSignature sig = (FolSignature) f3.getSignature();
		assertTrue(f1.containsQuantifier());
		assertTrue(f2.containsQuantifier());
		assertTrue(sig.containsConstant("kiwi"));
		assertTrue(sig.containsConstant("penguin"));
		assertTrue(sig.containsPredicate("Flies"));
		assertTrue(sig.containsPredicate("Knows"));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseBeliefBaseFromFileTest() throws ParserException, IOException {
		parser = new ModalParser();
		ModalBeliefSet b = (ModalBeliefSet) parser.parseBeliefBaseFromFile("examplebeliefbase.mlogic");
		assertEquals(b.size(),3);
	}
	
	@Test(expected = ParserException.class, timeout = DEFAULT_TIMEOUT) 
	public void EmptyPossibilityTest() throws ParserException, IOException {
		parser.parseFormula("<>()");
	}	
	
	@Test(expected = ParserException.class, timeout = DEFAULT_TIMEOUT) 
	public void EmptyNecessityTest() throws ParserException, IOException {
		parser.parseFormula("[]()");
	}
	
	@Test(expected = ParserException.class, timeout = DEFAULT_TIMEOUT) 
	public void MissingCharacterTest() throws ParserException, IOException {
		parser.parseFormula("[()");
	}
	@Test(expected = ParserException.class, timeout = DEFAULT_TIMEOUT) 
	public void WrongCharacterOrderTest() throws ParserException, IOException {
		parser.parseFormula("><()");
	}
}
