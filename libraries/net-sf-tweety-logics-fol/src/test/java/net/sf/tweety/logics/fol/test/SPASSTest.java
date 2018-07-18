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

import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.prover.FolTheoremProver;
import net.sf.tweety.logics.fol.prover.SPASS;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.writer.SPASSWriter;

/**
 * JUnitTest to test SPASS (test cases from {@link net.sf.tweety.logics.fol.test.TPTPTest})
 * 
 * @author Nils Geilen
 * @author Anna Gessler
 *
 */

public class SPASSTest {
	
	static FolTheoremProver spass;
	SPASSWriter printer = new SPASSWriter();
	
	@BeforeClass public static void init(){
		System.out.println("Initializing SPASS Test for Unix");
		 spass = new SPASS("/home/anna/sw/mlProver/SPASS/SPASS", Shell.getNativeShell());
	}

	@Test
	public void test1() throws Exception {
		FolParser parser = new FolParser();	
		String source = "type(a) \n type(b) \n type(c) \n"
				+ "a \n !b";
		FolBeliefSet b = parser.parseBeliefBase(source);
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("b")).getAnswerBoolean());
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("a")).getAnswerBoolean());
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("c")).getAnswerBoolean());
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("!c")).getAnswerBoolean());
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
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("Tame(cow)")).getAnswerBoolean());
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("exists X: (Tame(X))")).getAnswerBoolean());
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("Tame(horse)")).getAnswerBoolean());
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("!Ridable(lion)")).getAnswerBoolean());
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
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("Eats(lion, tree)")).getAnswerBoolean());
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("!Eats(lion, grass)")).getAnswerBoolean());
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("Eats(horse, tree)")).getAnswerBoolean());
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("!Eats(horse, tree)")).getAnswerBoolean());
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("Eats(horse, grass)")).getAnswerBoolean());
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("exists X: (forall Y: (!Eats(Y, X)))")).getAnswerBoolean());
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("forall X: (forall Y: (Eats(Y, X)))")).getAnswerBoolean());
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("!(forall X: (forall Y: (Eats(Y, X))))")).getAnswerBoolean());
	}
	
	
}




