package net.sf.tweety.logics.ml.analysis;

import java.util.List;

/** This aggregation function models the minimum function.
 * @author Matthias Thimm
 *
 */
public class MinAggregator implements AggregationFunction {

	private static final long serialVersionUID = -8571729834785975974L;

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.analysis.AggregationFunction#aggregate(java.util.List)
	 */
	@Override
	public double aggregate(List<Double> elements) {
		Double min = Double.MAX_VALUE;
		for(Double elem: elements)
			if(elem < min) min = elem;
		return min;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "min";
	}
}
