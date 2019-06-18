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
 *  The "cardinality precedence" postulate for ranking semantics as proposed 
 *  in [Amgoud, Ben-Naim. Ranking-based semantics for argumentation frameworks. 2013]:
 *  The greater the number of direct attackers for an argument,
 *  the weaker the level of acceptability of this argument.
 * 
 * @author Anna Gessler
 *
 */
public class RaCardinalityPrecedence extends RankingPostulate {

	@Override
	public String getName() {
		return "Cardinality Precedence";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		if (kb.size()<2)
			return false;
		DungTheory dt = (DungTheory) kb;
		Iterator<Argument> it = dt.iterator();
		Argument a = it.next();
		Argument b = it.next();
		return dt.getAttackers(a).size() <  dt.getAttackers(b).size();
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<ArgumentRanking> ev) {
		if (!this.isApplicable(kb))
			return true;
		DungTheory dt = (DungTheory) kb;
		Iterator<Argument> it = dt.iterator();
		Argument a = it.next();
		Argument b = it.next();
		ArgumentRanking ranking = ev.getModel((DungTheory)dt);
		return ranking.isStrictlyMoreAcceptableThan(a, b);
	}

}
