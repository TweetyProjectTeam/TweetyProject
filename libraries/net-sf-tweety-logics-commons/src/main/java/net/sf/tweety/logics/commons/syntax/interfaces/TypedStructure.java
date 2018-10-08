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
import net.sf.tweety.logics.commons.syntax.Sort;

/**
 * This interface defines method which are given by every TypedStructure like a
 * Predicate or an Functor. It has an name, an arity (argument count) and a list
 * of Sort representing the types for the different arguments.
 * 
 * @author Tim Janus
 */
public interface TypedStructure {
	
	/** @return the unique name of the structure */
	String getName();
	
	/**
	 * Changes the name of the Structure
	 * @param name	The new name of the structure
	 * @throws LanguageException if the new name is illegal in the language.
	 */
	void setName(String name) throws LanguageException;
	
	/** @return the arity of this structure */
	int getArity();
	
	/** 
	 * @return 	An unmodifiable list which length equals the 
	 * 			arity of the predicate if the structure isComplete().
	 */
	List<Sort> getArgumentTypes();
	
	/**
	 * Adds the given Sort as argument type to the typed Structure
	 * @param argType	The Sort descibing the argument type
	 * @throws LanguageException	
	 */
	void addArgumentType(Sort argType) throws LanguageException;
	
	/**
	 * Removes the argument type at the specified index
	 * @param index	The index
	 * @return A reference to the sort that has been remove or null if no sort has been removed.
	 * @throws LanguageException, IndexOutOfBoundsException
	 */
	Sort removeArgumentType(int index) throws LanguageException;
	
	/**
	 * Removes the given Sort from the list of argument types
	 * @param argType	The Sort which is removed
	 * @return	true if the Sort exists in the list of argument types
	 * 			and is successfully removed, false otherwise.
	 * @throws LanguageException
	 */
	boolean removeArgumentType(Sort argType) throws LanguageException;
	
	/**
	 * @return true if at least one sort for an argument is not "Thing".
	 */
	boolean isTyped();
	
	/**
	 * @return 	true if the arity of this structure matches the length of it's arguments,
	 * 			false if the arity is bigger than the length of it's arguments.
	 */
	boolean isComplete();
	
	/**
	 * Creates a deep copy of this object
	 * @return	A deep copy of this object
	 */
	TypedStructure clone();
}
