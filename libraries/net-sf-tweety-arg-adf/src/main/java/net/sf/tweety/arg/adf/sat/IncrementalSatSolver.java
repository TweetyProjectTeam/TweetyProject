package net.sf.tweety.arg.adf.sat;

import net.sf.tweety.logics.pl.sat.SatSolver;

public abstract class IncrementalSatSolver extends SatSolver {

	public abstract SatSolverState createState();

}
