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
package net.sf.tweety.logics.pl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.tweety.commons.BeliefBaseSampler;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This sampler generates random belief sets by selecting,
 * for each formula a random set of possible worlds as its models.
 * 
 * @author Matthias Thimm
 *
 */
public class RandomPlBeliefSetSampler extends BeliefBaseSampler<PlBeliefSet> {

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
	public RandomPlBeliefSetSampler(Signature signature, double worldProb) {
		super(signature);
		this.allWorlds = new ArrayList<PossibleWorld>(PossibleWorld.getAllPossibleWorlds((PropositionalSignature)signature));
		this.worldProb = worldProb;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseSampler#randomSample(int, int)
	 */
	@Override
	public PlBeliefSet randomSample(int minLength, int maxLength) {
		PlBeliefSet beliefSet = new PlBeliefSet();		
		int length;
		if(maxLength - minLength > 0)
			length = minLength + this.rand.nextInt(maxLength - minLength);
		else length = minLength;
		int idx = 0;
		// ensure that no two formulas are syntactically equivalent
		// by adding another proposition.
		while(beliefSet.size() < length){
			beliefSet.add(this.randomFormula().combineWithAnd(new Proposition("XSA"+idx++)));
		}
		return beliefSet;
	}
	
	/**
	 * Returns a random formula.
	 * @return a random formula.
	 */
	private PropositionalFormula randomFormula(){
		Proposition a = ((PropositionalSignature)this.getSignature()).iterator().next();
		PropositionalFormula p = a.combineWithAnd(new Negation(a));
		for(int i = 0; i < this.allWorlds.size(); i++)
			if(this.rand.nextDouble()<this.worldProb)
				p = p.combineWithOr(this.allWorlds.get(i).getCompleteConjunction((PropositionalSignature)this.getSignature()));		
		return p;
	}
}
