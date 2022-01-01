package org.tweetyproject.sat.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.picosat.PicosatSatSolver;

public class PicosatTest {

	private SatSolver solver;
	
	@Before
	public void setup() {
		this.solver = new PicosatSatSolver();
	}
	
	@After
	public void tearDown() {
		this.solver.close();
	}
	
	@Test
	public void taut1() {
		int var = solver.newVar();
		solver.add(new int[] {var,-var});
		assertTrue(solver.satisfiable());
	}
	
	@Test
	public void unsat1() {
		int var = solver.newVar();
		solver.add(new int[] {var});
		solver.add(new int[] {-var});
		assertFalse(solver.satisfiable());
	}

	@Test
	public void sat1() {
		int var1 = solver.newVar();
		int var2 = solver.newVar();
		solver.add(new int[] {var1,var2});
		assertTrue(solver.satisfiable());
	}
}
