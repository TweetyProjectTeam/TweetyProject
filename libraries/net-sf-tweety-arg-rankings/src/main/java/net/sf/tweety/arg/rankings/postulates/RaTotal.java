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
package net.sf.tweety.arg.rankings.postulates;

import java.util.Collection;

import net.sf.tweety.arg.dung.semantics.ArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.rankings.reasoner.AbstractRankingReasoner;

/**
 *  The "total" postulate for ranking semantics as proposed in 
 *  [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of Ranking-Based 
 *  Semantics for Abstract Argumentation. 2016]: 
 *  All pairs of arguments can be compared.
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
		return true;
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<ArgumentRanking> ev) {
		if (!this.isApplicable(kb))
			return true;
		DungTheory dt = new DungTheory((DungTheory) kb);
		ArgumentRanking ranking = ev.getModel((DungTheory)dt);
		for (Argument a : dt) {
			for (Argument b : dt) {
				if (ranking.isIncomparable(a, b)) {
					if (IGNORE_INCOMPARABLE_ARGUMENTS)
						return true;
					else
						return false;
				}
				ranking.compare(a, b);
			}
		}
		return true;
	}

}
