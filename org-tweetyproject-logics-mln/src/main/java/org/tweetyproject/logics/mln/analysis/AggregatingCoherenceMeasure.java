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
package org.tweetyproject.logics.mln.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.mln.reasoner.AbstractMlnReasoner;
import org.tweetyproject.logics.mln.syntax.MarkovLogicNetwork;
import org.tweetyproject.logics.mln.syntax.MlnFormula;
import org.tweetyproject.math.func.AggregationFunction;
import org.tweetyproject.math.norm.RealVectorNorm;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;

/**
 * This coherence measure uses an aggregation function and a distance function
 * to measure the coherence of an MLN. For each formula in the MLN the distance
 * function looks at the probabilities of all ground instance and compares this 
 * vector to the intended probabilities. The aggregation function is used to
 * aggregate the distances for all formulas. 
 * 
 * @author Matthias Thimm
 *
 */
public class AggregatingCoherenceMeasure extends AbstractCoherenceMeasure {

	private static final long serialVersionUID = 4162719595968757160L;
	
	/** The norm used to measure the difference of the probabilities
	 * of each ground instance for a single formula. */
	private RealVectorNorm norm;
	/** The aggregation function used to aggregate the distances for each formula. */
	private AggregationFunction aggregator;
	
	public AggregatingCoherenceMeasure(RealVectorNorm norm, AggregationFunction aggregator){
		this.aggregator = aggregator;
		this.norm = norm;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.markovlogic.analysis.AbstractCoherenceMeasure#coherence(org.tweetyproject.logics.markovlogic.MarkovLogicNetwork, org.tweetyproject.Reasoner, org.tweetyproject.logics.firstorderlogic.syntax.FolSignature)
	 */
	@Override
	public double coherence(MarkovLogicNetwork mln, AbstractMlnReasoner reasoner, FolSignature signature) {
		List<Double> distances = new ArrayList<Double>();
		for(MlnFormula f: mln){
			Vector<Double> intended = new Vector<Double>();
			Vector<Double> observed = new Vector<Double>();
			Double pObserved;
			if(f.isStrict())
				pObserved = 1d;
			else pObserved = (Math.exp(f.getWeight())/(f.getFormula().getSatisfactionRatio()+Math.exp(f.getWeight())));
			for(RelationalFormula groundFormula: f.getFormula().allGroundInstances(signature.getConstants())){
				observed.add(reasoner.query(mln,(FolFormula) groundFormula));
				intended.add(pObserved);
			}			
			distances.add(this.norm.distance(intended, observed));
		}
		return 1-this.aggregator.eval(distances);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.markovlogic.analysis.AbstractCoherenceMeasure#toString()
	 */
	@Override
	public String toString() {		
		return "C<" + this.norm.toString() + ", " + this.aggregator.toString() + ">";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((aggregator == null) ? 0 : aggregator.hashCode());
		result = prime * result
				+ ((norm == null) ? 0 : norm.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregatingCoherenceMeasure other = (AggregatingCoherenceMeasure) obj;
		if (aggregator == null) {
			if (other.aggregator != null)
				return false;
		} else if (!aggregator.equals(other.aggregator))
			return false;
		if (norm == null) {
			if (other.norm != null)
				return false;
		} else if (!norm.equals(other.norm))
			return false;
		return true;
	}

}
