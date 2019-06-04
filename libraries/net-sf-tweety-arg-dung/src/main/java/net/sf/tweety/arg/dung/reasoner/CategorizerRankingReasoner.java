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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.semantics.NumericalArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.math.matrix.Matrix;

/**
 * This class implements the "h-categorizer" argument ranking approach that was 
 * originally proposed by [Besnard, Hunter. A logic-based theory of deductive arguments. 2001]
 * for deductive logics. It uses the Fixed-point algorithm of 
 * [Pu, Zhang, Luo, Luo. Argument Ranking with Categoriser Function. 2014]
 * which allows for cycles in argumentation graphs.
 * 
 * @see {@link net.sf.tweety.arg.deductive.categorizer.HCategorizer}
 * 
 * @author Anna Gessler
 */
public class CategorizerRankingReasoner extends AbstractRankingReasoner<NumericalArgumentRanking> {
	@Override
	public Collection<NumericalArgumentRanking> getModels(DungTheory bbase) {
		Collection<NumericalArgumentRanking> ranks = new HashSet<NumericalArgumentRanking>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public NumericalArgumentRanking getModel(DungTheory base) {
		Matrix directAttackMatrix = base.getAdjancyMatrix().transpose(); //The matrix of direct attackers
		int n = directAttackMatrix.getXDimension();
		double valuations[] = new double[n];	 //Stores valuations of the current iteration
		double valuations_old[] = new double[n]; //Stores valuations of the last iteration
		
		//Keep computing valuations until the values stop changing much or converge 
		double epsilon = 0.001; 
		do {
			valuations_old = valuations.clone();
			for (int i = 0; i < n; i++) 
				valuations[i] = calculateCategorizerFunction(valuations_old,directAttackMatrix,i);
		} while (getDistance(valuations_old, valuations) > epsilon);
	
		//Use computed valuations as values for argument ranking
		//Note: The order of valuations v[i] is the same as the ordner of DungTheory.iterator()
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		int i = 0;
		for (Argument a : base) 
			ranking.put(a, valuations[i++]);
		return ranking;
	}

	/**
	 * Computes the h-Categorizer function.
	 * @param v_old array of double valuations that were computed in the previous iteration
	 * @param directAttackMatrix complete matrix of direct attacks
	 * @param i row of the attack matrix that will be used in the calculation
	 * @return categorizer valuation
	 */
	private double calculateCategorizerFunction(double[] v_old, Matrix directAttackMatrix, int i) {
		double c = 1.0;
		for (int j = 0; j < directAttackMatrix.getXDimension(); j++) {
			c += v_old[j] * directAttackMatrix.getEntry(i,j).doubleValue();
		}
		return (1.0 / c);
		
	}

	/**
	 * Computes the Euclidean distance between to the given arrays.
	 * @param v_old first array
	 * @param v second array
	 * @return distance between v and v_old
	 */
	private double getDistance(double[] v_old, double[] v) {
		double sum = 0.0;
		for (int i = 0; i < v.length; i++) {
			sum += Math.pow(v[i]-v_old[i],2.0);
		}
		return Math.sqrt(sum);
	}
	
	@Override 
	public String toString() {
		return "Categorizer";
	}

}
