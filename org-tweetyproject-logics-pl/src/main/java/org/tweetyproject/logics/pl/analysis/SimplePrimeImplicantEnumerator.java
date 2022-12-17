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

import java.util.HashSet;
import java.util.Set;


import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
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
	public SimplePrimeImplicantEnumerator(MinimalModelProvider<Proposition,PlBeliefSet,PlFormula> minModelProvider) {
		this.minModelProvider = minModelProvider;
	}
	/**
	 * 
	 * @param minModels the minimal models from which the prime implicants are computed
	 * @param forms the formulas to be based upon
	 * @return the prime implicants of forms
	 */
	@Override
	public Set<Set<PlFormula>> getPrimeImplicants( PlBeliefSet forms){
		
		Set<Set<PlFormula>> primeImplicants = new HashSet<Set<PlFormula>>();
		for(InterpretationSet<Proposition,PlBeliefSet,PlFormula> inter : this.minModelProvider.getMinModels(forms)) {
			Set<PlFormula> newModel = new HashSet<PlFormula>();
			

			for(PlFormula toDelete : inter) {
				InterpretationSet<Proposition,PlBeliefSet,PlFormula> curr = new PossibleWorld();
				for(PlFormula v : inter) {
					if(!v.equals(toDelete))
						if(v instanceof Proposition)
							curr.add((Proposition) v);
				}

				if(!curr.satisfies(forms)) {
					newModel.addAll(toDelete.getLiterals());
				}
				
			}
			primeImplicants.add(newModel);
		}
		return primeImplicants;
	}
	

}
