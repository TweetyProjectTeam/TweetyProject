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
package net.sf.tweety.lp.asp.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.tweety.lp.asp.reasoner.Clingo;
import net.sf.tweety.lp.asp.reasoner.Solver;
import net.sf.tweety.lp.asp.reasoner.SolverException;
import net.sf.tweety.lp.asp.syntax.DLPAtom;
import net.sf.tweety.lp.asp.syntax.DLPNeg;
import net.sf.tweety.lp.asp.syntax.DLPNot;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;

/**
 * Tests the functionality of PmInconsistencyMeasure 
 * @author Matthias Thimm 
 *
 */
public class AspInconsistencyMeasureTest {
	
	public static Solver solver;
	public static PmInconsistencyMeasure mpm;
	public static SdInconsistencyMeasure msd;
	
	@BeforeClass
	public static void init() {
		solver = new Clingo("C:/app/clingo/clingo.exe");
		mpm = new PmInconsistencyMeasure(solver);
		msd = new SdInconsistencyMeasure(solver);
	}
	
	@Test
	public void test1() throws SolverException{
		// Ex. 1a of [Ulbricht, Thimm, Brewka. Measuring Inconsistency in Answer Set Programs. JELIA 2016]
		Program p3 = new Program();
		DLPAtom a1 = new DLPAtom("a1");
		DLPAtom b = new DLPAtom("b");
		DLPAtom c = new DLPAtom("c");
		DLPAtom d = new DLPAtom("d");
		
		p3.add(new Rule(a1,new DLPNot(b)));
		p3.add(new Rule(new DLPNeg(a1),new DLPNot(b)));
		p3.add(new Rule(a1,new DLPNot(c)));
		p3.add(new Rule(new DLPNeg(a1),new DLPNot(c)));
		p3.add(new Rule(a1,new DLPNot(d)));
		p3.add(new Rule(new DLPNeg(a1),new DLPNot(d)));
		
		assertEquals(new Double(3), mpm.inconsistencyMeasure(p3));
		assertEquals(new Double(3), msd.inconsistencyMeasure(p3));
		
		// Ex. 1b of [Ulbricht, Thimm, Brewka. Measuring Inconsistency in Answer Set Programs. JELIA 2016]
		Program p4 = new Program();
		
		p4.add(new Rule(a1,new DLPNot(b)));
		p4.add(new Rule(new DLPNeg(a1),new DLPNot(b)));
		p4.add(new Rule(a1,new DLPNot(b)));
		p4.add(new Rule(new DLPNeg(a1),new DLPNot(b)));
		p4.add(new Rule(a1,new DLPNot(b)));
		p4.add(new Rule(new DLPNeg(a1),new DLPNot(b)));
				
		assertEquals(new Double(1), mpm.inconsistencyMeasure(p4));
		assertEquals(new Double(1), msd.inconsistencyMeasure(p4));
	}
}
