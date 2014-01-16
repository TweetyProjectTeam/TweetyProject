package net.sf.tweety.preferences.aggregation;

/**
 * Creates an aggregator for veto scoring
 * 
 * @author Bastian Wolf
 *
 * @param <T>
 */
public class VetoScoringPreferenceAggregator<T> extends
		ScoringPreferenceAggregator<T> {

	/**
	 * constructor for a new veto aggregator
	 * @param min the minimum rank, marking the element with the fewest vetos 
	 */
	public VetoScoringPreferenceAggregator(int min) {
		super(new SingleValeWeightVector(min));
	}

}
