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
import java.util.Stack;

import net.sf.tweety.commons.BeliefSetSampler;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;

/**
 * Generates random propositional belief base with a given
 * inconsistency measure (for the MI inconsistency measure)
 * and of a given size.
 * 
 * NOTE: this sampler is quite simple and only generates
 * MIs of size four and is very demanding on the size of the
 * signature.
 * 
 * @author Matthias Thimm
 */
public class MiSampler extends BeliefSetSampler<PlFormula,PlBeliefSet>{

	/**
	 * The inconsistency value of the generated belief sets
	 * (wrt. the MI inconsistency measure).
	 */
	private int incvalue;
	
	/**
	 * Creates a new sample for the given signature
	 * which generates propositional belief sets with the 
	 * given inconsistency value (wrt. the MI inconsistency measure)
	 * @param signature some propositional signature
	 * @param incvalue some inconsistency value.
	 */
	public MiSampler(PlSignature signature, int incvalue) {
		super(signature);
		if(incvalue > signature.size()/2)
			throw new IllegalArgumentException("A propositional belief base with inconsistency value " + this.incvalue + " cannot be generated with the given signature."); 
		this.incvalue = incvalue;
	}
	
	/**
	 * Creates a new sample for the given signature
	 * which generates propositional belief sets with the 
	 * given inconsistency value (wrt. the MI inconsistency measure)
	 * @param signature some propositional signature
	 * @param incvalue some inconsistency value.
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public MiSampler(PlSignature signature, int incvalue, int minLength, int maxLength) {
		super(signature,minLength,maxLength);
		if(incvalue > signature.size()/2)
			throw new IllegalArgumentException("A propositional belief base with inconsistency value " + this.incvalue + " cannot be generated with the given signature."); 
		this.incvalue = incvalue;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSetSampler#next()
	 */
	@Override
	public PlBeliefSet next() {
		List<PlFormula> formulas = new ArrayList<PlFormula>();
		// first generate MIs
		int num = 0;
		Stack<Proposition> st = new Stack<Proposition>();
		for (Proposition p : ((PlSignature) this.getSamplerSignature()))
			st.push(p);
		Proposition a,b;
		while(num < this.incvalue){
			a = st.pop();
			b = st.pop();
			formulas.add(new Disjunction(a,b));
			formulas.add(new Disjunction(a,new Negation(b)));
			formulas.add(new Disjunction(new Negation(a),b));
			formulas.add(new Disjunction(new Negation(a),new Negation(b)));
			num++;
		}
		// add remaining formulas
		Random rand = new Random();
		while(num < this.getMinLength()){
			Disjunction d = new Disjunction();
			for(Proposition p: st){
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
