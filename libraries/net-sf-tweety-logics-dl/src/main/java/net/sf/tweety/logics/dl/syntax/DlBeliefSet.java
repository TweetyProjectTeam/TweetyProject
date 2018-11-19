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

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Signature;


/**
 * This class models a knowledge base for description logics.
 * 
 * <br> A knowledge base for description logic distinguishes between:
 * <ul>
 * <li>the TBox (terminological axioms, i.e. properties of and relations between concepts). 
 * In the description logic ALC, the TBox consists of equivalence axioms (GCUs). 
 * <li>and the ABox (assertional axioms, i.e. ground assertions about individuals and what concepts and roles they belong to).
 * </ul>
 * 
 * @author Anna Gessler
 */
public class DlBeliefSet extends BeliefSet<DlAxiom> {
	/**
	 * Creates a new and empty description logics knowledge base.
	 */
	public DlBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new description logics knowledge base with the given set of axioms.
	 * @param formulas
	 */
	public DlBeliefSet(Set<DlAxiom> formulas){
		super(formulas);
	}

	@Override
	public Signature getSignature() {
		DlSignature sig = new DlSignature();
		sig.addAll(this);
		return sig;
	}
	
	/**
	 * Returns the TBox section of the knowledge.
	 * 
	 * @return a set of TBox formulas
	 */
	public Set<DlAxiom> getTBox() {
		Set<DlAxiom> TBox = new HashSet<DlAxiom>();
		for (DlAxiom f : this) {
			if (f instanceof EquivalenceAxiom) 
				TBox.add(f);
		}
		return TBox;	
	}
	
	/**
	 * Returns the ABox section of the knowledge.
	 * 
	 * @return a set of ABox formulas
	 */
	public Set<DlAxiom> getABox() {
		Set<DlAxiom> ABox = new HashSet<DlAxiom>();
		for (DlAxiom f : this) {
			if (f instanceof ConceptAssertion || f instanceof RoleAssertion) 
				ABox.add(f);
		}
		return ABox;	
	}
	
}
