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
 *  The "abstraction" postulate for ranking semantics as proposed in
 *  [Amgoud, Ben-Naim. Ranking-based semantics for argumentation frameworks. 2013]: 
 *  The ranking on an abstract argumentation framework should be defined only on the
 *  basics of the attacks between arguments.
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
		return ((kb instanceof DungTheory) && kb.size()>=2 && !kb.contains(new Argument("iso")));
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<GeneralComparator<Argument, DungTheory>> ev) {
		if (!this.isApplicable(kb))
			return true;
		if (ev.getModel((DungTheory) kb) == null)
			return true;
		
		DungTheory dt = new DungTheory((DungTheory) kb);
		Iterator<Argument> it = dt.iterator();
		Argument a = it.next();
		Argument b = it.next();
		
		DungTheory isoDt = new DungTheory(dt);
		isoDt.remove(a);
		Argument isoArg = new Argument("iso");
		isoDt.add(isoArg);
		
		for (Argument f: dt.getAttackers(a)) 
			if (f.equals(a))
				isoDt.add(new Attack(isoArg, isoArg));
			else
				isoDt.add(new Attack(f, isoArg));
		
		for (Argument f: dt.getAttacked(a)) 
			if (!f.equals(a))
				isoDt.add(new Attack(isoArg, f));
		
		GeneralComparator<Argument, DungTheory> ranking = ev.getModel(dt);
		GeneralComparator<Argument, DungTheory> isoRanking = ev.getModel(isoDt);
		return ranking.compare(a, b) == isoRanking.compare(isoArg, b);
	}

    /** Default Constructor */
    public RaAbstraction(){}
}
