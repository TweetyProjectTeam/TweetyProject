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
package org.tweetyproject.logics.pl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.tweetyproject.commons.BeliefSetSampler;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * This sampler generates random belief sets by selecting,
 * for each formula a random set of possible worlds as its models.
 * 
 * @author Matthias Thimm
 *
 */
/**
 * @author mthimm
 *
 */
public class RandomSampler extends BeliefSetSampler<PlFormula,PlBeliefSet> {

	/** All possible worlds */
	private List<PossibleWorld> allWorlds;
	/** for generating random numbers */
	private Random rand = new Random();
	/** Probability of selecting any world as a model of a formula*/
	private double worldProb;
	
	/**
	 * Creates a new sampler for the given signature
	 * @param signature some signature
	 * @param worldProb Probability of selecting any world as a model of a formula
	 */
	public RandomSampler(Signature signature, double worldProb) {
		super(signature);
		this.allWorlds = new ArrayList<PossibleWorld>(PossibleWorld.getAllPossibleWorlds((PlSignature)signature));
		this.worldProb = worldProb;
	}
	
	/**
	 * Creates a new sampler for the given signature
	 * @param signature some signature
	 * @param worldProb Probability of selecting any world as a model of a formula
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public RandomSampler(Signature signature, double worldProb, int minLength, int maxLength) {
		super(signature,minLength,maxLength);
		this.allWorlds = new ArrayList<PossibleWorld>(PossibleWorld.getAllPossibleWorlds((PlSignature)signature));
		this.worldProb = worldProb;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSetSampler#next()
	 */
	@Override
	public PlBeliefSet next() {
		PlBeliefSet beliefSet = new PlBeliefSet();		
		int length;
		if(this.getMaxLength() - this.getMinLength() > 0)
			length = this.getMinLength() + this.rand.nextInt(this.getMaxLength() - this.getMinLength());
		else length = this.getMinLength();
		int idx = 0;
		// ensure that no two formulas are syntactically equivalent
		// by adding another proposition.
		String c = Character.toString((char) (new Random().nextInt(26) + 'A')); //random letter to prevent generated belief sets from always having overlapping signatures
		while(beliefSet.size() < length){
			beliefSet.add(this.randomFormula().combineWithAnd(new Proposition(c+"SA"+idx++)));
		}
		return beliefSet;
	}
	
	/**
	 * Returns a random formula.
	 * @return a random formula.
	 */
	private PlFormula randomFormula(){
		Proposition a = ((PlSignature)this.getSamplerSignature()).iterator().next();
		PlFormula p = a.combineWithAnd(new Negation(a));
		for(int i = 0; i < this.allWorlds.size(); i++)
			if(this.rand.nextDouble()<this.worldProb)
				p = p.combineWithOr(this.allWorlds.get(i).getCompleteConjunction((PlSignature)this.getSamplerSignature()));		
		return p;
	}
}
