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
 /**
 * 
 */
package org.tweetyproject.lp.asp.analysis;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import org.tweetyproject.lp.asp.parser.ASPParser;
import org.tweetyproject.lp.asp.parser.InstantiateVisitor;
import org.tweetyproject.lp.asp.reasoner.ClingoSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * Test class for Clingo.
 * 
 * @author Nils Geilen geilenn(at)uni-koblenz.de
 * @author Anna Gessler
 *
 */
public class ClingoTest {
	/**
	 * parser
	 */
	static ASPParser parser;
	/**
	 * visitor
	 */
	static InstantiateVisitor visitor;
	/**
	 *  solver
	 */
	static ClingoSolver solver;
	
	/**
	 * initializes values
	 */
	@BeforeClass
	public static void init() {
		visitor = new InstantiateVisitor();
		parser = new ASPParser(new StringReader(""));
		solver = new ClingoSolver("/your/path/to/clingo");
	}

	/**
	 * basic test 1
	 * @throws Exception any exception
	 */
	@Test
	public void BasicExampleTest1() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex1.asp"));
		parser.ReInit(fistr);
		
		Program p = visitor.visit(parser.Program(), null);
		List<AnswerSet> asl = solver.getModels(p);
		
		assertTrue(asl.size() == 1);
		assertTrue(asl.get(0).size() == 3);
	}
	
	/**
	 * basic test 2
	 * @throws Exception any exception
	 */
	@Test
	public void BasicExampleTest2() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex2.asp"));
		parser.ReInit(fistr);

		Program p = visitor.visit(parser.Program(), null);
		List<AnswerSet> asl = solver.getModels(p);
		
		assertTrue(asl.size() == 2);
		assertTrue(asl.get(0).size() == 5);
	}
	
	/**
	 * basic example 3
	 * @throws Exception any exception
	 */
	@Test
	public void BasicExampleTest3() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex4.asp"));
		parser.ReInit(fistr);
			
		Program p = visitor.visit(parser.Program(), null);
		List<AnswerSet> asl = solver.getModels(p);
		
		assertTrue(asl.size() == 1);
		assertTrue(asl.get(0).size() == 5);
	}
	
	/**
	 * basic test 4
	 * @throws Exception any exception
	 */
	@Test
	public void BasicExampleTest4() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex5.asp"));
		parser.ReInit(fistr);
			
		Program p = visitor.visit(parser.Program(), null);
		List<AnswerSet> asl = solver.getModels(p);
		System.out.println(asl);
		
		assertTrue(asl.size() == 1);
		assertTrue(asl.get(0).size() == 4);
	}

}
