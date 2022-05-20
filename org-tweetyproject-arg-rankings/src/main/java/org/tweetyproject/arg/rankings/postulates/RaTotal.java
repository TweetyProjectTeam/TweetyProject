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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.postulates;

import java.util.Collection;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.comparator.TweetyComparator;

/**
 * The "total" postulate for ranking semantics as proposed in [Bonzon,
 * Delobelle, Konieczny, Maudet. A Comparative Study of Ranking-Based Semantics
 * for Abstract Argumentation. 2016]: All pairs of arguments can be compared.
 * 
 * @author Anna Gessler
 *
 */
public class RaTotal extends RankingPostulate {

	@Override
	public String getName() {
		return "Total";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb instanceof DungTheory);
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<TweetyComparator<Argument, DungTheory>> ev) {
		if (!this.isApplicable(kb))
			return true;
		if (ev.getModel((DungTheory) kb) == null)
			return true;
		DungTheory dt = new DungTheory((DungTheory) kb);
		TweetyComparator<Argument, DungTheory> ranking = ev.getModel((DungTheory) dt);
		for (Argument a : dt) {
			for (Argument b : dt) {
				if (ranking.isIncomparable(a, b)) 
					return false;
			}
		}
		return true;
	}

}
