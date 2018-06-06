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
package net.sf.tweety.logics.fol.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.Implication;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.Tautology;
/**
 * JUnit Test class for FolParser.
 * 
 *  @author Anna Gessler
 */
public class FolParserTest {

	FolParser parser;
	public static final int DEFAULT_TIMEOUT = 5000;
	
	@Before
	public void initParser() {
		parser = new FolParser();
		FolSignature sig = new FolSignature(true); //Create new signature with equality
		Sort s_animal = new Sort("Animal");
		sig.add(s_animal); 
		Constant c_penguin = new Constant("penguin",s_animal);
		Constant c_kiwi = new Constant("kiwi",s_animal);
		sig.add(c_penguin);
		sig.add(c_kiwi);
		Predicate p1 = new Predicate("Abba");
		sig.add(p1);
		List<Sort> predicate_list = new ArrayList<Sort>();
		predicate_list.add(s_animal);
		Predicate p2 = new Predicate("Flies",predicate_list);
		sig.add(p2); 
		List<Sort> predicate_list2 = new ArrayList<Sort>();
		predicate_list2.add(s_animal);
		predicate_list2.add(s_animal);
		Predicate p3 = new Predicate("Knows",predicate_list2);
		sig.add(p3); 
		parser.setSignature(sig);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseForallQuantificationTest() throws ParserException, IOException {
		FolFormula f1 = (FolFormula)parser.parseFormula("forall X:(!Knows(kiwi,X) && Abba)");
		FolSignature sig = f1.getSignature();
		
		assertTrue(f1.containsQuantifier());
		assertTrue(sig.containsSort("Animal"));
		assertTrue(sig.containsConstant("kiwi"));
		assertFalse(sig.containsConstant("penguin"));
		assertTrue(sig.containsPredicate("Abba"));
		assertTrue(sig.containsPredicate("Knows"));
		assertFalse(sig.containsPredicate("Flies"));
		assertEquals(f1.getTerms().size(),2);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseExistsQuantificationTest() throws ParserException, IOException {
		FolFormula f1 = (FolFormula)parser.parseFormula("exists X:(!Knows(kiwi,X) && Abba)");
		FolSignature sig = f1.getSignature();
		
		assertTrue(f1.containsQuantifier());
		assertTrue(sig.containsSort("Animal"));
		assertTrue(sig.containsConstant("kiwi"));
		assertFalse(sig.containsConstant("penguin"));
		assertTrue(sig.containsPredicate("Knows"));
		assertTrue(sig.containsPredicate("Abba"));
		assertFalse(sig.containsPredicate("Flies"));
		assertEquals(f1.getTerms().size(),2);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT) 
	public void EqualityPredicateTest() throws ParserException, IOException {
		FolFormula f1 = (FolFormula) parser.parseFormula("/==(kiwi,penguin)||(kiwi == penguin)");
		FolFormula f2 = (FolFormula) parser.parseFormula("exists X: (!Flies(X) => (X==kiwi))");
		
		assertTrue(f1.getSignature().containsPredicate(LogicalSymbols.EQUALITY()));
		assertTrue(f1.getSignature().containsPredicate(LogicalSymbols.INEQUALITY()));
		assertTrue(f1.getSignature().containsConstant("kiwi"));
		assertTrue(f1.getSignature().containsConstant("penguin"));
		assertTrue(f2.containsQuantifier());
		assertTrue(f2.getSignature().containsPredicate(LogicalSymbols.EQUALITY()));
		assertTrue(f2.getSignature().containsPredicate("Flies"));
		assertTrue(f2.getSignature().containsConstant("kiwi"));
	}
	
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void TautologyTest() throws ParserException, IOException {
		FolFormula f = (FolFormula) parser.parseFormula("+");
		Tautology t = new Tautology();
		assertTrue(f.equals(t));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ContradictionTest() throws ParserException, IOException {
		FolFormula f = (FolFormula) parser.parseFormula("-");
		Contradiction c = new Contradiction();
		assertTrue(f.equals(c));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ImplicationTest() throws ParserException, IOException {
		Implication f = (Implication) parser.parseFormula("Abba=>Abba");
		FolSignature sig = f.getSignature();
		assertTrue(sig.containsPredicate("Abba"));
		assertEquals(f.getFormulas().getFirst(),parser.parseFormula("Abba"));
		assertEquals(f.getFormulas().getSecond(),parser.parseFormula("Abba"));
	}
	
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void NestedQuantifiedFormulaTest() throws ParserException, IOException {
		FolFormula f1 = (FolFormula)parser.parseFormula("exists X:(!Knows(kiwi,X)) && Abba");
		FolFormula f2 = (FolFormula)parser.parseFormula("Abba && forall X:(!Knows(kiwi,X))");
		FolFormula f3 = (FolFormula)parser.parseFormula("Abba || forall X:(Flies(X)) && Abba || Abba && exists Y:(Knows(Y,kiwi))");
		
		assertTrue(f1.containsQuantifier());
		assertTrue(f2.containsQuantifier());
		assertTrue(f3.containsQuantifier());
		assertTrue(f1.getSignature().containsPredicate("Abba"));
		assertTrue(f1.getSignature().containsPredicate("Knows"));
		assertTrue(f2.getSignature().containsPredicate("Abba"));
		assertTrue(f2.getSignature().containsPredicate("Knows"));
		assertTrue(f3.getSignature().containsPredicate("Abba"));
		assertTrue(f3.getSignature().containsPredicate("Knows"));
		assertTrue(f3.getSignature().containsPredicate("Flies"));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ParseBeliefBaseFromFileTest() throws ParserException, IOException {
		parser = new FolParser();
		FolBeliefSet beliefSet = new FolBeliefSet();
		beliefSet = parser.parseBeliefBaseFromFile("../../examples/fol/examplebeliefbase.fologic");
		assertEquals(beliefSet.size(),5);
		
		FolSignature sig = (FolSignature) beliefSet.getSignature();
		assertEquals(sig.getConstants().size(),4);
		assertEquals(sig.getPredicates().size(),4);
		assertEquals(sig.getSorts().size(),2);
		}

	@Test(expected = ParserException.class,timeout = DEFAULT_TIMEOUT) 
	public void EmptyQuantificationTest() throws ParserException, IOException {
		parser.parseFormula("forall X:()");
	}
	
	@Test(expected = ParserException.class) 
	public void WrongArityTest() throws ParserException, IOException {
		parser.parseFormula("Flies(kiwi,X)");
	}	
}
