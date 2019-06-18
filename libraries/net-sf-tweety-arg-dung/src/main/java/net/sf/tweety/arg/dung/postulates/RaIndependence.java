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
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;

/**
 *  The "independence" postulate for ranking semantics as proposed in
 *  [Amgoud, Ben-Naim. Ranking-based semantics for argumentation frameworks. 2013]: 
 *  The ranking between to arguments a and b should be independent of any argument 
 *  that is neither connected to a nor to b.
 * 
 * @author Anna Gessler
 *
 */
public class RaIndependence extends RankingPostulate {

	@Override
	public String getName() {
		return "Independence";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb.size()>=2 && !kb.contains(new Argument("_t")) && !kb.contains(new Argument("_t2")));
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
		Argument t1 = new Argument("_t");
		Argument t2 = new Argument("_t2");
		dt.add(t1);
		dt.add(t2);
		dt.add(new Attack(t1,t2));
		ArgumentRanking ranking2 = ev.getModel((DungTheory)dt);
		return ranking.compare(a, b) == ranking2.compare(a, b);
	}

}
