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
package net.sf.tweety.lp.asp.analysis;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.reasoner.DLVSolver;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.syntax.Program;

/**
 * Test class for DLV.
 * 
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 * @author Anna Gessler
 *
 */
public class DLVTest {
	
	static ASPCore2Parser parser;
	
	static InstantiateVisitor visitor;
	
	static DLVSolver solver;
	
	@BeforeClass
	public static void init() {
		visitor = new InstantiateVisitor();
		parser = new ASPCore2Parser(new StringReader(""));
		solver = new DLVSolver("/home/anna/sw/asp/dlv");
	}

	@Test
	public void Example1() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex1.asp"));
		parser.ReInit(fistr);
		Program p = visitor.visit(parser.Program(), null);
		AnswerSetList asl = solver.getModels(p);
		
		assertTrue(asl.size() == 1);
		assertTrue(asl.get(0).size() == 3);
	}
	
	@Test
	public void Example2() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex2.asp"));
		parser.ReInit(fistr);
		Program p = visitor.visit(parser.Program(), null);
		AnswerSetList asl = solver.getModels(p);
		
		System.out.println(asl);
		
		assertTrue(asl.size() == 2);
		assertTrue(asl.get(0).size() == 5);
		assertTrue(asl.get(1).size() == 5);
	}
	
	@Test
	public void Example4() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("src/main/resources/ex4.asp"));
		parser.ReInit(fistr);
		Program p = visitor.visit(parser.Program(), null);
		
		AnswerSetList asl = solver.getModels(p);
		assertTrue(asl.size() == 1);
		assertTrue(asl.get(0).size() == 5);
	}
	
}
