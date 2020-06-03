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
package net.sf.tweety.arg.rankings.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.semantics.ArgumentRanking;
import net.sf.tweety.arg.rankings.semantics.NumericalArgumentRanking;

/**
 * Common utility methods for ranking reasoners.
 * 
 * @author Anna Gessler
 */
public class RankingTools {
	
	/**
	 * Given a set of argument paths of length i-1, this method returns a set of
	 * argument paths of length i for the given DungTheory.
	 * 
	 * @param old_paths set of paths of length i-1
	 * @param base      the DungTheory
	 * @return a set of paths of length i
	 */
	public static HashSet<ArrayList<Argument>> getPathsOfHigherSize(HashSet<ArrayList<Argument>> old_paths, DungTheory base) {
		HashSet<ArrayList<Argument>> new_paths = new HashSet<ArrayList<Argument>>();
		for (ArrayList<Argument> path : old_paths) {
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
	public static ArgumentRanking roundRanking(NumericalArgumentRanking ranking, int n) {
		Iterator<Entry<Argument, Double>> it = ranking.entrySet().iterator();
		NumericalArgumentRanking reval = new NumericalArgumentRanking();
		while (it.hasNext()) {
			Map.Entry<Argument, Double> pair = (Map.Entry<Argument, Double>) it.next();
			Argument a = pair.getKey();
			reval.put(a, round(pair.getValue(), n));
			it.remove();
		}
		return reval;
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
			throw new IllegalArgumentException();
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(n, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
}
