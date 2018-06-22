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
import net.sf.tweety.logics.fol.syntax.FolFormula;


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
	private Set<Pair<Interpretation<FolFormula>,Interpretation<FolFormula>>> tuples;
	
	/**
	 * Constructs a new accessibility relation.
	 * @param tuples	the accessibility relation 	
	 */
	public AccessibilityRelation(Set<Pair<Interpretation<FolFormula>,Interpretation<FolFormula>>> tuples) {
		this.tuples = tuples;
	}
	
	/**
	 * Returns all interpretations of the accessibility relation.
	 * @return interpretations	set of all interpretations
	 */
	public Set<Interpretation<FolFormula>> getNodes(){
		Set<Interpretation<FolFormula>> interpretations = new HashSet<Interpretation<FolFormula>>();
		for(Pair<Interpretation<FolFormula>,Interpretation<FolFormula>> p: this.tuples){
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
	public Set<Interpretation<FolFormula>> getSuccessors(Interpretation<FolFormula> i){
		Set<Interpretation<FolFormula>> successors = new HashSet<Interpretation<FolFormula>>();
		for(Pair<Interpretation<FolFormula>,Interpretation<FolFormula>> relation: this.tuples)
			if(relation.getFirst().equals(i))
				successors.add(relation.getSecond());
		return successors;
	}
	
	public String toString() {
		return tuples.toString();
	}
}
