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
 *  The "abstraction" postulate for ranking semantics as proposed in
 *  [Amgoud, Ben-Naim. Ranking-based semantics for argumentation frameworks. 2013]: 
 *  The ranking on an abstract argumentation framework A should be defined only on the
 *  basics of the attacks between arguments.
 *  This postulate was 
 * 
 * @author Anna Gessler
 *
 */
public class RaAbstraction extends RankingPostulate {

	@Override
	public String getName() {
		return "Abstraction";
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
		
		DungTheory iso_dt = new DungTheory(dt);
		iso_dt.remove(a);
		Argument iso_a = new Argument("iso_A_");
		iso_dt.add(iso_a);
		
		for (Argument f: dt.getAttackers(a)) 
			if (f.equals(a))
				iso_dt.add(new Attack(iso_a, iso_a));
			else
				iso_dt.add(new Attack(f, iso_a));
		
		for (Argument f: dt.getAttacked(a)) 
			if (!f.equals(a))
				iso_dt.add(new Attack(iso_a, f));
		
		ArgumentRanking ranking = ev.getModel(dt);
		ArgumentRanking iso_ranking = ev.getModel(iso_dt);
		return ranking.compare(a, b) == iso_ranking.compare(iso_a, b);
	}
}
