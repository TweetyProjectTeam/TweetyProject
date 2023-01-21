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
package org.tweetyproject.logics.pl.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * computes the prime implicants given a set of minimal models
 * @author Sebastian Franke
 *
 */
public class SimplePrimeImplicantEnumerator extends PrimeImplicantEnumerator{
	public MinimalModelProvider<Proposition,PlBeliefSet,PlFormula> minModelProvider;
	/**
	 * 
	 * @param minModelProvider a minimal model rpovider
	 */
	public SimplePrimeImplicantEnumerator(MinimalModelProvider<Proposition,PlBeliefSet,PlFormula> minModelProvider) {
		this.minModelProvider = minModelProvider;
	}
	/**
	 * 
	 * @param primeImplicants
	 * @return
	 */
	public Set<PlFormula> compressPrimeImplicants(Set<Set<PlFormula>> primeImplicants){
		Set<PlFormula> result = new HashSet<PlFormula>();
		PlFormula disjunction = new Disjunction();
		for(Set<PlFormula> p : primeImplicants) {
			for(PlFormula form : p) {
				boolean addToDisjunction = false;
				for(Set<PlFormula> p1 : primeImplicants) {
					//if a literal is mot mandatory for all models, it can be substituted by another non mandatory one
					if(!p1.contains(form)) {
						addToDisjunction = true;
						disjunction = new Disjunction(disjunction, form);
					}
						
				}
				if(addToDisjunction == false) {
					result.add(form);
				}
			}
		}
		if(disjunction.getAtoms().size() > 0)
			result.add(disjunction.toCnf());
		return result;
	}
	/**
	 * 
	 * @param minModels the minimal models from which the prime implicants are computed
	 * @param forms the formulas to be based upon
	 * @return the prime implicants of forms
	 */
	@Override
	public List<Set<PlFormula>> getPrimeImplicants( PlBeliefSet forms){
		
		ArrayList<Set<PlFormula>> primeImplicants = new ArrayList<Set<PlFormula>>();
		//check prime implicats for each formula individually
		for(PlFormula curr: forms) {
			Set<Set<PlFormula>> primeImplicantOfCurr = new  HashSet<Set<PlFormula>>();
			Set<PossibleWorld> modelsOfCurrInterpretationSet = curr.toCnf().getModels();
						
			//iterate through all possible models f the formula
			for(PossibleWorld model : modelsOfCurrInterpretationSet) {
				Set<PlFormula> newModel = new HashSet<PlFormula>();
				//try to remove each literal from he model indiviually and see if the model still works
				for(PlFormula toDelete : curr.getLiterals()) {
					//build a new world which is identical to model, but where todDelete has been flipped
					InterpretationSet<Proposition,PlBeliefSet,PlFormula> newWorld = new PossibleWorld();
					if(!model.contains(toDelete)) {
						if(toDelete instanceof Negation) {
							newWorld.add(toDelete.getAtoms().iterator().next());
						}
					}
					for(PlFormula v : model) {
						if(!v.equals(toDelete)) {

							if(v instanceof Proposition)
								newWorld.add((Proposition) v);
						}
					}
					//if the model fails without toDelete, toDelete is part of the prime implicants
					if(!newWorld.satisfies(curr)) {
						newModel.addAll(toDelete.getLiterals());
					}
					
				}
				primeImplicantOfCurr.add(newModel);
			}
			//compress all prime implicants that only appear in some models to a disjunction
			primeImplicants.add(this.compressPrimeImplicants(primeImplicantOfCurr));
		}
		
		return primeImplicants;
	}
	

}
