package net.sf.tweety.preferences.aggregation;

/**
 * Creates an aggregator for dynamic veto scoring
 * 
 * @author Bastian Wolf
 * 
 * @param <T>
 */
public class DynamicBordaScoringPreferenceAggregator<T> extends
		DynamicScoringPreferenceAggregator<T> {

	/**
	 * constructor for a new veto aggregator
	 * 
	 * @param size
	 *            the minimum rank, marking the element with the fewest vetos
	 */
	public DynamicBordaScoringPreferenceAggregator(int size) {
		super(new BordaWeightVector(size));
	}

}
