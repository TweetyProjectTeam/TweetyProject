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
package net.sf.tweety.arg.dung.semantics;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import net.sf.tweety.arg.dung.syntax.Argument;

/**
 * This abstract class is the common ancestor for semantical approaches to argument ranking, i.e. relations
 * that allow a more fine-grained comparison by e.g. utilizing numerical values for arguments.
 * 
 * @author Matthias Thimm
 */
public abstract class ArgumentRanking extends AbstractArgumentationInterpretation implements Comparator<Argument> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Argument arg0, Argument arg1) {
		// Recall that if arg0 is less than arg1 (arg0 < arg1) then
		// arg0 is more preferred than arg1
		if(this.isStrictlyLessAcceptableThan(arg0, arg1))
			return 1;
		if(this.isStrictlyLessAcceptableThan(arg1, arg0))
			return -1;
		return 0;
	}	

	/**
	 * Returns "true" iff a is strictly more acceptable than b, i.e. a < b
	 * (least arguments are maximally acceptable arguments)
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff a is strictly more acceptable than b
	 */
	public boolean isStrictlyMoreAcceptableThan(Argument a, Argument b){
		return !this.isStrictlyLessOrEquallyAcceptableThan(a, b);
	}
	
	/**
	 * Returns "true" iff a is strictly less acceptable than b, i.e. a > b
	 * (least arguments are maximally acceptable arguments)
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff a is strictly less acceptable than b
	 */
	public boolean isStrictlyLessAcceptableThan(Argument a, Argument b){
		return !this.isStrictlyLessOrEquallyAcceptableThan(b, a);
	}
	
	/**
	 * Returns "true" iff a is strictly more acceptable than b or a is equally
	 * acceptable as b or a and b are not comparable, i.e. a <= b (or a ~ b)
	 * (least arguments are maximally acceptable arguments)
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff a is strictly more acceptable than b or a is equally
	 * acceptable as b or a and b are not comparable
	 */
	public boolean isStrictlyMoreOrEquallyAcceptableThan(Argument a, Argument b){
		return this.isStrictlyLessOrEquallyAcceptableThan(b, a) || this.isEquallyAcceptableThan(a, b);
	}
	
	/**
	 * Returns "true" iff a is equally acceptable as b or a and b are not comparable,
	 * i.e. a = b (or a ~ b)
	 * (least arguments are maximally acceptable arguments)
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff a is equally acceptable as b or a and b are not comparable
	 */
	public boolean isEquallyAcceptableThan(Argument a, Argument b){
		return this.isStrictlyLessOrEquallyAcceptableThan(a, b) && this.isStrictlyLessOrEquallyAcceptableThan(b, a);
	}
	
	/**
	 * Returns the set of all arguments a from the given
	 * set that are maximally accepted, i.e.
	 * where there is no other argument that is strictly more acceptable.
	 * @param args a set of arguments
	 * @return the set of all arguments a that are maximally accepted
	 */
	public Collection<Argument> getMaximallyAcceptedArguments(Collection<Argument> args){
		Collection<Argument> result = new HashSet<Argument>();
		for(Argument a: args){
			boolean isMaximal = true;
			for(Argument b: args)
				if(this.isStrictlyMoreAcceptableThan(b, a)){
					isMaximal = false;
					break;
				}
			if(isMaximal)
				result.add(a);
		}
		return result;
	}
	
	/**
	 * Returns the set of all arguments a from the given
	 * set that are minimally accepted, i.e.
	 * where there is no other argument that is strictly less acceptable.
	 * @param args a set of arguments
	 * @return the set of all arguments a that are minimalle accepted
	 */
	public Collection<Argument> getMinimallyAcceptedArguments(Collection<Argument> args){
		Collection<Argument> result = new HashSet<Argument>();
		for(Argument a: args){
			boolean isMinimal = true;
			for(Argument b: args)
				if(this.isStrictlyLessAcceptableThan(b, a)){
					isMinimal = false;
					break;
				}
			if(isMinimal)
				result.add(a);
		}
		return result;
	}
	
	/**
	 * Checks whether this ranking is equivalent to the other one wrt. the given set of arguments.
	 * @param other some ranking
	 * @param args some arguments
	 * @return "true" if both rankings are equivalent.
	 */
	public boolean isEquivalent(ArgumentRanking other, Collection<Argument> args) {
		for(Argument a: args)
			for(Argument b: args) {
				if(this.isStrictlyLessOrEquallyAcceptableThan(a, b) && !other.isStrictlyLessOrEquallyAcceptableThan(a, b))
					return false;
				if(!this.isStrictlyLessOrEquallyAcceptableThan(a, b) && other.isStrictlyLessOrEquallyAcceptableThan(a, b))
					return false;
			}
		return true;
	}
	/**
	 * Returns "true" iff a is strictly less acceptable than b or a is equally
	 * acceptable as b or a and b are not comparable, i.e. a >= b (or a ~ b)
	 * (least arguments are maximally acceptable arguments)
	 * @param a some argument
	 * @param b some argument
	 * @return "true" iff a is strictly less acceptable than b or a is equally
	 * acceptable as b or a and b are not comparable
	 */
	public abstract boolean isStrictlyLessOrEquallyAcceptableThan(Argument a, Argument b);
}
