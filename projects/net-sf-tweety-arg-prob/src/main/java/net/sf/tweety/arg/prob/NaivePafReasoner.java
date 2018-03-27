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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.prob;

import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.math.probability.Probability;

/**
 * This class implements naive algorithm for computing
 * probabilities of extensions in probabilistic argumentation frameworks
 * from [Li, Oren, Norman. Probabilistic Argumentation Frameworks. TAFA'2011].
 * It considers all subgraphs and computes therefore exact probabilities
 * 
 * @author Matthias Thimm
 */
public class NaivePafReasoner extends Reasoner{

	/** Semantics for plain AAFs. */
	private Semantics semantics;
	/** The inference type used for estimating acceptability probability
	 * 	of single arguments (credulous or skeptical inference).*/
	private int inferenceType;
	
	/**
	 * Creates a new reasoner for the given framework
	 * @param aaf some probabilistic argumentation framework
	 * @param semantics semantics used for determining extensions.
	 * @param inferenceType The inference type used for estimating acceptability probability
	 * 	of single arguments (credulous or skeptical inference).
	 */
	public NaivePafReasoner(ProbabilisticArgumentationFramework aaf, Semantics semantics, int inferenceType) {
		super(aaf);
		this.semantics = semantics;
		this.inferenceType = inferenceType;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof Argument))
			throw new IllegalArgumentException("Formula of class argument expected");
		Argument arg = (Argument) query;
		double prob = 0d;
		SubgraphProbabilityFunction p = ((ProbabilisticArgumentationFramework)this.getKnowledgeBase()).getSubgraphProbabilityFunction(); 
		for(DungTheory sub: p.keySet()){
			AbstractExtensionReasoner r = AbstractExtensionReasoner.getReasonerForSemantics(sub, this.semantics, this.inferenceType);
			if(r.query(arg).getAnswerBoolean())
				prob += p.probability(sub).doubleValue();
		}
		Answer ans = new Answer(this.getKnowledgeBase(),query);
		ans.setAnswer(prob);		
		return ans;
	}

	/**
	 * Estimates the probability that the given set of
	 * arguments is an extension
	 * @param ext some set of arguments
	 * @return the estimated probability of the given set to be 
	 * an extension
	 */
	public Probability query(Extension ext){
		double prob = 0d;
		SubgraphProbabilityFunction p = ((ProbabilisticArgumentationFramework)this.getKnowledgeBase()).getSubgraphProbabilityFunction(); 
		for(DungTheory sub: p.keySet()){
			AbstractExtensionReasoner r = AbstractExtensionReasoner.getReasonerForSemantics(sub, this.semantics, this.inferenceType);
			if(r.getExtensions().contains(ext))
				prob += p.probability(sub).doubleValue();
		}		
		return new Probability(prob);
	}
}
