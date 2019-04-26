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
package net.sf.tweety.logics.pcl.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.pcl.analysis.MinimalViolationInconsistencyMeasure;
import net.sf.tweety.logics.pcl.parser.PclParser;
import net.sf.tweety.logics.pcl.syntax.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.math.norm.ManhattanNorm;
import net.sf.tweety.math.norm.MaximumNorm;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.opt.solver.LpSolve;

import org.junit.Before;
import org.junit.Test;



/**
 * @author Nico Potyka, Matthias Thimm
 *
 */
// TODO: there are some bugs to be fixed in the code of the minimal violation measure
//@Ignore
public class MinimalViolationInconsistencyMeasureLPSolveTest {

	double accuracy;
	
	InconsistencyMeasure<BeliefSet<ProbabilisticConditional,?>> inc;
	
	PclParser parser;
	LinkedList<PclBeliefSet> kbs;
	
	
	@Before
	public void setUp() {
		accuracy = 0.001;
		
		parser = new PclParser();
		
		LpSolve.setBinary("/opt/local/bin/lp_solve");
		Solver.setDefaultLinearSolver(new LpSolve());
		
		kbs = new LinkedList<PclBeliefSet>();
		try {
			kbs.add((PclBeliefSet) parser.parseBeliefBase("(A)[0.5]"));

			kbs.add((PclBeliefSet) parser.parseBeliefBase("(A)[0.49]\n"
					                                    + "(A)[0.51]"));

			kbs.add((PclBeliefSet) parser.parseBeliefBase("(A)[0.4]\n"
					                                    + "(A)[0.6]"));

			kbs.add((PclBeliefSet) parser.parseBeliefBase("(A)[0.2]\n"
					                                    + "(A)[0.8]"));

			kbs.add((PclBeliefSet) parser.parseBeliefBase("(A)[0d]\n"
					                                    + "(A)[1d]"));
			

			kbs.add((PclBeliefSet) parser.parseBeliefBase("(A)[0.8]\n"
					                                    + "(B)[0.6]\n"
					                                    + "(B|A)[0.9]"));
		} 
		catch (IOException e) {
		
			System.err.println("Parsing error in MinimalViolationInconsistencyMeasureLPSolveTest setup.");
			System.err.println(e.toString());
			
		}
		catch (ParserException e) {

			System.err.println("Parsing error in MinimalViolationInconsistencyMeasureLPSolveTest setup.");
			System.err.println(e.toString());
			
		}
			
		
		
	}

	
	
	@Test
	public void check1Norm() {
		
		inc = new MinimalViolationInconsistencyMeasure(new ManhattanNorm(), Solver.getDefaultLinearSolver());
		
		LinkedList<Double> expected = new LinkedList<Double>();
		
		expected.add(0d);
		expected.add(0.02);
		expected.add(0.2);
		expected.add(0.6);
		expected.add(1d);
		expected.add(0.12);
		
		
		for(PclBeliefSet kb: kbs) {
			assertEquals(expected.removeFirst(), inc.inconsistencyMeasure(kb),accuracy);			
		}
		
		
	}
	
	

	@Test
	public void checkMaxNorm() {
		inc = new MinimalViolationInconsistencyMeasure(new MaximumNorm(), Solver.getDefaultLinearSolver());
		
		LinkedList<Double> expected = new LinkedList<Double>();
		
		expected.add(0d);
		expected.add(0.02);
		expected.add(0.2);
		expected.add(0.6);
		expected.add(1.0);
		expected.add(0.12);
		
		
		for(PclBeliefSet kb: kbs) {
			assertEquals(expected.removeFirst(), inc.inconsistencyMeasure(kb),accuracy);			
		}
		
		
	}
	
}
