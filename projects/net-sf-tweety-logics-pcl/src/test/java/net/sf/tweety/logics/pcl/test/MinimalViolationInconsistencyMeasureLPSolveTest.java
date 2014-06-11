package net.sf.tweety.logics.pcl.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.analysis.MinimalViolationInconsistencyMeasure;
import net.sf.tweety.logics.pcl.parser.PclParser;
import net.sf.tweety.math.norm.MaximumNorm;
import net.sf.tweety.math.norm.PNorm;
import net.sf.tweety.math.opt.solver.LpSolve;

import org.junit.Before;
import org.junit.Test;



/**
 * @author Nico Potyka, Matthias Thimm
 *
 */
public class MinimalViolationInconsistencyMeasureLPSolveTest {

	boolean lpsolveConfigured;
	
	double accuracy;
	
	MinimalViolationInconsistencyMeasure inc;
	
	PclParser parser;
	LinkedList<PclBeliefSet> kbs;
	
	
	@Before
	public void setUp() {
		
		lpsolveConfigured = LpSolve.checkBinary();
		
		if(lpsolveConfigured) {
			
			accuracy = 0.001;
			
			parser = new PclParser();
			
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
		else {
			System.err.println("Warning: Can't perform unit test MinimalViolationInconsistencyMeasureLPSolveTest because LPSolve isn't configured properly.");
			System.err.println("Error message: lpsolve executable not found");
		}
		
	}

	
	
	@Test
	public void check1Norm() {
		
		if(!lpsolveConfigured) return;

		inc = new MinimalViolationInconsistencyMeasure(new PNorm(1), new LpSolve());
		
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
		
		if(!lpsolveConfigured) return;


		inc = new MinimalViolationInconsistencyMeasure(new MaximumNorm(), new LpSolve());
		
		LinkedList<Double> expected = new LinkedList<Double>();
		
		expected.add(0d);
		expected.add(0.01);
		expected.add(0.1);
		expected.add(0.3);
		expected.add(0.5);
		expected.add(0.0413);
		
		
		for(PclBeliefSet kb: kbs) {
			assertEquals(expected.removeFirst(), inc.inconsistencyMeasure(kb),accuracy);
		}
		
		
	}
	
}
