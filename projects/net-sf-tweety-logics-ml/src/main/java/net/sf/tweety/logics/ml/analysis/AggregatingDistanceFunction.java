/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.ml.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * This distance function uses an aggregator on the 1-norm distance of each 
 * value.
 * 
 * @author Matthias Thimm
 */
public class AggregatingDistanceFunction implements DistanceFunction {

	private static final long serialVersionUID = 7393475547325748126L;

	/** The aggregation function used for computing the distance. */	
	private AggregationFunction aggregator;
	
	/** Creates a new distance function with the given aggregator.
	 * @param aggregator some aggregation function.
	 */
	public AggregatingDistanceFunction(AggregationFunction aggregator){
		this.aggregator = aggregator;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.analysis.DistanceFunction#distance(java.util.List, java.util.List)
	 */
	@Override
	public double distance(List<Double> l1, List<Double> l2) {
		if(l1.size() != l2.size())
			throw new IllegalArgumentException("Lengths of lists must match.");
		List<Double> diff = new ArrayList<Double>();
		for(int i = 0; i< l1.size(); i++)
			diff.add(Math.abs(l1.get(i)-l2.get(i)));
		return this.aggregator.aggregate(diff);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.aggregator.toString()+"-dist";
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
		AggregatingDistanceFunction other = (AggregatingDistanceFunction) obj;
		if (aggregator == null) {
			if (other.aggregator != null)
				return false;
		} else if (!aggregator.equals(other.aggregator))
			return false;
		return true;
	}
}
