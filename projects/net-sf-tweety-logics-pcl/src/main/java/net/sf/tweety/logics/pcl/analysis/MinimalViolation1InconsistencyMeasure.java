package net.sf.tweety.logics.pcl.analysis;

import java.util.Set;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;


/**
 * This class models the minimal violation inconsistency measure for the 1-norm.
 * 
 * @author Nico Potyka
 */
public class MinimalViolation1InconsistencyMeasure extends MinimalViolationInconsistencyMeasureLPSolve {

	
	protected LpSolve createLP(PclBeliefSet beliefBase, Set<PossibleWorld> worlds) throws LpSolveException {
		
		log.debug("Create LP.");

		int noWorlds = worlds.size();
		int noConditionals = beliefBase.size();
		int i;

		//there is one variable for each world in dist and one variable for each constraint (LPSolve starts at index 1)
		double[] variableVector = new double[noWorlds + noConditionals + 1];
		
		LpSolve solver = LpSolve.makeLp(0, noWorlds + noConditionals);

		
		//generate non-negativity constraint
		for(i=0; i<noWorlds + noConditionals; i++) {
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
		for(; i<noWorlds+noConditionals; i++) {
			variableVector[i+1] = 1;
		}
		solver.setObjFn(variableVector);


		log.debug("Generate normalizing constraint.");
		
		//generate normalizing constraint sum(p)=1 for probabilities
		for(i=0 ; i<noWorlds; i++) {
			variableVector[i+1] = 1;
		}
		for(; i<noWorlds+noConditionals; i++) {
			variableVector[i+1] = 0;
		}
		solver.addConstraint(variableVector, LpSolve.EQ, 1);

		
		log.debug("Generate constraints for conditionals.");
		
		//for each conditional generate constraints a p <= y_k and -y_k <= a p
		//To do so, for k-th conditional we add a_k p - y_k <= 0 and a_k p + y_k >= 0
		int k = 0;
		for(ProbabilisticConditional c: beliefBase) {

			setWorldConstraints(worlds, c, variableVector);
			
			
			//add a_k p  <= y_k:  a_k p - y_k <= 0 
			variableVector[noWorlds+1+k] = -1;
			solver.addConstraint(variableVector, LpSolve.LE, 0); //for some reason, the variablevector is changed. Therefore the vector is cloned

			//add -y_k <= a_k p: a_k p + y_k >= 0
			variableVector[noWorlds+1+k] = 1;
			solver.addConstraint(variableVector, LpSolve.GE, 0);
			
			//reset
			variableVector[noWorlds+1+k] = 0;
			
			k++;
		}

		/**********************************************************************
		 * turn row entry mode off
		 ********************************************************************** */
		solver.setAddRowmode(false);
		return solver;
	}
	
	@Override
	public String toString() {
		return "1-Norm Minimal Violation Measure";
	}


}
