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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.sf.tweety.commons.BeliefSetSampler;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Generates random propositional belief base with a given
 * inconsistency measure (for the contension inconsistency measure)
 * and of a given size.
 * 
 * @author Matthias Thimm
 */
public class ContensionSampler extends BeliefSetSampler<PropositionalFormula,PlBeliefSet>{
	
	/**
	 * The inconsistency value of the generated belief sets
	 * (wrt. the contension inconsistency measure).
	 */
	private int incvalue;
	
	/**
	 * Creates a new sample for the given signature
	 * which generates propositional belief sets with the 
	 * given inconsistency value (wrt. the contension inconsistency measure)
	 * @param signature some propositional signature
	 * @param incvalue some inconsistency value.
	 */
	public ContensionSampler(PropositionalSignature signature, int incvalue) {
		super(signature);
		if(incvalue > signature.size())
			throw new IllegalArgumentException("A propositional belief base with inconsistency value " + this.incvalue + " cannot be generated with the given signature."); 
		this.incvalue = incvalue;
	}
	
	/**
	 * Creates a new sample for the given signature
	 * which generates propositional belief sets with the 
	 * given inconsistency value (wrt. the contension inconsistency measure)
	 * @param signature some propositional signature
	 * @param incvalue some inconsistency value.
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public ContensionSampler(PropositionalSignature signature, int incvalue, int minLength, int maxLength) {
		super(signature,minLength,maxLength);
		if(incvalue > signature.size())
			throw new IllegalArgumentException("A propositional belief base with inconsistency value " + this.incvalue + " cannot be generated with the given signature."); 
		this.incvalue = incvalue;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSetSampler#next()
	 */
	@Override
	public PlBeliefSet next() {
		List<Proposition> props = new ArrayList<Proposition>((PropositionalSignature)this.getSignature());
		List<PropositionalFormula> formulas = new ArrayList<PropositionalFormula>();
		// first add contradictoy formulas
		int num = 0;		
		for(Proposition p: props){
			formulas.add(p);
			formulas.add(new Negation(p));
			num++;
			if(num + 1 > this.incvalue)
				break;
		}
		// add some arbitrary formulas that cannot be inconsistent
		Random rand = new Random();
		while(num < this.getMaxLength()){
			Disjunction d = new Disjunction();
			for(Proposition p: props){
				if(rand.nextBoolean())
					d.add(p);
			}
			if(!formulas.contains(d)){
				num++;
				formulas.add(d);
			}				
		}
		// shuffle
		Collections.shuffle(formulas);
		return new PlBeliefSet(formulas);
	}

}


