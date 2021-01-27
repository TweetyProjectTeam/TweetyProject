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
import org.tweetyproject.logics.fol.reasoner.EFOLReasoner;
import org.tweetyproject.logics.fol.reasoner.FolReasoner;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.fol.writer.FolWriter;
import org.tweetyproject.logics.fol.writer.TPTPWriter;

/**
 * JUnitTest to test TPTP printer and EProver implementation
 * 
 * @author Nils Geilen
 *
 */

public class TPTPTest {
	
	static FolReasoner e;
	FolWriter printer = new TPTPWriter();
	
	@BeforeClass public static void init(){
		if(System.getProperty("os.name").matches("Win.*")){
			System.out.println("Initializing Eprover Test for Windows");
			 e = new EFOLReasoner("C:/app/E/PROVER/eprover.exe", Shell.getCygwinShell("C:/cygwin64/bin/bash.exe"));
		} else {
			System.out.println("Initializing Eprover Test for Unix");
			 e = new EFOLReasoner("/home/anna/sw/folProver/E/PROVER/eprover", Shell.getNativeShell());
			 //e = new EProver("/Users/mthimm/Projects/misc_bins/eprover/eprover");
		}
	}

	@Test
	public void test1() throws Exception {
		FolParser parser = new FolParser();	
		String source = "type(a) \n type(b) \n type(c) \n"
				+ "a \n !b";
		FolBeliefSet b = parser.parseBeliefBase(source);
		printer.printBase(b);
		System.out.println(printer);
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("b")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("a")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("c")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("!c")));
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
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Tame(cow)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("exists X: (Tame(X))")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Tame(horse)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!Ridable(lion)")));
	}
	
	@Test
	public void test3() throws Exception {
		FolParser parser = new FolParser();	
		parser.setSignature(new FolSignature(true));
		String source = "Animal = {horse, cow, lion} \n"
				+ "Plant = {grass, tree} \n"
				+ "type(Eats(Animal, Plant)) \n"
				+ "forall X: (!Eats(X,tree)) \n"
				+ "Eats(cow, grass) \n"
				+ "forall X: (!(exists Y: (Eats(X,Y) && X==Y))) \n"
				+ "forall X: (!Eats(cow, X) || Eats(horse, X)) \n"
				+ "exists X: (Eats(lion, X))";
		FolBeliefSet b = parser.parseBeliefBase(source);
		printer.printBase(b);
		System.out.println(printer);
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("Eats(lion, tree)")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("!Eats(lion, grass)")));
		//is not true according to the solver
		//assertTrue(e.query(b, (FolFormula)parser.parseFormula("Eats(lion, grass)")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("Eats(horse, tree)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!Eats(horse, tree)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("Eats(horse, grass)")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("exists X: (forall Y: (!Eats(Y, X)))")));
		assertFalse(e.query(b, (FolFormula)parser.parseFormula("forall X: (forall Y: (Eats(Y, X)))")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("!(forall X: (forall Y: (Eats(Y, X))))")));
		assertTrue(e.query(b, (FolFormula)parser.parseFormula("forall X: (forall Y: (Eats(X, Y) => X/==Y))")));
	}
	
}




