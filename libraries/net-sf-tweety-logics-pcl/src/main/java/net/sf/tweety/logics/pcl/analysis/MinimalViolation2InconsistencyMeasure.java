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
package net.sf.tweety.logics.pcl.analysis;

import java.util.Collection;
import java.util.Set;

import org.ojalgo.constant.BigMath;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pcl.PclBeliefSet;
import net.sf.tweety.logics.pcl.syntax.ProbabilisticConditional;
import net.sf.tweety.logics.pcl.util.OjAlgoPclUtils;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;


/**
 * This class models the minimal violation inconsistency measure for the 2-norm.
 * 
 * @author Nico Potyka
 */
public class MinimalViolation2InconsistencyMeasure extends BeliefSetInconsistencyMeasure<ProbabilisticConditional> {


	/**
	 * Logger.
	 */
	static protected Logger log = LoggerFactory.getLogger(MinimalViolation2InconsistencyMeasure.class);


	@Override
	public Double inconsistencyMeasure(Collection<ProbabilisticConditional> formulas) {

		PclBeliefSet beliefSet = new PclBeliefSet(formulas);
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds((PropositionalSignature) beliefSet.getSignature());
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
		double inc = Math.sqrt(tmpModel.minimise().getValue());
		log.info("Finished computation after "+(System.currentTimeMillis()-time)+" ms.");
		
		return inc;
		
	}	
	
	
	
	@Override
	public String toString() {
		return "2-Norm Minimal Violation Measure";
	}


}
