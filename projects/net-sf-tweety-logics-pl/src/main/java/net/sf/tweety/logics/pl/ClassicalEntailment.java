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
package net.sf.tweety.logics.pl;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements classical entailment for propositional logic.
 * 
 * @author Matthias Thimm
 */
public class ClassicalEntailment extends EntailmentRelation<PropositionalFormula> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#entails(java.util.Collection, net.sf.tweety.Formula)
	 */
	@Override
	public boolean entails(Collection<PropositionalFormula> formulas, PropositionalFormula formula) {
		PropositionalSignature signature = new PropositionalSignature();
		for(PropositionalFormula f: formulas)
			signature.addAll(f.getAtoms());
		signature.addAll(formula.getAtoms());
		Set<PossibleWorld> possibleWorlds = PossibleWorld.getAllPossibleWorlds(signature);
		for(PossibleWorld w: possibleWorlds)
			if(w.satisfies(formulas))
				if(!w.satisfies(formula))
					return false;
		return true;		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PropositionalFormula> formulas) {
		return !this.entails(formulas, new Contradiction());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#getWitness(java.util.Collection)
	 */
	@Override
	public Interpretation getWitness(Collection<PropositionalFormula> formulas) {
		PropositionalSignature signature = new PropositionalSignature();
		for(PropositionalFormula f: formulas)
			signature.addAll(f.getAtoms());		
		Set<PossibleWorld> possibleWorlds = PossibleWorld.getAllPossibleWorlds(signature);
		for(PossibleWorld w: possibleWorlds)
			if(w.satisfies(formulas))
				return w;
		return null;	
	}

}
