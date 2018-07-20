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

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.ml.reasoner.SPASSModalReasoner;
import net.sf.tweety.logics.ml.syntax.ModalBeliefSet;
import net.sf.tweety.logics.ml.writer.SPASSWriter;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.ml.parser.ModalParser;

/**
 * JUnit Test class for SPASS Prover for modal formulas.
 * 
 *  @author Anna Gessler
 */
public class SPASSTest {
	
	public static final int DEFAULT_TIMEOUT = 10000;
	static SPASSModalReasoner spass;
	SPASSWriter printer = new SPASSWriter();
	
	@BeforeClass public static void init(){
		System.out.println("Initializing SPASS Test for Unix");
		spass = new SPASSModalReasoner("/home/anna/sw/mlProver/SPASS/SPASS", Shell.getNativeShell());
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleQueryTest1() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("type(p) \n !(<>(p))");
				
		Boolean a1 = spass.query(b,(FolFormula) parser.parseFormula("<>(p)"));
		Boolean a2 = spass.query(b,(FolFormula) parser.parseFormula("!(<>(p))"));
		assertFalse(a1);
		assertTrue(a2);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleQueryTest2() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("type(p) \n p");
		
		Boolean a1 = spass.query(b,(FolFormula) parser.parseFormula("<>(p)"));
		Boolean a2 = spass.query(b,(FolFormula) parser.parseFormula("[](p)"));
		assertFalse(a1);
		assertTrue(a2);
	}
	
	/*
	 * Queries with predicates of non-zero arities like this example cause
	 * SPASS to fail with a "Symbol was declared with arity x" error (x being
	 * the predicate's arity) despite having the correct number of arguments.  
	 * TODO: Find out why. 
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComplexQueryTest() throws FileNotFoundException, ParserException, IOException {
		ModalParser parser = new ModalParser();
		ModalBeliefSet b = parser.parseBeliefBase("Sort1={obj1} \n type(p) \n type(q(Sort1)) \n p \n q(obj1)");
		Boolean a1 = spass.query(b, (FolFormula) parser.parseFormula("[](forall X:(q(X)))=>forall X:( [](q(X)))"));
		assertTrue(a1);
	}
	
}
