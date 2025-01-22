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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.cl.reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.logics.cl.semantics.RankingFunction;
import org.tweetyproject.logics.cl.syntax.ClBeliefSet;
import org.tweetyproject.logics.cl.syntax.Conditional;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;


/**
 * This class models a z-reasoner for conditional logic. Reasoning is performed
 * by computing a ranking function based on a tolerating partitioning of a consistent knowledge base<br>
 *
 * The conditionals of the knowledge base KB are splitted in different partitions. The first partition
 * KB_0 contains all conditionals (B_i|A_i) that are tolerated by all other rules of the knowledge base,
 * i.e., there exists a world w that satisfies A_iB_i and for all other conditionals (B_j|A_j) it satisfies
 * the material implication A_j =&gt; B_j. These conditionals are removed from the knowledge base.
 * The second partition KB_1 contains all conditionals of the remaining conditional set that are tolerated by it.
 * This is done until the original knowledge base is the empty set.
 * This is simultaniously a test on whether or not the knowledge base is consistent.
 * There exists such a partitioning iff the knowledge base is consistent.
 *
 * A world falsifying a conditional r_i gets penalty points according to the maxium partition that contains
 * a falsified world. The penalty points represent the ranking function and are computed as follows
 *
 *  Z(r_i) = j iff r_i in KB_j
 *
 *        ( 0, if w |=  A_i =&gt; B_i  for all 1&lt;=i&lt;=n
 * Z(w) = &lt;
 *        ( max {z(r_i)} + 1 for all conditionals r_i=(B_i|A_i) with w |= A_i !B_i
 *
 * <br><br>See Judea Pearl. System Z: a natural ordering of defaults with tractable applications to default
 * reasoning. In Proceedings of the 3rd conference on Theoretical aspects of reasoning about knowledge,
 * TARK '90, San Francicso, Morgan Kaufmann Publishers Inc. (1990) 121-135
 *
 * @author Katharina Diekmann
 * @author Matthias Thimm
 */
public class ZReasoner extends AbstractConditionalLogicReasoner {

	/** Constructor */
	public ZReasoner(){
		super();
	}

	/**
	 * Returns a partitioning of a knowledge base into partitions containing all conditionals that
	 * tolerate the remaining set of conditionals of a knowledge base.
	 * @param kb Knowledge base that needs to be partitioned
	 * @param omega a set of possible worlds
	 * @return ArrayList containing consistent belief sets
	 */
	private ArrayList<ClBeliefSet> partition( ClBeliefSet kb , Set<PossibleWorld> omega){

		// create empty set of belief bases for the partitioning
		ArrayList<ClBeliefSet> tolerancePartition = new ArrayList<ClBeliefSet>();

		// Copy knowledge base to a second set from which we can remove tolerated conditionals
		ClBeliefSet knowledgebase = new ClBeliefSet(kb);

		while( !knowledgebase.isEmpty() ){

			ClBeliefSet partition = new ClBeliefSet();

			for( Conditional f: knowledgebase ){
				// if the current conditional is tolerated by the remaining set of conditionals in the
				// knowledge base, add it to the current partition
				if( isTolerated(f,knowledgebase, omega) ) {
					partition.add( f );
				}

			}

			 /*
			  * If you're not doing any progress here, the belief base isn't
			  * consistent and evaluation should be aborted.
			  */
			 if (partition.isEmpty())
			  return new ArrayList<ClBeliefSet>();

			// adding the partition with all tolerated conditionals to the partition set
			// and remove all conditionals of this partition from the remaining conditional set
			tolerancePartition.add( partition );

			for( Conditional f : partition ){
				knowledgebase.remove(f);
			}
		}

		System.out.println( tolerancePartition );

		return tolerancePartition;
	}

	/**
	 * Checks whether or not the given formula is tolerated by the knowledge base,
	 * i.e., there is a world omega that satisfies the formula and does not falsify each conditional (B|A)
	 * in the knowledge base (it satisfies the material implication A => B).
	 * @param f  formula that should be tolerated by the knowledge base
	 * @param kb corresponding knowledge base
	 * @param omega a set of possible worlds
	 * @return true if the Conditional f is tolerated, false otherwise
	 */
	private boolean isTolerated( Conditional f, ClBeliefSet kb , Set<PossibleWorld> omega) {

		boolean tolerated = true;

		// Test whether or not there is a world that satisfies formula f
		// and does not falsify the remaining conditionals of the knowledge base
		for( PossibleWorld world : omega ){
			tolerated = true;

			// Test whether or not the current world satisfies the formula f.
			// If it does not satisfy the formula f, all further tests can be aborted.
			if( !world.satisfies(f.getPremise()) || ( world.satisfies(f.getPremise()) && !world.satisfies(f.getConclusion()) ) ){
				tolerated = false;
				continue;
			}

			for( Conditional c : kb ){

				// Test whether the current world which satisfies the given formula f does also
				// satisfy the material implication of the current conditional c.
				if( world.satisfies(c.getPremise()) ){
					if( !world.satisfies(c.getConclusion()) ){
						tolerated = false;
						break;
					}
				}
			}

			// if the current world represents an interpretation under which the formula f is tolerated, yeay
			if( tolerated == true ){
				break;
			}
		}

		return tolerated;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.cl.reasoner.AbstractConditionalLogicReasoner#getModel(org.tweetyproject.logics.cl.syntax.ClBeliefSet)
	 */
	@Override
	public RankingFunction getModel(ClBeliefSet beliefset) {
		Set<PossibleWorld> omega = PossibleWorld.getAllPossibleWorlds(beliefset.getMinimalSignature().toCollection());
		RankingFunction ocf = new RankingFunction(beliefset.getMinimalSignature());

		// Compute partitioning of the knowledge base
		ArrayList<ClBeliefSet> tolerancePartition = partition( beliefset ,omega);
		if( tolerancePartition.isEmpty() ){
			System.out.println("The belief base " + beliefset + " is not consistent.");
			return null;
		}

		// Store Z-value for each conditional of the knowledge base
		Map<Conditional,Integer> zValue = new HashMap<Conditional,Integer>();

		for( int index = 0; index < tolerancePartition.size(); index++ ){
			for( Conditional c : tolerancePartition.get(index) ) {
				zValue.put(c, index);
			}
		}

		// Compute penalty points for each world based on partitioning

		for( PossibleWorld w : omega ){
			int rank = 0;

			for( Conditional c : zValue.keySet() ){
				if( (w.satisfies(c.getPremise()) && !w.satisfies(c.getConclusion())) && rank < (zValue.get(c)+1)){
					rank = zValue.get(c) + 1;
				}
			}

			ocf.setRank(w, rank);
		}

		return ocf;
	}

	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}

}


