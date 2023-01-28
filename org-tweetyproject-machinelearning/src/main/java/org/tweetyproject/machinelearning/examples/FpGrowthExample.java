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
 * An example for the FP-Growth algorithm
 * 
 * @author Matthias Thimm
 *
 */
public class FpGrowthExample {
	/**
	 * 
	 * @param args arguments
	 */
	public static void main(String[] args) {
		// this is the example from
		// [Jiawei Han, Jian Pei, Yiwen Yin. Mining frequent patterns without candidate generation.
		//  ACM SIGMOD Record, Volume 29, Issue 2, June 2000 pp 1–12]
		Collection<Collection<String>> database = new LinkedList<Collection<String>>();
		database.add(List.of("f","a","c","d","g","i","m","p"));
		database.add(List.of("a","b","c","f","l","m","o"));
		database.add(List.of("b","f","h","j","o"));
		database.add(List.of("b","c","k","s","p"));
		database.add(List.of("a","f","c","e","l","p","m","n"));
		
		FpGrowthMiner<String> miner = new FpGrowthMiner<>(3d/5);
		System.out.println(miner.mineFrequentSets(database));
		// Apriori miner gives the same result
		AprioriMiner<String> miner2 = new AprioriMiner<>(3d/5,1);
		System.out.println(miner2.mineFrequentSets(database));
	}

}
