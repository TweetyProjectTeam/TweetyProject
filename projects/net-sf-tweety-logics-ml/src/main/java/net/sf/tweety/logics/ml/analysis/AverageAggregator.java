package net.sf.tweety.logics.ml.analysis;

import java.util.List;

/** This aggregation function models the average function.
 * @author Matthias Thimm
 *
 */
public class AverageAggregator implements AggregationFunction {

	private static final long serialVersionUID = -3619001311014631332L;

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.analysis.AggregationFunction#aggregate(java.util.List)
	 */
	@Override
	public double aggregate(List<Double> elements) {
		Double sum = new Double(0);
		for(Double elem: elements)
			sum += elem;
		return sum/elements.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "avg";
	}
}
