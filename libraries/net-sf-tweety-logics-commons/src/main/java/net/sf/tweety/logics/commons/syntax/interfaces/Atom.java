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
package net.sf.tweety.logics.commons.syntax.interfaces;

import java.util.List;

import net.sf.tweety.logics.commons.error.LanguageException;
import net.sf.tweety.logics.commons.syntax.Predicate;

/**
 * An atomic language construct, linked to its predicate
 * 
 * @author Tim Janus
 */
public interface Atom extends SimpleLogicalFormula  {
	
	/**
	 * An enum containing the different return values of the
	 * setPredicate() method of Atom.
	 * @author Tim Janus
	 */
	public static enum RETURN_SET_PREDICATE {
		RSP_SUCCESS,
		RSP_INCOMPLETE,
		RSP_TRUNCATED,
		RSP_CLEARED
	}
	
	/** @return	the name of the predicate */
	String getName();
	
	/** @return the predicate of the atom */
	Predicate getPredicate();
	
	/**
	 * Changes the predicate of the atom. Given an old Predicate po and a new predicate
	 * pn with their list of arguments types at(po) and at(pn) and the arguments of this
	 * Atom: arg(this) this method distinguishes between three cases:
	 * 1. 	The predicate only differ in the names, returning RSP_SUCCESS
	 * 2. 	The old predicates argument types is a sub-list of the new argument types then
	 * 		the method returns RSP_INCOMPLETE and the atoms isComplete() method returns false
	 * 3. 	The new predicates argument types is a sub-list of the old argument types then
	 * 		the method returns RSP_TRUNCATED and the arguments of this atom are truncated too
	 * 		and isComplete() returns true.
	 * 4. 	The old and new predicates' argument types differ then the list of arguments of the
	 * 		atom get cleared and isComplete() returns false.
	 * 
	 * @param predicate some predicate
	 * @return	Depends on the cases described above:
	 * 			1. RSP_SUCCESS
	 * 			2. RSP_INCOMPLETE
	 * 			3. RSP_TRUNCATED
	 * 			4. RSP_CLEARED
	 * 
	 */
	RETURN_SET_PREDICATE setPredicate(Predicate predicate);
	
	/**
	 * Adds an argument to the atom's argument list
	 * @param arg	The next argument
	 * @throws LanguageException	If the language does not support
	 * 								arguments for their constructs.
	 */
	void addArgument(Term<?> arg) throws LanguageException;
	
	/** @return A list containing all the arguments of this specific atom */
	List<? extends Term<?>> getArguments();
	
	/** @return true if the size of the argument list is equal to the arity of the predicate */
	boolean isComplete();
	
	/**
	 * Gives common implementations for methods shared among atoms of different types.
	 * @author Tim Janus
	 */
	public static class AtomImpl {
		static public RETURN_SET_PREDICATE implSetPredicate(Predicate old, 
				Predicate newer, List<Term<?>> arguments) {
			
			// Handles border cases: setting or changing a null value:
			if(old == null) {
				return RETURN_SET_PREDICATE.RSP_SUCCESS;
			} else if(newer == null) {
				if(old.getArgumentTypes().size() > 0) {
					arguments.clear();
					return RETURN_SET_PREDICATE.RSP_TRUNCATED;
				} else {
					return RETURN_SET_PREDICATE.RSP_SUCCESS;
				}
			}
			
			// same argument types... setting predicate is successful no operations required:
			if(old.getArgumentTypes().equals(newer.getArgumentTypes())) {
				return RETURN_SET_PREDICATE.RSP_SUCCESS;
			} else {
				// if the length of the argument types is the same then they differ:
				boolean differs = old.getArgumentTypes().size() == newer.getArgumentTypes().size();
				
				if(!differs) {
					// test if the arguments need to be truncated or are incomplete:
					boolean truncate = old.getArgumentTypes().size() > newer.getArgumentTypes().size();
					int length = truncate ? newer.getArgumentTypes().size() : old.getArgumentTypes().size();
					
					// check for different sorts on the length first argument types
					for(int i=0; i<length; ++i) {
						if(!(old.getArgumentTypes().get(i).equals(newer.getArgumentTypes().get(i)))) {
							differs = true;
							break;
						}
					}
					
					// if the common sublist of argument types does not differ:
					if(!differs) {
						// either truncate the arguments and return truncated
						if(truncate) {
							while(arguments.size() > newer.getArgumentTypes().size()) {
								arguments.remove(arguments.size()-1);
							}
							return RETURN_SET_PREDICATE.RSP_TRUNCATED;
						} else {
							// or do nothing and return incompete
							return RETURN_SET_PREDICATE.RSP_INCOMPLETE;
						}
					}
				}
				
				// the arguments types differ in a way that is not repairable
				arguments.clear();
				return RETURN_SET_PREDICATE.RSP_CLEARED;
			}
		}
	}
}
