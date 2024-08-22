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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.sat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * enumerates all models naivly
 * @author Sebastian Franke
 *
 */
public class SimpleModelEnumerator implements ModelProvider<PlFormula, PlBeliefSet, InterpretationSet<Proposition,PlBeliefSet,PlFormula>> {

	/**
	 * 
	 * @param formulas Pl Formulas to have all models enumerated
	 * @return all models satifying the formulas
	 */
	@SuppressWarnings("unchecked")
	public Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> getModels(Collection<PlFormula> formulas) {
		Collection<Disjunction> clauses = new HashSet<Disjunction>();
		// check if we are working with CNF; if not, convert
		for(PlFormula f: formulas) {
			if(f.isClause())
				clauses.add((Disjunction)f);
			else {
	 			f = f.toCnf();
				for(PlFormula c: ((Conjunction)f)) 
					clauses.add((Disjunction)c);				
			}
		}

		// keeps track of the atoms which have not assigned a truth value yet
		HashSet<Proposition> remaining_atoms = new HashSet<Proposition>();
		remaining_atoms.addAll(PlSignature.getSignature(clauses).toCollection());
		// for each literal on the "sel_literals" stack, the next stack says whether
		// this literal was selected to have its truth value (true),
		// or derived using unit propagation (false)
		Set<PossibleWorld> allModels = new HashSet<PossibleWorld>();
		Set<Proposition> allLiterals = new HashSet<Proposition>();

		for(Disjunction f : clauses) {
			allLiterals.addAll(((Set<Proposition>)(Set<?>) f.getAtoms()));
		}
		Set<Set<Proposition>> propPowerSet = (Set<Set<Proposition>>) SetTools.powerSet(allLiterals);

		for(Set<Proposition> model : propPowerSet) {
			PossibleWorld possWorld = new PossibleWorld(model);
			allModels.add(possWorld);
		}		
		for(PlFormula f : formulas)
			allModels.addAll(f.getModels());
		Set<InterpretationSet<Proposition, PlBeliefSet, PlFormula>> satModels = new HashSet<InterpretationSet<Proposition, PlBeliefSet, PlFormula>> ();
		for(PossibleWorld w : allModels) {
			if(w.satisfies(formulas)) {
				satModels.add(w);
			}
		}
		return satModels;
	}


	@Override
	public Collection<InterpretationSet<Proposition, PlBeliefSet, PlFormula>> getModels(PlBeliefSet bbase) {
		Set<PlFormula> form = new HashSet<PlFormula>();
		for (PlFormula f : bbase)
			form.add(f);
		return this.getModels(form);
	}

	@Override
	public InterpretationSet<Proposition, PlBeliefSet, PlFormula> getModel(PlBeliefSet bbase) {
		Set<PlFormula> form = new HashSet<PlFormula>();
		for (PlFormula f : bbase)
			form.add(f);
		return this.getModels(form).iterator().next();
	}


    /** Default Constructor */
    public SimpleModelEnumerator(){}
}
