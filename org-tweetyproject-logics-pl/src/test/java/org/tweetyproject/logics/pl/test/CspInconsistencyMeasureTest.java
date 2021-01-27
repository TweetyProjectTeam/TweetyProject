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
package org.tweetyproject.logics.pl.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.analysis.CspInconsistencyMeasure;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.MarcoMusEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.math.opt.solver.GlpkSolver;

public class CspInconsistencyMeasureTest {
	
	private CspInconsistencyMeasure<PlFormula> m; 
	private double accuracy = 0.001;
	
	@Before
	public void setUp() {		
		GlpkSolver.binary = "/usr/local/bin/glpsol";
		m = new CspInconsistencyMeasure<PlFormula>(new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py"),new GlpkSolver());
	}
	
	@Test
	public void test1() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a"));
		bs.add((PlFormula) parser.parseFormula("!a"));
		
		assertEquals(m.inconsistencyMeasure(bs), 1, accuracy);		
	}
	
	@Test
	public void test2() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a"));
		bs.add((PlFormula) parser.parseFormula("!a"));
		bs.add((PlFormula) parser.parseFormula("b"));
		bs.add((PlFormula) parser.parseFormula("!b"));
		
		assertEquals(m.inconsistencyMeasure(bs), 2, accuracy);		
	}
	
	@Test
	public void test3() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a"));
		bs.add((PlFormula) parser.parseFormula("!a"));
		bs.add((PlFormula) parser.parseFormula("b"));
		bs.add((PlFormula) parser.parseFormula("!b"));		
		bs.add((PlFormula) parser.parseFormula("c"));
		bs.add((PlFormula) parser.parseFormula("!c"));
		
		assertEquals(m.inconsistencyMeasure(bs), 3, accuracy);		
	}
	
	@Test
	public void test4() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a"));
		bs.add((PlFormula) parser.parseFormula("!a"));
		bs.add((PlFormula) parser.parseFormula("b"));
		bs.add((PlFormula) parser.parseFormula("!b && d"));		
		bs.add((PlFormula) parser.parseFormula("c && !d"));
		bs.add((PlFormula) parser.parseFormula("!c"));
		
		assertEquals(m.inconsistencyMeasure(bs), 2.833, accuracy);		
	}
	
	@Test
	public void test5() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a"));
		bs.add((PlFormula) parser.parseFormula("!a"));
		bs.add((PlFormula) parser.parseFormula("b"));
		bs.add((PlFormula) parser.parseFormula("!b && (d || e)"));
		bs.add((PlFormula) parser.parseFormula("!e"));
		bs.add((PlFormula) parser.parseFormula("c && (!d || e)"));
		bs.add((PlFormula) parser.parseFormula("!c"));
		
		assertEquals(m.inconsistencyMeasure(bs), 3.5, accuracy);		
	}
	
	@Test
	public void test6() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a"));
		bs.add((PlFormula) parser.parseFormula("!a"));
		bs.add((PlFormula) parser.parseFormula("b && (f || g)"));
		bs.add((PlFormula) parser.parseFormula("!b && (d || e)"));
		bs.add((PlFormula) parser.parseFormula("!e"));
		bs.add((PlFormula) parser.parseFormula("!g"));
		bs.add((PlFormula) parser.parseFormula("c && (!d || e)"));
		bs.add((PlFormula) parser.parseFormula("!c && (!f || g)"));
		
		assertEquals(m.inconsistencyMeasure(bs), 3.833, accuracy);		
	}
	
	@Test
	public void test7() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a"));
		bs.add((PlFormula) parser.parseFormula("!a"));
		bs.add((PlFormula) parser.parseFormula("b && (f || g)"));
		bs.add((PlFormula) parser.parseFormula("!b && (d || e)"));
		bs.add((PlFormula) parser.parseFormula("!e"));
		bs.add((PlFormula) parser.parseFormula("!g"));
		bs.add((PlFormula) parser.parseFormula("c && (!d || e)"));
		bs.add((PlFormula) parser.parseFormula("!c && (!f || g)"));
		bs.add((PlFormula) parser.parseFormula("h"));
		bs.add((PlFormula) parser.parseFormula("i"));
		bs.add((PlFormula) parser.parseFormula("!h || !i"));
		
		assertEquals(m.inconsistencyMeasure(bs), 4.833, accuracy);		
	}
	
	@Test
	public void test8() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a"));
		bs.add((PlFormula) parser.parseFormula("!a"));
		bs.add((PlFormula) parser.parseFormula("b && (f || g)"));
		bs.add((PlFormula) parser.parseFormula("!b && (d || e)"));
		bs.add((PlFormula) parser.parseFormula("!e"));
		bs.add((PlFormula) parser.parseFormula("!g"));
		bs.add((PlFormula) parser.parseFormula("c && (!d || e)"));
		bs.add((PlFormula) parser.parseFormula("!c && (!f || g)"));
		bs.add((PlFormula) parser.parseFormula("h"));
		bs.add((PlFormula) parser.parseFormula("i"));
		bs.add((PlFormula) parser.parseFormula("!h || !i"));
		bs.add((PlFormula) parser.parseFormula("j && !i"));
		bs.add((PlFormula) parser.parseFormula("!j && !h"));
		
		assertEquals(m.inconsistencyMeasure(bs), 5.916, accuracy);		
	}
	
	@Test
	public void test9() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PlFormula) parser.parseFormula("a && !b"));
		bs.add((PlFormula) parser.parseFormula("b && !c"));
		bs.add((PlFormula) parser.parseFormula("c && !d"));
		bs.add((PlFormula) parser.parseFormula("d && !e"));
		bs.add((PlFormula) parser.parseFormula("e && !f"));
		bs.add((PlFormula) parser.parseFormula("f && !g"));
		bs.add((PlFormula) parser.parseFormula("g && !a"));
		
		assertEquals(m.inconsistencyMeasure(bs), 3.916, accuracy);		
	}	
}
