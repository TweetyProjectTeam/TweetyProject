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
 *  Copyright 2016-2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.util;

import java.util.Random;

import org.tweetyproject.commons.BeliefSetSampler;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * A sampler for uniform random k-SAT instances.
 * 
 * @author Matthias Thimm
 *
 */
public class RandomSatSampler extends BeliefSetSampler<PlFormula,PlBeliefSet> {
	
	/**
	 * The length of each clause 
	 */
	private int k;
		
	/**
	 * For randomisation 
	 */
	private SetTools<Proposition> setTools = new SetTools<Proposition>();
	
	/**
	 * For randomisation 
	 */
	private Random rand = new Random();
	
	/**
	 * Constructs a new sampler
	 * @param signature some signature
	 * @param k the length of each clause
	 */
	public RandomSatSampler(Signature signature, int k) {
		super(signature);		
		this.k = k;
	}
	
	/**
	 * Constructs a new sampler
	 * @param signature some signature
	 * @param k the length of each clause
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public RandomSatSampler(Signature signature, int k, int minLength, int maxLength) {
		super(signature,minLength,maxLength);		
		this.k = k;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSetSampler#next()
	 */
	@Override
	public PlBeliefSet next() {
		int length;
		if(this.getMaxLength() - this.getMinLength() > 0)
			length = this.getMinLength() + this.rand.nextInt(this.getMaxLength() - this.getMinLength());
		else length = this.getMinLength();
		PlBeliefSet bs = new PlBeliefSet();
		while(bs.size() < length)
			bs.add(this.randomClause());		
		return bs;
	}
	
	/**
	 * Generates a random clause with k literals.
	 * @return a disjunction of literals.
	 */
	public Disjunction randomClause() {
		Disjunction clause = new Disjunction();
		while(clause.size() < this.k) {
			Proposition p = this.setTools.randomElement(((PlSignature)this.getSamplerSignature()).toCollection());
			if(!(clause.contains(p.complement()) || clause.contains(p)))
				if(this.rand.nextBoolean())
					clause.add(p);
				else clause.add((PlFormula) p.complement());
		}
		return clause;
	}
}
