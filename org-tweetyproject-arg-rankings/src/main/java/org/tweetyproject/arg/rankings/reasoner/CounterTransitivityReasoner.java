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
package org.tweetyproject.arg.rankings.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.LatticePartialOrder;

/**
 * This class implements the argument ranking approach of [Pu, Luo,
 * Ranking Arguments based on Counter-Transitivity 2017].
 *
 *
 * @author Sebastian Franke
 */
public class CounterTransitivityReasoner extends AbstractRankingReasoner<LatticePartialOrder<Argument, DungTheory>> {

	/**
	 * Represents the partial order of arguments in a lattice structure.
	 * <p>
	 * This is used to rank arguments based on the provided {@link DungTheory}.
	 * </p>
	 */
	LatticePartialOrder<Argument, DungTheory> rank;

	/** Solver */
	solver sol;


    /**
     * The solver used to determine the ranking of arguments.
     * <p>
     * This enum defines different strategies for ranking arguments. The strategies include:
     * <ul>
     * <li>{@code quality}: A strategy based on the quality of arguments.</li>
     * <li>{@code cardinality}: A strategy based on the number of arguments.</li>
     * <li>{@code qualityFirst}: A strategy that prioritizes quality over cardinality.</li>
     * <li>{@code cardinalityFirst}: A strategy that prioritizes cardinality over quality.</li>
     * <li>{@code gfpCardinality}: A strategy using the greatest fixed-point cardinality.</li>
     * <li>{@code simpleDominance}: A strategy based on simple dominance relationships.</li>
     * </ul>
     * </p>
     */
    public enum solver {
        /**
         * Strategy based on the quality of arguments.
         */
        quality,

        /**
         * Strategy based on the cardinality (number) of arguments.
         */
        cardinality,

        /**
         * Strategy that prioritizes quality over cardinality.
         */
        qualityFirst,

        /**
         * Strategy that prioritizes cardinality over quality.
         */
        cardinalityFirst,

        /**
         * Strategy using the greatest fixed-point (GFP) cardinality.
         */
        gfpCardinality,

        /**
         * Strategy based on simple dominance relationships between arguments.
         */
        simpleDominance
    }

	public CounterTransitivityReasoner(solver sol, LatticePartialOrder<Argument, DungTheory> rank) {
		this.sol = sol;
		this.rank = rank;
	}

	@Override
	public Collection<LatticePartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<LatticePartialOrder<Argument, DungTheory>> ranks = new HashSet<LatticePartialOrder<Argument, DungTheory>>();
		ranks.add(this.getModel(bbase));

		return ranks;
	}

	@Override
	public LatticePartialOrder<Argument, DungTheory> getModel(DungTheory bbase) {
		// switch the type of solver chosen in the constructor
		switch (this.sol) {
			case cardinality:
				return cardinality((DungTheory) bbase);
			case gfpCardinality:
				return gfpCardinality((DungTheory) bbase);
			case quality:
				return quality((DungTheory) bbase, this.rank);
			case qualityFirst:
				return qualityFirst((DungTheory) bbase, this.rank);
			case cardinalityFirst:
				return cardinalityFirst((DungTheory) bbase, rank);
			case simpleDominance:
				return simpleDominance((DungTheory) bbase, rank);
			default: {
				System.out.println("The chosen function does not exist.");
			}
		}
		return null;
	}

	/**
	 * orders arguments after the amount of attackers they have.
	 * The less attackers, the higher the rank
	 * @param af the af
	 * @return the cardinality
	 */
	public LatticePartialOrder<Argument, DungTheory> cardinality(DungTheory af) {
		LatticePartialOrder<Argument, DungTheory> card = new LatticePartialOrder<Argument, DungTheory>(af);
		// compare every argument to every argument
		for (Argument arg1 : af) {
			for (Argument arg2 : af) {
				if (arg1 != arg2) {
					// compare the sizes of the attacker-sets
					if (af.getAttackers(arg1).size() > af.getAttackers(arg2).size()) {
						card.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);

					} else if (af.getAttackers(arg1).size() < af.getAttackers(arg2).size()) {
						card.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
					} else if (af.getAttackers(arg1).size() == af.getAttackers(arg2).size()) {
						card.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						card.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
					}
				}
			}

		}

		return card;
	}

	/**
	 * ranks arguments according to a given ranking. It decides the highest ranked
	 * attacker of each argument wrt the underlying ranking
	 * and then ranks them after their best attacker
	 * @param af the af
	 * @param ra the ra
	 * @return the quality
	 */
	public LatticePartialOrder<Argument, DungTheory> quality(DungTheory af,
			LatticePartialOrder<Argument, DungTheory> ra) {

		LatticePartialOrder<Argument, DungTheory> mostValueableAttacker = new LatticePartialOrder<Argument, DungTheory>(
				af);
		HashMap<Argument, Argument> maxAttacker = new HashMap<Argument, Argument>();
		// find the strongest attacker of each argument
		for (Argument arg : af) {
			// create a placeholder argument for the strongest attacker
			Argument max = new Argument("placeholder");
			if (af.getAttackers(arg).size() == 0)
				maxAttacker.put(arg, null);
			else {

				max = af.getAttackers(arg).iterator().next();
				for (Argument att : af.getAttackers(arg)) {
					if (ra.isStrictlyLessOrEquallyAcceptableThan(max, att)) {
						max = att;
					}
					maxAttacker.put(arg, max);
				}
			}

		}
		// compare every argument to every argument
		for (Argument arg1 : maxAttacker.keySet()) {
			for (Argument arg2 : maxAttacker.keySet()) {
				if (arg1 != arg2) {
					// if an argument has no attackers, it is always superior
					if (maxAttacker.get(arg1) == null) {
						mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
					}
					if (maxAttacker.get(arg2) == null) {
						mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
					}
					if (maxAttacker.get(arg1) != null && maxAttacker.get(arg2) != null) {
						if (ra.compare(maxAttacker.get(arg1), maxAttacker.get(arg2)) == 0) {
							mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
							mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
						} else if (ra.compare(maxAttacker.get(arg1), maxAttacker.get(arg2)) > 0) {
							mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);

						} else if (ra.compare(maxAttacker.get(arg1), maxAttacker.get(arg2)) < 0) {
							mostValueableAttacker.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);

						}
					}
				}
			}
		}

		return mostValueableAttacker;
	}

	/**
	 * ranks arguments according to the quality function. If 2 arguments have the
	 * same avlue according to quality, the decsision is left to cardinality
	 * @param af the af
	 * @param ra the ra
	 * @return quality first
	 */
	public LatticePartialOrder<Argument, DungTheory> qualityFirst(DungTheory af,
			LatticePartialOrder<Argument, DungTheory> ra) {
		LatticePartialOrder<Argument, DungTheory> qual = quality(af, ra);
		LatticePartialOrder<Argument, DungTheory> card = cardinality(af);
		LatticePartialOrder<Argument, DungTheory> result = new LatticePartialOrder<Argument, DungTheory>(af);
		// compare every argument to every argument
		for (Argument arg1 : af) {
			for (Argument arg2 : af) {
				if (arg1 != arg2) {
					// only check cardinality, if quality is the same
					if (qual.compare(arg1, arg2) == 0) {
						if (card.compare(arg1, arg2) < 0) {
							result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
						} else if (card.compare(arg1, arg2) > 0) {
							result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						} else {
							result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
							result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						}
					} else if (qual.compare(arg1, arg2) < 0) {
						result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
					} else if (qual.compare(arg1, arg2) > 0) {
						result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
					}
				}
			}
		}

		return result;
	}

	/**
	 * ranks arguments according to the cardinality function. If 2 arguments have
	 * the same value according to cardinality, the decision is left to quality
	 *
	 * @param af the af
	 * @param ra the ra
	 * @return cardinality first
	 */
	public LatticePartialOrder<Argument, DungTheory> cardinalityFirst(DungTheory af,
			LatticePartialOrder<Argument, DungTheory> ra) {
		LatticePartialOrder<Argument, DungTheory> qual = quality(af, ra);
		LatticePartialOrder<Argument, DungTheory> card = cardinality(af);
		LatticePartialOrder<Argument, DungTheory> result = new LatticePartialOrder<Argument, DungTheory>(af);
		// compare every argument to every argument
		for (Argument arg1 : af) {
			for (Argument arg2 : af) {
				if (arg1 != arg2) {
					// only check quality, if cardinality is the same
					if (card.compare(arg1, arg2) == 0) {
						if (qual.compare(arg1, arg2) < 0) {
							result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
						} else if (qual.compare(arg1, arg2) > 0) {
							result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						} else {
							result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
							result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
						}
					} else if (card.compare(arg1, arg2) < 0) {
						result.setStrictlyLessOrEquallyAcceptableThan(arg1, arg2);
					} else if (card.compare(arg1, arg2) > 0) {
						result.setStrictlyLessOrEquallyAcceptableThan(arg2, arg1);
					}
				}
			}
		}

		return result;
	}

	/**
	 * calculates the greatest fix point.
	 * First the quality is computed based on a ranking that only ranks unattacked
	 * arguments higher than the rest.
	 * Based on this ranking a cardinality first ranking is constructed. Based on
	 * this new ranking a quality ranking is constructed.
	 * This process is repeated until two consecutive iterations of cardinality
	 * first reach the same conclusion
	 *  @param af the af
	 * @return the  gfpcardinality
	 */
	public LatticePartialOrder<Argument, DungTheory> gfpCardinality(DungTheory af) {

		LatticePartialOrder<Argument, DungTheory> ra = new LatticePartialOrder<Argument, DungTheory>(af);
		// set the rank in which only not attacked arguments are ranked higehr
		for (Argument a : af) {
			for (Argument b : af) {
				if (af.getAttackers(a).size() == 0 && af.getAttackers(b).size() > 0) {
					ra.setStrictlyLessOrEquallyAcceptableThan(b, a);
				}
			}
		}

		LatticePartialOrder<Argument, DungTheory> quality = this.quality(af, ra);
		LatticePartialOrder<Argument, DungTheory> card = this.cardinalityFirst(af, ra);

		LatticePartialOrder<Argument, DungTheory> card2 = new LatticePartialOrder<Argument, DungTheory>(af);
		// check if the last iteraions yield the same result
		while (!card.isSame(card2)) {

			quality = this.quality(af, card);
			card = card2;
			card2 = this.cardinalityFirst(af, quality);

		}
		return card2;
	}

	/**
	 * only ranks two arguments if one is better according to quality and
	 * cardinality
	 * @param af the af
	 * @param ra the ra
	 * @return the simple dominance
	 */
	public LatticePartialOrder<Argument, DungTheory> simpleDominance(DungTheory af,
			LatticePartialOrder<Argument, DungTheory> ra) {
		LatticePartialOrder<Argument, DungTheory> card = this.cardinality(af);
		LatticePartialOrder<Argument, DungTheory> qual = this.quality(af, ra);
		LatticePartialOrder<Argument, DungTheory> result = new LatticePartialOrder<Argument, DungTheory>(af);
		// compare every argument to every argument
		for (Argument a : af) {
			for (Argument b : af) {
				// check if the argument is superior in both categories
				if (card.compare(a, b) == 1 && qual.compare(a, b) == 1)
					result.setStrictlyLessOrEquallyAcceptableThan(b, a);
			}
		}

		return result;
	}

	/** natively installed */
	@Override
	public boolean isInstalled() {
		return true;
	}

}
