package net.sf.tweety.logics.ml.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * This distance function uses an aggregator on a probabilistically normalized distance
 * for probabilities of each value.
 * 
 * @author Matthias Thimm
 */
public class ProbabilisticAggregatingDistanceFunction implements DistanceFunction {

	private static final long serialVersionUID = -114048201529519281L;

	/** The aggregation function used for computing the distance. */	
	private AggregationFunction aggregator;
	
	/** A parameter for adjusting the aggregation. */
	private int weight;
	
	/** Creates a new distance function with the given aggregator.
	 * @param aggregator some aggregation function.
	 * @param weight a parameter for adjusting the aggregation.
	 */
	public ProbabilisticAggregatingDistanceFunction(AggregationFunction aggregator, int weight){
		if(weight <= 2 || (weight % 2 == 0))
				throw new IllegalArgumentException("The weight must be odd and greater two.");
		this.aggregator = aggregator;
		this.weight = weight;
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
			diff.add(Math.abs( Math.pow(0.5-l1.get(i),this.weight)-Math.pow(0.5-l2.get(i),this.weight)));
		return this.aggregator.aggregate(diff);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.aggregator.toString()+"-pdist";
	}
}
