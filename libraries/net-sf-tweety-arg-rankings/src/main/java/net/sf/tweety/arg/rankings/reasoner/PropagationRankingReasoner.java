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
package net.sf.tweety.arg.rankings.reasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.semantics.LatticeArgumentRanking;

/**
 * This class implements the argument ranking approach of [Delobelle. Ranking-
 * based Semantics for Abstract Argumentation. Thesis, 2017]
 *
 * In this approach, initial values are assigned to arguments and then
 * propagated into the graph. The paper describes three different ways of
 * computing a ranking out of the propagation vector.
 * 
 * @author Anna Gessler
 */
public class PropagationRankingReasoner extends AbstractRankingReasoner<LatticeArgumentRanking> {
	/**
	 * Determines the influence of attacked arguments. The smaller this value is,
	 * the more important is the influence of the non-attacked arguments.
	 */
	private double attacked_arguments_influence;

	/**
	 * Determines whether the multiset (M) of attackers/defenders of some length is used
	 * instead of the set (S). Using the multiset means that if there are multiple
	 * paths from an argument a to an argument b, multiple values will be
	 * propagated from a to b instead of just one.
	 */
	private boolean use_multiset;

	/**
	 * The three propagation semantics:
	 * <ol>
	 * <li>PROPAGATION1 ("Propa_epsilon")</li>
	 * <li>PROPAGATION2 ("Propa_{1+epsilon}")</li>
	 * <li>PROPAGATION3 ("Propa_{1->epsilon}")</li>
	 * </ol>
	 */
	public enum PropagationSemantics {
		PROPAGATION1, PROPAGATION2, PROPAGATION3,
	};

	private PropagationSemantics semantics;

	/**
	 * Creates a new PropagationRankingReasoner with the given parameters.
	 * 
	 * @param use_multiset determines whether the multiset (M) of
	 *                     attackers/defenders of length is used instead of the set
	 *                     (S)
	 */
	public PropagationRankingReasoner(boolean use_multiset) {
		this.attacked_arguments_influence = 0.75;
		this.use_multiset = use_multiset;
		this.semantics = PropagationSemantics.PROPAGATION1;
	}

	/**
	 * Creates a new PropagationRankingReasoner with the given parameters.
	 * 
	 * @param attacked_arguments_influence the smaller this value is, the more
	 *                                     important is the influence of the
	 *                                     non-attacked arguments.
	 * @param use_multiset                 determines whether the multiset (M) of
	 *                                     attackers/defenders of length is used
	 *                                     instead of the set (S)
	 * @param semantics                    one of the three propagation semantics
	 */
	public PropagationRankingReasoner(double attacked_arguments_influence, boolean use_multiset,
			PropagationSemantics semantics) {
		if (attacked_arguments_influence < 0.0 || attacked_arguments_influence > 1.0)
			throw new IllegalArgumentException("Parameter is outside of acceptable parameter range [0.0,1.0]");
		this.attacked_arguments_influence = attacked_arguments_influence;
		this.use_multiset = use_multiset;
		this.semantics = semantics;
	}

	@Override
	public Collection<LatticeArgumentRanking> getModels(DungTheory bbase) {
		Collection<LatticeArgumentRanking> ranks = new HashSet<LatticeArgumentRanking>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public LatticeArgumentRanking getModel(DungTheory kb) {
		LatticeArgumentRanking ranking = new LatticeArgumentRanking(kb.getNodes());
		LexicographicTupleComparator c = new LexicographicTupleComparator();
		Map<Argument, List<Double>> pv = calculatePropagationVector(kb);

		if (this.semantics == PropagationSemantics.PROPAGATION1) {
			for (Argument a : kb) {
				for (Argument b : kb) {
					double[] pv_a = pv.get(a).stream().mapToDouble(d -> d).toArray();
					double[] pv_b = pv.get(b).stream().mapToDouble(d -> d).toArray();
					int res = c.compare(pv_a, pv_b);
					if (res < 0)
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					else if (res > 0)
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					else {
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					}
				}
			}

		} else if (this.semantics == PropagationSemantics.PROPAGATION2) {
			double attacked_arguments_influence_backup = this.attacked_arguments_influence;
			this.attacked_arguments_influence = 0.0;
			Map<Argument, List<Double>> pv_0 = calculatePropagationVector(kb);
			this.attacked_arguments_influence = attacked_arguments_influence_backup;

			for (Argument a : kb) {
				for (Argument b : kb) {
					double[] pv_a = pv.get(a).stream().mapToDouble(d -> d).toArray();
					double[] pv_a_0 = pv_0.get(a).stream().mapToDouble(d -> d).toArray();
					pv_a = shufflePropagationVectors(pv_a_0, pv_a);
					double[] pv_b = pv.get(b).stream().mapToDouble(d -> d).toArray();
					double[] pv_b_0 = pv_0.get(b).stream().mapToDouble(d -> d).toArray();
					pv_b = shufflePropagationVectors(pv_b_0, pv_b);
					int res = c.compare(pv_a, pv_b);
					if (res < 0)
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					else if (res > 0)
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					else {
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					}
				}
			}

		} else
			throw new UnsupportedOperationException("Unknown semantic: " + this.semantics);

		return ranking;
	}

	/**
	 * Calculate the "propagation vector" for the given argumentation 
	 * framework.
	 * 
	 * @param kb
	 * @return propagation vector (a map of arguments and their corresponding lists of valuations)
	 */
	public Map<Argument, List<Double>> calculatePropagationVector(DungTheory kb) {
		Map<Argument, List<Double>> valuations = new HashMap<Argument, List<Double>>();
		
		// Assign initial weights
		// Non-attacked arguments always get the initial value 1
		for (Argument a : kb) {
			List<Double> l = new ArrayList<Double>();
			l.add(getInitialValuation(a, kb));
			valuations.put(a, l);
		}

		// Compute the propagation vector
		int nOfSteps = 5;
		for (int i = 1; i < nOfSteps; i++) {
			for (Argument a : kb) {
				List<Double> l = valuations.get(a);
				double result = l.get(i - 1);

				// accumulate and store weights from attackers and defenders
				double sum = 0.0;
				List<Argument> s = getArgsWithPathsOfLength(a, kb, i);
				for (Argument b : s)
					sum += getInitialValuation(b, kb);

				// change polarities based on attack relation meaning
				// (attack or defense)
				if (i % 2 == 0)
					result += sum;
				else
					result -= sum;

				l.add(i, result);
				valuations.put(a, l);
			}
		}
		return valuations;
	}

	/**
	 * Computes the (multi-)set of arguments such that there exists a path with a given
	 * length to the given argument a.
	 * 
	 * @param a argument
	 * @param kb the complete framework
	 * @param i path length
	 * @return the (multi)set of arguments b that are at the end of a path (a,...,b) of length i
	 */
	private List<Argument> getArgsWithPathsOfLength(Argument a, DungTheory kb, int i) {
		if (i == 0)
			return new ArrayList<Argument>(kb);
		if (kb.containsCycle())
			throw new IllegalArgumentException("Cyclic graphs are not currently supported by this reasoner.");

		// paths of length 2
		HashSet<ArrayList<Argument>> paths = new HashSet<ArrayList<Argument>>();
		for (Argument attacker : kb.getAttackers(a)) {
			ArrayList<Argument> path = new ArrayList<Argument>();
			path.add(a);
			path.add(attacker);
			paths.add(path);
		}
		int j = 2;
		while (j < i + 1 && !paths.isEmpty()) {
			paths = getPathsOfHigherSize(paths, kb); // recursively add linear discussions of length>2
			j++;
		}

		List<Argument> result = new ArrayList<Argument>();
		for (List<Argument> px : paths)
			result.add(px.get(px.size() - 1));
		if (!this.use_multiset) {
			HashSet<Argument> set = new HashSet<Argument>(result);
			result = new ArrayList<Argument>(set);
		}

		return result;
	}

	/**
	 * @param a  argument
	 * @param kb DungTheory
	 * @return the initial valuation of the given argument
	 */
	private double getInitialValuation(Argument a, DungTheory kb) {
		if (kb.isAttacked(a, new Extension(kb)))
			return this.attacked_arguments_influence;
		return 1.0;
	}

	/**
	 * Given a set of argument paths of length i-1, this method returns a set of
	 * argument paths of length i for the given DungTheory.
	 * 
	 * @param old_paths set of paths of length i-1
	 * @param base      the DungTheory
	 * @return a set of paths of length i
	 */
	public HashSet<ArrayList<Argument>> getPathsOfHigherSize(HashSet<ArrayList<Argument>> old_paths, DungTheory base) {
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
	 * Performs the "shuffle operation": The two input vectors are combined in an
	 * alternating fashion.
	 * 
	 * @param a double array
	 * @param b double array
	 * @return "shuffled" array containing the elements of a,b alternately
	 */
	double[] shufflePropagationVectors(double[] a, double[] b) {
		if (a.length != b.length)
			throw new IllegalArgumentException("The input arrays must be the same size");
		List<Double> shuffled_list = new ArrayList<Double>();
		for (int i = 0; i < a.length; i++) {
			shuffled_list.add(a[i]);
			shuffled_list.add(b[i]);
		}
		return shuffled_list.stream().mapToDouble(d -> d).toArray();
	}

	/**
	 * Compares two vectors of double according to the lexicographic ordering.
	 */
	public class LexicographicTupleComparator implements Comparator<double[]> {
		/**
		 * Precision for comparing values.
		 */
		public static final double PRECISION = 0.001;

		@Override
		public int compare(double[] o1, double[] o2) {
			for (int i = 0; i < o1.length; i++) {
				if (Math.abs(o1[i] - o2[i]) < PRECISION)
					continue;
				else if (o1[i] < o2[i] + PRECISION)
					return -1;
				else if (o1[i] > o2[i] + PRECISION)
					return 1;
			}
			return 0;
		}
	}

}