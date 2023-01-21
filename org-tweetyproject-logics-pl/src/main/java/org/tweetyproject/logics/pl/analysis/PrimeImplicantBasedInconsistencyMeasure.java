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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import org.tweetyproject.logics.pl.sat.SimpleModelEnumerator;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * computes the inconsistency measure of the 
 * @author Sebastian Franke
 *
 */
public class PrimeImplicantBasedInconsistencyMeasure extends BeliefSetInconsistencyMeasure<PlFormula> {
	
	public PrimeImplicantEnumerator primeImp;
	
	public PrimeImplicantBasedInconsistencyMeasure(PrimeImplicantEnumerator primeImp) {
		this.primeImp = primeImp;
	}
	public PrimeImplicantBasedInconsistencyMeasure() {
		this.primeImp = new SimplePrimeImplicantEnumerator(new SimpleMinimalModelProvider(new SimpleModelEnumerator()));
	}
	
	/**
	 * 
	 * @param beliefSet the bleiefSet of which the conflicts of prime implicants are calculated
	 * @return the conflicts of the prime implicants
	 */
	public Set<PlFormula> getConflicts(PlBeliefSet beliefSet){
		Set<PlFormula> conflicts = new HashSet<PlFormula>();
		List<Set<PlFormula>> primeImplicates = this.primeImp.getPrimeImplicants(beliefSet); 
		for(Set<PlFormula> primeImplicate1 : primeImplicates) {
			for(Set<PlFormula> primeImplicate2 : primeImplicates) {
				for(PlFormula a : primeImplicate1) {
					for(PlFormula b : primeImplicate2) {
						if(new Conjunction(a,b).getModels().equals(new HashSet<PossibleWorld>())) {
							conflicts.add(a);
							conflicts.add(b);
						}
					}
				}
			}
		}
		return conflicts;
	}

	@Override
	public Double inconsistencyMeasure(Collection<PlFormula> formulas) {
		PlBeliefSet beliefset = new PlBeliefSet();
		beliefset.addAll(formulas);
		double literalCount = 0;
		for(PlFormula f : formulas) {
			literalCount += f.getAtoms().size();
		}
		return (((double) this.getConflicts(beliefset).size()/2) / literalCount) ;
	}

	
}
