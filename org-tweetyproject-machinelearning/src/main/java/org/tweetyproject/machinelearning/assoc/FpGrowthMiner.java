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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.machinelearning.assoc;

import java.util.Collection;
import java.util.HashSet;

/**
 * Implements the FP-Growth Algorithm for frequent pattern mining, cf.
 * [Jiawei Han, Jian Pei, Yiwen Yin. Mining frequent patterns without candidate generation.
 * ACM SIGMOD Record, Volume 29, Issue 2, June 2000 pp 1–12]
 * 
 * @author Matthias Thimm
 *
 * @param <T> the type of items
 */
public class FpGrowthMiner<T extends Object> implements FrequentPatternMiner<T> {

	/** the minimum support for mined sets. */
	private double minsupport;
	
	/**
	 * Creates a new FPGrowth miner with the given minimum support value.
	 * @param minsupport the minimum confidence for mined sets.
	 */
	public FpGrowthMiner(double minsupport){
		this.minsupport = minsupport;		
	}
	
	@Override
	public Collection<Collection<T>> mineFrequentSets(Collection<Collection<T>> database) {
		// determine max number of items
		Collection<T> items = new HashSet<T>();
		for(Collection<T> t: database)
			items.addAll(t);
		return mineFrequentSets(database,items.size());
	}

	@Override
	public Collection<Collection<T>> mineFrequentSets(Collection<Collection<T>> database, int maxsize) {
		// create FP-Tree
		FrequentPatternTree<T> fptree = new FrequentPatternTree<>(database,this.minsupport); 
		// TODO Auto-generated method stub
		return null;
	}

}
