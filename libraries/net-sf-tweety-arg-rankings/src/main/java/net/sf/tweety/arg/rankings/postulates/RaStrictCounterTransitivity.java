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
import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.arg.rankings.reasoner.AbstractRankingReasoner;
import net.sf.tweety.arg.dung.semantics.ArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 *  The "strict counter-transitivity" postulate for ranking semantics as proposed by
 *  [Amgoud, Ben-Naim. Ranking-based semantics for argumentation frameworks. 2013]: 
 *  If the "counter-transitivity" postulate is satisfied and
 *  either the direct attackers of an b are strictly 
 *  more numerous or acceptable than those of a, then a
 *  is strictly more acceptable than b.
 *  
 * @author Anna Gessler
 */
public class RaStrictCounterTransitivity extends RankingPostulate {

	@Override
	public String getName() {
		return "Strict Counter-Transitivity";
	}
	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb.size()>=2);
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<ArgumentRanking> ev) {
		if (!this.isApplicable(kb))
			return true;
		DungTheory dt = new DungTheory((DungTheory) kb);
		Iterator<Argument> it = dt.iterator();
		Argument a = it.next();
		Argument b = it.next();
		Set<Argument> attackers_a = dt.getAttackers(a);
		Set<Argument> attackers_b = dt.getAttackers(b);
		
		if (attackers_b.size() < attackers_a.size())
			return true;
		
		ArgumentRanking ranking = ev.getModel(dt);
		boolean strict_flag = false;
		for (Argument ax : attackers_a) {
			boolean flag = false;
			for (Argument bx : attackers_b) {
				if (ranking.isStrictlyLessOrEquallyAcceptableThan(bx, ax)) 
					flag = true;
				if (ranking.isStrictlyLessAcceptableThan(bx, ax))
					strict_flag = true;
			}
			if (!flag)
				return true;
		}
		
		if (ranking.isIncomparable(a, b)) {
			if (IGNORE_INCOMPARABLE_ARGUMENTS)
				return true;
			else
				return false;
		}
		
		if ((attackers_a.size() < attackers_b.size()) || strict_flag) 
			return ranking.isStrictlyMoreAcceptableThan(a, b); 
		return true;
	}
}
