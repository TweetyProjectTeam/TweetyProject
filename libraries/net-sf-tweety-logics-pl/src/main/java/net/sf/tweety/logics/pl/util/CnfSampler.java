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
package net.sf.tweety.logics.pl.util;

import java.util.Random;

import net.sf.tweety.commons.BeliefSetSampler;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;

/**
 * A simple sampler for propositional belief bases. This sampler
 * always generates belief bases in CNF, i.e. every formula
 * appearing in the belief base is a disjunction of literals.
 * 
 * @author Matthias Thimm
 */
public class CnfSampler extends BeliefSetSampler<PlFormula,PlBeliefSet>{

	/** The maximum ratio of variables appearing in a single formula. */
	private double maxVariableRatio;
	
	/**
	 * Creates a new sampler for the given signature.
	 * @param signature a signature
	 * @param maxVariableRatio the maximum ratio (a value between 0 and 1) of variables
	 * of the signature appearing in some formula.
	 */
	public CnfSampler(Signature signature, double maxVariableRatio) {
		super(signature);
		if(!(signature instanceof PlSignature))
			throw new IllegalArgumentException("Signature of type \"PropositionalSignature\" expected. ");
		this.maxVariableRatio = maxVariableRatio;
	}

	/**
	 * Creates a new sampler for the given signature.
	 * @param signature a signature
	 * @param maxVariableRatio the maximum ratio (a value between 0 and 1) of variables
	 * of the signature appearing in some formula.
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public CnfSampler(Signature signature, double maxVariableRatio, int minLength, int maxLength) {
		super(signature, minLength, maxLength);
		if(!(signature instanceof PlSignature))
			throw new IllegalArgumentException("Signature of type \"PropositionalSignature\" expected. ");
		this.maxVariableRatio = maxVariableRatio;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.BeliefBaseSampler#randomSample(int, int)
	 */
	@Override
	public PlBeliefSet next() {
		PlBeliefSet beliefSet = new PlBeliefSet();
		Random rand = new Random();
		int length;
		if(this.getMaxLength() - this.getMinLength() > 0)
			length = this.getMinLength() + rand.nextInt(this.getMaxLength() - this.getMinLength());
		else length = this.getMinLength();
		while(beliefSet.size() < length){
			beliefSet.add(this.sampleFormula());
		}
		return beliefSet;
	}
	
	/**
	 * Returns a random formula
	 * @return a random formula
	 */
	public PlFormula sampleFormula(){
		
		PlSignature sig = (PlSignature)this.getSamplerSignature();
		Disjunction d = new Disjunction();		
		Random rand = new Random();
		for(Proposition p: sig){
			if(rand.nextDouble() <= this.maxVariableRatio){
				if(rand.nextBoolean())
					d.add(p);
				else d.add(new Negation(p));
			}
			if(d.size()+1 > this.maxVariableRatio * sig.size())
				break;
		}
		// at least one literal should be added
		if(d.isEmpty()){
			if(rand.nextBoolean())
				d.add((Proposition)sig.toArray()[rand.nextInt(sig.size())]);
			else
				d.add(new Negation((Proposition)sig.toArray()[rand.nextInt(sig.size())]));
		}
		return d;
	}

}
