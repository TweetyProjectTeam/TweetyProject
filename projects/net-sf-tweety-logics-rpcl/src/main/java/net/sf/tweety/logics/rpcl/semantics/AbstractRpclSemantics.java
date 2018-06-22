/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.rpcl.semantics;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.fol.semantics.*;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.rpcl.*;
import net.sf.tweety.logics.rpcl.syntax.*;
import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.term.*;
import net.sf.tweety.math.probability.*;


/**
 * This class bundles common answering behaviour for
 * relational conditional semantics.
 * 
 * @author Matthias Thimm
 * 
 */
public abstract class AbstractRpclSemantics implements RpclSemantics {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.rpcl.semantics.RpclSemantics#satisfies(net.sf.tweety.logics.rpcl.semantics.RpclProbabilityDistribution, net.sf.tweety.logics.rpcl.syntax.RelationalProbabilisticConditional)
	 */
	@Override
	public abstract boolean satisfies(RpclProbabilityDistribution<?> p, RelationalProbabilisticConditional r);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.rpcl.semantics.RpclSemantics#getSatisfactionStatement(net.sf.tweety.logics.rpcl.syntax.RelationalProbabilisticConditional, net.sf.tweety.logics.fol.syntax.FolSignature, java.util.Map)
	 */
	@Override
	public abstract Statement getSatisfactionStatement(RelationalProbabilisticConditional r, FolSignature signature, Map<Interpretation<FolFormula>,FloatVariable> worlds2vars);
	
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
	protected Term probabilityTerm(FolFormula f, Map<Interpretation<FolFormula>,FloatVariable> worlds2vars){
		Term result = null;		
		for(Interpretation<FolFormula> world: worlds2vars.keySet()){
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
