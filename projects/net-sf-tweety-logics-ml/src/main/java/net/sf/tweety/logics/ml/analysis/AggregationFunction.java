package net.sf.tweety.logics.ml.analysis;

import java.io.Serializable;
import java.util.List;

/**
 * This class aggregates a list of doubles to a single double.
 * 
 * @author Matthias Thimm
 */
public interface AggregationFunction extends Serializable {

	/** Aggregates the elements to a single double.
	 * @param elements a list of double
	 * @return a double
	 */
	public double aggregate(List<Double> elements);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString();
}
