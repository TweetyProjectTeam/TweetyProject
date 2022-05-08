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
package org.tweetyproject.arg.rankings.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.LatticePartialOrder;
import org.tweetyproject.arg.rankings.util.LexicographicDoubleTupleComparator;

/**
 * This class implements the "Burden-based" argument semantics approach as
 * proposed by [Amgoud, Ben-Naim. Ranking-based semantics for argumentation
 * frameworks. 2013]. It assigns a "Burden number" to every argument.
 * 
 * @author Anna Gessler
 */
public class BurdenBasedRankingReasoner extends AbstractRankingReasoner<LatticePartialOrder<Argument, DungTheory>> {

	@Override
	public Collection<LatticePartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<LatticePartialOrder<Argument, DungTheory>> ranks = new HashSet<LatticePartialOrder<Argument, DungTheory>>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public LatticePartialOrder<Argument, DungTheory> getModel(DungTheory base) {
		// Number of steps
		int iMax = 6;
		// Map for storing burden numbers of previous steps
		Map<Argument, double[]> burdenNumbers = new HashMap<Argument, double[]>();

		// Initialize burden numbers array
		for (Argument a : (DungTheory) base) {
			double[] initialNumbers = new double[iMax + 1];
			initialNumbers[0] = 1.0; // burden number for step 0 is 1.0 for all arguments
			burdenNumbers.put(a, initialNumbers);
		}

		// Compute burden numbers for all steps i
		for (int i = 1; i <= iMax; i++) {
			for (Argument a : (DungTheory) base) {
				Set<Argument> attackers = ( (DungTheory) base).getAttackers(a);
				double newBurden = 1.0;
				for (Argument b : attackers) {
					double[] attackerBurdenNumbers = burdenNumbers.get(b);
					newBurden += 1.0 / (attackerBurdenNumbers[i - 1]);
				}
				double[] burdenNumbersTemp = burdenNumbers.get(a);
				burdenNumbersTemp[i] = newBurden;
				burdenNumbers.put(a, burdenNumbersTemp);
			}
		}

		// Use the lexicographical order of the burden numbers as ranking
		LatticePartialOrder<Argument, DungTheory> ranking 
			= new LatticePartialOrder<Argument, DungTheory>( ((DungTheory) base).getNodes());
		LexicographicDoubleTupleComparator c = new LexicographicDoubleTupleComparator();
		for (Argument a :  (DungTheory) base) {
			for (Argument b :  (DungTheory) base) {
				double[] burdensA = burdenNumbers.get(a);
				double[] burdensB = burdenNumbers.get(b);
				int res = c.compare(burdensA, burdensB);
				if (res < 0)
					ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
				else if (res > 0)
					ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
				else {
					ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
				}
			}
		}
		return ranking;
	}
	
	/**natively installed*/
	@Override
	public boolean isInstalled() {
		return true;
	}

}
