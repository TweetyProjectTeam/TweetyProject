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

import java.util.List;
import java.util.Set;

/**
 * This interfaces describes associative formulas like a disjunction or
 * a conjunction.
 * 
 * @author Tim Janus
 */
public interface AssociativeFormula<T extends SimpleLogicalFormula> 
	extends SimpleLogicalFormula, List<T> {
	
	/** @return all the formulas saved as childs in the associative formula */
	List<T> getFormulas();
	
	/**
	 * Process the formulas of type C that are children of this associative
	 * formula
	 * @param cls	the class structure defining the type of formulas which
	 * 				are searched.
	 * @return		A set of formulas of type C which are members of the associative formula
	 */
	<C extends SimpleLogicalFormula> Set<C> getFormulas(Class<C> cls);
}
