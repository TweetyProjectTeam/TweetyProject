package net.sf.tweety.logics.pcl.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;

import net.sf.tweety.ParserException;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.analysis.MinimalViolation1InconsistencyMeasure;
import net.sf.tweety.logics.pcl.analysis.MinimalViolationInconsistencyMeasureLPSolve;
import net.sf.tweety.logics.pcl.analysis.MinimalViolationMaxInconsistencyMeasure;
import net.sf.tweety.logics.pcl.parser.PclParser;
import net.sf.tweety.math.util.LPSolveMathUtils;

import org.junit.Before;
import org.junit.Test;



public class MinimalViolationInconsistencyMeasureLPSolveTest {

	boolean lpsolveConfigured;
	
	double accuracy;
	
	MinimalViolationInconsistencyMeasureLPSolve inc;
	
	PclParser parser;
	LinkedList<PclBeliefSet> kbs;
	
	
	@Before
	public void setUp() {
		
		lpsolveConfigured = LPSolveMathUtils.isLPSolveConfigured();
		
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
			System.err.println("Can't perform unit test MinimalViolationInconsistencyMeasureLPSolveTest because LPSolve isn't configured properly.");
		}
		
	}

	
	
	@Test
	public void check1Norm() {
		
		if(!lpsolveConfigured) return;

		inc = new MinimalViolation1InconsistencyMeasure();
		
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


		inc = new MinimalViolationMaxInconsistencyMeasure();
		
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
