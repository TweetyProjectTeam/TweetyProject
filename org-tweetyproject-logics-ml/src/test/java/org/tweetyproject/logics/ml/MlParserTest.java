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
 package org.tweetyproject.logics.ml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.ml.parser.MlParser;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;
import org.tweetyproject.logics.ml.syntax.MlFormula;

/**
 * JUnit Test class for ModalParser.
 * 
 *  @author Anna Gessler
 */
public class MlParserTest {
	
	MlParser parser;
	public static final int DEFAULT_TIMEOUT = 5000;
	
	@Before
	public void initParser() {
		parser = new MlParser();
		FolSignature sig = new FolSignature(true);
		Sort sortAnimal = new Sort("Animal");
		sig.add(sortAnimal); 
		Constant constantPenguin = new Constant("penguin",sortAnimal);
		Constant constantKiwi = new Constant("kiwi",sortAnimal);
		sig.add(constantPenguin);
		sig.add(constantKiwi);
		List<Sort> predicateList = new ArrayList<Sort>();
		predicateList.add(sortAnimal);
		Predicate p = new Predicate("Flies",predicateList);
		sig.add(p); 
		List<Sort> predicateList2 = new ArrayList<Sort>();
		predicateList2.add(sortAnimal);
		predicateList2.add(sortAnimal);
		Predicate p2 = new Predicate("Knows",predicateList2);
		sig.add(p2); 
		Predicate p3 = new Predicate("SunIsShining");
		sig.add(p3); 
		parser.setSignature(sig);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseModalFormulaTest() throws ParserException, IOException {
		MlFormula f1 = (MlFormula)parser.parseFormula("[](Flies(penguin))");
		MlFormula f2 = (MlFormula)parser.parseFormula("<>(Flies(penguin))");
		Conjunction  f3 = (Conjunction) parser.parseFormula("!SunIsShining && <>(SunIsShining)");
		assertTrue(f1.containsModalityOperator());
		assertTrue(f2.containsModalityOperator());
		assertTrue(f1.getSignature().containsPredicate("Flies"));
		assertTrue(f1.getSignature().containsConstant("penguin"));
		assertTrue(f2.getSignature().containsPredicate("Flies"));
		assertTrue(f2.getSignature().containsConstant("penguin"));
		MlFormula f5c2 = (MlFormula)f3.get(1);
		assertTrue(f5c2.containsModalityOperator());
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void XorModalTest() throws ParserException, IOException {
		RelationalFormula f1 = (RelationalFormula)parser.parseFormula("([](Flies(penguin))) ^^ (<>(Flies(penguin)))");
		assertTrue(f1 instanceof ExclusiveDisjunction);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void NestedModalFormulaTest() throws ParserException, IOException {
		FolFormula f1= (FolFormula) parser.parseFormula("<>(SunIsShining)||[](SunIsShining)&& <>(Knows(kiwi,penguin) || Flies(penguin))");
		FolFormula f2 = (FolFormula) parser.parseFormula("[](Flies(kiwi)) || forall X:(==(penguin,X))");
		FolFormula f3 = (FolFormula) parser.parseFormula("[](<>(!Flies(kiwi)) && forall X:(Knows(kiwi,X)) || SunIsShining)");
		FolFormula f4 = (FolFormula) parser.parseFormula("SunIsShining && [](forall Y:(Flies(Y)) || forall X:(Knows(penguin,X)))");
		FolFormula f5 = (FolFormula)parser.parseFormula("exists BIRD:(forall MyVar :([](Knows(BIRD,MyVar))))");
		
		assertTrue(f1.getSignature().containsPredicate("SunIsShining"));
		assertTrue(f1.getSignature().containsPredicate("Flies"));
		assertTrue(f1.getSignature().containsPredicate("Knows"));
		assertTrue(f1.getSignature().containsConstant("kiwi"));
		assertTrue(f1.getSignature().containsConstant("penguin"));
		assertTrue(f2.containsQuantifier());
		assertTrue(f2.getSignature().containsPredicate("Flies"));
		assertFalse(f2.getSignature().containsPredicate("Knows"));   
		assertTrue(f2.getSignature().containsPredicate("=="));  
		assertTrue(f3.getSignature().containsPredicate("Flies"));
		assertTrue(f3.getSignature().containsPredicate("Knows"));
		assertTrue(f3.getSignature().containsPredicate("SunIsShining"));
		assertTrue(f4.getSignature().containsPredicate("Flies"));
		assertTrue(f4.getSignature().containsPredicate("Knows"));
		assertTrue(f4.getSignature().containsPredicate("SunIsShining"));
		assertTrue(f5.getSignature().containsPredicate("Knows"));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseBeliefBaseFromFileTest() throws ParserException, IOException {
		parser = new MlParser();
		MlBeliefSet b = (MlBeliefSet) parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase.mlogic");
		assertEquals(b.size(),5);
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
