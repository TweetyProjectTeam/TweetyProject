package net.sf.tweety.preferences.aggregation;

/**
 * Creates an dynamic aggregator with plurality scoring
 * 
 * @author Bastian Wolf
 *
 * @param <T>
 */

public class DynamicPluralityScoringPreferenceAggregator<T> extends DynamicScoringPreferenceAggregator<T> {
	
	/**
	 * calls the super-constructor with argument used for plurality scoring aggregation
	 */
	public DynamicPluralityScoringPreferenceAggregator() {
		super(new SinglePeakWeightVector(0));
		
	}
	
}


