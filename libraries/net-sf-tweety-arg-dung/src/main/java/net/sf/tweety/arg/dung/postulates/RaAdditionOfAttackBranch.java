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
 *  The "addition of attack branch" postulate for ranking semantics as formalized 
 *  in [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study of Ranking-Based 
 *  Semantics for Abstract Argumentation. 2016]: 
 *  Adding an attack branch to any argument degrades its ranking.
 * 
 * 'Adding an attack branch to the argument A' means adding the arguments {X1, ... , Xn} 
 *  which are not in the original knowledge base, for whom is true that 
 *  A <- X1 <- X2 ... <- Xn and where n is an odd number.
 *  
 * @author Anna Gessler
 *
 */
public class RaAdditionOfAttackBranch extends RankingPostulate {

	@Override
	public String getName() {
		return "Addition of Attack Branch";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		if (kb.size()<1)
			return false;
		Argument a_old = ((DungTheory) kb).iterator().next();		
		return (!((DungTheory) kb).getAttackers(a_old).isEmpty() && !kb.contains(new Argument("_t1")) && !kb.contains(new Argument("_t2")) && !kb.contains(new Argument("_t3")) 
				&& !kb.contains(new Argument(a_old.getName()+"_clone")));
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<ArgumentRanking> ev) {
		if (!this.isApplicable(kb))
			return true;
		DungTheory dt = (DungTheory) kb;
		Iterator<Argument> it = dt.iterator();
		Argument a_old = it.next();
		
		//clone argument and relations
		Argument a_clone = new Argument(a_old.getName()+"_clone");
		dt.add(a_clone);
		for (Argument attacker : dt.getAttackers(a_old)) {
			if (attacker.equals(a_old))
				dt.add(new Attack(a_clone,a_clone));
			else
				dt.add(new Attack(attacker,a_clone));
		}
		//add new attack branch
		Argument t1 = new Argument("_t1");
		Argument t2 = new Argument("_t2");
		Argument t3 = new Argument("_t3");
		dt.add(t1);
		dt.add(t2);
		dt.add(t3);
		dt.add(new Attack(t1,a_clone));
		dt.add(new Attack(t2,t1));
		dt.add(new Attack(t3,t2));
		ArgumentRanking ranking = ev.getModel((DungTheory)dt);
		
		return ranking.isStrictlyLessAcceptableThan(a_clone, a_old);
	}

	

}
