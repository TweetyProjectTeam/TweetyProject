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
 package net.sf.tweety.logics.fol.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.prover.FolTheoremProver;
import net.sf.tweety.logics.fol.prover.Prover9;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.writer.FolWriter;
import net.sf.tweety.logics.fol.writer.Prover9Writer;

/**
 * JUnitTest to test Prover9
 * @author Nils Geilen
 *
 */
public class Prover9Test {

	static FolTheoremProver e;
	FolWriter printer = new Prover9Writer();
	
	@BeforeClass public static void init(){
		e = new Prover9("C:\\app\\prover9\\bin\\prover9.exe");
	}

	@Test
	public void test1() throws Exception {
		FolParser parser = new FolParser();	
		String source = "type(a) \n type(b) \n type(c) \n"
				+ "a \n !b";
		FolBeliefSet b = parser.parseBeliefBase(source);
		//printer.printBase(b);
		System.out.println(printer);
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("b")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("a")).getAnswerBoolean());
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("c")).getAnswerBoolean());
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("!c")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("a&&b <=> b&&a")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("forall X:(X==X && !(X/==X))")).getAnswerBoolean());
	}
	
	@Test
	public void test2() throws Exception {
		FolParser parser = new FolParser();	
		String source = "Animal = {horse, cow, lion} \n"
				+ "type(Tame(Animal)) \n"
				+ "type(Ridable(Animal)) \n"
				+ "Tame(cow) \n"
				+ "!Tame(lion) \n"
				+ "Ridable(horse) \n"
				+ "forall X: (!Ridable(X) || Tame(X)) \n";
		FolBeliefSet b = parser.parseBeliefBase(source);
		printer.printBase(b);
		System.out.println(printer);
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Tame(cow)")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("exists X: (Tame(X))")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Tame(horse)")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!Ridable(lion)")).getAnswerBoolean());
	}
	
	@Test
	public void test3() throws Exception {
		FolParser parser = new FolParser();	
		String source = "Animal = {horse, cow, lion} \n"
				+ "Plant = {grass, tree} \n"
				+ "type(Eats(Animal, Plant)) \n"
				+ "forall X: (!Eats(X,tree)) \n"
				+ "Eats(cow, grass) \n"
				+ "forall X: (!Eats(cow, X) || Eats(horse, X)) \n"
				+ "exists X: (Eats(lion, X))";
		FolBeliefSet b = parser.parseBeliefBase(source);
		printer.printBase(b);
		System.out.println(printer);
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("Eats(lion, tree)")).getAnswerBoolean());
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("!Eats(lion, grass)")).getAnswerBoolean());
		//is not true according to the solver
		//assertTrue(e.query(b, (FolFormula)parser.parseFormula("Eats(lion, grass)")).getAnswerBoolean());
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("Eats(horse, tree)")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!Eats(horse, tree)")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Eats(horse, grass)")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("exists X: (forall Y: (!Eats(Y, X)))")).getAnswerBoolean());
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("forall X: (forall Y: (Eats(Y, X)))")).getAnswerBoolean());
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!(forall X: (forall Y: (Eats(Y, X))))")).getAnswerBoolean());
	}
}
