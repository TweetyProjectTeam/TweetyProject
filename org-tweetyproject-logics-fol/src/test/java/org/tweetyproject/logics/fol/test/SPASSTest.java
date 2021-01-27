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
 package org.tweetyproject.logics.fol.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import org.tweetyproject.commons.util.Shell;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.reasoner.FolReasoner;
import org.tweetyproject.logics.fol.reasoner.SpassFolReasoner;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.writer.SPASSWriter;

/**
 * JUnitTest to test SPASS (test cases from {@link org.tweetyproject.logics.fol.test.TPTPTest})
 * 
 * @author Nils Geilen
 * @author Anna Gessler
 *
 */

public class SPASSTest {
	
	static FolReasoner spass;
	SPASSWriter printer = new SPASSWriter();
	
	@BeforeClass public static void init(){
		System.out.println("Initializing SPASS Test for Unix");
		 spass = new SpassFolReasoner("/home/anna/sw/mlProver/SPASS/SPASS", Shell.getNativeShell());
	}

	@Test
	public void test1() throws Exception {
		FolParser parser = new FolParser();	
		String source = "type(a) \n type(b) \n type(c) \n"
				+ "a \n !b";
		FolBeliefSet b = parser.parseBeliefBase(source);
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("b")));
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("a")));
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("c")));
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("!c")));
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
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("Tame(cow)")));
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("exists X: (Tame(X))")));
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("Tame(horse)")));
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("!Ridable(lion)")));
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
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("Eats(lion, tree)")));
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("!Eats(lion, grass)")));
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("Eats(horse, tree)")));
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("!Eats(horse, tree)")));
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("Eats(horse, grass)")));
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("exists X: (forall Y: (!Eats(Y, X)))")));
		assertFalse(spass.query(b, (FolFormula)parser.parseFormula("forall X: (forall Y: (Eats(Y, X)))")));
		assertTrue(spass.query(b, (FolFormula)parser.parseFormula("!(forall X: (forall Y: (Eats(Y, X))))")));
	}
	
	
}




