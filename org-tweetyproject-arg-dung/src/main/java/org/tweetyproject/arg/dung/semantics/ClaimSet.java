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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.Claim;
import org.tweetyproject.arg.dung.syntax.ClaimArgument;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.AbstractInterpretation;
/**
 * a set of claims
 * @author Sebastian Franke
 *
 */
public class ClaimSet extends AbstractInterpretation<ClaimBasedTheory,ClaimArgument>  implements Collection<ClaimArgument>, Comparable<ClaimSet>{

	/**
	 * constructor
	 */
	public ClaimSet() {
		this.claims = new HashSet<ClaimArgument>();
	}
	/**
	 * constructor
	 * @param c a claimset
	 */
	public ClaimSet(ClaimSet c) {
		this.claims = new HashSet<ClaimArgument>(c);
	}
	/**
	 * constructor
	 * @param c claimset
	 */
	public ClaimSet(Set<ClaimArgument> c) {
		this.claims = new HashSet<ClaimArgument>(c);
	}
	/**
	 * the claims
	 */
	HashSet<ClaimArgument> claims;
	/**
	 * 
	 * @return the claims
	 */
	public HashSet<ClaimArgument> getClaims(){
		return this.claims;
	}
	@Override
	public int compareTo(ClaimSet o) {
		int result = o.claims.equals(this.claims) ? 0 : 1;
		return result;
	}

	@Override
	public int size() {
		return this.claims.size();
	}

	@Override
	public boolean isEmpty() {
		return this.claims.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if(o instanceof Claim)
			return this.claims.contains(o);
		else if(o instanceof ClaimArgument)
			return this.claims.contains((((ClaimArgument)o).getClaim()));
		else
			return this.claims.contains(o);
	}

	@Override
	public Iterator<ClaimArgument> iterator() {
		// TODO Auto-generated method stub
		return this.claims.iterator();
	}

	@Override
	public Object[] toArray() {
		
		return this.claims.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {		
		return a;
	}

	@Override
	public boolean add(ClaimArgument o) {
			return this.claims.add(o);
	}
	


	@Override
	public boolean remove(Object o) {

		return this.claims.remove((((ClaimArgument)o).getClaim()));

	}

	@Override
	public boolean containsAll(Collection<?> o) {
		return this.claims.containsAll(o);
	}

	@Override
	public boolean addAll(Collection<? extends ClaimArgument> c) {
		for(ClaimArgument a : c)
			this.claims.add(a);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		this.claims.removeAll(c);
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		this.claims.retainAll(c);
		return true;
	}

	@Override
	public void clear() {
		this.claims = new HashSet<ClaimArgument>();
		
	}


	@Override
	public String toString() {
		String result = new String();
		result += "[";
		boolean isFirst = false;
		for(ClaimArgument c : this.claims) {
			if(isFirst == true)
				result += ", ";
			result += c.toString();
			isFirst = true;
			
		}
			
		result += "]";
		return result;
	}

	@Override
	public boolean satisfies(ClaimArgument formula) throws IllegalArgumentException {
		return this.getArgumentsOfStatus(ArgumentStatus.IN).contains(formula);
	}



	/**
	 * 
	 * @param status like IN or OUT
	 * @return the claimset with the given status
	 */
	public ClaimSet getArgumentsOfStatus(ArgumentStatus status) {
		if(status.equals(ArgumentStatus.IN)) return this;
		throw new IllegalArgumentException("Arguments of status different from \"IN\" cannot be determined from an extension alone");
	}


	@Override
	public boolean satisfies(ClaimBasedTheory beliefBase) throws IllegalArgumentException {
		throw new IllegalArgumentException("Satisfaction of belief bases by extensions is undefined.");
	}

}
