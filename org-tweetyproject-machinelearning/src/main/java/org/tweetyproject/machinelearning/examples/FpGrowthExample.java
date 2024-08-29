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
package org.tweetyproject.machinelearning.examples;

import java.util.*;

import org.tweetyproject.machinelearning.assoc.AprioriMiner;
import org.tweetyproject.machinelearning.assoc.FpGrowthMiner;
/**
 * An example demonstrating the use of the FP-Growth algorithm for mining frequent patterns
 * from a dataset of transactions.

 * @author Matthias Thimm
 */
public class FpGrowthExample {
	/**
	 * Default Constructor
	 */
	public FpGrowthExample(){
		//default
	}

    /**
     * The main method demonstrates the FP-Growth algorithm and the Apriori algorithm
     * for mining frequent itemsets from a transaction dataset.
     *
     * @param args command-line arguments (not used in this example).
     */
    public static void main(String[] args) {
        // Example dataset from the paper:
        // [Jiawei Han, Jian Pei, Yiwen Yin. Mining frequent patterns without candidate generation.
        // ACM SIGMOD Record, Volume 29, Issue 2, June 2000 pp 1–12]
        Collection<Collection<String>> database = new LinkedList<>();
        database.add(List.of("f", "a", "c", "d", "g", "i", "m", "p"));
        database.add(List.of("a", "b", "c", "f", "l", "m", "o"));
        database.add(List.of("b", "f", "h", "j", "o"));
        database.add(List.of("b", "c", "k", "s", "p"));
        database.add(List.of("a", "f", "c", "e", "l", "p", "m", "n"));

        // FP-Growth miner with a minimum support threshold of 3/5
        FpGrowthMiner<String> miner = new FpGrowthMiner<>(3d / 5);
        System.out.println(miner.mineFrequentSets(database));

        // Apriori miner with the same minimum support threshold for comparison
        AprioriMiner<String> miner2 = new AprioriMiner<>(3d / 5, 1);
        System.out.println(miner2.mineFrequentSets(database));
    }
}
