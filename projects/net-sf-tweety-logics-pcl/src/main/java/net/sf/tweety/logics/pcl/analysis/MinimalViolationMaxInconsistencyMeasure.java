package net.sf.tweety.logics.pcl.analysis;

import java.util.Set;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;


/**
 * This class models the minimal violation inconsistency measure for the maximum norm.
 * 
 * @author Nico Potyka
 */
public class MinimalViolationMaxInconsistencyMeasure extends MinimalViolationInconsistencyMeasureLPSolve {

	
	/**
	 * Create  the linear program.
	 * @param beliefBase
	 * @param worlds
	 * @param noWorlds
	 * @return
	 * @throws LpSolveException
	 */
	protected LpSolve createLP(PclBeliefSet beliefBase, Set<PossibleWorld> worlds) throws LpSolveException {
		
		int noWorlds = worlds.size();
		int i;

		//there is one variable for each world in dist and one variable for the constraint violation (LPSolve starts at index 1)
		double[] variableVector = new double[noWorlds + 2];
		
		
		LpSolve solver = LpSolve.makeLp(0, noWorlds + 1);

		
		//generate non-negativity constraint
		for(i=0; i<noWorlds + 1; i++) {
			solver.setLowbo(i+1, 0);
		}
		
		solver.setMinim();
		solver.setVerbose(0);
		
		/**********************************************************************
		 * turn row entry mode on
		 ********************************************************************** */
		solver.setAddRowmode(true);
		

		log.debug("Generate objective.");
		
		//first generate objective!
		//the objective is to minimize the conditional constraint violation sum(y)
		for(i=0 ; i<noWorlds; i++) {
			variableVector[i+1] = 0;
		}
		variableVector[i+1] = 1;
		solver.setObjFn(variableVector);


		log.debug("Generate normalizing constraint.");
		
		//generate normalizing constraint sum(p)=1 for probabilities
		for(i=0 ; i<noWorlds; i++) {
			variableVector[i+1] = 1;
		}
		variableVector[i+1] = 0;
		solver.addConstraint(variableVector, LpSolve.EQ, 1);

		
		log.debug("Generate constraints for conditionals.");

		//for each conditional generate constraints a p <= y_k and -y_k <= a p
		//To do so, for k-th conditional we add a_k p - y_k <= 0 and a_k p + y_k >= 0
		for(ProbabilisticConditional c: beliefBase) {

			setWorldConstraints(worlds, c, variableVector);
			

			
			//add negative inequality a_k p - y_k <= 0 
			variableVector[noWorlds+1] = -1;
			
			solver.addConstraint(variableVector, LpSolve.LE, 0);

			//add positive inequality a_k p + y_k >= 0
			variableVector[noWorlds+1] = 1;

			solver.addConstraint(variableVector, LpSolve.GE, 0);	
		}

		/**********************************************************************
		 * turn row entry mode off
		 ********************************************************************** */
		solver.setAddRowmode(false);
		
		
		
		return solver;
	}
	
	
	
	@Override
	public String toString() {
		return "Maximum Norm Minimal Violation Measure";
	}


}
