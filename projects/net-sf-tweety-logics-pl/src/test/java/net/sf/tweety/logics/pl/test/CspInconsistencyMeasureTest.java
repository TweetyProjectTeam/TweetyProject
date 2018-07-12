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
package net.sf.tweety.logics.pl.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.CspInconsistencyMeasure;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.math.opt.solver.GlpkSolver;

public class CspInconsistencyMeasureTest {
	
	private CspInconsistencyMeasure<PropositionalFormula> m; 
	private double accuracy = 0.001;
	
	@Before
	public void setUp() {		
		GlpkSolver.binary = "/usr/local/bin/glpsol";
		m = new CspInconsistencyMeasure<PropositionalFormula>(new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py"),new GlpkSolver());
	}
	
	@Test
	public void test1() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		
		assertEquals(m.inconsistencyMeasure(bs), 1, accuracy);		
	}
	
	@Test
	public void test2() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		bs.add((PropositionalFormula) parser.parseFormula("b"));
		bs.add((PropositionalFormula) parser.parseFormula("!b"));
		
		assertEquals(m.inconsistencyMeasure(bs), 2, accuracy);		
	}
	
	@Test
	public void test3() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		bs.add((PropositionalFormula) parser.parseFormula("b"));
		bs.add((PropositionalFormula) parser.parseFormula("!b"));		
		bs.add((PropositionalFormula) parser.parseFormula("c"));
		bs.add((PropositionalFormula) parser.parseFormula("!c"));
		
		assertEquals(m.inconsistencyMeasure(bs), 3, accuracy);		
	}
	
	@Test
	public void test4() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		bs.add((PropositionalFormula) parser.parseFormula("b"));
		bs.add((PropositionalFormula) parser.parseFormula("!b && d"));		
		bs.add((PropositionalFormula) parser.parseFormula("c && !d"));
		bs.add((PropositionalFormula) parser.parseFormula("!c"));
		
		assertEquals(m.inconsistencyMeasure(bs), 2.833, accuracy);		
	}
	
	@Test
	public void test5() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		bs.add((PropositionalFormula) parser.parseFormula("b"));
		bs.add((PropositionalFormula) parser.parseFormula("!b && (d || e)"));
		bs.add((PropositionalFormula) parser.parseFormula("!e"));
		bs.add((PropositionalFormula) parser.parseFormula("c && (!d || e)"));
		bs.add((PropositionalFormula) parser.parseFormula("!c"));
		
		assertEquals(m.inconsistencyMeasure(bs), 3.5, accuracy);		
	}
	
	@Test
	public void test6() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		bs.add((PropositionalFormula) parser.parseFormula("b && (f || g)"));
		bs.add((PropositionalFormula) parser.parseFormula("!b && (d || e)"));
		bs.add((PropositionalFormula) parser.parseFormula("!e"));
		bs.add((PropositionalFormula) parser.parseFormula("!g"));
		bs.add((PropositionalFormula) parser.parseFormula("c && (!d || e)"));
		bs.add((PropositionalFormula) parser.parseFormula("!c && (!f || g)"));
		
		assertEquals(m.inconsistencyMeasure(bs), 3.833, accuracy);		
	}
	
	@Test
	public void test7() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		bs.add((PropositionalFormula) parser.parseFormula("b && (f || g)"));
		bs.add((PropositionalFormula) parser.parseFormula("!b && (d || e)"));
		bs.add((PropositionalFormula) parser.parseFormula("!e"));
		bs.add((PropositionalFormula) parser.parseFormula("!g"));
		bs.add((PropositionalFormula) parser.parseFormula("c && (!d || e)"));
		bs.add((PropositionalFormula) parser.parseFormula("!c && (!f || g)"));
		bs.add((PropositionalFormula) parser.parseFormula("h"));
		bs.add((PropositionalFormula) parser.parseFormula("i"));
		bs.add((PropositionalFormula) parser.parseFormula("!h || !i"));
		
		assertEquals(m.inconsistencyMeasure(bs), 4.833, accuracy);		
	}
	
	@Test
	public void test8() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a"));
		bs.add((PropositionalFormula) parser.parseFormula("b && (f || g)"));
		bs.add((PropositionalFormula) parser.parseFormula("!b && (d || e)"));
		bs.add((PropositionalFormula) parser.parseFormula("!e"));
		bs.add((PropositionalFormula) parser.parseFormula("!g"));
		bs.add((PropositionalFormula) parser.parseFormula("c && (!d || e)"));
		bs.add((PropositionalFormula) parser.parseFormula("!c && (!f || g)"));
		bs.add((PropositionalFormula) parser.parseFormula("h"));
		bs.add((PropositionalFormula) parser.parseFormula("i"));
		bs.add((PropositionalFormula) parser.parseFormula("!h || !i"));
		bs.add((PropositionalFormula) parser.parseFormula("j && !i"));
		bs.add((PropositionalFormula) parser.parseFormula("!j && !h"));
		
		assertEquals(m.inconsistencyMeasure(bs), 5.916, accuracy);		
	}
	
	@Test
	public void test9() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a && !b"));
		bs.add((PropositionalFormula) parser.parseFormula("b && !c"));
		bs.add((PropositionalFormula) parser.parseFormula("c && !d"));
		bs.add((PropositionalFormula) parser.parseFormula("d && !e"));
		bs.add((PropositionalFormula) parser.parseFormula("e && !f"));
		bs.add((PropositionalFormula) parser.parseFormula("f && !g"));
		bs.add((PropositionalFormula) parser.parseFormula("g && !a"));
		
		assertEquals(m.inconsistencyMeasure(bs), 3.916, accuracy);		
	}	
}
