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

import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.comparator.TweetyComparator;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * The "increase of defense branch" postulate for ranking semantics as
 * formalized in [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of
 * Ranking-Based Semantics for Abstract Argumentation. 2016]: Increasing the
 * length of a defense branch of an argument degrades its ranking.
 * 
 * @see org.tweetyproject.arg.rankings.postulates.RaStrictAdditionOfDefenseBranch
 * @author Anna Gessler
 *
 */
public class RaIncreaseOfDefenseBranch extends RankingPostulate {

	@Override
	public String getName() {
		return "Increase of Defense Branch";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		if (!(kb instanceof DungTheory))
			return false;
		else if (kb.size() < 1)
			return false;
		Argument argOld = ((DungTheory) kb).iterator().next();
		return (!((DungTheory) kb).getAttackers(argOld).isEmpty() && !kb.contains(new Argument("t1"))
				&& !kb.contains(new Argument("t2")) && !kb.contains(new Argument("t3"))
				&& !kb.contains(new Argument("t4")) && !kb.contains(new Argument(argOld.getName() + "clone"))
				&& !kb.contains(new Argument(argOld.getName() + "clone2")));
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<TweetyComparator<Argument, DungTheory>> ev) {
		if (!this.isApplicable(kb))
			return true;
		if (ev.getModel((DungTheory) kb) == null)
			return true;
		
		DungTheory dt = new DungTheory((DungTheory) kb);
		Iterator<Argument> it = dt.iterator();
		Argument argOld = it.next();

		// clone argument and relations
		Argument argClone = new Argument(argOld.getName() + "clone");
		Argument argClone2 = new Argument(argOld.getName() + "clone2");
		dt.add(argClone);
		dt.add(argClone2);
		for (Argument attacker : dt.getAttackers(argOld)) {
			if (attacker.equals(argOld)) {
				dt.add(new Attack(argClone, argClone));
				dt.add(new Attack(argClone2, argClone2));
			} else {
				dt.add(new Attack(attacker, argClone));
				dt.add(new Attack(attacker, argClone2));
			}
		}
		for (Argument attacked : dt.getAttacked(argOld)) {
			if (!attacked.equals(argOld)) {
				dt.add(new Attack(argClone, attacked));
				dt.add(new Attack(argClone2, attacked)); }
		}

		// add new defense branch
		Argument t1 = new Argument("t1");
		Argument t2 = new Argument("t2");
		Argument t3 = new Argument("t3");
		Argument t4 = new Argument("t4");
		dt.add(t1);
		dt.add(t2);
		dt.add(t3);
		dt.add(t4);
		dt.add(new Attack(t3, argClone));
		dt.add(new Attack(t4, t3));
		// add increased defense branch
		dt.add(new Attack(t1, argClone2));
		dt.add(new Attack(t2, t1));
		dt.add(new Attack(t3, t2));
		dt.add(new Attack(t4, t3));

		TweetyComparator<Argument, DungTheory> ranking = ev.getModel((DungTheory) dt);
		return ranking.isStrictlyLessAcceptableThan(argClone2, argClone);
	}
}
