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
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.FbInconsistencyMeasure;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Some examples from
 * [Besnard. Forgetting-based Inconsistency Measure. SUM 2016]
 * 
 * @author Matthias Thimm
 *
 */
public class FbInconsistencyMeasureTest {
	
	FbInconsistencyMeasure inc = new FbInconsistencyMeasure();
	
	@Before
	public void setUp() {		
		SatSolver.setDefaultSolver(new Sat4jSolver());
	}
	
	@Test
	public void example1() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a || a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a || !a"));
		
		assertEquals(inc.inconsistencyMeasure(bs), new Double(1));		
	
	}
	
	@Test
	public void example2() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("a && a"));
		bs.add((PropositionalFormula) parser.parseFormula("!a && !a"));
		
		assertEquals(inc.inconsistencyMeasure(bs), new Double(2));		
	}
	
	@Test
	public void example3() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("(a && !a) || (b && !b)"));
		
		assertEquals(inc.inconsistencyMeasure(bs), new Double(1));		
	}
	
	@Test
	public void example4() throws ParserException, IOException{
		PlBeliefSet bs = new PlBeliefSet();
		PlParser parser = new PlParser();
		bs.add((PropositionalFormula) parser.parseFormula("!a && !a && !a"));
		bs.add((PropositionalFormula) parser.parseFormula("a"));
		
		assertEquals(inc.inconsistencyMeasure(bs), new Double(1));
		
		bs = new PlBeliefSet();		
		bs.add((PropositionalFormula) parser.parseFormula("!a && !a && !a"));
		bs.add((PropositionalFormula) parser.parseFormula("a && a && a"));
		
		assertEquals(inc.inconsistencyMeasure(bs), new Double(3));
	}
}
