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

import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.solver.Clingo;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.util.AnswerSetList;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *
 */
public class ClingoTest {
	
	static ASPParser parser;
	
	static InstantiateVisitor visitor;
	
	static Clingo solver;
	
	@BeforeClass
	public static void init() {
		visitor = new InstantiateVisitor();
		parser = new ASPParser(new StringReader(""));
		solver = new Clingo("/Users/mthimm/Projects/misc_bins/clingo-4.5.4-macos-10.9/clingo");
	}

	@Test
	public void Example1() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("/Users/mthimm/Shared/SVN/sourceforge-tweety/trunk/examples/asp/ex1.asp"));
		parser.ReInit(fistr);
		
		Program p = visitor.visit(parser.Program(), null);
		solver.computeModels(p, 1);
		
		AnswerSetList asl = solver.computeModels(p, 1000);

		assertTrue(asl.size() == 2);
		assertTrue(asl.get(0).size() == 3);
	}
	
	@Test
	public void Example2() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("/Users/mthimm/Shared/SVN/sourceforge-tweety/trunk/examples/asp/ex2.asp"));
		parser.ReInit(fistr);
		
		Program p = visitor.visit(parser.Program(), null);
		
		AnswerSetList asl = solver.computeModels(p, 1000);
		assertTrue(asl.size() == 3);
		assertTrue(asl.get(0).size() == 5);
		assertTrue(asl.get(1).size() == 5);
	}
	
	@Test
	public void Example3() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("/Users/mthimm/Shared/SVN/sourceforge-tweety/trunk/examples/asp/ex3.asp"));
		parser.ReInit(fistr);
		
		Program p = visitor.visit(parser.Program(), null);
		solver.computeModels(p, 1);
		
		AnswerSetList asl = solver.computeModels(p, 1000);
		assertTrue(asl.size() ==3);
		assertTrue(asl.get(0).size() == 5);
		assertTrue(asl.get(1).size() == 5);
	}
	
	@Test
	public void Example4() throws Exception {
		FileInputStream fistr = new FileInputStream(new File("/Users/mthimm/Shared/SVN/sourceforge-tweety/trunk/examples/asp/ex4.asp"));
		parser.ReInit(fistr);
			
		Program p = visitor.visit(parser.Program(), null);
		solver.computeModels(p, 1);
			
		AnswerSetList asl = solver.computeModels(p, 1000);
		assertTrue(asl.size() ==2);
		assertTrue(asl.get(0).size() == 5);
	}

}
