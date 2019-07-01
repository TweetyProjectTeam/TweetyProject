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
import net.sf.tweety.math.matrix.Matrix;

/**
 *  The "attack vs full defense" postulate for ranking semantics as proposed in 
 *  [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of Ranking-Based 
 *  Semantics for Abstract Argumentation. 2016]: 
 *  An argument without any attack branch is ranked higher than an argument only
 *  attacked by one non-attacked argument.
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
		if (kb.size()<2)
			return false;
		
		//AF must be acyclic
		DungTheory dt = (DungTheory) kb;
		Matrix mat = dt.getAdjacencyMatrix();
		for (int i = 0; i < mat.getYDimension(); i++) {
			if (mat.getEntry(i, i).equals(1))
				return false;
		}
		
		Iterator<Argument> it = dt.iterator();
		Argument a = it.next();
		Argument b = it.next();
		if (!dt.getAttackers(a).isEmpty())
			return false;
		if (dt.getAttackers(b).size()!=1)
			return false;
		return (dt.getAttackers(dt.getAttackers(b).iterator().next()).isEmpty());
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
