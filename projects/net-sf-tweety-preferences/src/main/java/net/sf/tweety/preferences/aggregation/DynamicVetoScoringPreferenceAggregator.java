package net.sf.tweety.preferences.aggregation;

/**
 * Creates an dynamic aggregator for veto scoring
 * 
 * @author Bastian Wolf
 *
 * @param <T>
 */

public class DynamicVetoScoringPreferenceAggregator<T> extends DynamicScoringPreferenceAggregator<T> {

	public DynamicVetoScoringPreferenceAggregator(int min) {
		super(new SingleValeWeightVector(min));
	}
	
}
