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
package net.sf.tweety.logics.mln;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.mln.syntax.MlnFormula;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;

/**
 * This class provides common methods for MLN reasoner.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractMlnReasoner implements BeliefBaseReasoner<MarkovLogicNetwork> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(MarkovLogicNetwork mln, Formula query) {		
		return this.query(mln, query, (FolSignature) mln.getSignature());
	}
	
	/**
	 * Queries the given MLN wrt. the given signature 
	 * @param mln some mln
	 * @param query some query
	 * @param signature some signature
	 * @return the answer to the query
	 */
	public Answer query(MarkovLogicNetwork mln, Formula query, FolSignature signature) {
		if(!(query instanceof FolFormula) && !( ((FolFormula)query).isGround() ))
			throw new IllegalArgumentException("Reasoning in Markov logic with naive MLN reasoner is only defined for ground FOL formulas.");
		if(!mln.getSignature().isSubSignature(signature))
			throw new IllegalArgumentException("Given signature is not a super-signature of the belief base's signature.");
		double result = this.doQuery(mln,(FolFormula)query,signature);
		Answer ans = new Answer(mln,query);
		ans.setAnswer(result);		
		return ans;
	}
	
	/**
	 * Computes the (unnormalized) weight of the given Herbrand interpretation
	 * with respect to the formulas in this reasoner's MLN.
	 * @param mln an MLN
	 * @param hInt a Herbrand interpretation
	 * @param signature the underlying signature
	 * @return the (unnormalized) weight of the given Herbrand interpretation
	 */
	protected double computeWeight(MarkovLogicNetwork mln, HerbrandInterpretation hInt, FolSignature signature){
		int num;
		double weight = 0;
		for(MlnFormula f: mln){
			num = this.numberOfGroundSatisfactions(f.getFormula(), hInt, signature);
			if(f.isStrict()){
				if(num != f.getFormula().allGroundInstances(signature.getConstants()).size())
					return 0;		
				else weight += 1;
			}else 	
				weight += num * f.getWeight();
		}
		return Math.exp(weight);
	}
	
	/** Computes the number of instantiations of the formula, wrt. the given
	 * signature, that are satisfied in the given Herbrand interpretation. 
	 * @param formula some fol formula.
	 * @param hInt a Herbrand interpretation.
	 * @param signature the underlying signature
	 * @return the number of instantiations of the formula, wrt. the given
	 * signature, that are satisfied in the given Herbrand interpretation.
	 */
	protected int numberOfGroundSatisfactions(FolFormula formula, HerbrandInterpretation hInt, FolSignature signature){
		int num = 0;
		for(RelationalFormula f: formula.allGroundInstances(signature.getConstants()))
			if(hInt.satisfies((FolFormula) f)) num++;		
		return num;
	}
	
	/** Performs the actual querying. 
	 * @param mln an MLN
	 * @param query a fol formula guaranteed to be ground.
	 * @return the answer of the query.
	 */
	protected abstract double doQuery(MarkovLogicNetwork mln, FolFormula query, FolSignature signature);
}
