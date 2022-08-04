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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.GeneralComparator;
import org.tweetyproject.comparator.NumericalPartialOrder;

/**
 * This class contains common utility methods for ranking reasoners.
 * 
 * @author Anna Gessler
 */
public class RankingTools {
	
	/**
	 * Given a set of argument paths of length i-1, this method returns a set of
	 * argument paths of length i for the given DungTheory.
	 * 
	 * @param oldPaths set of paths of length i-1
	 * @param base      the DungTheory
	 * @return a set of paths of length i
	 */
	public static HashSet<ArrayList<Argument>> getPathsOfHigherSize(HashSet<ArrayList<Argument>> oldPaths, DungTheory base) {
		HashSet<ArrayList<Argument>> new_paths = new HashSet<ArrayList<Argument>>();
		for (ArrayList<Argument> path : oldPaths) {
			Argument tail = path.get(path.size() - 1);
			for (Argument attacker : base.getAttackers(tail)) {
					ArrayList<Argument> new_path = new ArrayList<Argument>();
					new_path.addAll(path);
					new_path.add(attacker);
					new_paths.add(new_path);
			}
		}
		return new_paths;
	}
	
	/**
	 * Rounds values in the given numerical argument ranking to n decimals.
	 * 
	 * @param ranking a NumericalArgumentRanking
	 * @param n       decimals
	 * @return rounded NumericalArgumentRanking
	 */
	public static GeneralComparator<Argument, DungTheory> roundRanking(NumericalPartialOrder<Argument, DungTheory> ranking, int n) {
		Iterator<Entry<Argument, Double>> it = ranking.entrySet().iterator();
		NumericalPartialOrder<Argument, DungTheory> roundedRanking = new NumericalPartialOrder<Argument, DungTheory>();
		while (it.hasNext()) {
			Map.Entry<Argument, Double> pair = (Map.Entry<Argument, Double>) it.next();
			Argument a = pair.getKey();
			roundedRanking.put(a, round(pair.getValue(), n));
			it.remove();
		}
		return roundedRanking;
	}
	
	/**
	 * Rounds a double value to n decimals.
	 * 
	 * @param value a double value
	 * @param n     number of decimals
	 * @return value rounded to n decimals
	 */
	private static double round(double value, int n) {
		if (n < 0)
			throw new IllegalArgumentException("A value cannot be rounded to less than 0 decimals.");
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(n, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
}
