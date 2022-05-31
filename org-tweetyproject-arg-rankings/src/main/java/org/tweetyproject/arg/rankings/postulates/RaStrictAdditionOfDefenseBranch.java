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
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.comparator.GeneralComparator;

/**
 * The "strict addition of defense branch" postulate for ranking semantics as
 * formalized in [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of
 * Ranking-Based Semantics for Abstract Argumentation. 2016]: Adding a defense
 * branch to any argument improves its ranking.
 * 
 * 'Adding a defense branch to the argument A' means adding the arguments {X1,
 * ... , Xn} which are not in the original knowledge base, for whom is true that
 * A &lt;- X1 &lt;- X2 ... &lt;- Xn and where n is an even number.
 * 
 * @see org.tweetyproject.arg.rankings.postulates.RaAdditionOfDefenseBranch
 * @author Anna Gessler
 *
 */
public class RaStrictAdditionOfDefenseBranch extends RankingPostulate {

	@Override
	public String getName() {
		return "Strict Addition of Defense Branch";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb instanceof DungTheory && kb.size() >= 1 && !kb.contains(new Argument("t1")) && !kb.contains(new Argument("t2"))
				&& !kb.contains(new Argument("clone")));
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<GeneralComparator<Argument, DungTheory>> ev) {
		if (!this.isApplicable(kb))
			return true;
		if (ev.getModel((DungTheory) kb) == null)
			return true;
		
		DungTheory dt = new DungTheory((DungTheory) kb);
		Iterator<Argument> it = dt.iterator();
		Argument argOld = it.next();

		// clone argument and relations
		Argument argClone = new Argument("clone");
		dt.add(argClone);
		for (Argument attacker : dt.getAttackers(argOld)) {
			if (attacker.equals(argOld))
				dt.add(new Attack(argClone, argClone));
			else
				dt.add(new Attack(attacker, argClone));
		}
		for (Argument attacked : dt.getAttacked(argOld)) {
			if (!attacked.equals(argOld)) 
				dt.add(new Attack(argClone, attacked));
		}
		
		// add new defense branch
		Argument t1 = new Argument("t1");
		Argument t2 = new Argument("t2");
		dt.add(t1);
		dt.add(t2);
		dt.add(new Attack(t1, argClone));
		dt.add(new Attack(t2, t1));
		
		GeneralComparator<Argument, DungTheory> ranking = ev.getModel(dt);
		return ranking.isStrictlyMoreAcceptableThan(argClone, argOld);
	}

}
