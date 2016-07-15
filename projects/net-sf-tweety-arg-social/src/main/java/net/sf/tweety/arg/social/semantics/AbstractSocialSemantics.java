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
package net.sf.tweety.arg.social.semantics;

import java.util.Collection;
import java.util.Comparator;

/**
 * This is the abstract ancestor of concrete semantics for social
 * abstract argumentation frameworks, cf. [Leite, Martins; IJCAI 2011, Def. 3]
 * @author Matthias Thimm
 *
 * @param <L> The set used for valuations 
 */
public abstract class AbstractSocialSemantics<L extends Object> implements Comparator<L> {
	
	/**
	 * Returns the bottom element of this semantics.
	 * @return the bottom element of this semantics.
	 */
	public abstract L bottomElement();
	
	/**
	 * Returns the top element of this semantics.
	 * @return the top element of this semantics.
	 */
	public abstract L topElement();
	
	/**
	 * The vote aggregation function
	 * @param pos the number of positive votes
	 * @param neg the number of negative votes
	 * @return the social support aggregated from the votes.
	 */
	public abstract L supp(int pos, int neg);
	
	/**
	 * The AND-operation on L in this framework
	 * @param arg1 some value in L
	 * @param arg2 some value in L
	 * @return the AND of the both values
	 */
	public abstract L and(L arg1, L arg2);
	
	/**
	 * The OR-operation on L in this framework
	 * @param arg1 some value in L
	 * @param arg2 some value in L
	 * @return the OR of the both values
	 */
	public abstract L or(L arg1, L arg2);
	
	/**
	 * The NEG-operation on L in this framework
	 * @param arg some value in L
	 * @return the negation of the given value.
	 */
	public abstract L neg(L arg);
	
	/** The AND-operation on all given arguments
	 * @param arg some values in L
	 * @return The AND of all given arguments
	 */
	public L and(Collection<L> arg){
		L result = null;
		for(L l: arg){
			if(result == null) result = l;
			else l = this.and(l,result);
		}
		return result;
	}
	
	/** The OR-operation on all given arguments
	 * @param arg some values in L
	 * @return The OR of all given arguments
	 */
	public L or(Collection<L> arg){
		L result = null;
		for(L l: arg){
			if(result == null) result = l;
			else l = this.or(l,result);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.social.semantics.AbstractSocialSemantics#compare(java.lang.Double, java.lang.Double)
	 */
	public abstract int compare(Double arg0, Double arg1);
}
