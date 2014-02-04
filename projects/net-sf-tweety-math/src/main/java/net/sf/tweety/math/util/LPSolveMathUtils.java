package net.sf.tweety.math.util;

import lpsolve.LpSolve;

public class LPSolveMathUtils {

	/**
	 * Test whether LPSolve is configured correctly by solving a simple optimization problem.
	 * @return
	 */
	public static boolean isLPSolveConfigured() {

		try {
			//minimize x1 subject to x1 >= 0
			double[] variableVector = new double[1];
			
			LpSolve solver = LpSolve.makeLp(0, 1);

			solver.setLowbo(1, 0);		
			solver.setMinim();
			solver.setVerbose(0);
			
			solver.setAddRowmode(true);
			variableVector[0] = 1;
			solver.setObjFn(variableVector);
			solver.setAddRowmode(false);
			
			solver.solve();
		}
		catch(Error e) {
			System.err.println(false);
			return false;
		}
		catch(Exception e) {
			System.err.println(false);
			return false;
		}
		
		return true;
	}
	
}
