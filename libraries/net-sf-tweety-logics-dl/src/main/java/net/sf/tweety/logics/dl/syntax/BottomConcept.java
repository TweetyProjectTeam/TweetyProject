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
package net.sf.tweety.logics.dl.syntax;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.Predicate;

/**
 * This class models the bottom concept (empty concept) in description logics.
 * No individual of the domain is an instance of the bottom concept.
 * 
 * @author Anna Gessler
 *
 */
public class BottomConcept extends ComplexConcept  {
	
	/**
	 * Creates a new BottomConcept.
	 */
	public BottomConcept() {	
	}
	
	@Override
	public ComplexConcept collapseAssociativeFormulas() {
		return this;
	}

	@Override
	public Set<Predicate> getPredicates() {
		return new HashSet<Predicate>();
	}
	
	@Override
	public BottomConcept clone() {
		return new BottomConcept();
	}

	@Override
	public String toString() {
		return "*bottom*";
	}
	
	@Override
	public boolean isLiteral() {
		//Note: In the DL library (here), TopConcept and BottomConcept are not seen as literals.
		//However, in the FOL library, Tautology and Contradiction are seen as literals (see net.sf.tweety.logics.fol.syntax.SpecialFormula).
		return false;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;		
		if (obj == null || getClass() != obj.getClass())
			return false;		
		return true;
	}
	
	@Override
	public int hashCode(){
		return 3;
	}

	@Override
	public DlSignature getSignature() {
		return new DlSignature();
	}
}
