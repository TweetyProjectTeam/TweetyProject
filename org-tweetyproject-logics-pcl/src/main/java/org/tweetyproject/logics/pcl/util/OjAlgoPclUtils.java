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
package org.tweetyproject.logics.pcl.util;

import java.util.Set;

import org.tweetyproject.logics.pcl.syntax.PclBeliefSet;
import org.tweetyproject.logics.pcl.syntax.ProbabilisticConditional;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import org.ojalgo.access.Access2D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;

/**
 * Provides some utility functions for solving Pcl specific reasoning problems with ojAlgo.
 * 
 * @author NicoPotyka
 *
 */
public class OjAlgoPclUtils {

	
	/**
	 * Add probability normalization constraint to model 
	 * (all probabilities have to sum to 1). 
	 * @param model the model
	 */
	public static void addProbabilityNormalizationConstraint(ExpressionsBasedModel model) {
		
		Expression tmpExpr = model.addExpression("Normalization").level(BigMath.ONE);
		for(int i=0; i<model.countVariables(); i++) {
			tmpExpr.setLinearFactor(i, BigMath.ONE);
		}
		
	}
	

	/**
	 * Create (non-negative) variables for the probabilities of possible worlds. 
	 * @param noWorlds the number of worlds
	 * @return the variables
	 */
	public static Variable[] createVariables(int noWorlds) {
	
		Variable[] tmpVariables = new Variable[noWorlds];		
		for(int i=0; i<noWorlds; i++) {
			tmpVariables[i] = new Variable(""+i).lower(BigMath.ZERO);
		}
		
		return tmpVariables;
	}
	


	/**
	 * Create constraint matrix for a set of PCL conditionals over a set of possible worlds.
	 * @param beliefSet a belief set
	 * @param worlds a set of possible worlds
	 * @return the constraint matrix
	 */
	public static PrimitiveMatrix createConstraintMatrix(PclBeliefSet beliefSet, Set<PossibleWorld> worlds) {
		
		int i = 0;
		
		Access2D.Builder<PrimitiveMatrix> aBuilder = PrimitiveMatrix.FACTORY.getBuilder(beliefSet.size(), worlds.size());
		
		for(ProbabilisticConditional c: beliefSet) {
			
			int j = 0; 
			double p = c.getProbability().doubleValue();
			PlFormula conclusion = c.getConclusion();
			
			if(c.isFact()) {
				
				for(PossibleWorld w: worlds) {
					if(w.satisfies(conclusion)) {
						aBuilder.set(i, j, 1-p);
					}
					else {
						aBuilder.set(i, j, -p);
					}
					
					j++;
				}
				
			}
			else {
				
				PlFormula premise = c.getPremise().iterator().next();
				
				for(PossibleWorld w: worlds) {
					
					if(w.satisfies(premise)) {
						
						if(w.satisfies(conclusion)) {
							aBuilder.set(i, j, 1-p);
						}
						else {
							aBuilder.set(i, j, -p);
						}
						
					}

					j++;
				}
			}
			
			i++;
		}
		
		return aBuilder.build();
	}
	

	
}
