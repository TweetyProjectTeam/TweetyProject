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
package org.tweetyproject.arg.rankings.reasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.LatticePartialOrder;
import org.tweetyproject.arg.rankings.util.RankingTools;
import org.tweetyproject.arg.rankings.util.LexicographicDoubleTupleComparator;

/**
 * This class implements the argument ranking approach of [Delobelle. Ranking-
 * based Semantics for Abstract Argumentation. Thesis, 2017].
 *
 * In this approach, initial values are assigned to arguments and then
 * propagated into the graph. The paper describes three different ways of
 * computing a ranking out of the propagation vector.
 *
 * <br>
 * <br>
 * Note: This implementation only works for acyclic argument graphs. For cyclic
 * graphs <b>null</b> is returned.
 *
 * @author Anna Gessler
 */
public class PropagationRankingReasoner extends AbstractRankingReasoner<LatticePartialOrder<Argument, DungTheory>> {
	/**
	 * Determines the influence of attacked arguments. The smaller this value is,
	 * the more important is the influence of the non-attacked arguments.
	 */
	private double attacked_arguments_influence;

	/**
	 * Determines whether the multiset (M) of attackers/defenders of some length is
	 * used instead of the set (S). Using the multiset means that if there are
	 * multiple paths from an argument a to an argument b, multiple values will be
	 * propagated from a to b instead of just one.
	 */
	private boolean useMultiset;

	/**
	 * The three propagation semantics:
	 * <ol>
	 * <li>PROPAGATION1 ("Propa_epsilon")</li>
	 * <li>PROPAGATION2 ("Propa_{1+epsilon}")</li>
	 * <li>PROPAGATION3 ("Propa_{1-&gt;epsilon}")</li>
	 * </ol>
	 */
	public enum PropagationSemantics {
		/**
		 * The first propagation semantics approach.
		 */
		PROPAGATION1,

		/**
		 * The second propagation semantics approach.
		 */
		PROPAGATION2,

		/**
		 * The third propagation semantics approach.
		 */
		PROPAGATION3
	}

	/**
	 * The variant of Propagation semantics that is used.
	 */
	private PropagationSemantics semantics;

	/**
	 * Creates a new PropagationRankingReasoner with the given parameters.
	 *
	 * @param useMultiset determines whether the multiset (M) of
	 *                    attackers/defenders of length is used instead of the set
	 *                    (S)
	 */
	public PropagationRankingReasoner(boolean useMultiset) {
		this.attacked_arguments_influence = 0.75;
		this.useMultiset = useMultiset;
		this.semantics = PropagationSemantics.PROPAGATION1;
	}

	/**
	 * Creates a new PropagationRankingReasoner with the given parameters.
	 *
	 * @param attackedArgumentsInfluence the smaller this value is, the more
	 *                                   important is the influence of the
	 *                                   non-attacked arguments.
	 * @param useMultiset                determines whether the multiset (M) of
	 *                                   attackers/defenders of length is used
	 *                                   instead of the set (S)
	 * @param semantics                  one of the three propagation semantics
	 */
	public PropagationRankingReasoner(double attackedArgumentsInfluence, boolean useMultiset,
			PropagationSemantics semantics) {
		if (attackedArgumentsInfluence < 0.0 || attackedArgumentsInfluence > 1.0)
			throw new IllegalArgumentException("Parameter is outside of acceptable parameter range [0.0,1.0]");
		this.attacked_arguments_influence = attackedArgumentsInfluence;
		this.useMultiset = useMultiset;
		this.semantics = semantics;
	}

	@Override
	public Collection<LatticePartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<LatticePartialOrder<Argument, DungTheory>> ranks = new HashSet<LatticePartialOrder<Argument, DungTheory>>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public LatticePartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
		if (((DungTheory) kb).containsCycle())
			return null;

		LatticePartialOrder<Argument, DungTheory> ranking = new LatticePartialOrder<Argument, DungTheory>(
				((DungTheory) kb).getNodes());
		LexicographicDoubleTupleComparator c = new LexicographicDoubleTupleComparator();
		Map<Argument, List<Double>> pv = calculatePropagationVector(((DungTheory) kb),
				this.attacked_arguments_influence);

		if (this.semantics == PropagationSemantics.PROPAGATION1) {
			for (Argument a : ((DungTheory) kb)) {
				for (Argument b : ((DungTheory) kb)) {
					double[] pvA = pv.get(a).stream().mapToDouble(d -> d).toArray();
					double[] pvB = pv.get(b).stream().mapToDouble(d -> d).toArray();
					int res = c.compare(pvA, pvB);
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
			Map<Argument, List<Double>> pv0 = calculatePropagationVector(((DungTheory) kb), 0.0);
			for (Argument a : ((DungTheory) kb)) {
				for (Argument b : ((DungTheory) kb)) {
					double[] pvA = pv.get(a).stream().mapToDouble(d -> d).toArray();
					double[] pvA0 = pv0.get(a).stream().mapToDouble(d -> d).toArray();
					pvA = shufflePropagationVectors(pvA0, pvA);
					double[] pvB = pv.get(b).stream().mapToDouble(d -> d).toArray();
					double[] pvB0 = pv0.get(b).stream().mapToDouble(d -> d).toArray();
					pvB = shufflePropagationVectors(pvB0, pvB);
					int res = c.compare(pvA, pvB);
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

		} else if (this.semantics == PropagationSemantics.PROPAGATION3) {
			Map<Argument, List<Double>> pv0 = calculatePropagationVector(((DungTheory) kb), 0.0);
			for (Argument a : ((DungTheory) kb)) {
				for (Argument b : ((DungTheory) kb)) {
					double[] pvA = pv0.get(a).stream().mapToDouble(d -> d).toArray();
					double[] pvB = pv0.get(b).stream().mapToDouble(d -> d).toArray();
					int res = c.compare(pvA, pvB);
					if (res < 0)
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					else if (res > 0)
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					else {
						// if two arguments are ranked equally using the 0.0
						// propagation vector, compare them using the epsilon
						// propagation vector instead
						pvA = pv.get(a).stream().mapToDouble(d -> d).toArray();
						pvB = pv.get(b).stream().mapToDouble(d -> d).toArray();
						res = c.compare(pvA, pvB);
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
			}

		} else
			throw new IllegalArgumentException("Unknown semantic: " + this.semantics);

		return ranking;
	}

	/**
	 * Calculate the "propagation vector" for the given argumentation framework.
	 *
	 * @param kb
	 * @param epsilon the influence of attacked arguments
	 * @return propagation vector (a map of arguments and their corresponding lists
	 *         of valuations)
	 */
	private Map<Argument, List<Double>> calculatePropagationVector(DungTheory kb, double epsilon) {
		Map<Argument, List<Double>> valuations = new HashMap<Argument, List<Double>>();

		// Assign initial weights
		// Non-attacked arguments always get the initial value 1
		for (Argument a : kb) {
			List<Double> l = new ArrayList<Double>();
			l.add(getInitialValuation(a, kb, epsilon));
			valuations.put(a, l);
		}

		// Compute the propagation vector
		int maxNOfSteps = 20;
		for (int i = 1; i < maxNOfSteps; i++) {
			boolean foundPathOfSizeI = false;
			for (Argument a : kb) {
				List<Double> l = valuations.get(a);
				double result = l.get(i - 1);

				// accumulate and store weights from attackers and defenders
				double sum = 0.0;
				List<Argument> s = getArgsWithPathsOfLength(a, kb, i);
				if (!s.isEmpty())
					foundPathOfSizeI = true;
				for (Argument b : s)
					sum += getInitialValuation(b, kb, epsilon);

				// change polarities based on attack relation meaning
				// (attack or defense)
				if (i % 2 == 0)
					result += sum;
				else
					result -= sum;

				l.add(i, result);
				valuations.put(a, l);
			}
			// stop if there are no paths of length >=i in the graph
			if (!foundPathOfSizeI)
				break;
		}
		return valuations;
	}

	/**
	 * Computes the (multi-)set of arguments such that there exists a path with a
	 * given length to the given argument a.
	 *
	 * @param a  argument
	 * @param kb the complete framework
	 * @param i  path length
	 * @return the (multi)set of arguments b that are at the end of a path (a,...,b)
	 *         of length i
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
			paths = RankingTools.getPathsOfHigherSize(paths, kb); // recursively add linear discussions of length>2
			j++;
		}

		List<Argument> result = new ArrayList<Argument>();
		for (List<Argument> px : paths)
			result.add(px.get(px.size() - 1));
		if (!this.useMultiset) {
			HashSet<Argument> set = new HashSet<Argument>(result);
			result = new ArrayList<Argument>(set);
		}

		return result;
	}

	/**
	 * @param a       argument
	 * @param kb      DungTheory
	 * @param epsilon the influence of attacked arguments
	 * @return the initial valuation of the given argument
	 */
	private double getInitialValuation(Argument a, DungTheory kb, double epsilon) {
		if (kb.isAttacked(a, new Extension<DungTheory>(kb)))
			return epsilon;
		return 1.0;
	}

	/**
	 * Performs the "shuffle operation": The two input vectors are combined in an
	 * alternating fashion.
	 *
	 * @param a double array
	 * @param b double array
	 * @return "shuffled" array containing the elements of a,b alternately
	 */
	private double[] shufflePropagationVectors(double[] a, double[] b) {
		if (a.length != b.length)
			throw new IllegalArgumentException("The input arrays must be the same size");
		List<Double> shuffled_list = new ArrayList<Double>();
		for (int i = 0; i < a.length; i++) {
			shuffled_list.add(a[i]);
			shuffled_list.add(b[i]);
		}
		return shuffled_list.stream().mapToDouble(d -> d).toArray();
	}

	/** natively installed */
	@Override
	public boolean isInstalled() {
		return true;
	}

}