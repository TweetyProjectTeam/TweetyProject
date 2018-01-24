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
 *  Copyright 2016-2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.util;

import java.util.Random;

import net.sf.tweety.commons.BeliefBaseSampler;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * A sampler for uniform random k-SAT instances.
 * 
 * @author Matthias Thimm
 *
 */
public class RandomSatSampler extends BeliefBaseSampler<PlBeliefSet> {
	
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

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseSampler#randomSample(int, int)
	 */
	@Override
	public PlBeliefSet randomSample(int minLength, int maxLength) {
		int length;
		if(maxLength - minLength > 0)
			length = minLength + this.rand.nextInt(maxLength - minLength);
		else length = minLength;
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
			Proposition p = this.setTools.randomElement((PropositionalSignature)this.getSignature());
			if(!(clause.contains(p.complement()) || clause.contains(p)))
				if(this.rand.nextBoolean())
					clause.add(p);
				else clause.add((PropositionalFormula) p.complement());
		}
		return clause;
	}

}
