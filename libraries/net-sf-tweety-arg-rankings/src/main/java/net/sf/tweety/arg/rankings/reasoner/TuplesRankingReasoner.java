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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.rankings.reasoner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.semantics.LatticeArgumentRanking;
import net.sf.tweety.commons.util.Pair;

/**
 * This class implements the "tuples*" argument ranking approach as proposed by
 * [Cayrol, Lagasquie-Schiex. Graduality in argumentation. 2005]. It takes into
 * account all the ancestors branches of an arguument stored in tupled values.
 * <br>
 * <br>
 * Notes on this implementation: <br>
 * - This implementation only works for acyclic argument graphs. For cyclic
 * graphs an empty ranking is returned. <br>
 * - This reasoner generates partial rankings because some arguments are
 * incomparable using this approach.
 * 
 * @author Anna Gessler
 */
public class TuplesRankingReasoner extends AbstractRankingReasoner<LatticeArgumentRanking> {

	/**
	 * Stores the tupled values computed by this reasoner for lookup.
	 */
	private Map<Argument, Pair<int[], int[]>> tupled_values = new HashMap<Argument, Pair<int[], int[]>>();


	@Override
	public Collection<LatticeArgumentRanking> getModels(DungTheory bbase) {
		Collection<LatticeArgumentRanking> ranks = new HashSet<LatticeArgumentRanking>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public LatticeArgumentRanking getModel(DungTheory kb) {
		LatticeArgumentRanking ranking = new LatticeArgumentRanking(kb.getNodes());

		// Check if kb is acyclic
		if (kb.containsCycle())
			return ranking;

		// Compute lookup table for tupled values
		this.tupled_values = new HashMap<Argument, Pair<int[], int[]>>();
		for (Argument a : kb)
			this.tupled_values.put(a, computeTupledValue(a, kb));

		// Tuples* Algorithm
		// Compare lengths of attack/defense branches
		// In case of a tie, compare values inside tuples
		LexicographicTupleComparator c = new LexicographicTupleComparator();

		for (Argument a : kb) {
			for (Argument b : kb) {
				Pair<int[], int[]> tv_a = this.tupled_values.get(a);
				Pair<int[], int[]> tv_b = this.tupled_values.get(b);

				if (tv_a.equals(tv_b)) {
					ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					continue;
				}

				int[] a_defense_tuple = tv_a.getFirst();
				int[] a_attack_tuple = tv_a.getSecond();
				int[] b_defense_tuple = tv_b.getFirst();
				int[] b_attack_tuple = tv_b.getSecond();
				if (a_attack_tuple.length == b_attack_tuple.length
						&& a_defense_tuple.length == b_defense_tuple.length) {
					if ((c.compare(a_defense_tuple, b_defense_tuple) <= 0)
							&& (c.compare(a_attack_tuple, b_attack_tuple) >= 0)) {
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					} else if ((c.compare(a_defense_tuple, b_defense_tuple) >= 1)
							&& (c.compare(a_attack_tuple, b_attack_tuple) < 1)) {
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					}
					// else: incomparable

				} else {
					if (a_attack_tuple.length >= b_attack_tuple.length
							&& a_defense_tuple.length <= b_defense_tuple.length) {
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					} else if (a_attack_tuple.length <= b_attack_tuple.length
							&& a_defense_tuple.length >= b_defense_tuple.length) {
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					}
					// else: incomparable
				}
			}
		}

		return ranking;
	}

	/**
	 * Computes the tupled value for the given argument.
	 * 
	 * @param a  an argument
	 * @param kb DungTheory
	 * @return a pair that consists of the two tuples that represent the defense
	 *         (first tuple) and attack (second tuple) branches of a.
	 */
	public Pair<int[], int[]> computeTupledValue(Argument a, DungTheory kb) {
		ArrayList<Integer> defense = new ArrayList<Integer>();
		ArrayList<Integer> attack = new ArrayList<Integer>();

		Set<Argument> attackers = kb.getAttackers(a);
		if (attackers.isEmpty())
			defense.add(0);
		else {
			for (Argument b : attackers) {
				for (Integer i : computeTupledValue(b, kb).getSecond()) {
					i++;
					defense.add(i);
					if (defense.contains(0))
						defense.remove(defense.indexOf(0));
				}
				Collections.sort(defense);
				for (Integer i : computeTupledValue(b, kb).getFirst()) {
					i++;
					attack.add(i);
					if (attack.contains(0))
						attack.remove(defense.indexOf(0));
				}
				Collections.sort(attack);
			}
		}
		return new Pair<int[], int[]>(defense.stream().mapToInt(i -> i).toArray(),
				attack.stream().mapToInt(i -> i).toArray());
	}

	/**
	 * @return the tupled values computed by previous calls of getModel oder
	 *         getModels
	 */
	public Map<Argument, Pair<int[], int[]>> getTupledValues() {
		return this.tupled_values;
	}

	/**
	 * Prints the tupled values computed by previous calls of getModel oder getModels in a 
	 * human-readable way.
	 */
	public String prettyPrintTupledValues() {
		Set<Argument> args = this.tupled_values.keySet();
		String tv = "";
		for (Argument a : args)
			tv += ", v(" + a + ") = [" + Arrays.toString(tupled_values.get(a).getFirst()) + ","
					+ Arrays.toString(tupled_values.get(a).getSecond()) + "]";
		return tv;
	}

	/**
	 * Compares tuples according to the lexicographic ordering as described in
	 * [Cayrol, Lagasquie-Schiex. Graduality in argumentation. 2005].
	 */
	public class LexicographicTupleComparator implements Comparator<int[]> {
		@Override
		public int compare(int[] o1, int[] o2) {
			if (o1.equals(o2))
				return 0;
			for (int i = 0; i < o1.length; i++) {
				if (o2.length < i + 1)
					break;
				if (o1[i] > o2[i])
					return 1;
			}
			return -1;
		}
	}

}
