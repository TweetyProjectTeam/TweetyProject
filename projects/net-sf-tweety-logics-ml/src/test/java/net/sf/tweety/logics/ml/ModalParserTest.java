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
 package net.sf.tweety.logics.ml;

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
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.ModalBeliefSet;
import net.sf.tweety.logics.ml.parser.ModalParser;
import net.sf.tweety.logics.ml.syntax.ModalFormula;

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
		Predicate p3 = new Predicate("Abba");
		sig.add(p3); 
		parser.setSignature(sig);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseModalFormulaTest() throws ParserException, IOException {
		ModalFormula f1 = (ModalFormula)parser.parseFormula("[](Flies(penguin))");
		ModalFormula f2 = (ModalFormula)parser.parseFormula("<>(Flies(penguin))");
		Conjunction  f3 = (Conjunction) parser.parseFormula("!Abba && <>(Abba)");
		assertTrue(f1.containsModalityOperator());
		assertTrue(f2.containsModalityOperator());
		assertTrue(f1.getSignature().containsPredicate("Flies"));
		assertTrue(f1.getSignature().containsConstant("penguin"));
		assertTrue(f2.getSignature().containsPredicate("Flies"));
		assertTrue(f2.getSignature().containsConstant("penguin"));
		ModalFormula f5c2 = (ModalFormula)f3.get(1);
		assertTrue(f5c2.containsModalityOperator());
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void NestedModalFormulaTest() throws ParserException, IOException {
		FolFormula f1= (FolFormula) parser.parseFormula("<>(Abba)||[](Abba)&& <>(Knows(kiwi,penguin) || Flies(penguin))");
		FolFormula f2 = (FolFormula) parser.parseFormula("[](Flies(kiwi)) || forall X:(Knows(penguin,X))");
		FolFormula f3 = (FolFormula) parser.parseFormula("[](<>(!Flies(kiwi)) && forall X:(Knows(kiwi,X)) || Abba)");
		FolFormula f4 = (FolFormula) parser.parseFormula("Abba && [](forall Y:(Flies(Y)) || forall X:(Knows(penguin,X)))");
		
		assertTrue(f1.getSignature().containsPredicate("Abba"));
		assertTrue(f1.getSignature().containsPredicate("Flies"));
		assertTrue(f1.getSignature().containsPredicate("Knows"));
		assertTrue(f1.getSignature().containsConstant("kiwi"));
		assertTrue(f1.getSignature().containsConstant("penguin"));
		assertTrue(f2.containsQuantifier());
		assertTrue(f2.getSignature().containsPredicate("Flies"));
		assertTrue(f2.getSignature().containsPredicate("Knows"));                                       
		assertTrue(f3.getSignature().containsPredicate("Flies"));
		assertTrue(f3.getSignature().containsPredicate("Knows"));
		assertTrue(f3.getSignature().containsPredicate("Abba"));
		assertTrue(f4.getSignature().containsPredicate("Flies"));
		assertTrue(f4.getSignature().containsPredicate("Knows"));
		assertTrue(f4.getSignature().containsPredicate("Abba"));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseBeliefBaseFromFileTest() throws ParserException, IOException {
		parser = new ModalParser();
		ModalBeliefSet b = (ModalBeliefSet) parser.parseBeliefBaseFromFile("../../examples/ml/examplebeliefbase.mlogic");
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
