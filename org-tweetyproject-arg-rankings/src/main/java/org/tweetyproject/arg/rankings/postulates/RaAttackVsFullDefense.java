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
import java.util.Iterator;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.arg.rankings.semantics.ArgumentRanking;

/**
 * The "attack vs full defense" postulate for ranking semantics as proposed in
 * [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of Ranking-Based
 * Semantics for Abstract Argumentation. 2016]: An argument without any attack
 * branch is ranked higher than an argument only attacked by one non-attacked
 * argument.
 * 
 * @author Anna Gessler
 *
 */
public class RaAttackVsFullDefense extends RankingPostulate {

	@Override
	public String getName() {
		return "Attack vs Full Defense";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb instanceof DungTheory && kb.size() >= 2);
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<ArgumentRanking> ev) {
		if (!this.isApplicable(kb))
			return true;
		if (ev.getModel((DungTheory) kb) == null)
			return true;
		
		DungTheory dt = new DungTheory((DungTheory) kb);
		if (dt.containsCycle())
			return true;

		Iterator<Argument> it = dt.iterator();
		Argument a = it.next();
		Argument b = it.next();
		
		if (dt.getAttackers(b).size() != 1)
			return true;
		if (!dt.getAttackers(dt.getAttackers(b).iterator().next()).isEmpty())
			return true;
		if (dt.hasAttackBranch(a))
			return true;

		ArgumentRanking ranking = ev.getModel((DungTheory) dt);
		return ranking.isStrictlyMoreAcceptableThan(a, b);
	}

}
