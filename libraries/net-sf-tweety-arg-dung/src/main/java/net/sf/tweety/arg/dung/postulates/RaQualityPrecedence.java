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
package net.sf.tweety.arg.dung.postulates;

import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.arg.dung.reasoner.AbstractRankingReasoner;
import net.sf.tweety.arg.dung.semantics.ArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 *  The "quality precedence" postulate for ranking semantics as proposed in
 *  [Amgoud, Ben-Naim. Ranking-based semantics for argumentation frameworks. 2013]:
 *  The greater the acceptability of one direct attacker
 *  for an argument, the weaker the level of acceptability of
 *  this argument.
 * 
 * @author Anna Gessler
 *
 */
public class RaQualityPrecedence extends RankingPostulate {

	@Override
	public String getName() {
		return "Quality Precedence";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb.size()>=2);
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<ArgumentRanking> ev) {
		if (!this.isApplicable(kb))
			return true;
		DungTheory dt = (DungTheory) kb;
		Iterator<Argument> it = dt.iterator();
		Argument a = it.next();
		Argument b = it.next();
		
		if (dt.getAttackers(b).isEmpty())
			return true;
		Argument c = dt.getAttackers(b).iterator().next();

		ArgumentRanking ranking = ev.getModel((DungTheory)dt);
		for (Argument f: dt.getAttackers(a)) 
			if (!ranking.isStrictlyMoreAcceptableThan(c,f))
				return true;
		
		return ranking.isStrictlyMoreAcceptableThan(a, b);
	}


}
