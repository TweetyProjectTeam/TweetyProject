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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.prob.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.QuantitativeReasoner;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract ancestor for PAF reasoner.
 * 
 * @author Matthias Thimm
 *
 */
public abstract class AbstractPafReasoner implements QuantitativeReasoner<ProbabilisticArgumentationFramework,Argument>{

	/** Semantics for plain AAFs. */
	private final Semantics semantics;
	
	/**
	 * Creates a new reasoner.
	 * @param semantics Semantics for plain AAFs.
	 */
	public AbstractPafReasoner(Semantics semantics) {
		this.semantics = semantics;
	}
	
	/**
	 * The semantics of this reasoner.
	 * @return The semantics of this reasoner.
	 */
	protected Semantics getSemantics() {
		return this.semantics;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.Reasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public Double query(ProbabilisticArgumentationFramework beliefbase, Argument formula) {
		return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
	}
	
	/**
	 * Queries the given PAF for the given argument using the given 
	 * inference type.
	 * @param beliefbase an PAF
	 * @param formula a single argument
	 * @param inferenceMode either InferenceMode.SKEPTICAL or InferenceMode.CREDULOUS
	 * @return probability of the argument
	 */
	public abstract Double query(ProbabilisticArgumentationFramework beliefbase, Argument formula, InferenceMode inferenceMode);
	
	/**
	 * Estimates the probability that the given set of
	 * arguments is an extension
	 * @param paf a PAF
	 * @param ext some set of arguments
	 * @return the estimated probability of the given set to be 
	 * an extension
	 */
	public abstract Double query(ProbabilisticArgumentationFramework paf, Extension<ProbabilisticArgumentationFramework> ext);

	/**
	 * Determine the set of acceptable arguments wrt. the given inference mode
	 * @param paf some probabilistic argumentation framework
	 * @param inferenceMode the inference mode
	 * @return a map of all arguments and their respective acceptance probability
	 */
	public Map<Argument,Double> queryAll(ProbabilisticArgumentationFramework paf, InferenceMode inferenceMode) {
		Map<Argument,Double> result = new HashMap<>();
		for (Argument arg : paf) {
			result.put(arg, this.query(paf, arg, inferenceMode));
		}
		return result;
	}
}
