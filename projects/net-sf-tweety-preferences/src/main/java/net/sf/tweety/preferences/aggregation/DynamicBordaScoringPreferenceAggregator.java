/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
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
