package net.sf.tweety.logics.pcl.analysis;

import java.util.Collection;
import java.util.Set;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * MinimalViolationInconsistencyMeasureLPSolve serves as a superclass for minimal violation
 * measure implementations based on LPSolve.
 * 
 * @author Nico Potyka
 */
public abstract class MinimalViolationInconsistencyMeasureLPSolve extends BeliefSetInconsistencyMeasure<ProbabilisticConditional,PclBeliefSet> {

	
	
	/**
	 * Logger.
	 */
	static protected Logger log = LoggerFactory.getLogger(MinimalViolationInconsistencyMeasureLPSolve.class);


	


	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<ProbabilisticConditional> formulas) {	
		PclBeliefSet beliefBase = new PclBeliefSet(formulas);

		log.info("Compute inconsistency value.");
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PropositionalSignature) beliefBase.getSignature());

		
		double inc = -1;

		try {

			log.debug("Create LP.");
			LpSolve solver = createLP(beliefBase, worlds);


			log.debug("Solve LP.");
		    //TODO check code returned by solve
			long time = System.currentTimeMillis();
		    solver.solve();
			log.info("Computation time: "+(System.currentTimeMillis()-time));
		    
		    inc = solver.getObjective();
			log.debug("Inconsistency value: "+inc);
		    
		    solver.deleteLp();
			
		}
		catch(Exception e)
		{
			log.error("Exception while computing inconsistency measure.", e);
		}
		
		return inc;
	}
	
	
	
	
	protected abstract LpSolve createLP(PclBeliefSet beliefBase, Set<PossibleWorld> worlds) throws LpSolveException;


	
	/**
	 * Set world constraints (1-p, -p, 0) in variable vector for current conditional c. 
	 * @param worlds
	 * @param c
	 * @param variableVector
	 */
	protected void setWorldConstraints(Set<PossibleWorld> worlds, ProbabilisticConditional c, double[] variableVector) {
		
		log.debug("Set constraint for "+c.toString()+".");
		
		int i = 1; //LPSolve starts indexing at 1
		double p = c.getProbability().doubleValue();
		PropositionalFormula conclusion = c.getConclusion();
		
		
		if(c.isFact()) {
			
			for(PossibleWorld w: worlds) {
				if(w.satisfies(conclusion)) {
					variableVector[i] = 1-p;
				}
				else {
					variableVector[i] = -p;
				}
				
				i++;
			}
			
		}
		else {
			
			PropositionalFormula premise = c.getPremise().iterator().next();
			
			for(PossibleWorld w: worlds) {
				
				if(w.satisfies(premise)) {
					
					if(w.satisfies(conclusion)) {
						variableVector[i] = 1-p;
					}
					else {
						variableVector[i] = -p;
					}
					
				}
				else {
					
					variableVector[i] = 0;
				}
				
				i++;
			}
		}
		
	}

}
