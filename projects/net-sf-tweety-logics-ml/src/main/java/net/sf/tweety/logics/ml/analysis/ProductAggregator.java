package net.sf.tweety.logics.ml.analysis;

import java.util.List;

/** This aggregation function models the product function.
 * @author Matthias Thimm
 *
 */
public class ProductAggregator implements AggregationFunction {

	private static final long serialVersionUID = -2717855227084369340L;

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.analysis.AggregationFunction#aggregate(java.util.List)
	 */
	@Override
	public double aggregate(List<Double> elements) {
		Double prod = new Double(1);
		for(Double elem: elements)
			prod *= elem;
		return prod;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "prod";
	}
}
