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

import net.sf.tweety.lp.asp.reasoner.ASPSolver;
import net.sf.tweety.lp.asp.reasoner.ClingoSolver;
import net.sf.tweety.lp.asp.reasoner.SolverException;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.ASPAtom;
import net.sf.tweety.lp.asp.syntax.StrictNegation;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.DefaultNegation;

/**
 * Tests the functionality of PmInconsistencyMeasure 
 * @author Matthias Thimm 
 *
 */
public class AspInconsistencyMeasureTest {
	
	public static ASPSolver solver;
	public static PmInconsistencyMeasure mpm;
	public static SdInconsistencyMeasure msd;
	
	@BeforeClass
	public static void init() {
		solver = new ClingoSolver("C:/app/clingo/clingo.exe");
		mpm = new PmInconsistencyMeasure(solver);
		msd = new SdInconsistencyMeasure(solver);
	}
	
	@Test
	public void test1() throws SolverException{
		// Ex. 1a of [Ulbricht, Thimm, Brewka. Measuring Inconsistency in Answer Set Programs. JELIA 2016]
		Program p3 = new Program();
		ASPAtom a1 = new ASPAtom("a1");
		ASPAtom b = new ASPAtom("b");
		ASPAtom c = new ASPAtom("c");
		ASPAtom d = new ASPAtom("d");
		
		p3.add(new ASPRule(a1,new DefaultNegation(b)));
		p3.add(new ASPRule(new StrictNegation(a1),new DefaultNegation(b)));
		p3.add(new ASPRule(a1,new DefaultNegation(c)));
		p3.add(new ASPRule(new StrictNegation(a1),new DefaultNegation(c)));
		p3.add(new ASPRule(a1,new DefaultNegation(d)));
		p3.add(new ASPRule(new StrictNegation(a1),new DefaultNegation(d)));
		
		assertEquals(Double.valueOf(3), mpm.inconsistencyMeasure(p3));
		assertEquals(Double.valueOf(3), msd.inconsistencyMeasure(p3));
		
		// Ex. 1b of [Ulbricht, Thimm, Brewka. Measuring Inconsistency in Answer Set Programs. JELIA 2016]
		Program p4 = new Program();
		
		p4.add(new ASPRule(a1,new DefaultNegation(b)));
		p4.add(new ASPRule(new StrictNegation(a1),new DefaultNegation(b)));
		p4.add(new ASPRule(a1,new DefaultNegation(b)));
		p4.add(new ASPRule(new StrictNegation(a1),new DefaultNegation(b)));
		p4.add(new ASPRule(a1,new DefaultNegation(b)));
		p4.add(new ASPRule(new StrictNegation(a1),new DefaultNegation(b)));
				
		assertEquals(Double.valueOf(1), mpm.inconsistencyMeasure(p4));
		assertEquals(Double.valueOf(1), msd.inconsistencyMeasure(p4));
	}
}
