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
package net.sf.tweety.beliefdynamics;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This class implements the default multiple base expansion operator, ie. an operator
 * that returns the union of the sets of formulas
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas that this operator works on.
 */
public class DefaultMultipleBaseExpansionOperator<T extends Formula> extends MultipleBaseExpansionOperator<T> {
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseExpansionOperator#expand(java.util.Collection, java.util.Collection)
	 */
	public Collection<T> expand(Collection<T> base, Collection<T> formulas){
		Set<T> expandedCollection = new HashSet<T>();
		expandedCollection.addAll(base);
		expandedCollection.addAll(formulas);
		return expandedCollection;		
	}
}
