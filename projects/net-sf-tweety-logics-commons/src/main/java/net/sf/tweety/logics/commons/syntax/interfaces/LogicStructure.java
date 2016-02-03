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
package net.sf.tweety.logics.commons.syntax.interfaces;

import java.util.Set;

/**
 * This interface captures the common functionalities of formulas,
 * sorts and terms. It allows to query for saved terms by using the
 * type of the term it also forces sub classes to implement the toString()
 * and clone() methods.
 * 
 * @author Tim Janus
 * @author Matthias Thimm
 */
public interface LogicStructure {
	/** @return a set containing all terms of this logical structure */
	Set<Term<?>> getTerms();
	
	/** 
	 * Processes the set containing all terms of type C. This method uses
	 * the equals method of the given Class and therefore does not add terms
	 * which are sub classes of type C to the set.
	 * 
	 * @param cls	The Class structure containing type information about the
	 * 				searched term
	 * @return		A set containing all terms of type C of this logical structure
	 */
	<C extends Term<?>> Set<C> getTerms(Class<C> cls);
	
	/**
	 * Checks if this logical structure contains at least one term of type C. 
	 * This method is a shortcut for !getTerms(TermImplementation.class).isEmpty().
	 * 
	 * @param cls	The class structure representing the type C of the term.
	 * @return		True if this logical structure contains at least one term
	 * 				of type C or false otherwise.
	 */
	<C extends Term<?>> boolean containsTermsOfType(Class<C> cls);
}
