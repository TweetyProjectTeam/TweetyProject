package net.sf.tweety.logics.ml.analysis;

import java.io.Serializable;
import java.util.List;

/**
 * This interface defines a distance function for two vectors of doubles.
 * 
 * @author Matthias Thimm
 */
public interface DistanceFunction extends Serializable {
	
	/** Measures the distance between the two vectors.
	 * @param l1 some list of doubles.
	 * @param l2 some list of doubles.
	 * @return the distance between the two vectors.
	 */
	public double distance(List<Double> l1, List<Double> l2);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString();
}
