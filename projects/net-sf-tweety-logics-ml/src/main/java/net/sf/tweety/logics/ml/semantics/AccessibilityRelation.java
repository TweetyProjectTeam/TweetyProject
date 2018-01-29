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
package net.sf.tweety.logics.ml.semantics;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.commons.util.*;


/**
 * This class models an accessibility relation for Kripke
 * models.
 * 
 * @author Matthias Thimm
 * @author Anna Gessler
 */
public class AccessibilityRelation {

	/**
	 * The actual relation
	 */
	private Set<Pair<Interpretation,Interpretation>> tuples;
	
	/**
	 * Constructs a new accessibility relation.
	 * @param tuples	the accessibility relation 	
	 */
	public AccessibilityRelation(Set<Pair<Interpretation,Interpretation>> tuples) {
		this.tuples = tuples;
	}
	
	/**
	 * Returns all interpretations of the accessibility relation.
	 * @return interpretations	set of all interpretations
	 */
	public Set<Interpretation> getNodes(){
		Set<Interpretation> interpretations = new HashSet<Interpretation>();
		for(Pair<Interpretation,Interpretation> p: this.tuples){
			interpretations.add(p.getFirst());
			interpretations.add(p.getSecond());
		}
		return interpretations;
	}
	
	/**
	 * Returns all interpretations that are accessible to a given interpretation i.
	 * @param  i			an interpretation (possible world)
	 * @return successors 	set of successors of i
	 */
	public Set<Interpretation> getSuccessors(Interpretation i){
		Set<Interpretation> successors = new HashSet<Interpretation>();
		for(Pair<Interpretation,Interpretation> relation: this.tuples)
			if(relation.getFirst().equals(i))
				successors.add(relation.getSecond());
		return successors;
	}
	
	public String toString() {
		return tuples.toString();
	}
}
