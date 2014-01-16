package net.sf.tweety.preferences.aggregation;

/**
 * Creates an aggregator with plurality scoring
 * 
 * @author Bastian Wolf
 *
 * @param <T>
 */
public class PluralityScoringPreferenceAggregator<T> extends
		ScoringPreferenceAggregator<T> {

	/**
	 * calls the super-constructor with argument used for plurality scoring aggregation
	 */
	public PluralityScoringPreferenceAggregator() {
		super(new SinglePeakWeightVector(0));
	}


}
