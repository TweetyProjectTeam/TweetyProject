package net.sf.tweety.preferences.aggregation;

/**
 * Implementation of the borda scoring preference aggregator
 * @author Bastian Wolf
 *
 * @param <T>
 */
public class BordaScoringPreferenceAggregator<T> extends
		ScoringPreferenceAggregator<T> {

	/**
	 * calls super-constructor with the amount of domain elements to aggregate
	 * @param size
	 */
	public BordaScoringPreferenceAggregator(int size) {
		super(new BordaWeightVector(size));
	}

}
