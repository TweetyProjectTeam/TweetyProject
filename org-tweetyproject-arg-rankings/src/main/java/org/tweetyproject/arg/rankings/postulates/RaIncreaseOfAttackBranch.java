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
import org.tweetyproject.arg.rankings.semantics.ArgumentRanking;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * The "increase of attack branch" postulate for ranking semantics as formalized
 * in [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of
 * Ranking-Based Semantics for Abstract Argumentation. 2016]: Increasing the
 * length of an attack branch of an argument improves its ranking.
 * 
 * @see org.tweetyproject.arg.rankings.postulates.RaAdditionOfAttackBranch
 * @author Anna Gessler
 *
 */
public class RaIncreaseOfAttackBranch extends RankingPostulate {

	@Override
	public String getName() {
		return "Increase of Attack Branch";
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
				&& !kb.contains(new Argument(argOld.getName() + "clone"))
				&& !kb.contains(new Argument(argOld.getName() + "clone2")));
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<ArgumentRanking> ev) {
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
		
		// add new attack branch
		Argument t1 = new Argument("t1");
		Argument t2 = new Argument("t2");
		Argument t3 = new Argument("t3");
		dt.add(t1);
		dt.add(t2);
		dt.add(t3);
		dt.add(new Attack(t1, argClone));
		// add increased attack branch
		dt.add(new Attack(t3, argClone2));
		dt.add(new Attack(t2, t3));
		dt.add(new Attack(t1, t2));

		ArgumentRanking ranking = ev.getModel(dt);
		return ranking.isStrictlyMoreAcceptableThan(argClone2, argClone);
	}

}
