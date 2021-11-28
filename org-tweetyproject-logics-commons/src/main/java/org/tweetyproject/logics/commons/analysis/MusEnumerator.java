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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.commons.analysis;

import java.util.Collection;
import java.util.Set;

import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;

/**
 * Interface for classes enumerating MUSes (minimal unsatisfiable sets) and
 * MCSs (maximal consistent sets). 
 * 
 * @author Matthias Thimm
 * @param <S> the type of formulas
 *
 */
public interface MusEnumerator<S extends Formula> extends BeliefSetConsistencyTester<S> {
	
	/**
	 * This method returns the minimal inconsistent subsets of the given
	 * set of formulas. 
	 * @param formulas a set of formulas.
	 * @return the minimal inconsistent subsets of the given
	 *  set of formulas
	 */
	public Collection<Collection<S>> minimalInconsistentSubsets(Collection<S> formulas);
		
	/**
	 * This method returns the maximal consistent subsets of the given
	 * set of formulas
	 * @param formulas a set of formulas
	 * @return the maximal consistent subsets of the given
	 *  set of formulas.
	 */
	public Collection<Collection<S>> maximalConsistentSubsets(Collection<S> formulas);
	
	/**
	 * This method returns the minimal correction subsets of the given
	 * set of formulas (i.e. the complements of maximal consistent subsets)
	 * @param formulas a set of formulas
	 * @return the minimal corrections subsets of the given set of formulas.
	 */
	public Set<Set<S>> minimalCorrectionSubsets(Collection<S> formulas);
	
	/**
	 * Computes the maximal (wrt. cardinality) partitioning {K1,...,Kn}
	 * of K (ie. K is a disjoint union of K1,...,Kn) such that MI(K)
	 * is a disjoint union of MI(K1),...,MI(Kn).
	 * @param formulas a set of formulas K
	 * @return the MI components of K
	 */
	public Collection<Collection<S>> getMiComponents(Collection<S> formulas);
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(org.tweetyproject.BeliefSet)
	 */
	public boolean isConsistent(BeliefSet<S,?> beliefSet);
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(java.util.Collection)
	 */
	public boolean isConsistent(Collection<S> formulas);
		
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetConsistencyTester#isConsistent(org.tweetyproject.Formula)
	 */
	public boolean isConsistent(S formula);
	/**
	 * 
	 * @return whether the consistency measure is installed
	 */
	public boolean isInstalled();
}
