package net.sf.tweety.arg.adf.sat;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

public abstract class IncrementalSatSolver extends SatSolver{

	public abstract SatSolverState createState();
	
	public abstract boolean isSatisfiable(SatSolverState state);
	
	public abstract Interpretation<PlBeliefSet, PlFormula> getWitness(SatSolverState state);
	
}
