/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pcl.analysis;

import java.util.Set;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.logics.pcl.semantics.ProbabilityDistribution;
import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pcl.util.OjAlgoPclUtils;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.math.probability.Probability;

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
	 * @param beliefSet some belief set
	 * 
	 */
	protected BeliefBase repair(PclBeliefSet beliefSet) {


		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PlSignature) beliefSet.getMinimalSignature());
		int noWorlds = worlds.size();
		
		

		log.debug("Create expression based model.");
		
		Variable[] tmpVariables = OjAlgoPclUtils.createVariables(noWorlds);				
		ExpressionsBasedModel tmpModel = new ExpressionsBasedModel(tmpVariables);
		
		
		
		log.debug("Create objective.");
		
		//Objective is x' Q x + C' x
		//Q = A' A for constraint matrix A, C' = 0
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
		//by construction, the correct solution is the square root of the computed solution
		double tmpObjFuncVal = Math.sqrt(2 * tmpModel.minimise().getValue());
		log.info("Finished computation after "+(System.currentTimeMillis()-time)+" ms. Inconsistency value: "+tmpObjFuncVal);
		
		

		log.debug("Repair knowledge base.");
		
		ProbabilityDistribution<PossibleWorld> p = new ProbabilityDistribution<PossibleWorld>(beliefSet.getMinimalSignature());
		int k=0;
		for(PossibleWorld world: worlds) {
			p.put(world, new Probability(tmpVariables[k++].getValue().doubleValue()));
		}
		
		PclBeliefSet repairedSet = new PclBeliefSet();
		for(ProbabilisticConditional pc: beliefSet) {
			repairedSet.add(new ProbabilisticConditional(pc,p.conditionalProbability(pc)));
		}
		
		return repairedSet;
		
	}



	

}
