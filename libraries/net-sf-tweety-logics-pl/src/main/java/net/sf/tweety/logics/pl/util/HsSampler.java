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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.sf.tweety.commons.BeliefSetSampler;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;

/**
 * Generates random propositional belief base with a given
 * inconsistency measure (for the Hs inconsistency measure)
 * and of a given size.
 * 
 * @author Matthias Thimm
 */
public class HsSampler extends BeliefSetSampler<PlFormula,PlBeliefSet>{

	/**
	 * The inconsistency value of the generated belief sets
	 * (wrt. the Hs inconsistency measure).
	 */
	private int incvalue;
	
	/**
	 * Creates a new sample for the given signature
	 * which generates propositional belief sets with the 
	 * given inconsistency value (wrt. the Hs inconsistency measure)
	 * @param signature some propositional signature
	 * @param incvalue some inconsistency value.
	 */
	public HsSampler(PlSignature signature, int incvalue) {
		super(signature);
		this.incvalue = incvalue;
	}
	
	/**
	 * Creates a new sample for the given signature
	 * which generates propositional belief sets with the 
	 * given inconsistency value (wrt. the Hs inconsistency measure)
	 * @param signature some propositional signature
	 * @param incvalue some inconsistency value.
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public HsSampler(PlSignature signature, int incvalue, int minLength, int maxLength) {
		super(signature,minLength,maxLength);
		this.incvalue = incvalue;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSetSampler#next()
	 */
	public PlBeliefSet next() {
		PlSignature sig = (PlSignature) this.getSignature();
		if(this.incvalue > Math.pow(2, sig.size()))
			throw new IllegalArgumentException("A propositional belief base with inconsistency value " + this.incvalue + " cannot be generated with the given signature.");
		List<PlFormula> canonical = this.getCanonicalFormulas(this.incvalue+1, sig);
		PlBeliefSet bs = new PlBeliefSet();
		bs.addAll(canonical);
		Random rand = new Random();
		PlFormula f1,f2;
		CnfSampler sampler = new CnfSampler(sig,3d/sig.size());
		while(bs.size() < this.getMinLength()){
			f1 = canonical.get(rand.nextInt(canonical.size()));
			f2 = sampler.sampleFormula();
			bs.add(f1.combineWithOr(f2));
		}		
		return bs;
	}

	/**
	 * For a signature {a1,...,an} creates num different canonical and mutually exclusive
	 * formulas of the form a1a2a3, a1a2-a3, a1-a2a3, etc.
	 * @param num the number of formulas to be generated.
	 * @param sig the signature (guaranteed to be large enough)
	 * @return a list of canonical formulas.
	 */
	private List<PlFormula> getCanonicalFormulas(int num, PlSignature sig){
		List<PlFormula> canonical = new ArrayList<PlFormula>();
		Iterator<Proposition> it = sig.iterator();
		if(num == 1){
			canonical.add(it.next());
		}else if(num % 2 == 1){
			PlSignature sig2 = new PlSignature(sig);
			Proposition p = it.next();
			sig2.remove(p);
			List<PlFormula> tmp = this.getCanonicalFormulas(num-1, sig2);			
			canonical.add(tmp.iterator().next().combineWithAnd(new Negation(p)));
			for(PlFormula t: tmp){
				canonical.add(t.combineWithAnd(p));
			}		
		}else if(num == 2){
			Proposition p = it.next();
			canonical.add(p);
			canonical.add(new Negation(p));
		}else{
			PlSignature sig2 = new PlSignature(sig);
			Proposition p = it.next();
			sig2.remove(p);
			List<PlFormula> tmp = this.getCanonicalFormulas(num/2, sig2);
			for(PlFormula t: tmp){
				canonical.add(t.combineWithAnd(p));
				canonical.add(t.combineWithAnd(new Negation(p)));
			}
		}
		return canonical;
	}
}
