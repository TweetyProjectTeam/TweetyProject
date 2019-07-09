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
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.semantics.NumericalArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 * This class implements the "Discussion-based" argument semantics approach as
 * proposed by [Amgoud, Ben-Naim. Ranking-based semantics for argumentation
 * frameworks. 2013]. It compares arguments by counting the number of paths
 * ending to to them. If some arguments are equivalent wrt. to their number of
 * direct attackers, the size of paths is increased recursively until a
 * difference is found.
 * 
 * @author Anna Gessler
 */
public class DiscussionBasedRankingReasoner extends AbstractRankingReasoner<NumericalArgumentRanking> {

	@Override
	public Collection<NumericalArgumentRanking> getModels(DungTheory bbase) {
		Collection<NumericalArgumentRanking> ranks = new HashSet<NumericalArgumentRanking>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public NumericalArgumentRanking getModel(DungTheory base) {
		// Maximum length of linear discussions (paths)
		int i_max = 10;

		ArrayList<NumericalArgumentRanking> rankings = new ArrayList<NumericalArgumentRanking>();
		for (int i = 1; i <= i_max; i++) {
			NumericalArgumentRanking ranking = new NumericalArgumentRanking();
			for (Argument a : base) {
				double discussion_count = getNumberOfPathsOfLength(base, a, i);
				if ((i & 1) != 0)
					discussion_count = -discussion_count; // odd value => negative discussion count
				ranking.put(a, discussion_count);
			}
			rankings.add(ranking);
		}
		
		//Return the ranking with the least amount of equal arguments
		int max =  1;
		NumericalArgumentRanking bestRanking = rankings.get(0);
		for (int i = 1; i < rankings.size(); i++) {
			int ranking_unique_values = new HashSet<Object>(rankings.get(i).values()).size();
			if (ranking_unique_values > max) {
				max = ranking_unique_values; 
				bestRanking = rankings.get(i);
			}
		}
		return bestRanking;
	}

	/**
	 * Returns the number of linear discussions of the given length in the given DungTheory for
	 * the given argument.
	 * @param base
	 * @param a an argument
	 * @param i length of linear discussions
	 * @return
	 */
	public int getNumberOfPathsOfLength(DungTheory base, Argument a, int i) {
		if (i == 0 || i == 1)
			return i;
		
		HashSet<ArrayList<Argument>> paths = new HashSet<ArrayList<Argument>>();
		
		//add linear discussions of length 2
		for (Argument attacker : base.getAttackers(a)) {
			ArrayList<Argument> path = new ArrayList<Argument>();
			path.add(a);
			path.add(attacker);
			paths.add(path);
		}

		int j = 2;
		while (j < i && !paths.isEmpty()) {
			paths = getPathsOfHigherSize(paths, base); //recursively add linear discussions of length>2
			j++;
		}
		return paths.size();
	}

	/**
	 * Given a set of argument paths of length i-1, this method returns a set of argument paths 
	 * of length i for the given DungTheory. 
	 * @param old_paths set of paths of length i-1
	 * @param base the DungTheory
	 * @return a set of paths of length i
	 */
	public HashSet<ArrayList<Argument>> getPathsOfHigherSize(HashSet<ArrayList<Argument>> old_paths, DungTheory base) {
		HashSet<ArrayList<Argument>> new_paths = new HashSet<ArrayList<Argument>>();
		for (ArrayList<Argument> path : old_paths) {
			Argument tail = path.get(path.size() - 1);
			for (Argument attacker : base.getAttackers(tail)) {
				if (!path.contains(attacker)) { //ignore cycles
					ArrayList<Argument> new_path = new ArrayList<Argument>();
					new_path.addAll(path);
					new_path.add(attacker);
					new_paths.add(new_path);
				}
			}
		}
		return new_paths;
	}

}
