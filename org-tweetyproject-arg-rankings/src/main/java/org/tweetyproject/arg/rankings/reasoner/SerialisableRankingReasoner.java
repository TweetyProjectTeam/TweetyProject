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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleInitialReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.comparator.LatticePartialOrder;

/**
 * Implements the serialisable ranking semantics from
 * [Blümel, Thimm. A Ranking Semantics for Abstract Argumentation based on Serialisability. In COMMA'22, 2022.]
 * 
 * @author Matthias Thimm
 *
 */
public class SerialisableRankingReasoner extends AbstractRankingReasoner<LatticePartialOrder<Argument, DungTheory>>{

	// the reasoner used for initial sets
	private AbstractExtensionReasoner initialSetReasoner;
	
	/**
	 * Creates a new serialisable ranking reasoner that uses
	 * SimpleInitialReasoner for determining initial sets.
	 */
	public SerialisableRankingReasoner() {
		this(new SimpleInitialReasoner());
	}
	
	/**
	 * Creates a new serialisable ranking reasoner that uses the 
	 * given reasoner for determining initial sets (this object should
	 * return all initial sets via getModels().
	 * @param initialSetReasoner some initial set reasoner
	 * 
	 */
	public SerialisableRankingReasoner(AbstractExtensionReasoner initialSetReasoner) {
		this.initialSetReasoner = initialSetReasoner;
	}
	
	@Override
	public Collection<LatticePartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<LatticePartialOrder<Argument, DungTheory>> ranks = new HashSet<LatticePartialOrder<Argument, DungTheory>>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	@Override
	public LatticePartialOrder<Argument, DungTheory> getModel(DungTheory bbase) {
		Map<Argument,Integer> min_ser_seq = new HashMap<>();
		Queue<Pair<DungTheory,Integer>> q = new LinkedList<>();
		q.add(new Pair<DungTheory,Integer>(bbase,0));
		while(!q.isEmpty()) {
			Pair<DungTheory,Integer> current = q.poll();
			Collection<Extension<DungTheory>> initial_sets = this.initialSetReasoner.getModels(current.getFirst());
			for(Extension<DungTheory> e: initial_sets) {
				// update serialisation index
				for(Argument a: e) {
					if(!min_ser_seq.containsKey(a)) {
						min_ser_seq.put(a, current.getSecond()+1);
					}else {
						if(min_ser_seq.get(a) > current.getSecond()+1)
							min_ser_seq.put(a, current.getSecond()+1);
					}
				}
				// get reduct
				DungTheory reduct = current.getFirst().getReduct(e);
				q.add(new Pair<DungTheory,Integer>(reduct,current.getSecond()+1));
			}
		}
		// construct ranking
		LatticePartialOrder<Argument, DungTheory> ranking = new LatticePartialOrder<Argument, DungTheory>( ((DungTheory) bbase).getNodes());
		for(Argument a: bbase)
			for(Argument b: bbase) {
				if(!min_ser_seq.containsKey(a) && !min_ser_seq.containsKey(b)) {
					ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
				}else if(!min_ser_seq.containsKey(a)) {
					ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
				}else if(!min_ser_seq.containsKey(b)) {
					ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
				}else {
					if(min_ser_seq.get(a) == min_ser_seq.get(b)) {
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					}else if(min_ser_seq.get(a) < min_ser_seq.get(b)) {
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
					}else {
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					}
				}
			}		
		return ranking;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}

}
