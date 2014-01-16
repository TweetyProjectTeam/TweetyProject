package net.sf.tweety.logics.pcl.analysis;

import java.util.Set;

import net.sf.tweety.BeliefBase;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pcl.util.OjAlgoPclUtils;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.math.probability.Probability;

import org.ojalgo.constant.BigMath;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;

/**
 * Repairs a probabilistic belief base by taking the probabilities from the probability function
 * that minimizes the "minimal violation inconsistency measure" with respect to the euclidean norm.
 * 
 * Implementation uses ojAlgos expression based representation (good numerical performance, but maybe slow).
 * 
 * @author Nico Potyka
 */
public class MinimalViolationEuclideanMachineShopOjAlgoExpression extends MinimalViolationEuclideanMachineShop {


	
	/**
	 * Compute solution using ojalgos matrix representation.
	 * @param beliefBase
	 * @return
	 */
	protected BeliefBase repair(PclBeliefSet beliefSet) {


		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PropositionalSignature) beliefSet.getSignature());
		int noWorlds = worlds.size();
		
		

		log.debug("Create expression based model.");
		
		Variable[] tmpVariables = OjAlgoPclUtils.createVariables(noWorlds);				
		ExpressionsBasedModel tmpModel = new ExpressionsBasedModel(tmpVariables);
		
		
		
		log.debug("Create objective.");
		
		//Objective is 1/2 x' Q x + C' x
		//Q = 2 A' A for constraint matrix A, C' = 0
		//tmpQ = 1/2 Q = A' A to avoid scalar multiplication. If interested in correct function value, multiply optimization result by 2. 
		PrimitiveMatrix tmpM = OjAlgoPclUtils.createConstraintMatrix(beliefSet, worlds);
		PrimitiveMatrix tmpQ = tmpM.transpose().multiplyRight(tmpM);
		
		Expression tmpExpr = tmpModel.addExpression("Objective");
		tmpModel.setMinimisation(true);
		for(int i=0; i<tmpQ.countRows(); i++) {
			for(int j=0; j<tmpQ.countColumns(); j++) {
				double q = tmpQ.get(i, j);
				if(q != 0) { //entries of Q can become arbitrary close to 0, therefore we have to check for equality
					tmpExpr.setQuadraticFactor(i, j, q);
				}
			}
		}
		tmpExpr.weight(BigMath.ONE);
		
		
		
		log.debug("Create normalization constraint.");
		
		OjAlgoPclUtils.addProbabilityNormalizationConstraint(tmpModel);
		
		

		log.debug("Solve.");
		
		long time = System.currentTimeMillis();
		//by construction, the correct solution is the square root of the multiple of 2 of the computed solution
		double tmpObjFuncVal = Math.sqrt(2 * tmpModel.minimise().getValue());
		log.info("Finished computation after "+(System.currentTimeMillis()-time)+" ms. Inconsistency value: "+tmpObjFuncVal);
		
		

		log.debug("Repair knowledge base.");
		
		ProbabilityDistribution<PossibleWorld> p = new ProbabilityDistribution<PossibleWorld>(beliefSet.getSignature());
		int k=0;
		for(PossibleWorld world: worlds) {
			p.put(world, new Probability(tmpVariables[k++].getValue().doubleValue()));
		}
		
		PclBeliefSet repairedSet = new PclBeliefSet();
		for(ProbabilisticConditional pc: beliefSet) {
			repairedSet.add(new ProbabilisticConditional(pc,p.probability(pc)));
		}
		
		return repairedSet;
		
	}



	

}
