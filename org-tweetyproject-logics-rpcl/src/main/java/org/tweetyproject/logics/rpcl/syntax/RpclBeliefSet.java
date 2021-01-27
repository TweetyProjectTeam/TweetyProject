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
package org.tweetyproject.logics.rpcl.syntax;

import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.*;


/**
 * This class models a belief set on relational probabilistic conditional logic, i.e. a set of
 * relational probabilistic conditionals.
 * 
 * @author Matthias Thimm
 *
 */
public class RpclBeliefSet extends BeliefSet<RelationalProbabilisticConditional,FolSignature> {

	/**
	 * Creates a new (empty) conditional belief set.
	 */
	public RpclBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new conditional belief set with the given collection of
	 * conditionals.
	 * @param conditionals a collection of conditionals.
	 */
	public RpclBeliefSet(Collection<? extends RelationalProbabilisticConditional> conditionals){
		super(conditionals);
	}
	
	/**
	 * Determines the equivalence classes of this belief set wrt. the given signature
	 * (which must be a super signature of this set's signature), i.e. all sets S of constants
	 * such that if one replaces one element of S with another one from S and vice versa the belief
	 * set remains the same.
	 * @param signature a fol signature (which must be a super signature of this set's signature) 
	 * @return a set of sets of constants.
	 */
	public Set<Set<Constant>> getEquivalenceClasses(FolSignature signature){
		if(!this.getMinimalSignature().isSubSignature(signature))
			throw new IllegalArgumentException("Signature must be super-signature of this set's formulas' signature.");
		Set<Set<Constant>> result = new HashSet<Set<Constant>>();
		Stack<Constant> allconstants = new Stack<Constant>();
		for(Term<?> t: signature.getConstants())
			allconstants.add((Constant)t);
		while(!allconstants.isEmpty()){
			Constant c = allconstants.pop();
			boolean match = false;
			for(Set<Constant> s: result){
				// each class contains at least one element
				// (bear in mind that equivalence is transitive)
				if(this.areEquivalent(c, s.iterator().next())){
					s.add(c);
					match = true;
					break;
				}					
			}
			// no previous class matched the constant
			// -> create new class
			if(!match){
				Set<Constant> s = new HashSet<Constant>();			
				s.add(c);
				result.add(s);
			}
		}		
		return result;
	}
	
	/**
	 * Determines the equivalence classes of this belief set, i.e. all sets S of constants
	 * such that if one replaces one element of S with another one from S and vice versa the belief
	 * set remains the same.
	 * @return a set of sets of constants.
	 */
	public Set<Set<Constant>> getEquivalenceClasses(){
		return this.getEquivalenceClasses((FolSignature)this.getMinimalSignature());
	}
	
	/**
	 * Checks whether the two given constants are equivalent wrt. this
	 * knowledge base. Two constants are equivalent if replacing each occurence
	 * of one constant with the other does not alter the belief set.
	 * @param a a constant
	 * @param b a constant
	 * @return "true" if both constants are equivalent
	 */
	public boolean areEquivalent(Constant a, Constant b){
		// If they belong to different sorts they cannot be equivalent
		if(!a.getSort().equals(b.getSort()))
			return false;
		// Each constant not appearing in he conditionals belongs to the same equivalence class
		Set<Constant> appearingConstants = new HashSet<Constant>();
		for(RelationalProbabilisticConditional c: this)
			appearingConstants.addAll(c.getTerms(Constant.class));
		if(!appearingConstants.contains(a) && !appearingConstants.contains(b))
			return true;
		if(!appearingConstants.contains(a) || !appearingConstants.contains(b))
			return false;
		// both constants appear in this belief set, so try replace them
		if(this.equals(this.exchange(a, b)))
			return true;
		return false;
	}
	
	/**
	 * Exchanges every occurence of "a" by "b" and vice versa. 
	 * @param a a term.
	 * @param b a term.
	 * @return a belief set with "a" and "b" exchanged.
	 */
	public RpclBeliefSet exchange(Term<?> a, Term<?> b){
		RpclBeliefSet bs = new RpclBeliefSet();
		for(RelationalProbabilisticConditional r: this)
			bs.add((RelationalProbabilisticConditional)r.exchange(a, b));
		return bs;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.BeliefSet#getSignature()
	 */
	@Override
	public Signature getMinimalSignature() {
		FolSignature sig = new FolSignature();
		for(RelationalProbabilisticConditional c: this){
			sig.addAll(c.getTerms(Constant.class));
			sig.addAll(c.getFunctors());
			sig.addAll(c.getPredicates());			
		}
		return sig;
	}

	@Override
	protected FolSignature instantiateSignature() {
		return new FolSignature();
	}	
}
