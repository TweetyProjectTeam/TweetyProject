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
package org.tweetyproject.logics.rpcl.semantics;

import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.fol.semantics.*;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.rpcl.syntax.*;
import org.tweetyproject.math.equation.*;
import org.tweetyproject.math.term.*;
import org.tweetyproject.math.probability.*;


/**
 * This class bundles common answering behaviour for
 * relational conditional semantics.
 * 
 * @author Matthias Thimm
 * 
 */
public abstract class AbstractRpclSemantics implements RpclSemantics {

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.rpcl.semantics.RpclSemantics#satisfies(org.tweetyproject.logics.rpcl.semantics.RpclProbabilityDistribution, org.tweetyproject.logics.rpcl.syntax.RelationalProbabilisticConditional)
	 */
	@Override
	public abstract boolean satisfies(RpclProbabilityDistribution<?> p, RelationalProbabilisticConditional r);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.rpcl.semantics.RpclSemantics#getSatisfactionStatement(org.tweetyproject.logics.rpcl.syntax.RelationalProbabilisticConditional, org.tweetyproject.logics.fol.syntax.FolSignature, java.util.Map)
	 */
	@Override
	public abstract Statement getSatisfactionStatement(RelationalProbabilisticConditional r, FolSignature signature, Map<Interpretation<FolBeliefSet,FolFormula>,FloatVariable> worlds2vars);
	
	/**
	 * Checks whether the given ground conditional is satisfied by the given distribution
	 * wrt. this semantics. For every rational semantics this satisfaction relation
	 * should coincide with the propositional case.
	 * @param p a probability distribution.
	 * @param groundConditional a ground conditional
	 * @return "true" iff the given ground conditional is satisfied by the given distribution
	 * 	wrt. this semantics
	 */
	protected boolean satisfiesGroundConditional(RpclProbabilityDistribution<?> p, RelationalProbabilisticConditional groundConditional){
		if(!groundConditional.isGround())
			throw new IllegalArgumentException("The conditional " + groundConditional + " is not ground.");
		return p.probability(groundConditional).getValue() < groundConditional.getProbability().getValue() + Probability.PRECISION &&
			p.probability(groundConditional).getValue() > groundConditional.getProbability().getValue() - Probability.PRECISION;
	}
	
	/**
	 * Constructs the term expressing the probability of the given formula "f"
	 * wrt. to the variables (describing probabilities) of the reference worlds.
	 * @param f a fol formula
	 * @param worlds2vars a map mapping reference worlds to variables.
	 * @return the term expressing the probability of the given formula "f".
	 */
	protected Term probabilityTerm(FolFormula f, Map<Interpretation<FolBeliefSet,FolFormula>,FloatVariable> worlds2vars){
		Term result = null;		
		for(Interpretation<FolBeliefSet,FolFormula> world: worlds2vars.keySet()){
			Integer multiplicator;
			if(world instanceof ReferenceWorld)
				multiplicator = ((ReferenceWorld)world).getMultiplicator(f);
			else if(world instanceof HerbrandInterpretation)
				multiplicator = (((HerbrandInterpretation)world).satisfies(f))?(1):(0);
			else throw new IllegalArgumentException("Intepretation of type reference world or Herbrand interpretation expected.");
			if(multiplicator != 0){
				Term t = new FloatConstant(multiplicator).mult(worlds2vars.get(world));
				if(result == null)
					result = t;
				else result = result.add(t);
			}				
		}			
		return (result == null)? new FloatConstant(0): result;
	}
	
}
