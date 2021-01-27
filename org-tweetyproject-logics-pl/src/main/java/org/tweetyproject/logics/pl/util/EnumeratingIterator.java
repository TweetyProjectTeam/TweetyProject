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

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import org.tweetyproject.commons.BeliefSetIterator;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * This sampler enumerates all possible propositional belief bases of the given signature.
 * It does so by taking all subsets of the set of interpretations as the models of some
 * formula and combines all these formulas in all ways.
 * 
 * @author Matthias Thimm
 *
 */
/**
 * @author mthimm
 *
 */
public class EnumeratingIterator implements BeliefSetIterator<PlFormula,PlBeliefSet> {

	/** The current length */
	private int currentLength = -1;
	
	/** All possible worlds */
	private List<PossibleWorld> allWorlds;
	/** The current indices of the worlds that construct the formulas. */
	private BitSet indices;
	
	/**
	 * The used signature.
	 */
	private PlSignature signature;
	
	
	/**
	 * Whether semantical variants of the same formula
	 * 	should be considered as distinct (="false")
	 */
	private boolean joinSemanticVariants;
	
	/**
	 * Creates a new sampler for the given signature
	 * @param signature some signature
	 */
	public EnumeratingIterator(PlSignature signature) {
		this(signature,false);
	}
	
	/**
	 * Creates a new sampler for the given signature
	 * @param signature some signature
	 * @param joinSemanticVariants whether semantical variants of the same formula
	 * 	should be considered as distinct (="false").
	 */
	public EnumeratingIterator(PlSignature signature, boolean joinSemanticVariants) {
		this.signature = signature;
		this.joinSemanticVariants = joinSemanticVariants;
		this.currentLength = 0;
		this.allWorlds = new LinkedList<PossibleWorld>(PossibleWorld.getAllPossibleWorlds(this.signature));
		this.indices = new BitSet(this.currentLength * this.allWorlds.size());
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSetIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !signature.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSetIterator#next()
	 */
	@Override
	public PlBeliefSet next(){
		if(this.indices == null){
			this.currentLength++;
			this.indices = new BitSet(this.currentLength * this.allWorlds.size());
		}
		Proposition a = this.signature.iterator().next();
		PlFormula contr = a.combineWithAnd(new Negation(a));		
		PlBeliefSet result = new PlBeliefSet();
		int size = this.allWorlds.size();		
		for(int i = 0; i < this.currentLength; i++){
			// we have to ensure that appearing formulas are not syntactically equivalent.
			PlFormula p;
			if(this.joinSemanticVariants)
				p = contr.clone();
			else
				p = contr.combineWithAnd(new Proposition("XSA"+i));
			for(int j = 0; j < size; j++){
				if(this.indices.get(i*size + j))
					p = p.combineWithOr(this.allWorlds.get(j).getCompleteConjunction(this.signature));				
			}
			result.add(p);
		}
		this.indices = this.increment(this.indices);
		return result;
	}
	
	/** Increments the given bit set, returns null
	 * if an overflow happens.
	 * @param bitSet some bit set.
	 * @return the incremented bit set
	 */
	private BitSet increment(BitSet bitSet){
		if(bitSet.size() == 0)
			return null;
		boolean carry = true, tmp;
		int i = 0;
		while(carry){
			tmp = carry;
			carry = carry && bitSet.get(i);
			bitSet.set(i, tmp ^ bitSet.get(i));
			i++;
		}
		if(this.currentLength * this.allWorlds.size() < i)
			return null;
		return bitSet;
	}
}
